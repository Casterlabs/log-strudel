package co.casterlabs.log_strudel.daemon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import co.casterlabs.log_strudel.daemon.api.API;
import co.casterlabs.log_strudel.daemon.config.Config;
import co.casterlabs.log_strudel.daemon.util.FileWatcher;
import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.serialization.JsonParseException;
import xyz.e3ndr.fastloggingframework.FastLoggingFramework;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

public class Bootstrap {
    private static final File CONFIG_FILE = new File("config.json");

    public static void main(String[] args) throws IOException {
        System.setProperty("fastloggingframework.wrapsystem", "true");
        FastLoggingFramework.setColorEnabled(false);

        reload();

        try {
            // Defaults...
            Files.writeString(
                CONFIG_FILE.toPath(),
                Rson.DEFAULT
                    .toJson(LogStrudel.config)
                    .toString(true)
            );
        } catch (IOException ignored) {}

        new FileWatcher(CONFIG_FILE) {
            @Override
            public void onChange() {
                try {
                    reload();
                    FastLogger.logStatic("Reloaded config!");
                } catch (Throwable t) {
                    FastLogger.logStatic(LogLevel.SEVERE, "Unable to reload config file:\n%s", t);
                }
            }
        }
            .start();
    }

    private static void reload() throws IOException {
        if (!CONFIG_FILE.exists()) {
            FastLogger.logStatic("Config file doesn't exist, creating a new file. Modify it and restart LogStrudel.");
            Files.writeString(
                CONFIG_FILE.toPath(),
                Rson.DEFAULT
                    .toJson(new Config())
                    .toString(true)
            );
            System.exit(1);
        }

        Config config;

        try {
            config = Rson.DEFAULT.fromJson(Files.readString(CONFIG_FILE.toPath()), Config.class);
        } catch (JsonParseException e) {
            FastLogger.logStatic(LogLevel.SEVERE, "Unable to parse config file, is it malformed?\n%s", e);
            return;
        }

//        boolean isNew = LogStrudel.config == null;
        LogStrudel.config = config;

        // Reconfigure the JWT verifiers.
        Algorithm signingAlg = Algorithm.HMAC256(config.jwtSecret);

        LogStrudel.verifier = JWT.require(signingAlg)
            .withSubject("logstrudel")
            .build();

        // Reconfigure heartbeats.
        if (LogStrudel.heartbeat != null) {
            LogStrudel.heartbeat.close();
            LogStrudel.heartbeat = null;
        }

        if (config.heartbeatUrl != null && config.heartbeatIntervalSeconds > 0) {
            LogStrudel.heartbeat = new Heartbeat();
            LogStrudel.heartbeat.start();
        }

        // Start the daemon if necessary.
        if (API.isRunning()) {
            if (LogStrudel.config.port != config.port) {
                FastLogger.logStatic(LogLevel.WARNING, "LogStrudel does not support changing the HTTP server port while running. You will need to fully restart for this to take effect.");
            }
        } else {
            try {
                LogStrudel.db = DriverManager.getConnection("jdbc:sqlite:database.sqlite");

                // Since this is our first launch, let's go ahead and create the necessary
                // tables.
                LogStrudel.db
                    .prepareStatement("CREATE TABLE IF NOT EXISTS logstrudel_lines (id PRIMARY KEY NOT NULL, timestamp INTEGER NOT NULL, key TEXT NOT NULL, level TEXT NOT NULL, line TEXT NOT NULL);")
                    .execute();
            } catch (SQLException e) {
                FastLogger.logStatic(LogLevel.FATAL, "Unable to initialize databse:\n%s", e);
                return;
            }

            API.start();
        }

        // Logging
        FastLoggingFramework.setDefaultLevel(config.debug ? LogLevel.DEBUG : LogLevel.INFO);
        API.getServer().getLogger().setCurrentLevel(FastLoggingFramework.getDefaultLevel());
    }

}

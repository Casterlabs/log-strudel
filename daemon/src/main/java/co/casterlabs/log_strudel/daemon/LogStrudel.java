package co.casterlabs.log_strudel.daemon;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

import org.jetbrains.annotations.Nullable;

import com.auth0.jwt.interfaces.JWTVerifier;

import co.casterlabs.log_strudel.daemon.config.Config;
import co.casterlabs.log_strudel.daemon.util.Misc;
import co.casterlabs.log_strudel.daemon.util.RsonBodyHandler;
import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.NonNull;

public class LogStrudel {
    public static Config config;
    public static Heartbeat heartbeat;

    public static JWTVerifier verifier;

    public static JsonArray query(@NonNull String sql, @Nullable Object... params) throws DatabaseException, IOException, InterruptedException {
        JsonObject response = Misc.httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create(LogStrudel.config.database.url))
                .header("Authorization", "Bearer " + config.database.token)
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        new JsonObject()
                            .put("sql", sql)
                            .put("params", Rson.DEFAULT.toJson(params))
                            .toString(false)
                    )
                )
                .build(),
            RsonBodyHandler.of(JsonObject.class)
        ).body();

        if (response.get("error").isJsonObject()) {
            throw new DatabaseException(
                String.format(
                    "[%s] %s",
                    response.getObject("error").getString("code"),
                    response.getObject("error").getString("message")
                )
            );
        }

        return response.getArray("results");
    }

    public static class DatabaseException extends Exception {
        private static final long serialVersionUID = 6248204611557908581L;

        public DatabaseException(String message) {
            super(message);
        }

    }

}

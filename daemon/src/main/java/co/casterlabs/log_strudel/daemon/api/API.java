package co.casterlabs.log_strudel.daemon.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.auth0.jwt.exceptions.JWTVerificationException;

import co.casterlabs.log_strudel.daemon.LogStrudel;
import co.casterlabs.rakurai.io.http.HttpMethod;
import co.casterlabs.rakurai.io.http.HttpStatus;
import co.casterlabs.rakurai.io.http.server.HttpResponse;
import co.casterlabs.rakurai.io.http.server.HttpServer;
import co.casterlabs.rakurai.io.http.server.HttpSession;
import co.casterlabs.rakurai.json.element.JsonArray;
import co.casterlabs.rakurai.json.element.JsonObject;
import co.casterlabs.sora.Sora;
import co.casterlabs.sora.SoraFramework;
import co.casterlabs.sora.SoraLauncher;
import co.casterlabs.sora.api.SoraPlugin;
import co.casterlabs.sora.api.http.SoraHttpSession;
import co.casterlabs.sora.plugins.SoraPlugins;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

public class API {
    private static @Getter boolean running = false;
    private static @Getter HttpServer server;

    private static final String ALLOWED_METHODS;
    static {
        List<String> methods = new LinkedList<>();
        for (HttpMethod method : HttpMethod.values()) {
            methods.add(method.name());
        }
        ALLOWED_METHODS = String.join(", ", methods);
    }

    @SneakyThrows
    public static void start() throws IOException {
        if (running) return;
        running = true;

        SoraFramework framework = new SoraLauncher()
            .setPort(LogStrudel.config.port)
            .buildWithoutPluginLoader();

        SoraPlugins sora = framework.getSora();
        DummyPlugin dummy = new DummyPlugin();

        sora.register(dummy);
        sora.addProvider(dummy, new RouteCORS());
        sora.addProvider(dummy, new RouteLines());
        sora.addProvider(dummy, new RoutePing());

        framework.startHttpServer();

        server = framework.getServer();
    }

    private static class DummyPlugin extends SoraPlugin {
        @Override
        public void onInit(Sora sora) {}

        @Override
        public void onClose() {}

        @Override
        public @Nullable String getVersion() {
            return null;
        }

        @Override
        public @Nullable String getAuthor() {
            return null;
        }

        @Override
        public @NonNull String getName() {
            return "Dummy";
        }

        @Override
        public @NonNull String getId() {
            return "dummy";
        }
    }

    public static HttpResponse success(SoraHttpSession session, HttpStatus status, @NonNull JsonObject data) {
        return cors(
            session,
            HttpResponse.newFixedLengthResponse(
                status,
                new JsonObject()
                    .put("data", data)
                    .put("errors", JsonArray.EMPTY_ARRAY)
            )
        );
    }

    public static HttpResponse error(SoraHttpSession session, HttpStatus status, String... errors) {
        return cors(
            session,
            HttpResponse.newFixedLengthResponse(
                status,
                new JsonObject()
                    .putNull("data")
                    .put("errors", JsonArray.of((Object[]) errors))
            )
        );
    }

    public static boolean authorize(HttpSession session) {
        try {
            String token = session.getHeader("Authorization");
            if (token == null) throw new IllegalAccessException();

            if (!token.startsWith("Bearer ")) throw new IllegalAccessException();
            token = token.substring("Bearer ".length());

            LogStrudel.verifier.verify(token); // Check it.
            return true;
        } catch (JWTVerificationException | IllegalAccessException e) {
            return false;
        }
    }

    public static HttpResponse cors(HttpSession session, HttpResponse response) {
        return response
            .putHeader("Cache-Control", "no-store, no-cache, no-transform")
            .putHeader("Access-Control-Allow-Methods", API.ALLOWED_METHODS)
            .putHeader("Access-Control-Allow-Headers", "Authorization, *")
            .putHeader("Access-Control-Allow-Origin", session.getHeaders().getOrDefault("Origin", Arrays.asList("*")).get(0));
    }

}

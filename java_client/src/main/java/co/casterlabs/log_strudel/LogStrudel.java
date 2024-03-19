package co.casterlabs.log_strudel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;

import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class LogStrudel {
    public static final HttpClient httpClient = HttpClient
        .newBuilder()
        .followRedirects(Redirect.ALWAYS)
        .build();

    private String lsUrl;
    private String lsToken;

    public void publish(@NonNull Line line) throws IOException, InterruptedException, LinePublishingException {
        JsonObject response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create(this.lsUrl))
                .header("Authorization", "Bearer " + this.lsToken)
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        Rson.DEFAULT.toJson(line).toString()
                    )
                )
                .build(),
            RsonBodyHandler.of(JsonObject.class)
        ).body();

        if (response.get("error").isJsonObject()) {
            throw new LinePublishingException(
                String.format(
                    "[%s] %s",
                    response.getObject("error").getString("code"),
                    response.getObject("error").getString("message")
                )
            );
        }
    }

    public static class LinePublishingException extends Exception {
        private static final long serialVersionUID = 6248204611557908581L;

        public LinePublishingException(String message) {
            super(message);
        }

    }

}

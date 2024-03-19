package co.casterlabs.log_strudel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import co.casterlabs.rakurai.json.Rson;
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

    public void publish(@NonNull Line line) throws IOException, InterruptedException {
        httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create(this.lsUrl + "/lines"))
                .header("Authorization", "Bearer " + this.lsToken)
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        Rson.DEFAULT.toJson(line).toString()
                    )
                )
                .build(),
            BodyHandlers.discarding()
        );
    }

}

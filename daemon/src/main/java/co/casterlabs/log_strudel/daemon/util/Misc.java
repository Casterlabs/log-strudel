package co.casterlabs.log_strudel.daemon.util;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;

public class Misc {
    public static final HttpClient httpClient = HttpClient
        .newBuilder()
        .followRedirects(Redirect.ALWAYS)
        .build();

}

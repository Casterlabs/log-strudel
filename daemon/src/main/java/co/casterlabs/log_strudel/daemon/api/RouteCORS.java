package co.casterlabs.log_strudel.daemon.api;

import co.casterlabs.rakurai.io.http.HttpMethod;
import co.casterlabs.rakurai.io.http.StandardHttpStatus;
import co.casterlabs.rakurai.io.http.server.HttpResponse;
import co.casterlabs.sora.api.http.HttpProvider;
import co.casterlabs.sora.api.http.SoraHttpSession;
import co.casterlabs.sora.api.http.annotations.HttpEndpoint;

public class RouteCORS implements HttpProvider {

    @HttpEndpoint(uri = "/.*", allowedMethods = {
            HttpMethod.OPTIONS
    })
    public HttpResponse onPostLine(SoraHttpSession session) {
        return API.cors(session, HttpResponse.newFixedLengthResponse(StandardHttpStatus.OK));
    }

}

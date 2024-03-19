package co.casterlabs.log_strudel.daemon.api;

import co.casterlabs.rakurai.io.http.StandardHttpStatus;
import co.casterlabs.rakurai.io.http.server.HttpResponse;
import co.casterlabs.rakurai.json.element.JsonObject;
import co.casterlabs.sora.api.http.HttpProvider;
import co.casterlabs.sora.api.http.SoraHttpSession;
import co.casterlabs.sora.api.http.annotations.HttpEndpoint;

public class RoutePing implements HttpProvider {

    @HttpEndpoint(uri = "/ping")
    public HttpResponse onPostLine(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            return API.success(session, StandardHttpStatus.OK, JsonObject.singleton("pong", true));
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

}

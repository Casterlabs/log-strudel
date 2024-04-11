package co.casterlabs.log_strudel.daemon.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import co.casterlabs.log_strudel.daemon.LogStrudel;
import co.casterlabs.log_strudel.daemon.LogStrudel.DatabaseRow;
import co.casterlabs.rakurai.io.http.HttpMethod;
import co.casterlabs.rakurai.io.http.StandardHttpStatus;
import co.casterlabs.rakurai.io.http.server.HttpResponse;
import co.casterlabs.rakurai.json.Rson;
import co.casterlabs.rakurai.json.element.JsonObject;
import co.casterlabs.sora.api.http.HttpProvider;
import co.casterlabs.sora.api.http.SoraHttpSession;
import co.casterlabs.sora.api.http.annotations.HttpEndpoint;

public class RouteLines implements HttpProvider {

    @HttpEndpoint(uri = "/lines", allowedMethods = {
            HttpMethod.POST
    })
    public HttpResponse onPostLine(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            Line toAdd = Rson.DEFAULT.fromJson(
                session.getRequestBodyJson(Rson.DEFAULT),
                Line.class
            );

            toAdd.id = UUID.randomUUID().toString();
            // We accept the supplied timestamp.
            // Just in case we missed something and they're doing catch-up.

            LogStrudel.query(
                "INSERT INTO logstrudel_lines (id, timestamp, key, level, line) VALUES (?1, ?2, ?3, ?4, ?5)",
                toAdd.id,
                toAdd.timestamp,
                toAdd.key,
                toAdd.level,
                toAdd.line
            );

            return API.success(session, StandardHttpStatus.CREATED, JsonObject.EMPTY_OBJECT);
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

    @SuppressWarnings("unchecked")
    @HttpEndpoint(uri = "/key-tree", allowedMethods = {
            HttpMethod.GET
    })
    public HttpResponse onGetKeyTree(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            List<DatabaseRow> keysEntries = LogStrudel.query(
                "SELECT DISTINCT key FROM logstrudel_lines WHERE true;"
            );

            Map<String, Object> tree = new HashMap<>();
            for (Map<String, Object> e : keysEntries) {
                String key = (String) e.get("key");
                Map<String, Object> root = tree;

                // Traverse, creating sub trees as we go.
                // What we're really looking for is the structure, not to put any data in it.
                for (String part : key.split("\\.")) {
                    if (root.containsKey(part)) {
                        root = (Map<String, Object>) root.get(part);
                    } else {
                        Map<String, Object> partsMap = new HashMap<>();
                        root.put(part, partsMap);
                        root = partsMap;
                    }
                }
            }

            return API.success(session, StandardHttpStatus.OK, Rson.DEFAULT.toJson(tree).getAsObject());
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

    @HttpEndpoint(uri = "/lines/:id", allowedMethods = {
            HttpMethod.GET
    })
    public HttpResponse onGetLine(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            String id = session.getUriParameters().get("id");

            Map<String, Object> result = LogStrudel.query(
                "SELECT * FROM logstrudel_lines WHERE id = ?1;",
                id
            ).get(0); // Only one result.

            return API.success(session, StandardHttpStatus.OK, JsonObject.singleton("line", result));
        } catch (IndexOutOfBoundsException e) {
            return API.error(session, StandardHttpStatus.NOT_FOUND, "NOT_FOUND");
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

    @HttpEndpoint(uri = "/lines/by-key/:key", allowedMethods = {
            HttpMethod.GET
    })
    public HttpResponse onGetLinesByKey(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            final String sort = "descending".equalsIgnoreCase(session.getQueryParameters().get("sort")) ? "DESC" : "ASC"; // SQL INJECTION NOTICE: DO NOT TRUST USER PARAMS.
            final long after = Long.parseLong(session.getQueryParameters().getOrDefault("after", "0"));
            final long before = Long.parseLong(session.getQueryParameters().getOrDefault("before", String.valueOf(Long.MAX_VALUE)));
            String key = session.getUriParameters().get("key");

            List<DatabaseRow> lineEntries = LogStrudel.query(
                String.format("SELECT * FROM logstrudel_lines WHERE key = ?1 AND timestamp > ?2 AND TIMESTAMP < ?3 ORDER BY timestamp %s;", sort),
                key,
                after,
                before
            );

            return API.success(session, StandardHttpStatus.OK, JsonObject.singleton("lines", lineEntries));
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

    @HttpEndpoint(uri = "/lines/:id", allowedMethods = {
            HttpMethod.DELETE
    })
    public HttpResponse onDeleteLine(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            String id = session.getUriParameters().get("id");

            LogStrudel.query(
                "DELETE FROM logstrudel_lines WHERE id = ?1;",
                id
            );

            return API.success(session, StandardHttpStatus.OK, JsonObject.EMPTY_OBJECT);
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

    @HttpEndpoint(uri = "/lines/by-key/:key", allowedMethods = {
            HttpMethod.DELETE
    })
    public HttpResponse onDeleteByKey(SoraHttpSession session) {
        try {
            if (!API.authorize(session)) {
                return API.error(session, StandardHttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
            }

            String key = session.getUriParameters().get("key");

            LogStrudel.query(
                "DELETE FROM logstrudel_lines WHERE key = ?1;",
                key
            );

            return API.success(session, StandardHttpStatus.OK, JsonObject.EMPTY_OBJECT);
        } catch (Exception e) {
            session.getLogger().exception(e);
            return API.error(session, StandardHttpStatus.INTERNAL_ERROR, "INTERNAL_ERROR");
        }
    }

}

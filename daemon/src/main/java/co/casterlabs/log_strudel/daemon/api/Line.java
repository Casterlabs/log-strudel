package co.casterlabs.log_strudel.daemon.api;

import co.casterlabs.rakurai.json.annotating.JsonClass;

@JsonClass(exposeAll = true)
public class Line {
    public String id = "";
    public String key = "";
    public String line = "";
    public LineLevel level = LineLevel.INFO;
    public long timestamp = System.currentTimeMillis();

    public static enum LineLevel {
        FATAL,
        SEVERE,
        WARNING,
        INFO,
        DEBUG,
        TRACE,
    }

}

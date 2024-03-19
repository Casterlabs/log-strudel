package co.casterlabs.dbohttp.api;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import xyz.e3ndr.fastloggingframework.logging.LogLevel;

@JsonClass(exposeAll = true)
public class Line {
    public String id = "";
    public String key = "";
    public String line = "";
    public LogLevel level = LogLevel.INFO;
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

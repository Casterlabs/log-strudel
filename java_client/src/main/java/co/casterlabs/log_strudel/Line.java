package co.casterlabs.log_strudel;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@JsonClass(exposeAll = true)
public class Line {
    public String key = "";
    public String line = "";
    public LineLevel level = LineLevel.INFO;
    public long timestamp = System.currentTimeMillis();

    public Line setKey(String... parts) {
        this.key = String.join(".", parts);
        return this;
    }

    public static enum LineLevel {
        FATAL,
        SEVERE,
        WARNING,
        INFO,
        DEBUG,
        TRACE,
    }

}

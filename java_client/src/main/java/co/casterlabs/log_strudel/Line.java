package co.casterlabs.log_strudel;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
@JsonClass(exposeAll = true)
public class Line {
    private String key = "";
    private String line = "";
    private LineLevel level = LineLevel.INFO;
    private long timestamp = System.currentTimeMillis();

    public String key() {
        return this.key;
    }

    public Line key(String... parts) {
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

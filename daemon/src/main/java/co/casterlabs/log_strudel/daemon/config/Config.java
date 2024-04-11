package co.casterlabs.log_strudel.daemon.config;

import org.jetbrains.annotations.Nullable;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import lombok.ToString;

@ToString
@JsonClass(exposeAll = true)
public class Config {
    public boolean debug = false;
    public int port = 10244;

    public @Nullable String heartbeatUrl = null;
    public long heartbeatIntervalSeconds = 15;

    /*
     * Testing token:
     * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjIsInN1YiI6ImxvZ3N0cnVkZWwifQ.BGJ4WZfPC4Dsp0rTffFfFBLI4R-GzQiOblQu6n6kbzo
     */
    public String jwtSecret = "CHANGEMEPLEASE";

}

package co.casterlabs.log_strudel.daemon.config;

import co.casterlabs.rakurai.json.annotating.JsonClass;
import lombok.ToString;

@ToString
@JsonClass(exposeAll = true)
public class DatabaseConfig {
    public String url;
    public String token;

}

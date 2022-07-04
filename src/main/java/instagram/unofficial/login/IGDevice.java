package instagram.unofficial.login;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class IGDevice implements Serializable {
    private static final long serialVersionUID = -823447845648L;
    private final String userAgent;
    private final String capabilities;
    private final Map<String, Object> deviceMap;
}

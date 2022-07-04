package instagram.unofficial.login.responses.challenge;

import lombok.Data;

@Data
public class Challenge {
    private String url;
    private String api_path;
    private String challenge_context;
}

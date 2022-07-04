package instagram.unofficial.login.responses;

import instagram.unofficial.login.model.User;
import instagram.unofficial.login.responses.challenge.Challenge;
import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
public class LoginResponse extends IGResponse {
    private User logged_in_user;
    private Challenge challenge;
    private TwoFactorInfo two_factor_info;

    @Data
    public class TwoFactorInfo {
        private String two_factor_identifier;
    }
}

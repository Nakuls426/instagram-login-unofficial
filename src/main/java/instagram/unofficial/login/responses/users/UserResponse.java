package instagram.unofficial.login.responses.users;

import instagram.unofficial.login.model.User;
import instagram.unofficial.login.responses.IGResponse;
import lombok.Data;

@Data
public class UserResponse extends IGResponse {
    private User user;
}
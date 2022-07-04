package instagram.unofficial.login.requests.users;

import instagram.unofficial.login.requests.IGGetRequest;
import instagram.unofficial.login.responses.users.UserResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsersInfoRequest extends IGGetRequest<UserResponse> {
    private long userId;

    @Override
    public String path() {
        return "users/" + userId + "/info/";
    }

    @Override
    public Class<UserResponse> getResponseType() {
        return UserResponse.class;
    }
}


package instagram.unofficial.login.requests.accounts;

import instagram.unofficial.login.IGClient;
import instagram.unofficial.login.models.IGPayload;
import instagram.unofficial.login.requests.IGPostRequest;
import instagram.unofficial.login.responses.LoginResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AccountsLoginRequest extends IGPostRequest<LoginResponse> {
    @NonNull
    private String username;
    @NonNull
    private String password;

    @Override
    public String path() {
        return "accounts/login/";
    }

    @Override
    public IGPayload getPayload(IGClient client) {
        return new LoginPayload(username, password);
    }

    @Override
    public Class<LoginResponse> getResponseType() {
        return LoginResponse.class;
    }

    @Data
    public static class LoginPayload extends IGPayload {
        @NonNull
        private String username;
        @NonNull
        private String enc_password;
        private int login_attempt_account = 0;
    }
}
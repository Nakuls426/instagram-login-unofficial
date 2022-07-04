package instagram.unofficial.login.actions.users;

import instagram.unofficial.login.IGClient;
import instagram.unofficial.login.model.User;
import instagram.unofficial.login.requests.users.UsersInfoRequest;
import instagram.unofficial.login.responses.users.UserResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class UsersAction {
    @NonNull
    private IGClient client;

    /*public CompletableFuture<UserAction> findByUsername(String username) {
        return new UsersUsernameInfoRequest(username).execute(client)
                .thenApply(response -> new UserAction(client, response.getUser()));
    }*/

    public CompletableFuture<User> info(long pk) {
        return new UsersInfoRequest(pk).execute(client)
                .thenApply(UserResponse::getUser);
    }

    /*public CompletableFuture<UsersSearchResponse> search(String query) {
        return new UsersSearchRequest(query).execute(client);
    }*/
}

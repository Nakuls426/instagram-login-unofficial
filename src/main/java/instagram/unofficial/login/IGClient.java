package instagram.unofficial.login;

import instagram.unofficial.login.actions.IGClientActions;
import instagram.unofficial.login.exceptions.ExceptionallyHandler;
import instagram.unofficial.login.exceptions.IGLoginException;
import instagram.unofficial.login.exceptions.IGResponseException.IGFailedResponse;
import instagram.unofficial.login.model.Profile;
import instagram.unofficial.login.models.IGPayload;
import instagram.unofficial.login.requests.IGRequest;
import instagram.unofficial.login.requests.QeSyncRequest;
import instagram.unofficial.login.requests.accounts.AccountsLoginRequest;
import instagram.unofficial.login.responses.IGResponse;
import instagram.unofficial.login.responses.LoginResponse;
import instagram.unofficial.login.utils.IGUtils;
import instagram.unofficial.login.utils.SerializableCookieJar;
import instagram.unofficial.login.utils.SerializeUtil;

import kotlin.Pair;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;

@Data
@Slf4j
public class IGClient implements Serializable {

    private static final long serialVersionUID = -893265874836L;
    private final String $username;
    private final String $password;
    private transient String encryptionId, encryptionKey, authorization;
    @Accessors(chain = true)
    private transient OkHttpClient httpClient;
    private transient String sessionId;
    private transient IGClientActions actions;
    @Accessors(chain = true)
    private transient ExceptionallyHandler exceptionallyHandler;
    private String deviceId;
    private String guid;
    private String phoneId;
    @Setter(AccessLevel.PRIVATE)
    private boolean loggedIn = false;
    @Setter(AccessLevel.PRIVATE)
    private Profile selfProfile;
    @Accessors(chain = true)
    private IGDevice device = IGAndroidDevice.GOOD_DEVICE[0];

    public IGClient(String username, String password) {
        this(username, password, IGUtils.defaultHttpClientBuilder().build());
    }

    public IGClient(String username, String password, OkHttpClient client) {
        this.$username = username;
        this.$password = password;
        this.guid = IGUtils.randomUuid();
        this.phoneId = IGUtils.randomUuid();
        this.deviceId = IGUtils.generateDeviceId(username, password);
        this.httpClient = client;
        this.initializeDefaults();
    }

    private void initializeDefaults() {
        this.sessionId = IGUtils.randomUuid();
        this.actions = new IGClientActions(this);
        this.exceptionallyHandler = new ExceptionallyHandler() {

            @Override
            public <T> T handle(Throwable throwable, Class<T> type) {
                throw new CompletionException(throwable.getCause());
            }

        };
    }

    public IGClientActions actions() {
        return this.actions;
    }

    public CompletableFuture<LoginResponse> sendLoginRequest() {
        return new QeSyncRequest().execute(this)
                .thenCompose(res -> {
                    IGUtils.sleepSeconds(1);
                    return new AccountsLoginRequest($username,
                            IGUtils.encryptPassword(this.$password, this.encryptionId,
                                    this.encryptionKey)).execute(this);
                })
                .thenApply((res) -> {
                    this.setLoggedInState(res);

                    return res;
                });
    }

    /*public CompletableFuture<LoginResponse> sendLoginRequest(String code, String identifier) {
        return new QeSyncRequest().execute(this)
                .thenCompose(res -> new AccountsTwoFactorLoginRequest($username,
                        IGUtils.encryptPassword(this.$password, this.encryptionId,
                                this.encryptionKey),
                        code,
                        identifier).execute(this))
                .thenApply((res) -> {
                    this.setLoggedInState(res);

                    return res;
                });
    }*/

    public <T extends IGResponse> CompletableFuture<T> sendRequest(@NonNull IGRequest<T> req) {
        CompletableFuture<Pair<Response, String>> responseFuture = new CompletableFuture<>();
        log.info("Sending request : {}", req.formUrl(this).toString());
        this.httpClient.newCall(req.formRequest(this)).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                responseFuture.completeExceptionally(exception);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response res) throws IOException {
                log.info("Response for {} : {}", call.request().url(), res.code());
                try (ResponseBody body = res.body()) {

                    responseFuture.complete(new Pair<>(res, Objects.requireNonNull(body).string()));
                }
            }

        });

        return responseFuture
                .thenApply(res -> {
                    setFromResponseHeaders(res.getFirst());
                    log.info("Response for {} with body (truncated) : {}",
                            res.getFirst().request().url(),
                            IGUtils.truncate(res.getSecond()));

                    return req.parseResponse(res);
                })
                .exceptionally((tr) -> this.exceptionallyHandler.handle(tr, req.getResponseType()));
    }

    private void setLoggedInState(LoginResponse state) {
        if (!state.getStatus().equals("ok"))
            return;
        this.loggedIn = true;
        this.selfProfile = state.getLogged_in_user();
        //log.info("Logged into {} ({})", selfProfile.getUsername(), selfProfile.getPk());
    }

    public String getCsrfToken() {
        return IGUtils.getCookieValue(this.getHttpClient().cookieJar(), "csrftoken")
                .orElse("missing");
    }

    public void setFromResponseHeaders(Response res) {
        Optional.ofNullable(res.header("ig-set-password-encryption-key-id"))
                .ifPresent(s -> this.encryptionId = s);
        Optional.ofNullable(res.header("ig-set-password-encryption-pub-key"))
                .ifPresent(s -> this.encryptionKey = s);
        Optional.ofNullable(res.header("ig-set-authorization"))
                .ifPresent(s -> this.authorization = s);
    }

    public IGPayload setIGPayloadDefaults(IGPayload load) {
        load.set_csrftoken(this.getCsrfToken());
        load.setDevice_id(this.deviceId);
        if (selfProfile != null) {
            load.set_uid(selfProfile.getPk().toString());
            load.set_uuid(this.guid);
        } else {
            load.setId(this.guid);
        }
        load.setGuid(this.guid);
        load.setPhone_id(this.phoneId);

        return load;
    }

    public static IGClient.Builder builder() {
        return new IGClient.Builder();
    }

    public static IGClient deserialize(File clientFile, File cookieFile)
            throws ClassNotFoundException, IOException {
        return deserialize(clientFile, cookieFile, IGUtils.defaultHttpClientBuilder());
    }

    public static IGClient deserialize(File clientFile, File cookieFile,
                                       OkHttpClient.Builder clientBuilder) throws ClassNotFoundException, IOException {
        IGClient client = SerializeUtil.deserialize(clientFile, IGClient.class);
        CookieJar jar = SerializeUtil.deserialize(cookieFile, SerializableCookieJar.class);

        client.httpClient = clientBuilder
                .cookieJar(jar)
                .build();

        return client;
    }

    public void serialize(File clientFile, File cookieFile) throws IOException {
        SerializeUtil.serialize(this, clientFile);
        SerializeUtil.serialize(this.httpClient.cookieJar(), cookieFile);
    }

    private Object readResolve() throws ObjectStreamException {
        this.initializeDefaults();
        if (loggedIn)
            log.info("Logged into {} ({})", selfProfile.getUsername(), selfProfile.getPk());
        return this;
    }

    @Accessors(fluent = true)
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Builder {
        private String username;
        private String password;
        private OkHttpClient client;
        private IGDevice device = IGAndroidDevice.GOOD_DEVICE[0];
        private LoginHandler onChallenge;
        private LoginHandler onTwoFactor;
        private BiConsumer<IGClient, LoginResponse> onLogin = (client, login) -> {
        };

        public IGClient build() {
            return new IGClient(username, password, Optional.ofNullable(client)
                    .orElseGet(() -> IGUtils.defaultHttpClientBuilder().build())).setDevice(device);
        }

        /*public IGClient simulatedLogin(Consumer<List<CompletableFuture<?>>> postLoginResponses)
                throws IGLoginException {
            IGClient client = build();
            client.actions.simulate().preLoginFlow().forEach(CompletableFuture::join);
            onLogin.accept(client, performLogin(client));
            postLoginResponses.accept(client.actions.simulate().postLoginFlow());

            return client;
        }

        public IGClient simulatedLogin() throws IGLoginException {
            return simulatedLogin((res) -> {
            });
        }*/

        public IGClient login() throws IGLoginException {
            IGClient client = build();

            onLogin.accept(client, performLogin(client));

            return client;
        }

        private LoginResponse performLogin(IGClient client) throws IGLoginException {
            LoginResponse response = client.sendLoginRequest()
                    .exceptionally(tr -> {
                        LoginResponse loginResponse =
                                IGFailedResponse.of(tr.getCause(), LoginResponse.class);
                        if (loginResponse.getTwo_factor_info() != null && onTwoFactor != null) {
                            loginResponse = onTwoFactor.accept(client, loginResponse);
                        }
                        if (loginResponse.getChallenge() != null && onChallenge != null) {
                            loginResponse = onChallenge.accept(client, loginResponse);
                            client.setLoggedInState(loginResponse);
                        }

                        return loginResponse;
                    })
                    .join();

            if (!client.isLoggedIn()) {
                throw new IGLoginException(client, response);
            }

            return response;
        }

        @FunctionalInterface
        public interface LoginHandler {
            LoginResponse accept(IGClient client, LoginResponse t);
        }

    }
}
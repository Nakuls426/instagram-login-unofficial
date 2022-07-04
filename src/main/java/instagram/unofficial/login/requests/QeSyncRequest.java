package instagram.unofficial.login.requests;

import instagram.unofficial.login.IGConstants;
import instagram.unofficial.login.models.IGBaseModel;
import instagram.unofficial.login.models.IGPayload;
import instagram.unofficial.login.responses.IGResponse;
import instagram.unofficial.login.IGClient;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class QeSyncRequest extends IGPostRequest<IGResponse> {
    private boolean preLogin;

    @Override
    public String baseApiUrl() {
        return preLogin ? IGConstants.B_BASE_API_URL : super.baseApiUrl();
    }

    @Override
    protected IGBaseModel getPayload(IGClient client) {
        return preLogin ? new PrePayload(client.getGuid()) : new IGPayload() {
            @Getter
            private final String experiments = IGConstants.DEVICE_EXPERIMENTS;
        };
    }

    @Override
    public String path() {
        return "qe/sync/";
    }

    @Override
    public Class<IGResponse> getResponseType() {
        return IGResponse.class;
    }

    @Data
    private class PrePayload extends IGBaseModel {
        private final String id;
        private final String server_config_retrieval = "1";
        private final String experiments = IGConstants.DEVICE_EXPERIMENTS;
    }

    @Data
    private class PostPayload extends IGPayload {
        private final String experiments = IGConstants.DEVICE_EXPERIMENTS;
    }
}

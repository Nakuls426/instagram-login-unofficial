package instagram.unofficial.login.exceptions;

import lombok.*;
import instagram.unofficial.login.responses.IGResponse;
import instagram.unofficial.login.utils.IGUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class IGResponseException extends IOException {
    @NonNull
    private final IGResponse response;

    @NotNull
    public IGResponse getResponse() {
        return response;
    }

    public IGResponseException(IGResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class IGFailedResponse extends IGResponse {
        private String status = "fail";
        private final String message;

        public IGFailedResponse(String message) {
            super();
            this.message = message;
        }

        public static IGResponse of(Throwable throwable) {
            if (throwable instanceof IGResponseException)
                return ((IGResponseException) throwable).getResponse();
            return new IGFailedResponse(throwable.toString());
        }

        public static <T> T of(Throwable throwable, Class<T> clazz) {
            return IGUtils.convertToView(of(throwable), clazz);
        }
    }

}

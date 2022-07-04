package instagram.unofficial.login.exceptions;

import instagram.unofficial.login.exceptions.IGResponseException.IGFailedResponse;
public interface ExceptionallyHandler {

    ExceptionallyHandler WRAPPED_TO_IGRESPONSE = new ExceptionallyHandler() {

        @Override
        public <T> T handle(Throwable throwable, Class<T> type) {
            return IGFailedResponse.of(throwable.getCause(), type);
        }

    };

    public <T> T handle(Throwable throwable, Class<T> type);
}

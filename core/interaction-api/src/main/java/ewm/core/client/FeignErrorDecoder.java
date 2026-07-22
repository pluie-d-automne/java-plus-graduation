package ewm.core.client;

import ewm.core.exception.ConflictException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
    if (response.status() == 409) {
        return new ConflictException(response.reason());
    }

    return defaultDecoder.decode(methodKey, response);
    }
}

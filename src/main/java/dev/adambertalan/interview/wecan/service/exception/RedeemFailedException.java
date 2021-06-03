package dev.adambertalan.interview.wecan.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RedeemFailedException extends RuntimeException {

    public RedeemFailedException() {
        super();
    }

    public RedeemFailedException(String message) {
        super(message);
    }

    public RedeemFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

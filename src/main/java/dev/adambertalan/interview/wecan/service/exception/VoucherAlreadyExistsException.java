package dev.adambertalan.interview.wecan.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VoucherAlreadyExistsException extends RuntimeException {

    public VoucherAlreadyExistsException() {
        super();
    }

    public VoucherAlreadyExistsException(String message) {
        super(message);
    }

    public VoucherAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

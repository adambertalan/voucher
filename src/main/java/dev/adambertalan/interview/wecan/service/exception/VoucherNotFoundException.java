package dev.adambertalan.interview.wecan.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VoucherNotFoundException extends RuntimeException {

    public VoucherNotFoundException() {
        super();
    }

    public VoucherNotFoundException(String message) {
        super(message);
    }

    public VoucherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

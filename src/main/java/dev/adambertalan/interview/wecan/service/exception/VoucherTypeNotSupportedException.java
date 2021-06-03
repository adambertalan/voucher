package dev.adambertalan.interview.wecan.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class VoucherTypeNotSupportedException extends RuntimeException {

    public VoucherTypeNotSupportedException() {
        super();
    }

    public VoucherTypeNotSupportedException(String message) {
        super(message);
    }

    public VoucherTypeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}

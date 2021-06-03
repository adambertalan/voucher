package dev.adambertalan.interview.wecan.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidVoucherTypeDataException extends RuntimeException {
    public InvalidVoucherTypeDataException() {
        super();
    }

    public InvalidVoucherTypeDataException(String message) {
        super(message);
    }

    public InvalidVoucherTypeDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

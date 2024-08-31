package kr.co.polycube.backendtest.Global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
    private final String reason;

    public InvalidRequestException(String reason) {
        super(reason);
        this.reason = reason;
    }
}

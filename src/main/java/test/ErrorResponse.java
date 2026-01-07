package test;


import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String, String> errors;
    private final LocalDateTime timestamp;

    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}

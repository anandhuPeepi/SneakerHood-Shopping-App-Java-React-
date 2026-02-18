package PPs.ShoppingApp.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // NEW: for validation errors
    private Map<String, List<String>> fieldErrors;

    // Constructor without field errors (for normal errors)
    public ApiError(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Constructor WITH field errors (for validation)
    public ApiError(LocalDateTime timestamp, int status, String error, String message, String path,
                    Map<String, List<String>> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, List<String>> getFieldErrors() { return fieldErrors; }
}

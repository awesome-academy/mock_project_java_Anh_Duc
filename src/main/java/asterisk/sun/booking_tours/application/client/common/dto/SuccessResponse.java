package asterisk.sun.booking_tours.application.client.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard success response format for REST API
 * Contains status code, message, and optional data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public SuccessResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

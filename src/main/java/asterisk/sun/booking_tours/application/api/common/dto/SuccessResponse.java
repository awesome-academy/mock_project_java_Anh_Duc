package asterisk.sun.booking_tours.application.api.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard success response format for REST API
 * Contains status code, message, and optional data
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination metadata wrapper
 * Contains the actual data list and pagination information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private T data;
}

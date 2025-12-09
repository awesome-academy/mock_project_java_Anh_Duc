package asterisk.sun.booking_tours.application.api.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Paginated response wrapper for API endpoints
 * Extends SuccessResponse to avoid code duplication
 * Contains pagination metadata and the actual data
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> extends SuccessResponse<PaginationMetadata<T>> {

    public PaginatedResponse() {
        super();
    }

    public PaginatedResponse(int status, String message, List<T> items, long total, int page, int limit) {
        super(status, message, new PaginationMetadata<>(items, total, page, limit));
    }
}

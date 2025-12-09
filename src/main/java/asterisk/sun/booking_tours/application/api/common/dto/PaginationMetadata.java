package asterisk.sun.booking_tours.application.api.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class PaginationMetadata<T> {
    private List<T> items;
    private long total;
    private int page;
    private int limit;
    private int totalPages;

    public PaginationMetadata(List<T> items, long total, int page, int limit) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.limit = limit;
        this.totalPages = (int) Math.ceil((double) total / limit);
    }
}

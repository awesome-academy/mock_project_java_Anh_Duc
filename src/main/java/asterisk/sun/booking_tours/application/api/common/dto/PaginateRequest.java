package asterisk.sun.booking_tours.application.api.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginateRequest {
    private Integer page;
    private Integer limit;
    private String sortBy;

    public Integer getPage() {
        return (page == null || page < 0) ? 0 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return (limit == null || limit < 1) ? 10 : limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Pageable getPageable() {
        return PageRequest.of(getPage(), getLimit(), Sort.by(Sort.Direction.ASC, "id"));
    }
}

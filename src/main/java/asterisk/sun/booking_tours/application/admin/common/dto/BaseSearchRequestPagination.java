package asterisk.sun.booking_tours.application.admin.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class BaseSearchRequestPagination {
    private Integer page;
    private Integer size;
    private String sortBy;

    public Integer getPage() {
        return (page == null || page < 0) ? 0 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return (size == null || size < 1) ? 10 : size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Pageable getPageable() {
        return PageRequest.of(getPage(), getSize(), Sort.by(Sort.Direction.ASC, "id"));
    }
}

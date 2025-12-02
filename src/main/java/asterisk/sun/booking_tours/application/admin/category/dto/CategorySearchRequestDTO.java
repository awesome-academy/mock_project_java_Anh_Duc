package asterisk.sun.booking_tours.application.admin.category.dto;

import asterisk.sun.booking_tours.application.admin.common.dto.BaseSearchRequestPagination;

public class CategorySearchRequestDTO extends BaseSearchRequestPagination {
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

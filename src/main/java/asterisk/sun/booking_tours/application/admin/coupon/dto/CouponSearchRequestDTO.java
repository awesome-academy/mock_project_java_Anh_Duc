package asterisk.sun.booking_tours.application.admin.coupon.dto;

public class CouponSearchRequestDTO {
    private String keyword;
    private String status;
    private int page = 0;
    private int size = 10;

    // Constructors
    public CouponSearchRequestDTO() {}

    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

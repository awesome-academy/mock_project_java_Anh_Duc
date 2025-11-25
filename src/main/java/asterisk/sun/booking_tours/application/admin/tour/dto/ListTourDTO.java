package asterisk.sun.booking_tours.application.admin.tour.dto;

public class ListTourDTO {
    private Long id;
    private String name;
    private String title;
    private String description;
    private String slug;
    private Double price;
    private String thumbnailUrl;
    private String departureLocation;
    private String mainDestination;
    private Integer durationDays;
    private Integer durationNights;
    private Double priceAdult;
    private Double priceChild;
    private String currency;
    private String categoryName;
    private Long categoryId;
    private String creatorUsername;
    private Long creatorId;

    public ListTourDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getMainDestination() {
        return mainDestination;
    }

    public void setMainDestination(String mainDestination) {
        this.mainDestination = mainDestination;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getDurationNights() {
        return durationNights;
    }

    public void setDurationNights(Integer durationNights) {
        this.durationNights = durationNights;
    }

    public Double getPriceAdult() {
        return priceAdult;
    }

    public void setPriceAdult(Double priceAdult) {
        this.priceAdult = priceAdult;
    }

    public Double getPriceChild() {
        return priceChild;
    }

    public void setPriceChild(Double priceChild) {
        this.priceChild = priceChild;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}

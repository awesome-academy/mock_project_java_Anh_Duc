package asterisk.sun.booking_tours.application.admin.tour.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FormEditTourDTO {
    private Long id;

    @NotBlank(message = "Tour name is required")
    @Size(min = 3, max = 255, message = "Tour name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotBlank(message = "Slug is required")
    @Size(min = 3, max = 255, message = "Slug must be between 3 and 255 characters")
    private String slug;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private String thumbnailUrl;

    @NotBlank(message = "Departure location is required")
    private String departureLocation;

    @NotBlank(message = "Main destination is required")
    private String mainDestination;

    private String itinerary;

    @NotNull(message = "Duration days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Duration nights is required")
    @Min(value = 0, message = "Duration nights must be at least 0")
    private Integer durationNights;

    @NotNull(message = "Adult price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Adult price must be greater than 0")
    private Double priceAdult;

    @NotNull(message = "Child price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Child price must be greater than 0")
    private Double priceChild;

    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    private String termsAndConditions;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Creator is required")
    private Long creatorId;

    public FormEditTourDTO() {
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

    public String getItinerary() {
        return itinerary;
    }

    public void setItinerary(String itinerary) {
        this.itinerary = itinerary;
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

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}

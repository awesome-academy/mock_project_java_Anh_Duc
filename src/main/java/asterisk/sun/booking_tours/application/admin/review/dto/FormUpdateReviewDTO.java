package asterisk.sun.booking_tours.application.admin.review.dto;

import asterisk.sun.booking_tours.core.review.ReviewStatus;
import asterisk.sun.booking_tours.core.review.ReviewableType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FormUpdateReviewDTO {
    private Long id;

    @NotNull(message = "Status is required")
    private ReviewStatus status;

    private ReviewableType reviewableType;
    private Long reviewableId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String content;
    private String userName;
    private String userEmail;

    // Constructors
    public FormUpdateReviewDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public ReviewableType getReviewableType() {
        return reviewableType;
    }

    public void setReviewableType(ReviewableType reviewableType) {
        this.reviewableType = reviewableType;
    }

    public Long getReviewableId() {
        return reviewableId;
    }

    public void setReviewableId(Long reviewableId) {
        this.reviewableId = reviewableId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

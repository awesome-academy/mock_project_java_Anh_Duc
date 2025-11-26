package asterisk.sun.booking_tours.application.admin.review.dto;

import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.review.ReviewStatus;
import asterisk.sun.booking_tours.core.review.ReviewableType;

public class ListReviewDTO {
    private Long id;
    private String userName;
    private String userEmail;
    private ReviewableType reviewableType;
    private Long reviewableId;
    private Integer rating;
    private String content;
    private ReviewStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ListReviewDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

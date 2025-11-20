package sun.asterisk.booking_tours.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import sun.asterisk.booking_tours.core.enums.ReviewStatus;
import sun.asterisk.booking_tours.core.enums.ReviewableType;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reviewable_type")
    private ReviewableType reviewableType;
    
    @Column(name = "reviewable_id")
    private Long reviewableId;
    
    @Column(name = "rating")
    private Integer rating;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status;
    
    // Constructors
    public Review() {}
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
}

package asterisk.sun.booking_tours.module.entities;

import asterisk.sun.booking_tours.module.common.abtracts.BaseEntity;
import asterisk.sun.booking_tours.module.enums.LikeableType;
import asterisk.sun.booking_tours.module.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "parent_id")
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "likeable_type")
    private LikeableType likeableType;

    @Column(name = "likeable_id")
    private Long likeableId;

    // Constructors
    public Like() {}

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LikeableType getLikeableType() {
        return likeableType;
    }

    public void setLikeableType(LikeableType likeableType) {
        this.likeableType = likeableType;
    }

    public Long getLikeableId() {
        return likeableId;
    }

    public void setLikeableId(Long likeableId) {
        this.likeableId = likeableId;
    }
}

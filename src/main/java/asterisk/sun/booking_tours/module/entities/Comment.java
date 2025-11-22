package asterisk.sun.booking_tours.module.entities;

import asterisk.sun.booking_tours.module.common.entities.BaseEntity;
import asterisk.sun.booking_tours.module.enums.CommentableType;
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
@Table(name = "comments")
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "commentable_type")
    private CommentableType commentableType;

    @Column(name = "commentable_id")
    private Long commentableId;

    // Constructors
    public Comment() {}

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentableType getCommentableType() {
        return commentableType;
    }

    public void setCommentableType(CommentableType commentableType) {
        this.commentableType = commentableType;
    }

    public Long getCommentableId() {
        return commentableId;
    }

    public void setCommentableId(Long commentableId) {
        this.commentableId = commentableId;
    }
}

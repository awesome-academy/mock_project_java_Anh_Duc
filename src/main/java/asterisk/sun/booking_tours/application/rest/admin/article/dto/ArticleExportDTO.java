package asterisk.sun.booking_tours.application.rest.admin.article.dto;

import asterisk.sun.booking_tours.common.utils.excel.ExcelColumn;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;

import java.time.LocalDateTime;

/**
 * DTO for Article export to Excel
 */
public class ArticleExportDTO {

    @ExcelColumn(value = "ID", index = 0)
    private Long id;

    @ExcelColumn(value = "Title", index = 1)
    private String title;

    @ExcelColumn(value = "Slug", index = 2)
    private String slug;

    @ExcelColumn(value = "Content", index = 3)
    private String content;

    @ExcelColumn(value = "Article Type", index = 4)
    private ArticleType articleType;

    @ExcelColumn(value = "Thumbnail", index = 5)
    private String thumbnail;

    @ExcelColumn(value = "Status", index = 6)
    private ArticleStatus status;

    @ExcelColumn(value = "User ID", index = 7)
    private Long userId;

    @ExcelColumn(value = "Author", index = 8)
    private String userName;

    @ExcelColumn(value = "Created At", index = 9)
    private LocalDateTime createdAt;

    @ExcelColumn(value = "Updated At", index = 10)
    private LocalDateTime updatedAt;

    // Constructors
    public ArticleExportDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

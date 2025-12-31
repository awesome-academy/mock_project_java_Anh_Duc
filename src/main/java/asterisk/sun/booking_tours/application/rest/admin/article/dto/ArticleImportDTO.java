package asterisk.sun.booking_tours.application.rest.admin.article.dto;

import asterisk.sun.booking_tours.common.utils.excel.ExcelColumn;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;

/**
 * DTO for importing Article data from Excel
 * Uses @ExcelColumn annotation for Reflection-based mapping
 */
public class ArticleImportDTO {

    @ExcelColumn(value = "title", required = true)
    private String title;

    @ExcelColumn(value = "content", required = true)
    private String content;

    @ExcelColumn(value = "article_type", required = true)
    private ArticleType articleType;

    @ExcelColumn(value = "thumbnail")
    private String thumbnail;

    @ExcelColumn(value = "status", defaultValue = "DRAFT")
    private ArticleStatus status;

    @ExcelColumn(value = "user_id")
    private Long userId;

    // Constructors
    public ArticleImportDTO() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}

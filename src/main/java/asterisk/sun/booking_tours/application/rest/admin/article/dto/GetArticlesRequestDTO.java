package asterisk.sun.booking_tours.application.rest.admin.article.dto;

import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;

/**
 * Request DTO for getting articles with pagination and filtering
 */
public class GetArticlesRequestDTO {

    private int page = 1;
    private int size = 10;
    private String keyword;
    private ArticleType articleType;
    private ArticleStatus status;
    private String sortBy = "createdAt";
    private String sortDir = "desc";

    // Constructors
    public GetArticlesRequestDTO() {}

    // Getters and Setters
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}

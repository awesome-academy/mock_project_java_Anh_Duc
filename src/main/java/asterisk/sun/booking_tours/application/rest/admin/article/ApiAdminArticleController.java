package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportException;
import asterisk.sun.booking_tours.core.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for Article Admin operations
 * Includes Excel import functionality using Apache POI and Reflection
 */
@RestController
@RequestMapping("/api/v1/admin/articles")
public class ApiAdminArticleController {

    private final ArticleAdminService articleAdminService;

    public ApiAdminArticleController(ArticleAdminService articleAdminService) {
        this.articleAdminService = articleAdminService;
    }

    /**
     * Get paginated list of articles with filtering
     *
     * @param param Query parameters for pagination and filtering
     * @return Paginated list of articles
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<ArticleResponseDTO>> getListArticles(GetArticlesRequestDTO param) {
        Page<Article> articles = articleAdminService.getArticles(param);
        List<ArticleResponseDTO> data = articles.getContent().stream()
                .map(articleAdminService::mapEntityToResponse)
                .toList();

        PaginatedResponse<ArticleResponseDTO> response = new PaginatedResponse<>(
                HttpStatus.OK.value(),
                "Get List Articles Successfully",
                data,
                articles.getTotalElements(),
                articles.getNumber() + 1,
                articles.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * Get article by ID
     *
     * @param id Article ID
     * @return Article details
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ArticleResponseDTO>> getArticleById(@PathVariable Long id) {
        Article article = articleAdminService.getArticleById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        ArticleResponseDTO dto = articleAdminService.mapEntityToResponse(article);

        SuccessResponse<ArticleResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Get Article Successfully",
                dto);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete article by ID
     *
     * @param id Article ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteArticle(@PathVariable Long id) {
        articleAdminService.deleteArticle(id);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Article deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Import articles from Excel file
     * Uses Apache POI for Excel parsing and Reflection for DTO mapping
     *
     * Excel file format:
     * | slug | content | article_type | thumbnail | status | user_id |
     *
     * - slug: Required. Unique identifier for URL
     * - content: Required. Article content
     * - article_type: Required. One of: NEWS, BLOG, GUIDE, TIPS, DESTINATION,
     * ANNOUNCEMENT
     * - thumbnail: Optional. Image URL
     * - status: Optional. One of: DRAFT, PUBLISHED, ARCHIVED, DELETED. Default:
     * DRAFT
     * - user_id: Optional. Author's user ID
     *
     * @param file Excel file (.xlsx or .xls)
     * @return Import result with success count, error count, and details
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ArticleImportResponseDTO>> importFromExcel(
            @RequestParam("file") MultipartFile file) {

        try {
            ArticleImportResponseDTO result = articleAdminService.importFromExcel(file);

            String message = String.format(
                    "Import completed: %d/%d articles imported successfully",
                    result.getSuccessCount(),
                    result.getTotalRows());

            SuccessResponse<ArticleImportResponseDTO> response = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    message,
                    result);

            return ResponseEntity.ok(response);
        } catch (ExcelImportException e) {
            SuccessResponse<ArticleImportResponseDTO> response = new SuccessResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get Excel template information for article import
     *
     * @return Template format description
     */
    @GetMapping("/import/template")
    public ResponseEntity<SuccessResponse<ImportTemplateInfo>> getImportTemplate() {
        ImportTemplateInfo template = new ImportTemplateInfo();
        template.setDescription("Excel template for importing articles");
        template.setColumns(List.of(
                new ColumnInfo("title", "String", true, "Article title (slug will be auto-generated from title)"),
                new ColumnInfo("content", "String", true, "Article content (HTML or plain text)"),
                new ColumnInfo("article_type", "Enum", true, "NEWS, BLOG, GUIDE, TIPS, DESTINATION, ANNOUNCEMENT"),
                new ColumnInfo("thumbnail", "String", false, "Image URL for thumbnail"),
                new ColumnInfo("status", "Enum", false, "DRAFT, PUBLISHED, ARCHIVED, DELETED (default: DRAFT)"),
                new ColumnInfo("user_id", "Long", false, "Author's user ID")));

        SuccessResponse<ImportTemplateInfo> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Import template information",
                template);

        return ResponseEntity.ok(response);
    }

    /**
     * Template information for Excel import
     */
    public static class ImportTemplateInfo {
        private String description;
        private List<ColumnInfo> columns;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<ColumnInfo> getColumns() {
            return columns;
        }

        public void setColumns(List<ColumnInfo> columns) {
            this.columns = columns;
        }
    }

    /**
     * Column information for template
     */
    public static class ColumnInfo {
        private String name;
        private String type;
        private boolean required;
        private String description;

        public ColumnInfo() {
        }

        public ColumnInfo(String name, String type, boolean required, String description) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

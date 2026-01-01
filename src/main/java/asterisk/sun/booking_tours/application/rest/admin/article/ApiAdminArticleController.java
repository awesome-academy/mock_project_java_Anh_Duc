package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.utils.excel.ExcelColumnInfo;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportException;
import asterisk.sun.booking_tours.common.utils.excel.ExcelTemplateInfo;
import asterisk.sun.booking_tours.core.article.Article;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
     * User ID is automatically set from the currently logged-in user
     *
     * Excel file format:
     * | title | content | article_type | thumbnail | status |
     *
     * - title: Required. Article title (slug will be auto-generated)
     * - content: Required. Article content
     * - article_type: Required. One of: NEWS, BLOG, GUIDE, TIPS, DESTINATION,
     * ANNOUNCEMENT
     * - thumbnail: Optional. Image URL
     * - status: Optional. One of: DRAFT, PUBLISHED, ARCHIVED, DELETED. Default:
     * DRAFT
     *
     * @param file Excel file (.xlsx or .xls)
     * @param userDetails Current logged-in user (automatically injected)
     * @return Import result with success count, error count, and details
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ArticleImportResponseDTO>> importFromExcel(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            ArticleImportResponseDTO result = articleAdminService.importFromExcel(file, userDetails);

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
    public ResponseEntity<SuccessResponse<ExcelTemplateInfo>> getImportTemplate() {
        ExcelTemplateInfo template = new ExcelTemplateInfo(
                "Excel template for importing articles. User ID is automatically set from the currently logged-in user.",
                List.of(
                        new ExcelColumnInfo("title", "String", true, "Article title (slug will be auto-generated from title)"),
                        new ExcelColumnInfo("content", "String", true, "Article content (HTML or plain text)"),
                        new ExcelColumnInfo("article_type", "Enum", true, "NEWS, BLOG, GUIDE, TIPS, DESTINATION, ANNOUNCEMENT"),
                        new ExcelColumnInfo("thumbnail", "String", false, "Image URL for thumbnail"),
                        new ExcelColumnInfo("status", "Enum", false, "DRAFT, PUBLISHED, ARCHIVED, DELETED (default: DRAFT)")));

        SuccessResponse<ExcelTemplateInfo> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Import template information",
                template);

        return ResponseEntity.ok(response);
    }
}

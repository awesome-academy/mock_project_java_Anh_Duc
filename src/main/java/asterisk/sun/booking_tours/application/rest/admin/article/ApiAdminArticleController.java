package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.utils.excel.ExcelColumnInfo;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportException;
import asterisk.sun.booking_tours.common.utils.excel.ExcelTemplateInfo;
import asterisk.sun.booking_tours.core.article.Article;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/articles")
public class ApiAdminArticleController {

    private final ArticleAdminService articleAdminService;

    public ApiAdminArticleController(ArticleAdminService articleAdminService) {
        this.articleAdminService = articleAdminService;
    }

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

    @PostMapping
    public ResponseEntity<SuccessResponse<ArticleResponseDTO>> store(@Valid @RequestBody ArticleImportDTO request) {
        Article article = articleAdminService.createArticle(request);
        ArticleResponseDTO dto = articleAdminService.mapEntityToResponse(article);

        SuccessResponse<ArticleResponseDTO> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Article created successfully",
                dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ArticleResponseDTO>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleImportDTO request) {
        Article article = articleAdminService.updateArticle(id, request);
        ArticleResponseDTO dto = articleAdminService.mapEntityToResponse(article);

        SuccessResponse<ArticleResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Article updated successfully",
                dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteArticle(@PathVariable Long id) {
        articleAdminService.deleteArticle(id);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Article deleted successfully");

        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/import/template")
    public ResponseEntity<SuccessResponse<ExcelTemplateInfo>> getImportTemplate() {
        ExcelTemplateInfo template = new ExcelTemplateInfo(
                "Excel template for importing articles. User ID is automatically set from the currently logged-in user.",
                List.of(
                        new ExcelColumnInfo("title", "String", true,
                                "Article title (slug will be auto-generated from title)"),
                        new ExcelColumnInfo("content", "String", true, "Article content (HTML or plain text)"),
                        new ExcelColumnInfo("article_type", "Enum", true,
                                "NEWS, BLOG, GUIDE, TIPS, DESTINATION, ANNOUNCEMENT"),
                        new ExcelColumnInfo("thumbnail", "String", false, "Image URL for thumbnail"),
                        new ExcelColumnInfo("status", "Enum", false,
                                "DRAFT, PUBLISHED, ARCHIVED, DELETED (default: DRAFT)")));

        SuccessResponse<ExcelTemplateInfo> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Import template information",
                template);

        return ResponseEntity.ok(response);
    }
}

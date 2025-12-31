package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportResult;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportService;
import asterisk.sun.booking_tours.core.article.Article;
import asterisk.sun.booking_tours.core.article.ArticleRepository;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import com.github.slugify.Slugify;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for Article admin operations
 */
@Service
public class ArticleAdminService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ExcelImportService excelImportService;
    private final Slugify slugify;

    public ArticleAdminService(ArticleRepository articleRepository,
            UserRepository userRepository,
            ExcelImportService excelImportService) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.excelImportService = excelImportService;
        this.slugify = Slugify.builder().build();
    }

    /**
     * Get paginated list of articles with filtering
     */
    public Page<Article> getArticles(GetArticlesRequestDTO request) {
        Sort sort = Sort.by(
                request.getSortDir().equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                request.getSortBy());

        Pageable pageable = PageRequest.of(
                Math.max(0, request.getPage() - 1),
                request.getSize(),
                sort);

        Specification<Article> spec = buildSpecification(request);
        return articleRepository.findAll(spec, pageable);
    }

    /**
     * Build JPA Specification for filtering articles
     */
    private Specification<Article> buildSpecification(GetArticlesRequestDTO request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search in slug and content
            if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate slugPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("slug")), keyword);
                Predicate contentPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("content")), keyword);
                predicates.add(criteriaBuilder.or(slugPredicate, contentPredicate));
            }

            // Filter by article type
            if (request.getArticleType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("articleType"), request.getArticleType()));
            }

            // Filter by status
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Get article by ID
     */
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * Create new article
     */
    @Transactional
    public Article createArticle(ArticleImportDTO dto) {
        Article article = new Article();
        mapDtoToEntity(dto, article);
        return articleRepository.save(article);
    }

    /**
     * Update existing article
     */
    @Transactional
    public Article updateArticle(Long id, ArticleImportDTO dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        mapDtoToEntity(dto, article);
        return articleRepository.save(article);
    }

    /**
     * Delete article by ID
     */
    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new RuntimeException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    /**
     * Import articles from Excel file using Apache POI and Reflection
     * User ID is automatically set from the currently logged-in user
     */
    @Transactional
    public ArticleImportResponseDTO importFromExcel(MultipartFile file, UserDetails userDetails) {
        // Get current logged-in user
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Parse Excel file using Reflection-based ExcelImportService
        ExcelImportResult<ArticleImportDTO> importResult = excelImportService.importFromExcel(file,
                ArticleImportDTO.class);

        List<ArticleResponseDTO> importedArticles = new ArrayList<>();
        List<ExcelImportResult.ExcelImportError> allErrors = new ArrayList<>(importResult.getErrors());

        // Process each successfully parsed row
        for (int i = 0; i < importResult.getSuccessItems().size(); i++) {
            ArticleImportDTO dto = importResult.getSuccessItems().get(i);
            try {
                // Validate and save article with current user
                Article article = createArticleFromDto(dto, currentUser);
                Article savedArticle = articleRepository.save(article);

                ArticleResponseDTO responseDTO = mapEntityToResponse(savedArticle);
                importedArticles.add(responseDTO);
            } catch (Exception e) {
                // Add error for this row
                allErrors.add(new ExcelImportResult.ExcelImportError(
                        i + 2, // Row number (1-indexed, plus header)
                        "general",
                        e.getMessage(),
                        dto.getTitle()));
            }
        }

        return new ArticleImportResponseDTO(
                importResult.getTotalRows(),
                importedArticles.size(),
                allErrors.size(),
                importedArticles,
                allErrors);
    }

    /**
     * Create Article entity from DTO with current logged-in user
     */
    private Article createArticleFromDto(ArticleImportDTO dto, User currentUser) {
        // Generate slug from title
        String slug = slugify.slugify(dto.getTitle());

        // Ensure slug uniqueness
        String baseSlug = slug;
        int counter = 1;
        while (articleRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSlug(slug);
        article.setContent(dto.getContent());
        article.setArticleType(dto.getArticleType() != null ? dto.getArticleType() : ArticleType.NEWS);
        article.setThumbnail(dto.getThumbnail());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : ArticleStatus.DRAFT);

        // Always set the current logged-in user as the article author
        article.setUser(currentUser);

        return article;
    }

    /**
     * Map DTO to existing entity
     * Note: User is not updated from DTO - it's set from the currently logged-in user during import
     */
    private void mapDtoToEntity(ArticleImportDTO dto, Article article) {
        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
            // Generate new slug from title
            String slug = slugify.slugify(dto.getTitle());
            // Check uniqueness for new or different slug
            if (article.getId() == null || !slug.equals(article.getSlug())) {
                String baseSlug = slug;
                int counter = 1;
                while (articleRepository.existsBySlug(slug)) {
                    slug = baseSlug + "-" + counter++;
                }
            }
            article.setSlug(slug);
        }

        if (dto.getContent() != null) {
            article.setContent(dto.getContent());
        }

        if (dto.getArticleType() != null) {
            article.setArticleType(dto.getArticleType());
        }

        if (dto.getThumbnail() != null) {
            article.setThumbnail(dto.getThumbnail());
        }

        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }
    }

    /**
     * Map Article entity to response DTO
     */
    public ArticleResponseDTO mapEntityToResponse(Article article) {
        ArticleResponseDTO dto = MapperHelper.map(article, ArticleResponseDTO.class);
        if (article.getUser() != null) {
            dto.setUserId(article.getUser().getId());
            dto.setUserName(article.getUser().getUsername());
        }
        return dto;
    }
}

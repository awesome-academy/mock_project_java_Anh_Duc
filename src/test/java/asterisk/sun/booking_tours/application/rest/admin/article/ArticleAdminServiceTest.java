package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportResult;
import asterisk.sun.booking_tours.common.utils.excel.ExcelImportService;
import asterisk.sun.booking_tours.core.article.Article;
import asterisk.sun.booking_tours.core.article.ArticleRepository;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArticleAdminService using Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleAdminService Tests")
class ArticleAdminServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExcelImportService excelImportService;

    @InjectMocks
    private ArticleAdminService articleAdminService;

    @Captor
    private ArgumentCaptor<Article> articleCaptor;

    private Article testArticle;
    private User testUser;
    private ArticleImportDTO testArticleImportDTO;

    @BeforeEach
    void setUp() {
        // Setup test article
        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setSlug("test-article");
        testArticle.setContent("Test content");
        testArticle.setArticleType(ArticleType.NEWS);
        testArticle.setStatus(ArticleStatus.PUBLISHED);
        testArticle.setThumbnail("https://example.com/thumbnail.jpg");

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testArticle.setUser(testUser);

        // Setup test DTO
        testArticleImportDTO = new ArticleImportDTO();
        testArticleImportDTO.setTitle("New Article");
        testArticleImportDTO.setContent("New content");
        testArticleImportDTO.setArticleType(ArticleType.BLOG);
        testArticleImportDTO.setStatus(ArticleStatus.DRAFT);
    }

    @Nested
    @DisplayName("getArticles Tests")
    class GetArticlesTests {

        @Test
        @DisplayName("Should get paginated articles with default parameters")
        void shouldGetPaginatedArticlesWithDefaultParameters() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(testArticle);
            verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should get articles with keyword filter")
        void shouldGetArticlesWithKeywordFilter() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            request.setKeyword("test");
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result.getContent()).hasSize(1);
            verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should get articles with article type filter")
        void shouldGetArticlesWithArticleTypeFilter() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            request.setArticleType(ArticleType.NEWS);
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get articles with status filter")
        void shouldGetArticlesWithStatusFilter() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            request.setStatus(ArticleStatus.PUBLISHED);
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should return empty page when no articles found")
        void shouldReturnEmptyPageWhenNoArticlesFound() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            Page<Article> emptyPage = new PageImpl<>(Collections.emptyList());

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(emptyPage);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Should apply sorting correctly")
        void shouldApplySortingCorrectly() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            request.setSortBy("title");
            request.setSortDir("asc");
            Page<Article> page = new PageImpl<>(List.of(testArticle));

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result).isNotNull();
            verify(articleRepository).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle page number less than 1")
        void shouldHandlePageNumberLessThanOne() {
            // Given
            GetArticlesRequestDTO request = new GetArticlesRequestDTO();
            request.setPage(0);
            Page<Article> page = new PageImpl<>(List.of(testArticle));

            given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .willReturn(page);

            // When
            Page<Article> result = articleAdminService.getArticles(request);

            // Then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getArticleById Tests")
    class GetArticleByIdTests {

        @Test
        @DisplayName("Should get article by ID when exists")
        void shouldGetArticleByIdWhenExists() {
            // Given
            given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

            // When
            Optional<Article> result = articleAdminService.getArticleById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getTitle()).isEqualTo("Test Article");
            verify(articleRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when article not found")
        void shouldReturnEmptyWhenArticleNotFound() {
            // Given
            given(articleRepository.findById(999L)).willReturn(Optional.empty());

            // When
            Optional<Article> result = articleAdminService.getArticleById(999L);

            // Then
            assertThat(result).isEmpty();
            verify(articleRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("createArticle Tests")
    class CreateArticleTests {

        @Test
        @DisplayName("Should create article successfully")
        void shouldCreateArticleSuccessfully() {
            // Given
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            Article result = articleAdminService.createArticle(testArticleImportDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(articleRepository).save(any(Article.class));
        }

        @Test
        @DisplayName("Should generate unique slug when duplicate exists")
        void shouldGenerateUniqueSlugWhenDuplicateExists() {
            // Given
            given(articleRepository.existsBySlug("new-article")).willReturn(true);
            given(articleRepository.existsBySlug("new-article-1")).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            Article result = articleAdminService.createArticle(testArticleImportDTO);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            Article savedArticle = articleCaptor.getValue();
            assertThat(savedArticle.getSlug()).isEqualTo("new-article-1");
        }

        @Test
        @DisplayName("Should set default article type when null")
        void shouldSetDefaultArticleTypeWhenNull() {
            // Given
            testArticleImportDTO.setArticleType(null);
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            articleAdminService.createArticle(testArticleImportDTO);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            // Default type is set in mapDtoToEntity or createArticleFromDto
        }

        @Test
        @DisplayName("Should set default status when null")
        void shouldSetDefaultStatusWhenNull() {
            // Given
            testArticleImportDTO.setStatus(null);
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            articleAdminService.createArticle(testArticleImportDTO);

            // Then
            verify(articleRepository).save(any(Article.class));
        }
    }

    @Nested
    @DisplayName("updateArticle Tests")
    class UpdateArticleTests {

        @Test
        @DisplayName("Should update article successfully")
        void shouldUpdateArticleSuccessfully() {
            // Given
            given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willReturn(testArticle);

            ArticleImportDTO updateDTO = new ArticleImportDTO();
            updateDTO.setTitle("Updated Title");
            updateDTO.setContent("Updated Content");

            // When
            Article result = articleAdminService.updateArticle(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            verify(articleRepository).save(any(Article.class));
        }

        @Test
        @DisplayName("Should throw exception when article not found")
        void shouldThrowExceptionWhenArticleNotFound() {
            // Given
            given(articleRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> articleAdminService.updateArticle(999L, testArticleImportDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Article not found");
        }

        @Test
        @DisplayName("Should update only non-null fields")
        void shouldUpdateOnlyNonNullFields() {
            // Given
            given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willReturn(testArticle);

            ArticleImportDTO partialUpdate = new ArticleImportDTO();
            partialUpdate.setTitle("Only Title Updated");
            // content, articleType, status are null

            // When
            articleAdminService.updateArticle(1L, partialUpdate);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            Article savedArticle = articleCaptor.getValue();
            assertThat(savedArticle.getContent()).isEqualTo("Test content"); // Original content preserved
        }

        @Test
        @DisplayName("Should regenerate slug when title changes")
        void shouldRegenerateSlugWhenTitleChanges() {
            // Given
            given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));
            given(articleRepository.existsBySlug("completely-new-title")).willReturn(false);
            given(articleRepository.save(any(Article.class))).willReturn(testArticle);

            ArticleImportDTO updateDTO = new ArticleImportDTO();
            updateDTO.setTitle("Completely New Title");

            // When
            articleAdminService.updateArticle(1L, updateDTO);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            Article savedArticle = articleCaptor.getValue();
            assertThat(savedArticle.getSlug()).isEqualTo("completely-new-title");
        }
    }

    @Nested
    @DisplayName("deleteArticle Tests")
    class DeleteArticleTests {

        @Test
        @DisplayName("Should delete article successfully")
        void shouldDeleteArticleSuccessfully() {
            // Given
            given(articleRepository.existsById(1L)).willReturn(true);
            doNothing().when(articleRepository).deleteById(1L);

            // When
            articleAdminService.deleteArticle(1L);

            // Then
            verify(articleRepository).existsById(1L);
            verify(articleRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when article not found for deletion")
        void shouldThrowExceptionWhenArticleNotFoundForDeletion() {
            // Given
            given(articleRepository.existsById(999L)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> articleAdminService.deleteArticle(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Article not found");

            verify(articleRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("importFromExcel Tests")
    class ImportFromExcelTests {

        @Mock
        private UserDetails userDetails;

        @Test
        @DisplayName("Should import articles from Excel successfully")
        void shouldImportArticlesFromExcelSuccessfully() {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "articles.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "test content".getBytes());

            given(userDetails.getUsername()).willReturn("test@example.com");
            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));

            List<ArticleImportDTO> successItems = List.of(testArticleImportDTO);
            ExcelImportResult<ArticleImportDTO> importResult = new ExcelImportResult<>(
                    successItems, new ArrayList<>(), 1);

            given(excelImportService.importFromExcel(any(MultipartFile.class), eq(ArticleImportDTO.class)))
                    .willReturn(importResult);
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            ArticleImportResponseDTO result = articleAdminService.importFromExcel(file, userDetails);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTotalRows()).isEqualTo(1);
            assertThat(result.getSuccessCount()).isEqualTo(1);
            assertThat(result.getErrorCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should handle import with errors")
        void shouldHandleImportWithErrors() {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "articles.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "test content".getBytes());

            given(userDetails.getUsername()).willReturn("test@example.com");
            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));

            List<ExcelImportResult.ExcelImportError> errors = List.of(
                    new ExcelImportResult.ExcelImportError(2, "title", "Title is required", null));
            ExcelImportResult<ArticleImportDTO> importResult = new ExcelImportResult<>(
                    List.of(testArticleImportDTO), errors, 2);

            given(excelImportService.importFromExcel(any(MultipartFile.class), eq(ArticleImportDTO.class)))
                    .willReturn(importResult);
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            ArticleImportResponseDTO result = articleAdminService.importFromExcel(file, userDetails);

            // Then
            assertThat(result.getTotalRows()).isEqualTo(2);
            assertThat(result.getSuccessCount()).isEqualTo(1);
            assertThat(result.getErrorCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "articles.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "test content".getBytes());

            given(userDetails.getUsername()).willReturn("nonexistent@example.com");
            given(userRepository.findByEmail("nonexistent@example.com")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> articleAdminService.importFromExcel(file, userDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Current user not found");
        }

        @Test
        @DisplayName("Should set current user as article author")
        void shouldSetCurrentUserAsArticleAuthor() {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "articles.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "test content".getBytes());

            given(userDetails.getUsername()).willReturn("test@example.com");
            given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));

            List<ArticleImportDTO> successItems = List.of(testArticleImportDTO);
            ExcelImportResult<ArticleImportDTO> importResult = new ExcelImportResult<>(
                    successItems, new ArrayList<>(), 1);

            given(excelImportService.importFromExcel(any(MultipartFile.class), eq(ArticleImportDTO.class)))
                    .willReturn(importResult);
            given(articleRepository.existsBySlug(anyString())).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            articleAdminService.importFromExcel(file, userDetails);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            Article savedArticle = articleCaptor.getValue();
            assertThat(savedArticle.getUser()).isEqualTo(testUser);
        }
    }

    @Nested
    @DisplayName("mapEntityToResponse Tests")
    class MapEntityToResponseTests {

        @Test
        @DisplayName("Should map entity to response DTO")
        void shouldMapEntityToResponseDTO() {
            // When
            ArticleResponseDTO result = articleAdminService.mapEntityToResponse(testArticle);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(testUser.getId());
            assertThat(result.getUserName()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("Should handle article without user")
        void shouldHandleArticleWithoutUser() {
            // Given
            testArticle.setUser(null);

            // When
            ArticleResponseDTO result = articleAdminService.mapEntityToResponse(testArticle);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isNull();
            assertThat(result.getUserName()).isNull();
        }
    }

    @Nested
    @DisplayName("Slug Generation Tests")
    class SlugGenerationTests {

        @Test
        @DisplayName("Should generate unique slug with incremental suffix")
        void shouldGenerateUniqueSlugWithIncrementalSuffix() {
            // Given
            given(articleRepository.existsBySlug("new-article")).willReturn(true);
            given(articleRepository.existsBySlug("new-article-1")).willReturn(true);
            given(articleRepository.existsBySlug("new-article-2")).willReturn(false);
            given(articleRepository.save(any(Article.class))).willAnswer(invocation -> {
                Article article = invocation.getArgument(0);
                article.setId(1L);
                return article;
            });

            // When
            articleAdminService.createArticle(testArticleImportDTO);

            // Then
            verify(articleRepository).save(articleCaptor.capture());
            Article savedArticle = articleCaptor.getValue();
            assertThat(savedArticle.getSlug()).isEqualTo("new-article-2");
        }
    }
}

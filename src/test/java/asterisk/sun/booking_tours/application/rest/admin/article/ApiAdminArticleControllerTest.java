package asterisk.sun.booking_tours.application.rest.admin.article;

import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.core.article.Article;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;
import asterisk.sun.booking_tours.core.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ApiAdminArticleController using MockMvc
 */
@WebMvcTest(ApiAdminArticleController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DisplayName("ApiAdminArticleController Tests")
class ApiAdminArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleAdminService articleAdminService;

    private Article testArticle;
    private ArticleResponseDTO testArticleResponseDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Setup test article
        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setSlug("test-article");
        testArticle.setContent("Test content");
        testArticle.setArticleType(ArticleType.NEWS);
        testArticle.setStatus(ArticleStatus.PUBLISHED);
        testArticle.setThumbnail("https://example.com/thumbnail.jpg");
        testArticle.setUser(testUser);
        testArticle.setCreatedAt(LocalDateTime.now());
        testArticle.setUpdatedAt(LocalDateTime.now());

        // Setup test response DTO
        testArticleResponseDTO = new ArticleResponseDTO();
        testArticleResponseDTO.setId(1L);
        testArticleResponseDTO.setTitle("Test Article");
        testArticleResponseDTO.setSlug("test-article");
        testArticleResponseDTO.setContent("Test content");
        testArticleResponseDTO.setArticleType(ArticleType.NEWS);
        testArticleResponseDTO.setStatus(ArticleStatus.PUBLISHED);
        testArticleResponseDTO.setThumbnail("https://example.com/thumbnail.jpg");
        testArticleResponseDTO.setUserId(1L);
        testArticleResponseDTO.setUserName("testuser");
        testArticleResponseDTO.setCreatedAt(LocalDateTime.now());
        testArticleResponseDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/v1/admin/articles - Get List Articles")
    class GetListArticlesTests {

        @Test
        @DisplayName("Should return list of articles with pagination")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnListOfArticlesWithPagination() throws Exception {
            // Given
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(page);
            given(articleAdminService.mapEntityToResponse(any(Article.class)))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode", is(200)))
                    .andExpect(jsonPath("$.message", is("Get List Articles Successfully")))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id", is(1)))
                    .andExpect(jsonPath("$.data[0].title", is("Test Article")));

            verify(articleAdminService).getArticles(any(GetArticlesRequestDTO.class));
        }

        @Test
        @DisplayName("Should return empty list when no articles found")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyListWhenNoArticlesFound() throws Exception {
            // Given
            Page<Article> emptyPage = new PageImpl<>(List.of());

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("Should filter articles by keyword")
        @WithMockUser(roles = "ADMIN")
        void shouldFilterArticlesByKeyword() throws Exception {
            // Given
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(page);
            given(articleAdminService.mapEntityToResponse(any(Article.class)))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .param("keyword", "test")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        @Test
        @DisplayName("Should filter articles by article type")
        @WithMockUser(roles = "ADMIN")
        void shouldFilterArticlesByArticleType() throws Exception {
            // Given
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(page);
            given(articleAdminService.mapEntityToResponse(any(Article.class)))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .param("articleType", "NEWS")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        @Test
        @DisplayName("Should filter articles by status")
        @WithMockUser(roles = "ADMIN")
        void shouldFilterArticlesByStatus() throws Exception {
            // Given
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(page);
            given(articleAdminService.mapEntityToResponse(any(Article.class)))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .param("status", "PUBLISHED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        @Test
        @DisplayName("Should return paginated results with page and size params")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnPaginatedResults() throws Exception {
            // Given
            List<Article> articles = List.of(testArticle);
            Page<Article> page = new PageImpl<>(articles);

            given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class)))
                    .willReturn(page);
            given(articleAdminService.mapEntityToResponse(any(Article.class)))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currentPage", is(1)))
                    .andExpect(jsonPath("$.pageSize", is(10)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/admin/articles/{id} - Get Article By ID")
    class GetArticleByIdTests {

        @Test
        @DisplayName("Should return article when found")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnArticleWhenFound() throws Exception {
            // Given
            given(articleAdminService.getArticleById(1L))
                    .willReturn(Optional.of(testArticle));
            given(articleAdminService.mapEntityToResponse(testArticle))
                    .willReturn(testArticleResponseDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode", is(200)))
                    .andExpect(jsonPath("$.message", is("Get Article Successfully")))
                    .andExpect(jsonPath("$.data.id", is(1)))
                    .andExpect(jsonPath("$.data.title", is("Test Article")))
                    .andExpect(jsonPath("$.data.slug", is("test-article")));
        }

        @Test
        @DisplayName("Should throw exception when article not found")
        @WithMockUser(roles = "ADMIN")
        void shouldThrowExceptionWhenArticleNotFound() throws Exception {
            // Given
            given(articleAdminService.getArticleById(999L))
                    .willReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/admin/articles/{id} - Delete Article")
    class DeleteArticleTests {

        @Test
        @DisplayName("Should delete article successfully")
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteArticleSuccessfully() throws Exception {
            // Given
            doNothing().when(articleAdminService).deleteArticle(1L);

            // When & Then
            mockMvc.perform(delete("/api/v1/admin/articles/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode", is(200)))
                    .andExpect(jsonPath("$.message", is("Article deleted successfully")));

            verify(articleAdminService).deleteArticle(1L);
        }

        @Test
        @DisplayName("Should throw exception when article not found for deletion")
        @WithMockUser(roles = "ADMIN")
        void shouldThrowExceptionWhenArticleNotFoundForDeletion() throws Exception {
            // Given
            doThrow(new RuntimeException("Article not found with id: 999"))
                    .when(articleAdminService).deleteArticle(999L);

            // When & Then
            mockMvc.perform(delete("/api/v1/admin/articles/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/admin/articles/import - Import from Excel")
    class ImportFromExcelTests {

        @Test
        @DisplayName("Should return import template information")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnImportTemplateInformation() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles/import/template")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode", is(200)))
                    .andExpect(jsonPath("$.message", is("Import template information")))
                    .andExpect(jsonPath("$.data.columns", hasSize(5)));
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/admin/articles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }
}

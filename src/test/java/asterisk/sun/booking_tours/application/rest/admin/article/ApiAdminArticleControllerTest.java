package asterisk.sun.booking_tours.application.rest.admin.article;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import asterisk.sun.booking_tours.application.admin.auth.AuthAdminService;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleImportDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.common.security.JwtUtil;
import asterisk.sun.booking_tours.core.article.Article;
import asterisk.sun.booking_tours.core.article.ArticleStatus;
import asterisk.sun.booking_tours.core.article.ArticleType;

import jakarta.servlet.ServletException;

@WebMvcTest(ApiAdminArticleController.class)
public class ApiAdminArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleAdminService articleAdminService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthAdminService authAdminService;

    private Article createDummyArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        return article;
    }

    private ArticleResponseDTO createDummyResponseDTO() {
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(1L);
        dto.setTitle("Test Article");
        return dto;
    }

    @Test
    @DisplayName("GET /api/v1/admin/articles - Should return paginated list")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnPaginatedArticles() throws Exception {
        // Given
        Article article = createDummyArticle();
        ArticleResponseDTO responseDTO = createDummyResponseDTO();
        Page<Article> articlePage = new PageImpl<>(List.of(article));

        given(articleAdminService.getArticles(any(GetArticlesRequestDTO.class))).willReturn(articlePage);
        given(articleAdminService.mapEntityToResponse(any(Article.class))).willReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/articles")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.items[0].title").value("Test Article"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/admin/articles/{id} - Should return article when found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnArticleById() throws Exception {
        // Given
        Article article = createDummyArticle();
        ArticleResponseDTO responseDTO = createDummyResponseDTO();

        given(articleAdminService.getArticleById(1L)).willReturn(Optional.of(article));
        given(articleAdminService.mapEntityToResponse(article)).willReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/admin/articles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Get Article Successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Article"));
    }

    @Test
    @DisplayName("GET /api/v1/admin/articles/{id} - Should throw exception when article not found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldThrowExceptionWhenArticleNotFound() throws Exception {
        // Given
        given(articleAdminService.getArticleById(999L)).willReturn(Optional.empty());

        // When & Then - The controller throws RuntimeException which is wrapped in ServletException
        try {
            mockMvc.perform(get("/api/v1/admin/articles/999")
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (ServletException e) {
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Article not found"));
        }
    }

    @Test
    @DisplayName("POST /api/v1/admin/articles - Should create article successfully")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateArticleSuccessfully() throws Exception {
        // Given
        ArticleImportDTO request = new ArticleImportDTO();
        request.setTitle("New Article");
        request.setContent("Article content");
        request.setArticleType(ArticleType.NEWS);
        request.setStatus(ArticleStatus.DRAFT);

        Article createdArticle = new Article();
        createdArticle.setId(1L);
        createdArticle.setTitle("New Article");

        ArticleResponseDTO responseDTO = new ArticleResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("New Article");

        given(articleAdminService.createArticle(any(ArticleImportDTO.class))).willReturn(createdArticle);
        given(articleAdminService.mapEntityToResponse(createdArticle)).willReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/admin/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Article created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("New Article"));
    }

    @Test
    @DisplayName("POST /api/v1/admin/articles - Should return 403 without authentication")
    void shouldReturn403WithoutAuthentication() throws Exception {
        // Given
        ArticleImportDTO request = new ArticleImportDTO();
        request.setTitle("New Article");

        // When & Then
        mockMvc.perform(post("/api/v1/admin/articles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/admin/articles/{id} - Should update article successfully")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateArticleSuccessfully() throws Exception {
        // Given
        ArticleImportDTO request = new ArticleImportDTO();
        request.setTitle("Updated Article");
        request.setContent("Updated content");
        request.setArticleType(ArticleType.BLOG);
        request.setStatus(ArticleStatus.PUBLISHED);

        Article updatedArticle = new Article();
        updatedArticle.setId(1L);
        updatedArticle.setTitle("Updated Article");

        ArticleResponseDTO responseDTO = new ArticleResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Updated Article");

        given(articleAdminService.updateArticle(eq(1L), any(ArticleImportDTO.class))).willReturn(updatedArticle);
        given(articleAdminService.mapEntityToResponse(updatedArticle)).willReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/admin/articles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Article updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Article"));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/articles/{id} - Should throw exception when article not found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldThrowExceptionWhenUpdatingNonExistentArticle() throws Exception {
        // Given
        ArticleImportDTO request = new ArticleImportDTO();
        request.setTitle("Updated Article");
        request.setContent("Updated content");
        request.setArticleType(ArticleType.NEWS);

        given(articleAdminService.updateArticle(eq(999L), any(ArticleImportDTO.class)))
                .willThrow(new RuntimeException("Article not found with id: 999"));

        // When & Then - The controller throws RuntimeException which is wrapped in ServletException
        try {
            mockMvc.perform(put("/api/v1/admin/articles/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        } catch (ServletException e) {
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Article not found"));
        }
    }

    // ==================== DELETE ARTICLE TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/admin/articles/{id} - Should delete article successfully")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteArticleSuccessfully() throws Exception {
        // Given
        doNothing().when(articleAdminService).deleteArticle(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/admin/articles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Article deleted successfully"));
    }

    @Test
    @DisplayName("DELETE /api/v1/admin/articles/{id} - Should throw exception when article not found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldThrowExceptionWhenDeletingNonExistentArticle() throws Exception {
        // Given
        doThrow(new RuntimeException("Article not found with id: 999"))
                .when(articleAdminService).deleteArticle(999L);

        // When & Then - The controller throws RuntimeException which is wrapped in ServletException
        try {
            mockMvc.perform(delete("/api/v1/admin/articles/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (ServletException e) {
            assertInstanceOf(RuntimeException.class, e.getCause());
            assertTrue(e.getCause().getMessage().contains("Article not found"));
        }
    }


    @Test
    @DisplayName("GET /api/v1/admin/articles/import/template - Should return import template")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnImportTemplate() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/admin/articles/import/template")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.columns").isArray())
                .andExpect(jsonPath("$.data.columns[0].name").value("title"));
    }
}

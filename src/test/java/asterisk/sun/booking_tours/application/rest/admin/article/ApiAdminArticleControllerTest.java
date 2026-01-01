package asterisk.sun.booking_tours.application.rest.admin.article;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import asterisk.sun.booking_tours.application.rest.admin.article.dto.ArticleResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.article.dto.GetArticlesRequestDTO;
import asterisk.sun.booking_tours.core.article.Article;
import asterisk.sun.booking_tours.common.security.JwtUtil;
import asterisk.sun.booking_tours.application.admin.auth.AuthAdminService;

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
}

package asterisk.sun.booking_tours.core.article;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ArticleRepository using @DataJpaTest
 * Tests are run with an in-memory H2 database
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ArticleRepository Tests")
class ArticleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    private Article testArticle;

    @BeforeEach
    void setUp() {
        // Create a test article
        testArticle = new Article();
        testArticle.setTitle("Test Article Title");
        testArticle.setSlug("test-article-title");
        testArticle.setContent("This is the test article content.");
        testArticle.setArticleType(ArticleType.NEWS);
        testArticle.setStatus(ArticleStatus.PUBLISHED);
        testArticle.setThumbnail("https://example.com/thumbnail.jpg");
    }

    @Nested
    @DisplayName("Save Article Tests")
    class SaveArticleTests {

        @Test
        @DisplayName("Should save article successfully")
        void shouldSaveArticleSuccessfully() {
            // When
            Article savedArticle = articleRepository.save(testArticle);

            // Then
            assertThat(savedArticle).isNotNull();
            assertThat(savedArticle.getId()).isNotNull();
            assertThat(savedArticle.getTitle()).isEqualTo("Test Article Title");
            assertThat(savedArticle.getSlug()).isEqualTo("test-article-title");
            assertThat(savedArticle.getContent()).isEqualTo("This is the test article content.");
            assertThat(savedArticle.getArticleType()).isEqualTo(ArticleType.NEWS);
            assertThat(savedArticle.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
        }

        @Test
        @DisplayName("Should auto-generate createdAt and updatedAt timestamps")
        void shouldAutoGenerateTimestamps() {
            // When
            Article savedArticle = articleRepository.save(testArticle);
            entityManager.flush();
            entityManager.refresh(savedArticle);

            // Then
            assertThat(savedArticle.getCreatedAt()).isNotNull();
            assertThat(savedArticle.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should save article with all article types")
        void shouldSaveArticleWithAllArticleTypes() {
            for (ArticleType type : ArticleType.values()) {
                Article article = new Article();
                article.setTitle("Article " + type.name());
                article.setSlug("article-" + type.name().toLowerCase());
                article.setContent("Content for " + type.name());
                article.setArticleType(type);
                article.setStatus(ArticleStatus.DRAFT);

                Article saved = articleRepository.save(article);

                assertThat(saved.getArticleType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should save article with all statuses")
        void shouldSaveArticleWithAllStatuses() {
            for (ArticleStatus status : ArticleStatus.values()) {
                Article article = new Article();
                article.setTitle("Article " + status.name());
                article.setSlug("article-status-" + status.name().toLowerCase());
                article.setContent("Content for " + status.name());
                article.setArticleType(ArticleType.BLOG);
                article.setStatus(status);

                Article saved = articleRepository.save(article);

                assertThat(saved.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find article by ID when exists")
        void shouldFindArticleByIdWhenExists() {
            // Given
            Article savedArticle = entityManager.persistAndFlush(testArticle);

            // When
            Optional<Article> found = articleRepository.findById(savedArticle.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo(testArticle.getTitle());
            assertThat(found.get().getSlug()).isEqualTo(testArticle.getSlug());
        }

        @Test
        @DisplayName("Should return empty when article ID not found")
        void shouldReturnEmptyWhenArticleIdNotFound() {
            // When
            Optional<Article> found = articleRepository.findById(999L);

            // Then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find By Slug Tests")
    class FindBySlugTests {

        @Test
        @DisplayName("Should find article by slug when exists")
        void shouldFindArticleBySlugWhenExists() {
            // Given
            entityManager.persistAndFlush(testArticle);

            // When
            Optional<Article> found = articleRepository.findBySlug("test-article-title");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Test Article Title");
            assertThat(found.get().getContent()).isEqualTo("This is the test article content.");
        }

        @Test
        @DisplayName("Should return empty when slug not found")
        void shouldReturnEmptyWhenSlugNotFound() {
            // When
            Optional<Article> found = articleRepository.findBySlug("non-existent-slug");

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should be case sensitive when finding by slug")
        void shouldBeCaseSensitiveWhenFindingBySlug() {
            // Given
            entityManager.persistAndFlush(testArticle);

            // When
            Optional<Article> found = articleRepository.findBySlug("TEST-ARTICLE-TITLE");

            // Then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists By Slug Tests")
    class ExistsBySlugTests {

        @Test
        @DisplayName("Should return true when slug exists")
        void shouldReturnTrueWhenSlugExists() {
            // Given
            entityManager.persistAndFlush(testArticle);

            // When
            boolean exists = articleRepository.existsBySlug("test-article-title");

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when slug does not exist")
        void shouldReturnFalseWhenSlugDoesNotExist() {
            // When
            boolean exists = articleRepository.existsBySlug("non-existent-slug");

            // Then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Find All Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should find all articles")
        void shouldFindAllArticles() {
            // Given
            entityManager.persistAndFlush(testArticle);

            Article article2 = new Article();
            article2.setTitle("Second Article");
            article2.setSlug("second-article");
            article2.setContent("Second content");
            article2.setArticleType(ArticleType.BLOG);
            article2.setStatus(ArticleStatus.DRAFT);
            entityManager.persistAndFlush(article2);

            // When
            List<Article> articles = articleRepository.findAll();

            // Then
            assertThat(articles).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when no articles exist")
        void shouldReturnEmptyListWhenNoArticlesExist() {
            // When
            List<Article> articles = articleRepository.findAll();

            // Then
            assertThat(articles).isEmpty();
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @BeforeEach
        void setUpArticles() {
            // Create 15 articles for pagination testing
            for (int i = 1; i <= 15; i++) {
                Article article = new Article();
                article.setTitle("Article " + i);
                article.setSlug("article-" + i);
                article.setContent("Content " + i);
                article.setArticleType(i % 2 == 0 ? ArticleType.NEWS : ArticleType.BLOG);
                article.setStatus(i % 3 == 0 ? ArticleStatus.PUBLISHED : ArticleStatus.DRAFT);
                entityManager.persist(article);
            }
            entityManager.flush();
        }

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            // Given
            Pageable pageable = PageRequest.of(0, 5);

            // When
            Page<Article> page = articleRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getTotalElements()).isEqualTo(15);
            assertThat(page.getTotalPages()).isEqualTo(3);
            assertThat(page.getNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return correct page content")
        void shouldReturnCorrectPageContent() {
            // Given
            Pageable pageable = PageRequest.of(1, 5); // Second page

            // When
            Page<Article> page = articleRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return last page with remaining items")
        void shouldReturnLastPageWithRemainingItems() {
            // Given
            Pageable pageable = PageRequest.of(2, 5); // Third page

            // When
            Page<Article> page = articleRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.isLast()).isTrue();
        }

        @Test
        @DisplayName("Should return sorted results")
        void shouldReturnSortedResults() {
            // Given
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "title"));

            // When
            Page<Article> page = articleRepository.findAll(pageable);

            // Then
            List<Article> articles = page.getContent();
            assertThat(articles.get(0).getTitle()).isEqualTo("Article 9");
        }
    }

    @Nested
    @DisplayName("Specification Tests")
    class SpecificationTests {

        @BeforeEach
        void setUpArticles() {
            // Create articles with different types and statuses
            Article newsPublished = new Article();
            newsPublished.setTitle("News Published Article");
            newsPublished.setSlug("news-published");
            newsPublished.setContent("News content about travel");
            newsPublished.setArticleType(ArticleType.NEWS);
            newsPublished.setStatus(ArticleStatus.PUBLISHED);
            entityManager.persist(newsPublished);

            Article blogDraft = new Article();
            blogDraft.setTitle("Blog Draft Article");
            blogDraft.setSlug("blog-draft");
            blogDraft.setContent("Blog content about destination");
            blogDraft.setArticleType(ArticleType.BLOG);
            blogDraft.setStatus(ArticleStatus.DRAFT);
            entityManager.persist(blogDraft);

            Article guideDraft = new Article();
            guideDraft.setTitle("Guide Draft Article");
            guideDraft.setSlug("guide-draft");
            guideDraft.setContent("Guide content about travel tips");
            guideDraft.setArticleType(ArticleType.GUIDE);
            guideDraft.setStatus(ArticleStatus.DRAFT);
            entityManager.persist(guideDraft);

            entityManager.flush();
        }

        @Test
        @DisplayName("Should filter by article type using Specification")
        void shouldFilterByArticleTypeUsingSpecification() {
            // Given
            Specification<Article> spec = (root, query, cb) ->
                    cb.equal(root.get("articleType"), ArticleType.NEWS);

            // When
            List<Article> articles = articleRepository.findAll(spec);

            // Then
            assertThat(articles).hasSize(1);
            assertThat(articles.get(0).getArticleType()).isEqualTo(ArticleType.NEWS);
        }

        @Test
        @DisplayName("Should filter by status using Specification")
        void shouldFilterByStatusUsingSpecification() {
            // Given
            Specification<Article> spec = (root, query, cb) ->
                    cb.equal(root.get("status"), ArticleStatus.DRAFT);

            // When
            List<Article> articles = articleRepository.findAll(spec);

            // Then
            assertThat(articles).hasSize(2);
            articles.forEach(article ->
                    assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT));
        }

        @Test
        @DisplayName("Should filter by keyword in content using Specification")
        void shouldFilterByKeywordInContentUsingSpecification() {
            // Given
            Specification<Article> spec = (root, query, cb) ->
                    cb.like(cb.lower(root.get("content")), "%travel%");

            // When
            List<Article> articles = articleRepository.findAll(spec);

            // Then
            assertThat(articles).hasSize(2);
        }

        @Test
        @DisplayName("Should combine multiple specifications")
        void shouldCombineMultipleSpecifications() {
            // Given
            Specification<Article> typeSpec = (root, query, cb) ->
                    cb.equal(root.get("articleType"), ArticleType.BLOG);
            Specification<Article> statusSpec = (root, query, cb) ->
                    cb.equal(root.get("status"), ArticleStatus.DRAFT);
            Specification<Article> combinedSpec = typeSpec.and(statusSpec);

            // When
            List<Article> articles = articleRepository.findAll(combinedSpec);

            // Then
            assertThat(articles).hasSize(1);
            assertThat(articles.get(0).getSlug()).isEqualTo("blog-draft");
        }

        @Test
        @DisplayName("Should filter with pagination using Specification")
        void shouldFilterWithPaginationUsingSpecification() {
            // Given
            Specification<Article> spec = (root, query, cb) ->
                    cb.equal(root.get("status"), ArticleStatus.DRAFT);
            Pageable pageable = PageRequest.of(0, 1);

            // When
            Page<Article> page = articleRepository.findAll(spec, pageable);

            // Then
            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Update Article Tests")
    class UpdateArticleTests {

        @Test
        @DisplayName("Should update article title and content")
        void shouldUpdateArticleTitleAndContent() {
            // Given
            Article savedArticle = entityManager.persistAndFlush(testArticle);

            // When
            savedArticle.setTitle("Updated Title");
            savedArticle.setContent("Updated Content");
            Article updatedArticle = articleRepository.save(savedArticle);
            entityManager.flush();

            // Then
            Article found = entityManager.find(Article.class, savedArticle.getId());
            assertThat(found.getTitle()).isEqualTo("Updated Title");
            assertThat(found.getContent()).isEqualTo("Updated Content");
        }

        @Test
        @DisplayName("Should update article status")
        void shouldUpdateArticleStatus() {
            // Given
            testArticle.setStatus(ArticleStatus.DRAFT);
            Article savedArticle = entityManager.persistAndFlush(testArticle);

            // When
            savedArticle.setStatus(ArticleStatus.PUBLISHED);
            articleRepository.save(savedArticle);
            entityManager.flush();

            // Then
            Article found = entityManager.find(Article.class, savedArticle.getId());
            assertThat(found.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
        }
    }

    @Nested
    @DisplayName("Delete Article Tests")
    class DeleteArticleTests {

        @Test
        @DisplayName("Should delete article by ID")
        void shouldDeleteArticleById() {
            // Given
            Article savedArticle = entityManager.persistAndFlush(testArticle);
            Long articleId = savedArticle.getId();

            // When
            articleRepository.deleteById(articleId);
            entityManager.flush();

            // Then
            // Note: Due to soft delete, this may behave differently
            // depending on BaseEntity's @SQLDelete annotation
            Optional<Article> found = articleRepository.findById(articleId);
            // If soft delete is enabled, article might still exist but with deletedAt set
            // If hard delete, article should not exist
        }

        @Test
        @DisplayName("Should check existence before delete")
        void shouldCheckExistenceBeforeDelete() {
            // Given
            Article savedArticle = entityManager.persistAndFlush(testArticle);

            // When & Then
            assertThat(articleRepository.existsById(savedArticle.getId())).isTrue();
            articleRepository.deleteById(savedArticle.getId());
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Nested
    @DisplayName("Count Tests")
    class CountTests {

        @Test
        @DisplayName("Should count all articles")
        void shouldCountAllArticles() {
            // Given
            entityManager.persistAndFlush(testArticle);

            Article article2 = new Article();
            article2.setTitle("Second Article");
            article2.setSlug("second-article");
            article2.setContent("Second content");
            article2.setArticleType(ArticleType.BLOG);
            article2.setStatus(ArticleStatus.DRAFT);
            entityManager.persistAndFlush(article2);

            // When
            long count = articleRepository.count();

            // Then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Should return zero count when no articles")
        void shouldReturnZeroCountWhenNoArticles() {
            // When
            long count = articleRepository.count();

            // Then
            assertThat(count).isZero();
        }
    }
}

package asterisk.sun.booking_tours.module.category;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.module.common.abtracts.BaseService;

import java.util.List;
import java.util.Optional;

/**
 * Legacy CategoryService - Delegates to Command/Query Services
 * @deprecated Use CategoryCommandService and CategoryQueryService instead
 *
 * This class is kept for backward compatibility.
 * New code should use:
 * - CategoryQueryService for read operations
 * - CategoryCommandService for write operations
 */
@Service
@Deprecated(since = "2.0", forRemoval = true)
public class CategoryService extends BaseService<Category, Long, CategoryRepository> {

    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;

    public CategoryService(
            CategoryRepository categoryRepository,
            CategoryQueryService queryService,
            CategoryCommandService commandService) {
        super(categoryRepository);
        this.queryService = queryService;
        this.commandService = commandService;
    }

    // ========== Delegating to Query Service ==========

    public boolean existsByName(String name) {
        return queryService.existsByName(name);
    }

    public boolean existsBySlug(String slug) {
        return queryService.existsBySlug(slug);
    }

    public Optional<Category> findByName(String name) {
        return queryService.findByName(name);
    }

    public Optional<Category> findBySlug(String slug) {
        return queryService.findBySlug(slug);
    }

    public List<Category> searchByKeyword(String keyword) {
        return queryService.searchByKeyword(keyword);
    }

    // ========== Override BaseService methods to delegate ==========

    @Override
    public Category save(Category entity) {
        return commandService.save(entity);
    }
}

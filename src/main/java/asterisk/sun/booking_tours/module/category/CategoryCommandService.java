package asterisk.sun.booking_tours.module.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Category Command Service - Handles WRITE operations
 * Following CQRS pattern (Command Query Responsibility Segregation)
 */
@Service
@Transactional
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryService queryService;

    public CategoryCommandService(
            CategoryRepository categoryRepository,
            CategoryQueryService queryService) {
        this.categoryRepository = categoryRepository;
        this.queryService = queryService;
    }

    /**
     * Create new category with validation
     */
    public Category createCategory(String name, String description, String slug) {
        // Business rule: Category name must be unique
        if (queryService.existsByName(name)) {
            throw new IllegalStateException("Category with name '" + name + "' already exists");
        }

        // Business rule: Category slug must be unique
        if (queryService.existsBySlug(slug)) {
            throw new IllegalStateException("Category with slug '" + slug + "' already exists");
        }

        // Use factory method with built-in validation
        Category category = Category.create(name, description, slug);

        return categoryRepository.save(category);
    }

    /**
     * Update existing category
     */
    public Category updateCategory(Long id, String name, String description, String slug) {
        Category category = queryService.findByIdOrThrow(id);

        // Business rule: Name must be unique (except current category)
        queryService.findByName(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalStateException("Category with name '" + name + "' already exists");
            }
        });

        // Business rule: Slug must be unique (except current category)
        queryService.findBySlug(slug).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalStateException("Category with slug '" + slug + "' already exists");
            }
        });

        // Use domain method with validation
        category.updateInfo(name, description, slug);

        return categoryRepository.save(category);
    }

    /**
     * Delete category (soft delete via BaseEntity)
     */
    public void deleteCategory(Long id) {
        Category category = queryService.findByIdOrThrow(id);

        // Future: Add business rule checks here
        // e.g., check if category has associated tours

        categoryRepository.delete(category);
    }

    /**
     * Save category directly (for backward compatibility)
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}

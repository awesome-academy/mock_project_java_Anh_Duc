package asterisk.sun.booking_tours.module.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Category Query Service - Handles READ operations
 * Following CQRS pattern (Command Query Responsibility Segregation)
 * All methods are read-only
 */
@Service
@Transactional(readOnly = true)
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ========== Existence Checks ==========

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    // ========== Find Single Entity ==========

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Find by ID or throw exception
     * Utility method to reduce boilerplate code
     */
    public Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    // ========== Find Multiple Entities ==========

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    /**
     * Search categories by keyword in name
     */
    public List<Category> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return categoryRepository.searchByKeyword(keyword.trim());
    }

    // ========== Statistics ==========

    public long count() {
        return categoryRepository.count();
    }
}

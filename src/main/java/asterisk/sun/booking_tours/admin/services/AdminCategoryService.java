package asterisk.sun.booking_tours.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.admin.dto.category.FormEditCategoryDTO;
import asterisk.sun.booking_tours.admin.dto.category.ListCategoryDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.module.category.Category;
import asterisk.sun.booking_tours.module.category.CategoryCommandService;
import asterisk.sun.booking_tours.module.category.CategoryQueryService;

/**
 * Admin Category Application Service
 * Orchestrates use cases for admin panel
 * Handles DTO transformation and delegates to domain services
 */
@Service
@Transactional
public class AdminCategoryService {

    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;

    public AdminCategoryService(
            CategoryQueryService queryService,
            CategoryCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    // ========== Query Use Cases ==========

    /**
     * Get all categories or search by keyword
     */
    @Transactional(readOnly = true)
    public List<ListCategoryDTO> queryCategoriesByKeyword(String keyword) {
        List<Category> categories = queryService.searchByKeyword(keyword);
        return MapperHelper.mapList(categories, ListCategoryDTO.class);
    }

    /**
     * Get category for editing
     */
    @Transactional(readOnly = true)
    public FormEditCategoryDTO getCategoryById(Long id) {
        Category category = queryService.findByIdOrThrow(id);
        return MapperHelper.map(category, FormEditCategoryDTO.class);
    }

    // ========== Command Use Cases ==========

    /**
     * Create new category
     * Delegates validation to domain service
     */
    public void createCategory(FormCreateCategoryDTO dto) {
        // Domain service handles uniqueness check and validation
        commandService.createCategory(
            dto.getName(),
            dto.getDescription(),
            dto.getSlug()
        );
    }

    /**
     * Update existing category
     * Delegates validation to domain service
     */
    public void updateCategory(FormEditCategoryDTO dto) {
        // Domain service handles uniqueness check and validation
        commandService.updateCategory(
            dto.getId(),
            dto.getName(),
            dto.getDescription(),
            dto.getSlug()
        );
    }

    /**
     * Delete category
     * Future: Check if category is in use before deletion
     */
    public void deleteCategory(Long id) {
        commandService.deleteCategory(id);
    }
}

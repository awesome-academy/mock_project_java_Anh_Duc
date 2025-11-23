package asterisk.sun.booking_tours.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.admin.dto.category.ListCategoryDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.module.category.CategoryService;

@Service
public class AdminCategoryService {
    private final CategoryService categoryService;

    public AdminCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<ListCategoryDTO> getAllCategoriesForListing() {
        return MapperHelper.mapList(categoryService.findAll(), ListCategoryDTO.class);
    }
}

package asterisk.sun.booking_tours.application.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.ListCategoryDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.domain.category.Category;
import asterisk.sun.booking_tours.domain.category.CategoryRepository;

@Service
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;

    public CategoryAdminService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ListCategoryDTO> queryCategoriesByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return MapperHelper.mapList(categoryRepository.searchByKeyword(keyword), ListCategoryDTO.class);
        }

        return MapperHelper.mapList(categoryRepository.findAll(), ListCategoryDTO.class);
    }

    public void createCategory(FormCreateCategoryDTO formCreateCategoryDTO) {
        var category = MapperHelper.map(formCreateCategoryDTO, Category.class);
        categoryRepository.save(category);
    }
}

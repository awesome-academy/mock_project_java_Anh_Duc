package asterisk.sun.booking_tours.module.category;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.module.common.abtracts.BaseService;

@Service
public class CategoryService extends BaseService<Category, Long, CategoryRepository> {
    public CategoryService(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }
}

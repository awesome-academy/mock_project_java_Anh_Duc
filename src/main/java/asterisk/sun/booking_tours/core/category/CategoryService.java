package asterisk.sun.booking_tours.core.category;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.core.common.abtracts.BaseService;

import java.util.List;
import java.util.Optional;

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

    public Optional<Category> findByName(String name) {
        return repository.findByName(name);
    }

    public Optional<Category> findBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    public List<Category> searchByKeyword(String keyword) {
        return repository.searchByKeyword(keyword);
    }
}

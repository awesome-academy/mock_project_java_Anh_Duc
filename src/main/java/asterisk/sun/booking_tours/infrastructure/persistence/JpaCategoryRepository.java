package asterisk.sun.booking_tours.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import asterisk.sun.booking_tours.domain.category.Category;
import asterisk.sun.booking_tours.domain.category.CategoryRepository;

public interface JpaCategoryRepository extends JpaRepository<Category, Long>, CategoryRepository {

}

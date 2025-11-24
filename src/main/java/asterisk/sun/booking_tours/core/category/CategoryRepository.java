package asterisk.sun.booking_tours.core.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE (:keyword IS NULL OR :keyword = '') OR (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Category> searchByKeyword(@Param("keyword") String keyword);
}

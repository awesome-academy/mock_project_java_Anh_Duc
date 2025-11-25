package asterisk.sun.booking_tours.core.tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<Tour> findByName(String name);

    Optional<Tour> findBySlug(String slug);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.category LEFT JOIN FETCH t.creator WHERE (:keyword IS NULL OR :keyword = '') OR (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Tour> searchByKeyword(@Param("keyword") String keyword);
}

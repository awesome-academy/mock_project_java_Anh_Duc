package asterisk.sun.booking_tours.core.tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<Tour> findByName(String name);

    Optional<Tour> findBySlug(String slug);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.category LEFT JOIN FETCH t.creator WHERE (:keyword IS NULL OR :keyword = '') OR (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Tour> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT t FROM Tour t WHERE (:mainDestination IS NULL OR :mainDestination = '') OR LOWER(t.mainDestination) LIKE LOWER(CONCAT('%', :mainDestination, '%'))")
    List<Tour> searchByLocation(@Param("mainDestination") String mainDestination);

    @Query("SELECT DISTINCT t FROM Tour t JOIN t.departures d WHERE :date IS NULL OR d.departureDate = :date")
    List<Tour> searchByDate(@Param("date") LocalDate date);
}

package asterisk.sun.booking_tours.core.tourdepartures;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TourDeparturesRepository extends JpaRepository<TourDepartures, Long> {

    @Query("SELECT td FROM TourDepartures td LEFT JOIN FETCH td.tour t LEFT JOIN FETCH t.category WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TourDepartures> searchByKeyword(@Param("keyword") String keyword);

    List<TourDepartures> findByTourId(Long tourId);

    List<TourDepartures> findByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT td FROM TourDepartures td WHERE td.tour.id = :tourId AND td.departureDate >= :fromDate")
    List<TourDepartures> findUpcomingDeparturesByTourId(@Param("tourId") Long tourId,
            @Param("fromDate") LocalDate fromDate);

    boolean existsByTourIdAndDepartureDate(Long tourId, LocalDate departureDate);
}

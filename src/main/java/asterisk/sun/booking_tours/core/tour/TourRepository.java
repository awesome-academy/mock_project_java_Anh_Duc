package asterisk.sun.booking_tours.core.tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<Tour> findByName(String name);

    Optional<Tour> findBySlug(String slug);
}

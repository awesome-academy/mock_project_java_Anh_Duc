package asterisk.sun.booking_tours.domain.common.abstracts;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T save(T entity); // Create / Update

    void delete(T entity); // Delete

    Optional<T> findById(ID id); // Find by ID

    List<T> findAll(); // Find all

    boolean existsById(ID id); // Check existence
}

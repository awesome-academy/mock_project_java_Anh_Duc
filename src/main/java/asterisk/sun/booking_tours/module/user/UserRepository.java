package asterisk.sun.booking_tours.module.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asterisk.sun.booking_tours.module.user.projection.UserBasicProjection;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /**
     * Generic method to query users with any projection type.
     * Spring Data JPA will automatically generate the query based on the projection interface.
     *
     * @param <T> The projection type
     * @return List of projected user data
     */
    <T> List<T> findAllProjectedBy(Class<T> type);

    /**
     * Convenience method for querying basic user information.
     * Returns only: id, username, email, role, status
     */
    List<UserBasicProjection> findAllBy();

    @Query("SELECT u FROM User u WHERE CONCAT(u.username, ' ', u.email) LIKE %:keyword%")
    List<User> searchByKeyword(String keyword);
}

package asterisk.sun.booking_tours.core.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asterisk.sun.booking_tours.core.user.projection.UserBasicProjection;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<User> findByEmail(String email);
    /**
     * Generic method to query users with any projection type.
     * Spring Data JPA will automatically generate the query based on the projection
     * interface.
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

    @Query("SELECT u FROM User u WHERE (:keyword IS NULL OR :keyword = '') OR (LOWER(CONCAT(COALESCE(u.username, ''), ' ', COALESCE(u.email, ''))) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<UserBasicProjection> searchByKeyword(@Param("keyword") String keyword);

    Optional<User> findByPhone(String phone);

    List<User> findByRole(Role role);

    Long countByStatus(UserStatus status);
}

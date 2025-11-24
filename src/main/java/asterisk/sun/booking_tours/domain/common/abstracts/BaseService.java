package asterisk.sun.booking_tours.domain.common.abstracts;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Base Service class providing common CRUD operations for entities.
 * This class encapsulates common repository operations to reduce code duplication.
 *
 * @param <T> The entity type
 * @param <ID> The type of the entity's identifier
 * @param <R> The repository type extending JpaRepository
 */
public abstract class BaseService<T, ID, R extends JpaRepository<T, ID>> {

    protected final R repository;

    /**
     * Constructor to initialize the repository.
     *
     * @param repository The JpaRepository instance
     */
    protected BaseService(R repository) {
        this.repository = repository;
    }

    /**
     * Save an entity.
     *
     * @param entity The entity to save
     * @return The saved entity
     */
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * Save multiple entities.
     *
     * @param entities The list of entities to save
     * @return The list of saved entities
     */
    public List<T> saveAll(Iterable<T> entities) {
        return repository.saveAll(entities);
    }

    /**
     * Find an entity by its ID.
     *
     * @param id The entity ID
     * @return Optional containing the entity if found
     */
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    /**
     * Find an entity by its ID or return null if not found.
     *
     * @param id The entity ID
     * @return The entity or null
     */
    public T findByIdOrNull(ID id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Check if an entity exists by its ID.
     *
     * @param id The entity ID
     * @return true if exists, false otherwise
     */
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    /**
     * Find all entities.
     *
     * @return List of all entities
     */
    public List<T> findAll() {
        return repository.findAll();
    }

    /**
     * Find all entities with sorting.
     *
     * @param sort The sort specification
     * @return Sorted list of entities
     */
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    /**
     * Find all entities with pagination.
     *
     * @param pageable The pagination information
     * @return Page of entities
     */
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Find all entities by their IDs.
     *
     * @param ids The list of IDs
     * @return List of entities
     */
    public List<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Count all entities.
     *
     * @return The total count of entities
     */
    public long count() {
        return repository.count();
    }

    /**
     * Delete an entity by its ID.
     *
     * @param id The entity ID
     */
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    /**
     * Delete an entity.
     *
     * @param entity The entity to delete
     */
    public void delete(T entity) {
        repository.delete(entity);
    }

    /**
     * Delete multiple entities by their IDs.
     *
     * @param ids The list of IDs
     */
    public void deleteAllById(Iterable<ID> ids) {
        repository.deleteAllById(ids);
    }

    /**
     * Delete multiple entities.
     *
     * @param entities The list of entities to delete
     */
    public void deleteAll(Iterable<T> entities) {
        repository.deleteAll(entities);
    }

    /**
     * Delete all entities.
     */
    public void deleteAll() {
        repository.deleteAll();
    }

    /**
     * Flush pending changes to the database.
     */
    public void flush() {
        repository.flush();
    }

    /**
     * Save an entity and flush changes immediately.
     *
     * @param entity The entity to save
     * @return The saved entity
     */
    public T saveAndFlush(T entity) {
        return repository.saveAndFlush(entity);
    }

    /**
     * Get the repository instance.
     * Protected method for subclasses to access the repository if needed for custom operations.
     *
     * @return The repository instance
     */
    protected R getRepository() {
        return repository;
    }
}

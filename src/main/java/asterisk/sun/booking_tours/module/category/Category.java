package asterisk.sun.booking_tours.module.category;

import asterisk.sun.booking_tours.common.constants.ValidationConstants;
import asterisk.sun.booking_tours.common.helper.SlugifyHelper;
import asterisk.sun.booking_tours.module.common.abtracts.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Category Domain Entity
 * Rich domain model with business logic and validation
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name= "slug", nullable = false, unique = true)
    private String slug;

    // ========== Constructors ==========

    /**
     * JPA requires default constructor
     */
    protected Category() {}

    /**
     * Private constructor to enforce factory method usage
     */
    private Category(String name, String description, String slug) {
        this.name = name;
        this.description = description;
        this.slug = slug;
    }

    // ========== Factory Methods (DDD Pattern) ==========

    /**
     * Factory method to create new Category
     * Encapsulates creation logic and validation
     */
    public static Category create(String name, String description, String slug) {
        validateName(name);
        validateDescription(description);
        validateSlug(slug);

        return new Category(name, description, slug);
    }

    // ========== Business Methods ==========

    /**
     * Update category information
     * Business logic: ensure data consistency
     */
    public void updateInfo(String name, String description, String slug) {
        validateName(name);
        validateDescription(description);
        validateSlug(slug);

        this.name = name;
        this.description = description;
        this.slug = slug;
    }

    /**
     * Check if category name matches
     */
    public boolean hasName(String name) {
        return this.name != null && this.name.equalsIgnoreCase(name);
    }

    /**
     * Check if category is valid for publishing
     */
    public boolean isValid() {
        return name != null && !name.isBlank()
            && description != null && !description.isBlank()
            && slug != null && !slug.isBlank();
    }

    // ========== Domain Validation (Business Rules) ==========

    /**
     * Validate category name
     * Uses centralized validation constants
     */
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (name.length() < ValidationConstants.NAME_MIN_LENGTH
            || name.length() > ValidationConstants.NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(ValidationConstants.NAME_LENGTH_MESSAGE);
        }
    }

    /**
     * Validate category description
     * Uses centralized validation constants
     */
    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.length() < ValidationConstants.DESCRIPTION_MIN_LENGTH
            || description.length() > ValidationConstants.DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException(ValidationConstants.DESCRIPTION_LENGTH_MESSAGE);
        }
    }

    /**
     * Validate category slug
     * Delegates to SlugifyHelper for reusability
     */
    private static void validateSlug(String slug) {
        // Delegate to SlugifyHelper which uses ValidationConstants
        SlugifyHelper.validateSlug(slug);
    }

    // ========== Getters (No Setters - Immutability) ==========

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    // ========== For backward compatibility (will be removed later) ==========
    // These setters should be avoided, use updateInfo() instead

    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public void setSlug(String slug) {
        this.slug = slug;
    }
}

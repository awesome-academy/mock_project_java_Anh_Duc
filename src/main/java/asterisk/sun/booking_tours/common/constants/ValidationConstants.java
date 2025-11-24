package asterisk.sun.booking_tours.common.constants;

/**
 * Centralized Validation Constants
 * Used across DTOs, Entities, and Validation logic
 *
 * Benefits:
 * - Single source of truth for validation rules
 * - Prevents magic numbers scattered across codebase
 * - Easy to update validation rules in one place
 * - Reusable across different modules (Category, Product, Tour, etc.)
 */
public class ValidationConstants {

    // ========== SLUG VALIDATION ==========

    /**
     * Minimum length for slugs (URL-friendly identifiers)
     * Example: "abc" is minimum valid slug
     */
    public static final int SLUG_MIN_LENGTH = 3;

    /**
     * Maximum length for slugs
     * Keeps URLs manageable and SEO-friendly
     */
    public static final int SLUG_MAX_LENGTH = 100;

    /**
     * Regex pattern for valid slug format
     * Rules:
     * - Only lowercase letters (a-z)
     * - Numbers (0-9)
     * - Hyphens (-) as separators
     * - Cannot start or end with hyphen
     * - No consecutive hyphens
     *
     * Valid examples: "hello-world", "category-123", "my-awesome-tour"
     * Invalid examples: "Hello World", "test_slug", "-invalid-", "test--slug"
     */
    public static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";


    // ========== NAME VALIDATION ==========

    /**
     * Minimum length for names (Category, Product, etc.)
     */
    public static final int NAME_MIN_LENGTH = 3;

    /**
     * Maximum length for names
     */
    public static final int NAME_MAX_LENGTH = 100;


    // ========== DESCRIPTION VALIDATION ==========

    /**
     * Minimum length for descriptions
     * Ensures meaningful content
     */
    public static final int DESCRIPTION_MIN_LENGTH = 10;

    /**
     * Maximum length for descriptions
     * Prevents excessively long text
     */
    public static final int DESCRIPTION_MAX_LENGTH = 500;


    // ========== VALIDATION MESSAGES ==========

    /**
     * Error message for invalid slug format
     */
    public static final String SLUG_FORMAT_MESSAGE =
        "Slug must be lowercase with hyphens only (e.g., 'my-category')";

    /**
     * Error message for slug length
     */
    public static final String SLUG_LENGTH_MESSAGE =
        "Slug must be between " + SLUG_MIN_LENGTH + " and " + SLUG_MAX_LENGTH + " characters";

    /**
     * Error message for name length
     */
    public static final String NAME_LENGTH_MESSAGE =
        "Name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters";

    /**
     * Error message for description length
     */
    public static final String DESCRIPTION_LENGTH_MESSAGE =
        "Description must be between " + DESCRIPTION_MIN_LENGTH + " and " + DESCRIPTION_MAX_LENGTH + " characters";


    // ========== CONSTRUCTOR ==========

    /**
     * Private constructor to prevent instantiation
     * This is a utility class with only static members
     */
    private ValidationConstants() {
        throw new AssertionError("Cannot instantiate ValidationConstants");
    }
}

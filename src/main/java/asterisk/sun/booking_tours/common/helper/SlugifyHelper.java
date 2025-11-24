package asterisk.sun.booking_tours.common.helper;

import asterisk.sun.booking_tours.common.constants.ValidationConstants;
import com.github.slugify.Slugify;

/**
 * Slug Utility Helper
 * Provides slug generation and validation
 *
 * Reusable across all modules:
 * - Category slugs
 * - Product slugs
 * - Tour slugs
 * - Blog post slugs
 * - etc.
 *
 * Usage:
 * - Generate: String slug = SlugifyHelper.toSlug("Hello World"); // "hello-world"
 * - Validate: SlugifyHelper.validateSlug("my-slug");
 * - Check: boolean valid = SlugifyHelper.isValidSlug("my-slug");
 */
public class SlugifyHelper {
    private static final Slugify slugify = Slugify.builder().build();

    /**
     * Generate slug from text
     * Converts text to URL-friendly format
     *
     * Examples:
     * - "Hello World" -> "hello-world"
     * - "Category 123" -> "category-123"
     * - "Awesome Tour!" -> "awesome-tour"
     *
     * @param input Text to convert to slug
     * @return URL-friendly slug
     * @throws IllegalArgumentException if input is null or empty
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input text cannot be empty");
        }
        return slugify.slugify(input);
    }

    /**
     * Check if slug is valid format
     * Does NOT throw exception, just returns boolean
     *
     * Valid slug format:
     * - Lowercase letters (a-z)
     * - Numbers (0-9)
     * - Hyphens (-) as separators
     * - Length between 3-100 characters
     *
     * @param slug Slug to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return false;
        }

        // Check length
        if (slug.length() < ValidationConstants.SLUG_MIN_LENGTH
            || slug.length() > ValidationConstants.SLUG_MAX_LENGTH) {
            return false;
        }

        // Check format
        return slug.matches(ValidationConstants.SLUG_PATTERN);
    }

    /**
     * Validate slug and throw exception if invalid
     * Use this when you want to enforce validation
     *
     * @param slug Slug to validate
     * @throws IllegalArgumentException if slug is invalid
     */
    public static void validateSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Slug cannot be empty");
        }

        if (slug.length() < ValidationConstants.SLUG_MIN_LENGTH
            || slug.length() > ValidationConstants.SLUG_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Slug must be between " + ValidationConstants.SLUG_MIN_LENGTH
                + " and " + ValidationConstants.SLUG_MAX_LENGTH
                + " characters. Got: " + slug.length()
            );
        }

        if (!slug.matches(ValidationConstants.SLUG_PATTERN)) {
            throw new IllegalArgumentException(
                ValidationConstants.SLUG_FORMAT_MESSAGE + ". Got: " + slug
            );
        }
    }
}

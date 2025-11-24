package asterisk.sun.booking_tours.module.category.valueobject;

import java.util.Objects;

/**
 * Slug Value Object
 * Encapsulates slug validation and formatting logic
 * Immutable by design
 */
public class Slug {
    private final String value;

    private Slug(String value) {
        this.value = value;
    }

    /**
     * Factory method to create Slug with validation
     */
    public static Slug of(String value) {
        validate(value);
        return new Slug(normalize(value));
    }

    /**
     * Create slug from text (auto-generate)
     */
    public static Slug fromText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        String normalized = text.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special chars
                .replaceAll("\\s+", "-")          // Replace spaces with hyphens
                .replaceAll("-+", "-")            // Replace multiple hyphens with single
                .replaceAll("^-|-$", "");         // Remove leading/trailing hyphens

        return of(normalized);
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Slug cannot be empty");
        }
        if (!value.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException(
                "Slug must be lowercase with hyphens only (e.g., 'my-category'). Got: " + value
            );
        }
        if (value.length() < 3 || value.length() > 100) {
            throw new IllegalArgumentException("Slug must be between 3 and 100 characters");
        }
    }

    private static String normalize(String value) {
        return value.toLowerCase().trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slug slug = (Slug) o;
        return Objects.equals(value, slug.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

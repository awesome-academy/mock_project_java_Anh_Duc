package asterisk.sun.booking_tours.module.category.valueobject;

import java.util.Objects;

/**
 * CategoryName Value Object
 * Encapsulates category name validation logic
 * Immutable by design
 */
public class CategoryName {
    private final String value;

    private CategoryName(String value) {
        this.value = value;
    }

    /**
     * Factory method to create CategoryName with validation
     */
    public static CategoryName of(String value) {
        validate(value);
        return new CategoryName(normalize(value));
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (value.length() < 3 || value.length() > 100) {
            throw new IllegalArgumentException(
                "Category name must be between 3 and 100 characters. Got: " + value.length()
            );
        }
    }

    private static String normalize(String value) {
        return value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryName that = (CategoryName) o;
        return Objects.equals(value, that.value);
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

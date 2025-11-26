package asterisk.sun.booking_tours.common.dto;

import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * Abstract base class for DTOs that provides automatic toString() implementation
 * using reflection to print all fields and their values.
 *
 * Any DTO extending this class will automatically have a readable toString() representation
 * without needing to manually override the toString() method.
 */
public abstract class BaseDTO {

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}");

        Class<?> currentClass = this.getClass();

        // Traverse up the class hierarchy to include fields from parent classes
        while (currentClass != null && currentClass != BaseDTO.class && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                try {
                    // Make private fields accessible
                    field.setAccessible(true);
                    Object value = field.get(this);

                    // Format the field value
                    String formattedValue = formatValue(value);
                    joiner.add(field.getName() + "=" + formattedValue);

                } catch (IllegalAccessException e) {
                    // If we can't access the field, skip it
                    joiner.add(field.getName() + "=<inaccessible>");
                }
            }

            // Move to parent class
            currentClass = currentClass.getSuperclass();
        }

        return joiner.toString();
    }

    /**
     * Format a field value for string representation
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "'" + value + "'";
        } else if (value instanceof CharSequence) {
            return "'" + value + "'";
        } else {
            return value.toString();
        }
    }
}

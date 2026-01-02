package asterisk.sun.booking_tours.common.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields for Excel column mapping
 * Used with Reflection to automatically map Excel columns to DTO fields
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    /**
     * Column name in Excel header (case-insensitive)
     */
    String value();

    /**
     * Column index (0-based), -1 means auto-detect by header name
     */
    int index() default -1;

    /**
     * Whether this column is required
     */
    boolean required() default false;

    /**
     * Default value if cell is empty
     */
    String defaultValue() default "";

    /**
     * Whether this field should be included in export
     */
    boolean exportable() default true;
}

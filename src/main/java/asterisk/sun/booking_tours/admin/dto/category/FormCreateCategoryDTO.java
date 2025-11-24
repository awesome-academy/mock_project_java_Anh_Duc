package asterisk.sun.booking_tours.admin.dto.category;

import asterisk.sun.booking_tours.common.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FormCreateCategoryDTO {
    @NotBlank(message = "Category name is required")
    @Size(
        min = ValidationConstants.NAME_MIN_LENGTH,
        max = ValidationConstants.NAME_MAX_LENGTH,
        message = ValidationConstants.NAME_LENGTH_MESSAGE
    )
    private String name;

    @NotBlank(message = "Description is required")
    @Size(
        min = ValidationConstants.DESCRIPTION_MIN_LENGTH,
        max = ValidationConstants.DESCRIPTION_MAX_LENGTH,
        message = ValidationConstants.DESCRIPTION_LENGTH_MESSAGE
    )
    private String description;

    @NotBlank(message = "Slug is required")
    @Size(
        min = ValidationConstants.SLUG_MIN_LENGTH,
        max = ValidationConstants.SLUG_MAX_LENGTH,
        message = ValidationConstants.SLUG_LENGTH_MESSAGE
    )
    @Pattern(
        regexp = ValidationConstants.SLUG_PATTERN,
        message = ValidationConstants.SLUG_FORMAT_MESSAGE
    )
    private String slug;

    public FormCreateCategoryDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}

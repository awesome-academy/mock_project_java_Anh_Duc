package asterisk.sun.booking_tours.common.utils.excel;

/**
 * Column information for Excel import template
 */
public class ExcelColumnInfo {
    private String name;
    private String type;
    private boolean required;
    private String description;

    public ExcelColumnInfo() {
    }

    public ExcelColumnInfo(String name, String type, boolean required, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

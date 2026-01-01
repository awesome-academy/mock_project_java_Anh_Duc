package asterisk.sun.booking_tours.common.utils.excel;

import java.util.List;

/**
 * Template information for Excel import
 */
public class ExcelTemplateInfo {
    private String description;
    private List<ExcelColumnInfo> columns;

    public ExcelTemplateInfo() {
    }

    public ExcelTemplateInfo(String description, List<ExcelColumnInfo> columns) {
        this.description = description;
        this.columns = columns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ExcelColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ExcelColumnInfo> columns) {
        this.columns = columns;
    }
}

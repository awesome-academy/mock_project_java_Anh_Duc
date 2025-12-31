package asterisk.sun.booking_tours.application.rest.admin.article.dto;

import asterisk.sun.booking_tours.common.utils.excel.ExcelImportResult;

import java.util.List;

/**
 * Response DTO for Excel import operation
 */
public class ArticleImportResponseDTO {

    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<ArticleResponseDTO> importedArticles;
    private List<ExcelImportResult.ExcelImportError> errors;

    // Constructors
    public ArticleImportResponseDTO() {}

    public ArticleImportResponseDTO(int totalRows, int successCount, int errorCount,
                                    List<ArticleResponseDTO> importedArticles,
                                    List<ExcelImportResult.ExcelImportError> errors) {
        this.totalRows = totalRows;
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.importedArticles = importedArticles;
        this.errors = errors;
    }

    // Getters and Setters
    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<ArticleResponseDTO> getImportedArticles() {
        return importedArticles;
    }

    public void setImportedArticles(List<ArticleResponseDTO> importedArticles) {
        this.importedArticles = importedArticles;
    }

    public List<ExcelImportResult.ExcelImportError> getErrors() {
        return errors;
    }

    public void setErrors(List<ExcelImportResult.ExcelImportError> errors) {
        this.errors = errors;
    }
}

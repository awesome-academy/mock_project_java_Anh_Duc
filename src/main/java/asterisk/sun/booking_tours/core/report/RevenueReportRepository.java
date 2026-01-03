package asterisk.sun.booking_tours.core.report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueReportRepository extends JpaRepository<RevenueReport, Long> {

    /**
     * Find report by report code
     */
    Optional<RevenueReport> findByReportCode(String reportCode);

    /**
     * Find reports by status
     */
    List<RevenueReport> findByStatus(ReportStatus status);

    /**
     * Find reports by user ID with pagination
     */
    @Query("SELECT r FROM RevenueReport r WHERE r.requestedBy.id = :userId ORDER BY r.createdAt DESC")
    Page<RevenueReport> findByRequestedByIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find all reports with pagination
     */
    Page<RevenueReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find reports by type
     */
    List<RevenueReport> findByReportType(ReportType reportType);

    /**
     * Check if report code exists
     */
    boolean existsByReportCode(String reportCode);

    /**
     * Find pending reports
     */
    @Query("SELECT r FROM RevenueReport r WHERE r.status = :status ORDER BY r.createdAt ASC")
    List<RevenueReport> findPendingReports(@Param("status") ReportStatus status);
}

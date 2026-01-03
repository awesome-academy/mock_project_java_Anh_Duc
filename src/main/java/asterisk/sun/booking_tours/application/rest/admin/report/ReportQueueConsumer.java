package asterisk.sun.booking_tours.application.rest.admin.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportRequestMessage;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportResponseDTO;
import asterisk.sun.booking_tours.config.JmsConfig;
import asterisk.sun.booking_tours.core.report.RevenueReport;
import asterisk.sun.booking_tours.core.report.RevenueReportRepository;

/**
 * JMS Consumer for processing report generation requests from the queue
 */
@Component
public class ReportQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReportQueueConsumer.class);

    @Autowired
    @Lazy
    private ReportService reportService;

    @Autowired
    private RevenueReportRepository reportRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Listen to the report queue and process report generation requests
     * @param message The report request message from the queue
     */
    @JmsListener(destination = JmsConfig.REPORT_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void onReportRequest(ReportRequestMessage message) {
        logger.info("Received report request from queue: {}", message);

        try {
            // Process the report generation
            reportService.processReportGeneration(message);
            logger.info("Report generated successfully. Report Code: {}", message.getReportCode());

            // Send WebSocket notification if available
            notifyReportComplete(message.getReportCode());

        } catch (Exception e) {
            logger.error("Failed to process report request: {}", e.getMessage(), e);
            // Error handling is done in ReportService.processReportGeneration
            notifyReportFailed(message.getReportCode(), e.getMessage());
        }
    }

    /**
     * Send WebSocket notification when report is complete
     */
    private void notifyReportComplete(String reportCode) {
        if (messagingTemplate != null) {
            try {
                RevenueReport report = reportRepository.findByReportCode(reportCode).orElse(null);
                if (report != null) {
                    ReportResponseDTO dto = ReportResponseDTO.builder()
                            .id(report.getId())
                            .reportCode(report.getReportCode())
                            .reportType(report.getReportType())
                            .status(report.getStatus())
                            .fileName(report.getFileName())
                            .fileSize(report.getFileSize())
                            .generatedAt(report.getGeneratedAt())
                            .build();

                    messagingTemplate.convertAndSend("/topic/reports/" + reportCode, dto);
                    logger.info("WebSocket notification sent for report: {}", reportCode);
                }
            } catch (Exception e) {
                logger.warn("Failed to send WebSocket notification: {}", e.getMessage());
            }
        }
    }

    /**
     * Send WebSocket notification when report generation fails
     */
    private void notifyReportFailed(String reportCode, String errorMessage) {
        if (messagingTemplate != null) {
            try {
                RevenueReport report = reportRepository.findByReportCode(reportCode).orElse(null);
                if (report != null) {
                    ReportResponseDTO dto = ReportResponseDTO.builder()
                            .id(report.getId())
                            .reportCode(report.getReportCode())
                            .reportType(report.getReportType())
                            .status(report.getStatus())
                            .errorMessage(errorMessage)
                            .build();

                    messagingTemplate.convertAndSend("/topic/reports/" + reportCode, dto);
                }
            } catch (Exception e) {
                logger.warn("Failed to send WebSocket failure notification: {}", e.getMessage());
            }
        }
    }
}

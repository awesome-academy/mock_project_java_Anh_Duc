package asterisk.sun.booking_tours.application.rest.admin.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportRequestMessage;
import asterisk.sun.booking_tours.config.JmsConfig;

/**
 * JMS Producer for sending report generation requests to the queue
 */
@Component
public class ReportQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReportQueueProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Send a report generation request to the queue
     * @param message The report request message containing all necessary info
     */
    public void sendReportRequest(ReportRequestMessage message) {
        logger.info("Sending report request to queue: {}", message);
        try {
            jmsTemplate.convertAndSend(JmsConfig.REPORT_QUEUE, message);
            logger.info("Report request sent successfully. Report Code: {}", message.getReportCode());
        } catch (Exception e) {
            logger.error("Failed to send report request to queue: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to queue report request", e);
        }
    }
}

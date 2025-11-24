package asterisk.sun.booking_tours.module.category.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * DEMO: Ví dụ thực tế về cách sử dụng Domain Events
 *
 * Khi một Category được tạo, có nhiều việc cần làm:
 * 1. Clear cache
 * 2. Gửi notification
 * 3. Update search index
 * 4. Track analytics
 *
 * Thay vì làm TẤT CẢ trong CategoryCommandService,
 * ta dùng Events để tách biệt các concerns
 */
@Component
public class CategoryEventListenerDemo {

    private static final Logger log = LoggerFactory.getLogger(CategoryEventListenerDemo.class);

    // ========== Listener 1: Cache Management ==========

    /**
     * Xử lý cache khi category được tạo
     * Chạy ĐỒNG BỘ (synchronous) - chờ xong mới return
     */
    @EventListener
    public void handleCategoryCacheInvalidation(CategoryCreatedEvent event) {
        log.info("🗑️  [CACHE] Clearing category cache after creating: {}", event.getName());

        // Giả lập xóa cache
        try {
            Thread.sleep(100); // Giả lập thời gian xóa cache
            // cacheService.clearCategoryCache();
            // cacheService.evict("categories");
            log.info("✅ [CACHE] Cache cleared successfully");
        } catch (InterruptedException e) {
            log.error("❌ [CACHE] Failed to clear cache", e);
        }
    }

    // ========== Listener 2: Email Notification ==========

    /**
     * Gửi email thông báo cho admins
     * Chạy BẤT ĐỒNG BỘ (asynchronous) - không chặn transaction
     *
     * Lưu ý: Cần @EnableAsync trong config để async hoạt động
     */
    @Async
    @EventListener
    public void handleCategoryEmailNotification(CategoryCreatedEvent event) {
        log.info("📧 [EMAIL] Sending notification email for category: {}", event.getName());

        // Giả lập gửi email (thường mất 1-2 giây)
        try {
            Thread.sleep(2000); // Email mất thời gian

            // emailService.sendToAdmins(
            //     "New Category Created",
            //     "Category '" + event.getName() + "' was created at " + event.getOccurredOn()
            // );

            log.info("✅ [EMAIL] Email sent successfully to admins");
        } catch (InterruptedException e) {
            log.error("❌ [EMAIL] Failed to send email", e);
            // Email fail KHÔNG ảnh hưởng đến việc tạo category!
        }
    }

    // ========== Listener 3: Search Index ==========

    /**
     * Cập nhật search index (Elasticsearch, Solr, etc.)
     * Chạy BẤT ĐỒNG BỘ để không làm chậm response
     */
    @Async
    @EventListener
    public void handleCategorySearchIndexing(CategoryCreatedEvent event) {
        log.info("🔍 [SEARCH] Indexing category: {}", event.getName());

        try {
            Thread.sleep(500); // Giả lập indexing

            // searchIndexService.indexCategory(
            //     event.getCategoryId(),
            //     event.getName(),
            //     event.getSlug()
            // );

            log.info("✅ [SEARCH] Category indexed successfully");
        } catch (InterruptedException e) {
            log.error("❌ [SEARCH] Failed to index category", e);
        }
    }

    // ========== Listener 4: Analytics Tracking ==========

    /**
     * Track analytics data
     * Ghi lại thống kê để phân tích sau
     */
    @EventListener
    public void handleCategoryAnalyticsTracking(CategoryCreatedEvent event) {
        log.info("📊 [ANALYTICS] Tracking category creation: {}", event.getName());

        try {
            // analyticsService.track("category_created", Map.of(
            //     "category_id", event.getCategoryId(),
            //     "category_name", event.getName(),
            //     "timestamp", event.getOccurredOn()
            // ));

            log.info("✅ [ANALYTICS] Analytics tracked successfully");
        } catch (Exception e) {
            log.error("❌ [ANALYTICS] Failed to track analytics", e);
        }
    }

    // ========== Listener 5: Audit Log ==========

    /**
     * Ghi audit log để compliance & security
     * Quan trọng: phải thành công
     */
    @EventListener
    public void handleCategoryAuditLog(CategoryCreatedEvent event) {
        log.info("📝 [AUDIT] Logging category creation: {}", event.getName());

        try {
            // auditLogService.log(
            //     AuditAction.CATEGORY_CREATED,
            //     event.getCategoryId(),
            //     getCurrentUserId(),
            //     event.getOccurredOn(),
            //     Map.of("category_name", event.getName())
            // );

            log.info("✅ [AUDIT] Audit log recorded");
        } catch (Exception e) {
            log.error("❌ [AUDIT] Failed to record audit log", e);
            // Có thể throw exception nếu audit log là bắt buộc
        }
    }

    // ========== Listener 6: Webhook Notification ==========

    /**
     * Gửi webhook đến external systems
     * Ví dụ: Notify third-party services
     */
    @Async
    @EventListener
    public void handleCategoryWebhookNotification(CategoryCreatedEvent event) {
        log.info("🔔 [WEBHOOK] Sending webhook for category: {}", event.getName());

        try {
            Thread.sleep(1000); // HTTP request mất thời gian

            // webhookService.send("https://api.external.com/webhook", Map.of(
            //     "event", "category.created",
            //     "data", Map.of(
            //         "id", event.getCategoryId(),
            //         "name", event.getName(),
            //         "occurred_at", event.getOccurredOn()
            //     )
            // ));

            log.info("✅ [WEBHOOK] Webhook sent successfully");
        } catch (Exception e) {
            log.error("❌ [WEBHOOK] Failed to send webhook", e);
            // Webhook fail không ảnh hưởng đến category creation
        }
    }

    // ========== Listener 7: Real-time Notification (WebSocket) ==========

    /**
     * Push real-time notification đến admin dashboard
     * Admins khác sẽ thấy ngay khi có category mới
     */
    @EventListener
    public void handleCategoryRealtimeNotification(CategoryCreatedEvent event) {
        log.info("🔴 [REALTIME] Broadcasting category creation: {}", event.getName());

        try {
            // websocketService.broadcast("/topic/categories", Map.of(
            //     "type", "CATEGORY_CREATED",
            //     "id", event.getCategoryId(),
            //     "name", event.getName(),
            //     "timestamp", event.getOccurredOn()
            // ));

            log.info("✅ [REALTIME] Real-time notification broadcasted");
        } catch (Exception e) {
            log.error("❌ [REALTIME] Failed to broadcast notification", e);
        }
    }

    // ========== Error Handling Example ==========

    /**
     * Ví dụ về xử lý lỗi trong listener
     * Nếu listener này fail, không ảnh hưởng đến:
     * - Category đã được tạo thành công
     * - Các listeners khác vẫn chạy
     */
    @EventListener
    public void handleCategoryWithErrorHandling(CategoryCreatedEvent event) {
        try {
            log.info("🔧 [PROCESS] Processing category: {}", event.getName());

            // Giả lập lỗi
            if (event.getName().contains("ERROR")) {
                throw new RuntimeException("Intentional error for demo!");
            }

            // Process normally
            log.info("✅ [PROCESS] Processing completed");

        } catch (Exception e) {
            // Log error nhưng KHÔNG throw lại
            log.error("❌ [PROCESS] Error processing category: {}", event.getName(), e);

            // Optional: Send alert
            // alertService.sendAlert("Listener failed", e.getMessage());

            // Optional: Retry mechanism
            // retryQueue.add(event);
        }
    }

    // ========== Performance Monitoring ==========

    /**
     * Monitor performance của event processing
     */
    @EventListener
    public void measureEventProcessingTime(CategoryCreatedEvent event) {
        long startTime = System.currentTimeMillis();

        log.info("⏱️  [METRICS] Starting to process CategoryCreatedEvent");

        // Giả lập processing
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("⏱️  [METRICS] Event processed in {}ms", duration);

        // Track metrics
        // metricsService.recordEventProcessingTime("category_created", duration);
    }

    // ========== Conditional Listener ==========

    /**
     * Listener chỉ chạy khi thỏa điều kiện
     * Sử dụng SpEL (Spring Expression Language)
     */
    @EventListener(condition = "#event.name.startsWith('Premium')")
    public void handlePremiumCategoryCreated(CategoryCreatedEvent event) {
        log.info("💎 [PREMIUM] Premium category detected: {}", event.getName());

        // Special handling for premium categories
        // premiumService.notifyPremiumPartners(event);
    }

    // ========== Debug Listener ==========

    /**
     * Debug listener để log tất cả events (chỉ dùng trong development)
     * Có thể enable/disable bằng @Profile("dev")
     */
    // @Profile("dev")
    @EventListener
    public void debugLogAllCategoryEvents(CategoryCreatedEvent event) {
        log.debug("🐛 [DEBUG] CategoryCreatedEvent details: " +
                "\n  - Category ID: {}" +
                "\n  - Name: {}" +
                "\n  - Slug: {}" +
                "\n  - Occurred At: {}",
                event.getCategoryId(),
                event.getName(),
                event.getSlug(),
                event.getOccurredOn()
        );
    }
}

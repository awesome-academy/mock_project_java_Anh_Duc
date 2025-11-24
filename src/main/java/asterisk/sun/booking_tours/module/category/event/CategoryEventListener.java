package asterisk.sun.booking_tours.module.category.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Example Event Listener for Category Events
 * Demonstrates how to handle domain events
 */
@Component
public class CategoryEventListener {

    /**
     * Handle CategoryCreatedEvent
     * Example: Send notification, update cache, trigger other processes
     */
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        // Example usage:
        // - Clear cache
        // - Send notification to admins
        // - Update search index
        // - Log analytics

        System.out.println("📢 CategoryCreatedEvent received: " + event);

        // TODO: Implement your business logic here
        // e.g., emailService.notifyAdmins("New category created: " + event.getName());
    }
}

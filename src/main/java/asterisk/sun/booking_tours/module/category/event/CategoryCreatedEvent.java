package asterisk.sun.booking_tours.module.category.event;

import java.time.LocalDateTime;

/**
 * Domain Event - Category Created
 * Published when a new category is created
 */
public class CategoryCreatedEvent {
    private final Long categoryId;
    private final String name;
    private final String slug;
    private final LocalDateTime occurredOn;

    public CategoryCreatedEvent(Long categoryId, String name, String slug) {
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
        this.occurredOn = LocalDateTime.now();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String toString() {
        return "CategoryCreatedEvent{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}

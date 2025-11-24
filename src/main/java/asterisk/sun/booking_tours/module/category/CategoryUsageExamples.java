package asterisk.sun.booking_tours.module.category;

import asterisk.sun.booking_tours.module.category.valueobject.CategoryName;
import asterisk.sun.booking_tours.module.category.valueobject.Slug;

/**
 * Examples demonstrating how to use the improved Category module
 *
 * This class shows best practices for working with:
 * - Rich Domain Model (Category)
 * - Command/Query Separation
 * - Value Objects
 * - Domain Events
 */
public class CategoryUsageExamples {

    // ========== Example 1: Creating Category the RIGHT way ==========

    public void example1_CreateCategoryCorrectly(CategoryCommandService commandService) {
        // ✅ GOOD: Use domain service with validation
        Category category = commandService.createCategory(
            "Technology Tours",
            "Explore the latest tech innovations",
            "technology-tours"
        );
        // - Automatic validation
        // - Uniqueness check
        // - Domain event published
        // - Transaction managed
    }

    public void example1_CreateCategoryWrong() {
        // ❌ BAD: Don't do this
        // Category category = new Category();  // Constructor is protected!
        // category.setName("Tech");            // Setters are deprecated!
    }

    // ========== Example 2: Using Value Objects ==========

    public void example2_ValueObjects() {
        // ✅ GOOD: Using Value Objects for type safety
        CategoryName name = CategoryName.of("Technology");
        Slug slug = Slug.fromText("My Category Name");  // Auto: "my-category-name"

        System.out.println("Name: " + name.getValue());
        System.out.println("Slug: " + slug.getValue());

        // Value objects validate themselves
        try {
            Slug invalid = Slug.of("Invalid Slug!");  // Throws exception
        } catch (IllegalArgumentException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }

    // ========== Example 3: Query vs Command Separation ==========

    public void example3_QueryVsCommand(
            CategoryQueryService queryService,
            CategoryCommandService commandService) {

        // ✅ For READ operations - use QueryService
        var allCategories = queryService.findAll();
        var categoryOpt = queryService.findBySlug("technology");
        boolean exists = queryService.existsByName("Technology");

        // ✅ For WRITE operations - use CommandService
        Category created = commandService.createCategory(
            "New Category",
            "Description",
            "new-category"
        );

        Category updated = commandService.updateCategory(
            1L,
            "Updated Name",
            "Updated Description",
            "updated-slug"
        );

        commandService.deleteCategory(1L);
    }

    // ========== Example 4: Using Factory Method ==========

    public void example4_FactoryMethod() {
        // ✅ GOOD: Use factory method
        Category category = Category.create(
            "Adventure Tours",
            "Exciting adventure destinations",
            "adventure-tours"
        );
        // Automatic validation inside factory method

        // Check validity
        if (category.isValid()) {
            System.out.println("Category is valid!");
        }

        // Use business methods
        boolean matches = category.hasName("Adventure Tours");
    }

    // ========== Example 5: Updating Category ==========

    public void example5_UpdateCategory(
            CategoryCommandService commandService,
            CategoryQueryService queryService) {

        // ✅ GOOD: Use command service
        Long categoryId = 1L;
        Category updated = commandService.updateCategory(
            categoryId,
            "New Name",
            "New Description",
            "new-slug"
        );
        // - Automatic validation
        // - Uniqueness check (excluding current category)
        // - Transaction managed

        // ❌ BAD: Don't do manual update
        // Category cat = queryService.findById(1L).get();
        // cat.setName("New");  // Deprecated!
        // repository.save(cat);  // Missing validation!
    }

    // ========== Example 6: Error Handling ==========

    public void example6_ErrorHandling(CategoryCommandService commandService) {
        try {
            // This will throw exception if name already exists
            commandService.createCategory(
                "Duplicate Name",
                "Description",
                "slug"
            );
        } catch (IllegalStateException e) {
            System.out.println("Business rule violation: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    // ========== Example 7: Using in Admin Service ==========

    // AdminCategoryService demonstrates proper usage:
    /*
    @Service
    public class AdminCategoryService {
        private final CategoryQueryService queryService;
        private final CategoryCommandService commandService;

        public void createCategory(FormCreateCategoryDTO dto) {
            // Delegate to command service
            commandService.createCategory(
                dto.getName(),
                dto.getDescription(),
                dto.getSlug()
            );
            // That's it! No manual validation, no direct entity creation
        }
    }
    */

    // ========== Example 8: Domain Events ==========

    public void example8_DomainEvents(CategoryCommandService commandService) {
        // When you create a category:
        Category category = commandService.createCategory(
            "Event Example",
            "This will trigger an event",
            "event-example"
        );

        // CategoryCreatedEvent is automatically published
        // Listeners can handle it:
        // - Clear cache
        // - Send notifications
        // - Update search index
        // - Log analytics

        // See: CategoryEventListener.java
    }

    // ========== Example 9: Backward Compatibility ==========

    public void example9_BackwardCompatibility(CategoryService legacyService) {
        // ⚠️ OLD CODE still works (deprecated)
        var categories = legacyService.findAll();
        boolean exists = legacyService.existsByName("Tech");

        // But you should migrate to:
        // - CategoryQueryService for reads
        // - CategoryCommandService for writes
    }

    // ========== Example 10: Best Practices Summary ==========

    public void example10_BestPractices(
            CategoryQueryService queryService,
            CategoryCommandService commandService) {

        // ✅ DO:
        // 1. Use CommandService for writes
        Category created = commandService.createCategory("Name", "Desc", "slug");

        // 2. Use QueryService for reads
        var all = queryService.findAll();

        // 3. Use factory methods
        Category cat = Category.create("Name", "Desc", "slug");

        // 4. Use business methods
        cat.updateInfo("New Name", "New Desc", "new-slug");

        // 5. Use Value Objects for important concepts
        Slug slug = Slug.fromText("My Category");

        // ❌ DON'T:
        // 1. Don't use deprecated setters
        // cat.setName("New");  // Deprecated!

        // 2. Don't create entities directly in application services
        // Category c = new Category();  // Protected constructor!

        // 3. Don't bypass domain validation
        // repository.save(category);  // Use CommandService instead!

        // 4. Don't mix read/write in one service
        // Use Query/Command services separately
    }
}

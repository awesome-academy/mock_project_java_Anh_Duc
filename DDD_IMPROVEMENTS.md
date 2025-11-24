# Hướng dẫn cải thiện cấu trúc dự án theo DDD Principles

## 📋 Tổng quan các cải tiến đã thực hiện

Dự án đã được cải thiện từ **Anemic Domain Model** sang **Rich Domain Model** với các DDD principles, nhưng vẫn giữ tính đơn giản và thực tế.

> **🎯 Chiến lược:** Áp dụng DDD một cách **pragmatic** - chỉ lấy những gì cần thiết!
>
> ✅ **ĐÃ IMPLEMENT:**
> - Rich Domain Model (Category entity với business logic)
> - CQRS Pattern (Command/Query services)
> - Value Objects (Slug, CategoryName)
> - Application Layer improvements
>
> ⏳ **CHƯA IMPLEMENT (giữ lại cho sau):**
> - ❌ Domain Events (quá phức tạp cho dự án hiện tại)
> - ❌ Event Sourcing (không cần thiết)
> - ❌ Aggregate boundaries nghiêm ngặt (giữ đơn giản)
>
> **Lý do:** Ưu tiên code đơn giản, dễ hiểu, dễ maintain hơn là "chuẩn DDD"!

---

## ✅ Các cải tiến đã hoàn thành

### 1️⃣ **Rich Domain Model - Category Entity**

**Trước khi cải thiện:**
```java
// Anemic Model - chỉ có getter/setter
public class Category {
    private String name;
    private String description;
    private String slug;

    // Chỉ getter/setter
}
```

**Sau khi cải thiện:**
```java
public class Category {
    // ✅ Factory method thay vì constructor public
    public static Category create(String name, String description, String slug)

    // ✅ Business methods
    public void updateInfo(String name, String description, String slug)
    public boolean isValid()
    public boolean hasName(String name)

    // ✅ Domain validation
    private static void validateName(String name)
    private static void validateSlug(String slug)
}
```

**Lợi ích:**
- ✅ Encapsulation: Logic validation nằm trong entity
- ✅ Immutability: Sử dụng factory method
- ✅ Self-validating: Entity tự validate dữ liệu
- ✅ Business behavior: Entity có hành vi, không chỉ là data container

---

### 2️⃣ **CQRS Pattern - Command/Query Separation**

**Trước:**
```java
// Một service làm tất cả
public class CategoryService {
    public List<Category> findAll()           // Query
    public Category save(Category entity)     // Command
    public void delete(Category entity)       // Command
}
```

**Sau:**
```java
// ✅ Tách biệt Command và Query

// Query Service - READ operations only
@Transactional(readOnly = true)
public class CategoryQueryService {
    public List<Category> findAll()
    public Optional<Category> findById(Long id)
    public boolean existsByName(String name)
}

// Command Service - WRITE operations only
@Transactional
public class CategoryCommandService {
    public Category createCategory(...)
    public Category updateCategory(...)
    public void deleteCategory(Long id)
}
```

**Lợi ích:**
- ✅ Separation of Concerns: Read/Write logic tách biệt
- ✅ Performance: Query service có thể optimize riêng
- ✅ Security: Dễ dàng apply different permissions
- ✅ Scalability: Có thể scale read/write khác nhau

---

### 3️⃣ **Value Objects**

Tạo các Value Objects để encapsulate domain concepts:

```java
// Slug Value Object
public class Slug {
    private final String value;

    public static Slug of(String value)           // Validation
    public static Slug fromText(String text)      // Auto-generate
}

// CategoryName Value Object
public class CategoryName {
    private final String value;

    public static CategoryName of(String value)   // Validation
}
```

**Lợi ích:**
- ✅ Type Safety: Không nhầm lẫn giữa các String
- ✅ Validation: Logic validation tập trung
- ✅ Immutability: Value objects không thể thay đổi
- ✅ Domain Language: Code dễ đọc hơn

**Cách sử dụng (optional):**
```java
// Thay vì:
String slug = "my-category";

// Dùng:
Slug slug = Slug.fromText("My Category");  // Auto: "my-category"
```

---

### 4️⃣ **Application Service Layer - AdminCategoryService**

**Trước:**
```java
public class AdminCategoryService {
    // Trực tiếp tạo entity và save
    public void createCategory(FormCreateCategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        categoryService.save(category);
    }
}
```

**Sau:**
```java
public class AdminCategoryService {
    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;

    // Delegate sang domain services
    public void createCategory(FormCreateCategoryDTO dto) {
        commandService.createCategory(
            dto.getName(),
            dto.getDescription(),
            dto.getSlug()
        );
        // Domain service xử lý validation và business rules
    }
}
```

**Lợi ích:**
- ✅ Thin Application Layer: Chỉ orchestration
- ✅ Reusable: Domain services dùng được cho admin, API, client
- ✅ Testable: Dễ test từng layer riêng

---

### 5️⃣ **Domain Events** ⚠️ KHÔNG IMPLEMENT

> **📝 Quyết định:** Domain Events CHƯA được implement vì:
> - Dự án còn đơn giản, chưa có nhiều side effects
> - Ưu tiên code dễ hiểu hơn là "theo chuẩn"
> - Có thể thêm sau khi cần thiết

**Khi nào sẽ implement?**
- Khi cần gửi email/SMS sau khi tạo category
- Khi cần clear cache, update search index
- Khi có nhiều listeners quan tâm đến một event
- Khi cần async processing

**Tài liệu tham khảo (để học tập sau):**
- `DOMAIN_EVENTS_EXPLAINED.md` - Giải thích chi tiết
- `BEFORE_AFTER_EVENTS_COMPARISON.md` - So sánh trước/sau

**Ví dụ (nếu implement sau này):**

```java
// Event
public class CategoryCreatedEvent {
    private final Long categoryId;
    private final String name;
    private final LocalDateTime occurredOn;
}

// Publisher (trong CommandService)
public Category createCategory(...) {
    Category category = Category.create(...);
    Category saved = repository.save(category);

    // Publish event (CHƯA CÓ)
    // eventPublisher.publishEvent(
    //     new CategoryCreatedEvent(saved.getId(), saved.getName())
    // );

    return saved;
}

// Listener (CHƯA CÓ)
// @EventListener
// public void handleCategoryCreated(CategoryCreatedEvent event) {
//     // Clear cache, send notification, update search index...
// }
```

**Lợi ích (khi implement):**
- ✅ Decoupling: Services không phụ thuộc trực tiếp
- ✅ Extensibility: Dễ thêm side effects
- ✅ Async Processing: Có thể xử lý bất đồng bộ
- ✅ Event Sourcing: Foundation cho event sourcing

**Nhưng hiện tại:** Chưa cần thiết! ✅

---

## 📁 Cấu trúc sau khi cải tiến

```
module/category/
├── Category.java                      # Rich Domain Entity
├── CategoryRepository.java            # Repository Interface
├── CategoryQueryService.java          # Read Operations
├── CategoryCommandService.java        # Write Operations
├── CategoryService.java               # @Deprecated (backward compat)
├── valueobject/
│   ├── CategoryName.java             # Value Object
│   └── Slug.java                     # Value Object
└── event/
    ├── CategoryCreatedEvent.java     # Domain Event
    └── CategoryEventListener.java    # Event Handler (example)

admin/services/
└── AdminCategoryService.java          # Application Service (orchestration)
```

---

## 🎯 So sánh với DDD thuần túy

| Aspect | DDD Thuần túy | Cải tiến của bạn | Lý do |
|--------|---------------|------------------|-------|
| **Entity** | POJO thuần | JPA Entity | Thực tế, đỡ phức tạp |
| **Repository** | Interface + Impl | Spring Data JPA | Giảm boilerplate |
| **Value Objects** | Bắt buộc | Optional | Dùng khi cần |
| **Aggregate Root** | Strict boundaries | Flexible | Đủ dùng |
| **Domain Events** | Core pattern | Optional | Thêm khi cần |
| **Layers** | 4 layers riêng biệt | 3 layers | Đơn giản hơn |

---

## 🚀 Hướng dẫn sử dụng

### Cho Admin Operations:

```java
@RestController
@RequestMapping("/admin/categories")
public class CategoryController {
    private final AdminCategoryService adminService;

    @PostMapping
    public void create(@Valid @RequestBody FormCreateCategoryDTO dto) {
        // Admin service tự động delegate sang domain services
        adminService.createCategory(dto);
    }
}
```

### Cho Domain Logic:

```java
// Sử dụng Command Service trực tiếp
@Service
public class SomeService {
    private final CategoryCommandService commandService;

    public void doSomething() {
        // Business logic với validation tự động
        Category category = commandService.createCategory(
            "Technology",
            "Tech tours description",
            "technology"
        );
    }
}
```

### Cho Query:

```java
@Service
public class ReportService {
    private final CategoryQueryService queryService;

    public List<Category> getActiveCategories() {
        return queryService.findAll();
    }
}
```

---

## 📚 Best Practices đã áp dụng

### ✅ 1. Factory Methods
```java
// Thay vì: new Category(...)
Category category = Category.create(name, description, slug);
```

### ✅ 2. Validation trong Domain
```java
// Validation tự động trong factory method
Category.create("ab", "desc", "slug"); // ❌ Throws exception
```

### ✅ 3. Immutability
```java
// Không dùng setter
category.setName("new");  // ❌ Deprecated

// Dùng business method
category.updateInfo(name, desc, slug);  // ✅ Validated
```

### ✅ 4. Explicit Business Rules
```java
// Rõ ràng: Category name phải unique
if (queryService.existsByName(name)) {
    throw new IllegalStateException("Category already exists");
}
```

### ✅ 5. Transaction Boundaries
```java
@Transactional(readOnly = true)  // Query
@Transactional                    // Command
```

---

## 🎓 Khi nào dùng pattern nào?

### Use Value Objects when:
- ✅ Concept có validation phức tạp (Email, PhoneNumber, Money)
- ✅ Cần type safety (không nhầm lẫn giữa các String)
- ✅ Có business logic riêng (Money.add(), Slug.fromText())

### Use Domain Events when:
- ✅ Có side effects sau action (send email, clear cache)
- ✅ Cần decouple giữa các modules
- ✅ Cần audit trail/logging

### Use Factory Methods when:
- ✅ Có validation khi tạo object
- ✅ Có nhiều cách tạo object (Category.create, Category.createWithDefaults)

---

## 🔄 Migration từ code cũ

Code cũ vẫn hoạt động nhờ `@Deprecated` CategoryService:

```java
// Code cũ vẫn chạy
@Service
public class LegacyService {
    private final CategoryService categoryService;  // Deprecated nhưng vẫn dùng được

    public void oldCode() {
        categoryService.findAll();  // Tự động delegate sang QueryService
    }
}
```

Từ từ refactor sang code mới:

```java
// Code mới
@Service
public class NewService {
    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;
}
```

---

## 📈 Lợi ích tổng thể

1. **Maintainability** ⬆️
   - Logic tập trung, dễ tìm
   - Mỗi class có trách nhiệm rõ ràng

2. **Testability** ⬆️
   - Mock dễ dàng
   - Unit test từng layer riêng

3. **Scalability** ⬆️
   - Query/Command có thể scale riêng
   - Dễ thêm caching layer

4. **Code Quality** ⬆️
   - Type safety tốt hơn
   - Business rules rõ ràng

5. **Team Collaboration** ⬆️
   - Naming conventions nhất quán
   - Domain language chung

---

## 🎯 Next Steps (Tùy chọn)

1. **Apply pattern cho User module** tương tự Category
2. **Thêm Integration Tests** cho Command/Query services
3. **Add Caching** vào QueryService
4. **Implement Specification Pattern** cho complex queries
5. **Add Audit Logging** qua Domain Events

---

## ⚠️ Lưu ý

- ✅ **KHÔNG** over-engineer: Chỉ dùng pattern khi cần thiết
- ✅ **KHÔNG** áp dụng 100% DDD: Chọn những gì phù hợp
- ✅ **ƯU TIÊN** đơn giản và maintainable hơn là "chuẩn"
- ✅ **REFACTOR** từ từ, không làm hết một lúc

---

## 📞 Khi cần giúp đỡ

- Tham khảo code trong `Category` module làm mẫu
- Value Objects là optional, dùng khi thấy cần
- Domain Events chỉ cần khi có side effects
- Ưu tiên đơn giản trước, refactor sau khi cần

---

**Tác giả:** GitHub Copilot
**Ngày:** November 24, 2025
**Version:** 2.0 - DDD-lite Improvements

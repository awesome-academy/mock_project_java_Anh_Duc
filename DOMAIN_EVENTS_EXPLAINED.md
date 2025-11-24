# 🎯 Domain Events - Giải thích chi tiết

> **⚠️ LƯU Ý:** File này chỉ dùng để **THAM KHẢO** và **HỌC TẬP**.
>
> Domain Events **CHƯA** được implement trong dự án vì:
> - ✅ Dự án còn đơn giản, chưa cần phức tạp hóa
> - ✅ Ưu tiên code đơn giản, dễ hiểu trước
> - ✅ Có thể thêm sau khi dự án lớn hơn
>
> **Khi nào nên implement?**
> - Khi có nhiều side effects sau một action
> - Khi cần async processing (email, notifications...)
> - Khi hệ thống trở nên phức tạp hơn
>
> ---

## 📚 Domain Event là gì?

**Domain Event** là một **sự kiện quan trọng** đã xảy ra trong domain (nghiệp vụ) của bạn mà các phần khác của hệ thống có thể quan tâm.

### 🔍 Định nghĩa đơn giản:
> "Một điều gì đó đã xảy ra trong quá khứ mà hệ thống cần biết"

### 📝 Ví dụ trong cuộc sống thực:

Khi bạn **mua hàng online**, nhiều sự kiện xảy ra:
```
Hành động: Khách hàng đặt đơn hàng
   ↓
Events được tạo ra:
   - OrderPlacedEvent (Đơn hàng đã được đặt)
   - PaymentRequestedEvent (Yêu cầu thanh toán)
   - InventoryReservedEvent (Hàng đã được giữ)
   - EmailSentEvent (Email xác nhận đã gửi)
```

---

## 🎬 Ví dụ cụ thể trong dự án của bạn

### Kịch bản: Admin tạo Category mới "Technology Tours"

#### 1️⃣ **KHÔNG dùng Events** (Cách cũ):

```java
@Service
public class AdminCategoryService {
    private final CategoryRepository repository;
    private final CacheService cacheService;
    private final EmailService emailService;
    private final SearchIndexService searchService;
    private final AnalyticsService analyticsService;

    public void createCategory(FormCreateCategoryDTO dto) {
        // 1. Tạo category
        Category category = new Category();
        category.setName(dto.getName());
        repository.save(category);

        // 2. Xóa cache
        cacheService.clearCategoryCache();

        // 3. Gửi email thông báo cho admin
        emailService.notifyAdmins("New category: " + category.getName());

        // 4. Cập nhật search index
        searchService.indexCategory(category);

        // 5. Log analytics
        analyticsService.trackCategoryCreation(category);
    }
}
```

**❌ Vấn đề:**
- 😣 Service phụ thuộc vào QUẤT nhiều services khác
- 😣 Code dài, khó đọc
- 😣 Khó test (phải mock 5 services)
- 😣 Muốn thêm tính năng mới → phải sửa service này
- 😣 Nếu EmailService lỗi → toàn bộ transaction fail

---

#### 2️⃣ **CÓ dùng Events** (Cách mới):

```java
@Service
public class CategoryCommandService {
    private final CategoryRepository repository;
    private final ApplicationEventPublisher eventPublisher;  // Chỉ cần 1 dependency!

    public Category createCategory(String name, String desc, String slug) {
        // 1. Tạo category
        Category category = Category.create(name, desc, slug);
        Category saved = repository.save(category);

        // 2. Publish event - XONG!
        eventPublisher.publishEvent(
            new CategoryCreatedEvent(saved.getId(), saved.getName(), saved.getSlug())
        );

        return saved;  // Clean & Simple!
    }
}
```

**Các Listeners tự động nhận event:**

```java
// Listener 1: Xử lý cache
@Component
public class CategoryCacheListener {
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        cacheService.clearCategoryCache();
        System.out.println("✅ Cache cleared for: " + event.getName());
    }
}

// Listener 2: Gửi email
@Component
public class CategoryEmailListener {
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        emailService.notifyAdmins("New category: " + event.getName());
        System.out.println("✅ Email sent for: " + event.getName());
    }
}

// Listener 3: Search indexing
@Component
public class CategorySearchListener {
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        searchService.indexCategory(event.getCategoryId());
        System.out.println("✅ Search indexed: " + event.getName());
    }
}

// Listener 4: Analytics
@Component
public class CategoryAnalyticsListener {
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        analyticsService.trackCreation(event);
        System.out.println("✅ Analytics tracked: " + event.getName());
    }
}
```

**✅ Lợi ích:**
- 🚀 Service đơn giản, chỉ làm 1 việc
- 🚀 Dễ test (chỉ mock repository)
- 🚀 Thêm tính năng mới → tạo listener mới, KHÔNG sửa service
- 🚀 Nếu EmailService lỗi → các listener khác vẫn chạy
- 🚀 Có thể chạy async (không chặn transaction)

---

## 🔄 Flow hoạt động

```
┌──────────────────────────────────────────────────────────┐
│  1. Admin clicks "Create Category"                       │
└───────────────────────┬──────────────────────────────────┘
                        ▼
┌──────────────────────────────────────────────────────────┐
│  2. CategoryCommandService.createCategory()              │
│     - Validate                                           │
│     - Save to DB                                         │
│     - eventPublisher.publishEvent(CategoryCreatedEvent)  │
└───────────────────────┬──────────────────────────────────┘
                        ▼
┌──────────────────────────────────────────────────────────┐
│  3. Spring ApplicationEventPublisher                     │
│     "Có ai quan tâm CategoryCreatedEvent không?"        │
└────┬────────┬─────────┬──────────┬──────────────────────┘
     │        │         │          │
     ▼        ▼         ▼          ▼
┌─────────┐ ┌──────┐ ┌────────┐ ┌───────────┐
│ Cache   │ │Email │ │Search  │ │Analytics  │
│Listener │ │List. │ │Listener│ │Listener   │
└─────────┘ └──────┘ └────────┘ └───────────┘
     │        │         │          │
     ▼        ▼         ▼          ▼
  Clear    Send      Index      Track
  Cache    Email     Search     Stats
```

---

## 🎯 Tác dụng của Domain Events

### 1️⃣ **Decoupling (Tách rời phụ thuộc)**

**Không có Events:**
```java
CategoryService
    ↓ depends on
    ├─→ CacheService
    ├─→ EmailService
    ├─→ SearchService
    └─→ AnalyticsService
```
→ 4 dependencies! 😱

**Có Events:**
```java
CategoryCommandService
    ↓ depends on
    └─→ ApplicationEventPublisher (Spring built-in)
```
→ Chỉ 1 dependency! 😊

---

### 2️⃣ **Single Responsibility (Mỗi class chỉ làm 1 việc)**

```java
// CategoryCommandService chỉ lo việc CRUD
public Category createCategory(...) {
    // Chỉ save vào DB
    // Không quan tâm email, cache, analytics...
}

// CacheListener chỉ lo việc cache
@EventListener
public void handleCategoryCreated(CategoryCreatedEvent event) {
    // Chỉ clear cache
}

// EmailListener chỉ lo việc email
@EventListener
public void handleCategoryCreated(CategoryCreatedEvent event) {
    // Chỉ send email
}
```

---

### 3️⃣ **Open/Closed Principle (Mở để mở rộng, đóng để sửa đổi)**

**Thêm tính năng mới:**

```java
// ✅ Muốn send SMS khi tạo category?
// Tạo listener mới, KHÔNG SỬA CategoryCommandService!

@Component
public class CategorySmsListener {
    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        smsService.sendNotification("New category: " + event.getName());
    }
}
```

**Không cần sửa code cũ!** Chỉ thêm class mới.

---

### 4️⃣ **Error Isolation (Cách ly lỗi)**

```java
@Service
public class CategoryCommandService {
    public Category createCategory(...) {
        Category saved = repository.save(category);

        // Publish event
        eventPublisher.publishEvent(new CategoryCreatedEvent(...));

        return saved;  // ✅ Category đã được save
    }
}

// Nếu EmailListener bị lỗi:
@EventListener
public void handleCategoryCreated(CategoryCreatedEvent event) {
    throw new RuntimeException("Email server down!");  // ❌ Lỗi!
}

// → Category VẪN được tạo thành công! ✅
// → Chỉ email không gửi được
// → Các listeners khác vẫn chạy bình thường
```

---

### 5️⃣ **Async Processing (Xử lý bất đồng bộ)**

```java
// Chạy đồng bộ (default)
@EventListener
public void handleSync(CategoryCreatedEvent event) {
    // Chặn transaction, chờ hoàn thành
}

// Chạy bất đồng bộ
@Async
@EventListener
public void handleAsync(CategoryCreatedEvent event) {
    // Không chặn transaction
    // Chạy trong thread riêng

    // Ví dụ: Send email (có thể mất 2-3 giây)
    emailService.sendWelcomeEmail();  // Không làm user chờ!
}
```

**Configuration:**
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    // Enable async processing
}
```

---

### 6️⃣ **Audit Trail (Lịch sử hoạt động)**

```java
@Component
public class AuditLogListener {
    @EventListener
    public void logEvent(CategoryCreatedEvent event) {
        auditLog.save(new AuditEntry(
            "CATEGORY_CREATED",
            event.getCategoryId(),
            event.getOccurredOn(),
            getCurrentUser()
        ));
    }
}
```

→ Tự động log mọi thay đổi!

---

## 📊 So sánh: Có vs Không có Events

### Ví dụ thực tế: Tạo Category

| Khía cạnh | Không có Events | Có Events |
|-----------|-----------------|-----------|
| **Dependencies** | 5+ services | 1 service |
| **Code length** | 50+ lines | 10 lines |
| **Testability** | Mock 5 services | Mock 1 publisher |
| **Thêm tính năng** | Sửa service cũ | Thêm listener mới |
| **Error handling** | 1 lỗi → fail all | Lỗi isolated |
| **Performance** | Đồng bộ (chậm) | Có thể async |
| **Maintainability** | Khó bảo trì | Dễ bảo trì |

---

## 🎭 Ví dụ thực tế trong cuộc sống

### Ví dụ 1: Đặt hàng Pizza 🍕

```java
// Event: PizzaOrderedEvent
public class PizzaOrderedEvent {
    private String customerName;
    private String pizzaType;
    private LocalDateTime orderedAt;
}

// Listeners:
1. KitchenListener     → Bắt đầu làm pizza
2. DeliveryListener    → Chuẩn bị xe giao hàng
3. PaymentListener     → Xử lý thanh toán
4. NotificationListener → Gửi SMS cho khách hàng
5. InventoryListener   → Trừ nguyên liệu
```

### Ví dụ 2: User đăng ký tài khoản 👤

```java
// Event: UserRegisteredEvent
public class UserRegisteredEvent {
    private Long userId;
    private String email;
    private LocalDateTime registeredAt;
}

// Listeners:
1. WelcomeEmailListener → Gửi email chào mừng
2. ProfileListener      → Tạo profile mặc định
3. RewardListener       → Tặng điểm thưởng
4. AnalyticsListener    → Track conversion
5. MarketingListener    → Thêm vào email list
```

---

## 🔧 Cách implement trong dự án của bạn

### Bước 1: Tạo Event class

```java
package asterisk.sun.booking_tours.module.category.event;

import java.time.LocalDateTime;

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

    // Getters...
}
```

### Bước 2: Publish event

```java
@Service
public class CategoryCommandService {
    private final ApplicationEventPublisher eventPublisher;

    public Category createCategory(...) {
        Category saved = repository.save(category);

        // Publish event
        eventPublisher.publishEvent(
            new CategoryCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getSlug()
            )
        );

        return saved;
    }
}
```

### Bước 3: Tạo Listeners

```java
@Component
public class CategoryEventListener {

    @EventListener
    public void handleCategoryCreated(CategoryCreatedEvent event) {
        System.out.println("📢 Event received: " + event);

        // Xử lý logic của bạn:
        // - Clear cache
        // - Send notification
        // - Update search index
        // - Log analytics
    }
}
```

---

## 🎯 Khi nào nên dùng Domain Events?

### ✅ NÊN dùng khi:

1. **Có side effects** (tác dụng phụ) sau một action:
   - ✅ Tạo user → gửi email, tặng điểm
   - ✅ Đặt hàng → trừ kho, gửi thông báo
   - ✅ Tạo category → clear cache, index search

2. **Nhiều systems quan tâm** đến cùng 1 event:
   - ✅ Email service
   - ✅ SMS service
   - ✅ Analytics service
   - ✅ Cache service

3. **Muốn decouple** các modules:
   - ✅ Core business không phụ thuộc vào email/sms
   - ✅ Dễ test riêng từng phần

4. **Cần async processing**:
   - ✅ Send email (chậm) → không làm user chờ
   - ✅ Generate report (chậm) → chạy background

### ❌ KHÔNG cần dùng khi:

1. **Logic đơn giản**, chỉ 1-2 bước:
   ```java
   // Đơn giản quá, không cần events
   public void updateName(String name) {
       this.name = name;
   }
   ```

2. **Không có side effects**:
   - Chỉ query dữ liệu → không cần events

3. **Cần transaction nhất quán** (ACID):
   - Nếu A và B phải cùng success/fail
   - Events có thể fail riêng lẻ

---

## 🚀 Các loại Events trong dự án của bạn

### 1. Category Events
```java
CategoryCreatedEvent     // Khi tạo category mới
CategoryUpdatedEvent     // Khi update category
CategoryDeletedEvent     // Khi xóa category
```

### 2. User Events (có thể thêm)
```java
UserRegisteredEvent      // Khi user đăng ký
UserLoggedInEvent        // Khi user login
UserPasswordChangedEvent // Khi đổi password
```

### 3. Tour Events (có thể thêm)
```java
TourCreatedEvent         // Khi tạo tour
TourBookedEvent          // Khi đặt tour
TourCancelledEvent       // Khi hủy tour
```

---

## 🔍 Debug và Monitor Events

### Log events để debug:

```java
@Component
public class EventLoggingListener {
    private static final Logger log = LoggerFactory.getLogger(EventLoggingListener.class);

    @EventListener
    public void logAllEvents(Object event) {
        if (event.getClass().getSimpleName().endsWith("Event")) {
            log.info("📢 Event published: {}", event);
        }
    }
}
```

### Monitor performance:

```java
@Component
public class EventMetricsListener {

    @EventListener
    public void measureEventProcessing(CategoryCreatedEvent event) {
        long startTime = System.currentTimeMillis();

        // Process event
        processEvent(event);

        long duration = System.currentTimeMillis() - startTime;
        log.info("⏱️ Event processed in {}ms", duration);
    }
}
```

---

## 🎓 Advanced: Event Sourcing (Nâng cao)

Domain Events là nền tảng cho **Event Sourcing** - lưu toàn bộ events thay vì state:

```java
// Thay vì lưu state hiện tại:
Category {
  id: 1,
  name: "Technology",
  createdAt: "2025-11-24"
}

// Event Sourcing lưu history:
Events:
1. CategoryCreatedEvent(name="Tech", time="2025-11-24 10:00")
2. CategoryRenamedEvent(oldName="Tech", newName="Technology", time="2025-11-24 11:00")
3. CategoryDescriptionUpdatedEvent(description="...", time="2025-11-24 12:00")

// Có thể replay history để biết state tại bất kỳ thời điểm nào!
```

---

## 📝 Best Practices

### ✅ DO:
```java
// 1. Events phải immutable (final fields)
public class CategoryCreatedEvent {
    private final Long categoryId;  // final!
    private final String name;      // final!
}

// 2. Events phải có timestamp
private final LocalDateTime occurredOn;

// 3. Tên events dùng quá khứ (đã xảy ra)
CategoryCreatedEvent     // ✅ Good
CategoryCreateEvent      // ❌ Bad

// 4. Events nhẹ, chỉ chứa data cần thiết
public class CategoryCreatedEvent {
    private final Long id;
    private final String name;
    // Không chứa toàn bộ Category object!
}
```

### ❌ DON'T:
```java
// 1. Không throw exception trong listener
@EventListener
public void handle(CategoryCreatedEvent event) {
    throw new RuntimeException("Error!");  // ❌ Bad!
    // Nên log error và continue
}

// 2. Không modify event
@EventListener
public void handle(CategoryCreatedEvent event) {
    event.setName("Modified");  // ❌ Bad! Events immutable!
}

// 3. Không có logic phức tạp trong listener
@EventListener
public void handle(CategoryCreatedEvent event) {
    // 100 lines of code...  // ❌ Bad!
    // Nên delegate sang service
}
```

---

## 🎉 Tóm tắt

### Domain Events là:
- 📢 **Notification** về điều gì đã xảy ra
- 🔗 **Decoupling** các phần của hệ thống
- 🚀 **Extensibility** dễ dàng thêm tính năng
- 🛡️ **Error isolation** lỗi không lan rộng
- ⚡ **Performance** có thể async

### Trong dự án của bạn:
```java
createCategory()
    ↓
Publish: CategoryCreatedEvent
    ↓
Listeners tự động:
    - Clear cache     ✅
    - Send email      ✅
    - Index search    ✅
    - Track analytics ✅
```

### Lợi ích:
- ✅ Code đơn giản, dễ hiểu
- ✅ Dễ test
- ✅ Dễ mở rộng
- ✅ Dễ bảo trì

---

**Bây giờ bạn đã hiểu Domain Events chưa?**

Nếu còn thắc mắc, hãy hỏi tôi về bất kỳ phần nào nhé! 😊

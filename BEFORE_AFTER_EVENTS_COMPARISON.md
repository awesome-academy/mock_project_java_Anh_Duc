# 🔄 So sánh: Trước và Sau khi dùng Domain Events

## 📊 Kịch bản: Admin tạo Category "Technology Tours"

---

## ❌ TRƯỚC KHI DÙNG EVENTS (Tightly Coupled)

### Code:
```java
@Service
public class AdminCategoryService {
    // Phụ thuộc vào 6 services khác nhau! 😱
    private final CategoryRepository categoryRepository;
    private final CacheService cacheService;
    private final EmailService emailService;
    private final SearchIndexService searchIndexService;
    private final AnalyticsService analyticsService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public void createCategory(FormCreateCategoryDTO dto) {
        // 1. Validate
        if (categoryRepository.existsByName(dto.getName())) {
            throw new IllegalStateException("Category already exists");
        }

        // 2. Create and save
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSlug(dto.getSlug());
        Category saved = categoryRepository.save(category);

        // 3. Clear cache
        try {
            cacheService.clearCategoryCache();
        } catch (Exception e) {
            log.error("Failed to clear cache", e);
            // Rollback cả transaction? Hay ignore?
        }

        // 4. Send email notification
        try {
            emailService.sendToAdmins(
                "New Category Created",
                "Category: " + saved.getName()
            );
        } catch (Exception e) {
            log.error("Failed to send email", e);
            // Email fail → rollback transaction?
        }

        // 5. Update search index
        try {
            searchIndexService.indexCategory(saved);
        } catch (Exception e) {
            log.error("Failed to index category", e);
        }

        // 6. Track analytics
        try {
            analyticsService.track("category_created", saved.getId());
        } catch (Exception e) {
            log.error("Failed to track analytics", e);
        }

        // 7. Send notification to other admins
        try {
            notificationService.notifyAdmins("New category: " + saved.getName());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }

        // 8. Log audit trail
        try {
            auditLogService.log(
                "CATEGORY_CREATED",
                saved.getId(),
                getCurrentUser()
            );
        } catch (Exception e) {
            log.error("Failed to log audit", e);
        }

        // 😱 Code dài 80+ lines chỉ để tạo category!
    }
}
```

### Vấn đề:

| Vấn đề | Mô tả | Impact |
|--------|-------|---------|
| **High Coupling** | Service phụ thuộc 7 dependencies | 😱😱😱 |
| **Long Method** | 80+ lines code | 😱😱 |
| **Mixed Concerns** | Business logic + side effects lẫn lộn | 😱😱😱 |
| **Hard to Test** | Phải mock 7 services | 😱😱😱 |
| **Error Handling** | Không rõ nên rollback hay ignore | 😱😱 |
| **Performance** | Tất cả chạy đồng bộ → chậm | 😱😱 |
| **Extensibility** | Thêm tính năng → sửa service | 😱😱 |

### Flow Chart:
```
User Request
    ↓
AdminCategoryService
    ├─→ Validate
    ├─→ Save to DB
    ├─→ Clear Cache        [WAIT 100ms]
    ├─→ Send Email         [WAIT 2000ms] ⏰
    ├─→ Update Search      [WAIT 500ms]
    ├─→ Track Analytics    [WAIT 200ms]
    ├─→ Send Notification  [WAIT 300ms]
    └─→ Audit Log          [WAIT 100ms]
    ↓
Total Time: ~3200ms 😱
    ↓
Response to User
```

### Timeline:
```
0ms     |████| Save to DB
100ms   |██| Clear Cache
2100ms  |████████████████████| Send Email (SLOW!)
2600ms  |████| Update Search
2800ms  |██| Analytics
3100ms  |██| Notification
3200ms  |█| Audit Log
        ↓
   User waits 3200ms for response! 😱
```

---

## ✅ SAU KHI DÙNG EVENTS (Loosely Coupled)

### Code:
```java
@Service
@Transactional
public class CategoryCommandService {
    // Chỉ 2 dependencies! 😊
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Category createCategory(String name, String description, String slug) {
        // 1. Validate
        if (categoryRepository.existsByName(name)) {
            throw new IllegalStateException("Category already exists");
        }

        // 2. Create and save
        Category category = Category.create(name, description, slug);
        Category saved = categoryRepository.save(category);

        // 3. Publish event - DONE! ✅
        eventPublisher.publishEvent(
            new CategoryCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getSlug()
            )
        );

        return saved;

        // 🎉 Code chỉ 20 lines, sạch sẽ!
    }
}

// ========== Listeners tự động xử lý ==========

@Component
public class CategoryCacheListener {
    @EventListener
    public void onCategoryCreated(CategoryCreatedEvent event) {
        cacheService.clearCategoryCache();
    }
}

@Component
public class CategoryEmailListener {
    @Async  // Chạy async!
    @EventListener
    public void onCategoryCreated(CategoryCreatedEvent event) {
        emailService.sendToAdmins("New category: " + event.getName());
    }
}

@Component
public class CategorySearchListener {
    @Async  // Chạy async!
    @EventListener
    public void onCategoryCreated(CategoryCreatedEvent event) {
        searchIndexService.indexCategory(event.getCategoryId());
    }
}

// ... và các listeners khác
```

### Lợi ích:

| Aspect | Improvement | Impact |
|--------|-------------|---------|
| **Coupling** | 2 dependencies vs 7 | 😊😊😊 |
| **Code Length** | 20 lines vs 80 lines | 😊😊 |
| **Separation** | Business logic tách biệt side effects | 😊😊😊 |
| **Testing** | Mock 2 services vs 7 | 😊😊😊 |
| **Error Handling** | Lỗi isolated, không ảnh hưởng nhau | 😊😊😊 |
| **Performance** | Async processing → nhanh | 😊😊😊 |
| **Extensibility** | Thêm listener, không sửa service | 😊😊😊 |

### Flow Chart:
```
User Request
    ↓
CategoryCommandService
    ├─→ Validate
    ├─→ Save to DB
    └─→ Publish Event
    ↓
Response to User [150ms] ✅
    ↓
=========== Background Processing ===========
    ↓
CategoryCreatedEvent
    ├──→ [SYNC]  Cache Listener       [100ms]
    ├──→ [ASYNC] Email Listener       [2000ms]
    ├──→ [ASYNC] Search Listener      [500ms]
    ├──→ [ASYNC] Analytics Listener   [200ms]
    ├──→ [ASYNC] Notification Listener[300ms]
    └──→ [SYNC]  Audit Log Listener   [100ms]
```

### Timeline (Async):
```
Main Thread:
0ms     |████| Save to DB
100ms   |█| Publish Event
150ms   → Response to User ✅ (User chỉ đợi 150ms!)

Background Threads (Async):
0ms     |██| Clear Cache (sync)
0ms     |████████████████████| Send Email (async, parallel)
0ms     |████| Update Search (async, parallel)
0ms     |██| Analytics (async, parallel)
0ms     |██| Notification (async, parallel)
100ms   |█| Audit Log (sync)

Total user wait: 150ms (vs 3200ms trước đây!) 🚀
Background completes in: ~2000ms (parallel)
```

---

## 📊 So sánh Chi tiết

### 1. Dependencies

**Trước:**
```
AdminCategoryService
    ↓ depends on
    ├─→ CategoryRepository
    ├─→ CacheService
    ├─→ EmailService
    ├─→ SearchIndexService
    ├─→ AnalyticsService
    ├─→ NotificationService
    └─→ AuditLogService

Total: 7 dependencies! 😱
```

**Sau:**
```
CategoryCommandService
    ↓ depends on
    ├─→ CategoryRepository
    └─→ ApplicationEventPublisher (Spring built-in)

Total: 2 dependencies! 😊

Listeners tách biệt:
    CacheListener → CacheService
    EmailListener → EmailService
    SearchListener → SearchIndexService
    ...
```

---

### 2. Error Handling

**Trước:**
```java
try {
    emailService.send(...);
} catch (Exception e) {
    // Phải quyết định:
    // 1. Rollback cả transaction? → User không tạo được category
    // 2. Ignore error? → Category tạo nhưng không gửi email
    // 3. Retry? → Code phức tạp
    log.error("Email failed", e);
    // ??? Làm gì đây ???
}
```

**Sau:**
```java
@EventListener
public void onCategoryCreated(CategoryCreatedEvent event) {
    try {
        emailService.send(...);
    } catch (Exception e) {
        log.error("Email failed", e);
        // Category đã được tạo thành công! ✅
        // Các listeners khác vẫn chạy bình thường ✅
        // Có thể retry riêng listener này ✅
    }
}
```

---

### 3. Testing

**Trước:**
```java
@Test
public void testCreateCategory() {
    // Phải mock 7 services! 😱
    CategoryRepository mockRepo = mock(CategoryRepository.class);
    CacheService mockCache = mock(CacheService.class);
    EmailService mockEmail = mock(EmailService.class);
    SearchIndexService mockSearch = mock(SearchIndexService.class);
    AnalyticsService mockAnalytics = mock(AnalyticsService.class);
    NotificationService mockNotif = mock(NotificationService.class);
    AuditLogService mockAudit = mock(AuditLogService.class);

    AdminCategoryService service = new AdminCategoryService(
        mockRepo, mockCache, mockEmail, mockSearch,
        mockAnalytics, mockNotif, mockAudit
    );

    // Test...
}
```

**Sau:**
```java
@Test
public void testCreateCategory() {
    // Chỉ mock 2! 😊
    CategoryRepository mockRepo = mock(CategoryRepository.class);
    ApplicationEventPublisher mockPublisher = mock(ApplicationEventPublisher.class);

    CategoryCommandService service = new CategoryCommandService(
        mockRepo, mockPublisher
    );

    // Test business logic
    Category result = service.createCategory("Tech", "Desc", "tech");

    // Verify event published
    verify(mockPublisher).publishEvent(any(CategoryCreatedEvent.class));
}

// Test listeners riêng:
@Test
public void testEmailListener() {
    EmailService mockEmail = mock(EmailService.class);
    CategoryEmailListener listener = new CategoryEmailListener(mockEmail);

    listener.onCategoryCreated(new CategoryCreatedEvent(1L, "Tech", "tech"));

    verify(mockEmail).sendToAdmins(anyString());
}
```

---

### 4. Adding New Feature

**Trước - Muốn thêm SMS notification:**
```java
@Service
public class AdminCategoryService {
    // Thêm dependency mới
    private final SmsService smsService; // ← NEW

    // Update constructor
    public AdminCategoryService(..., SmsService smsService) {
        this.smsService = smsService;
    }

    @Transactional
    public void createCategory(...) {
        // ... existing code ...

        // Thêm code mới vào method đã có
        try {
            smsService.sendNotification(...); // ← NEW
        } catch (Exception e) {
            log.error("SMS failed", e);
        }
    }
}

// ❌ Phải sửa:
// - Service class
// - Constructor
// - Tests
// - Method createCategory
```

**Sau - Muốn thêm SMS notification:**
```java
// Tạo listener mới, KHÔNG SỬA code cũ!
@Component
public class CategorySmsListener {
    private final SmsService smsService;

    @Async
    @EventListener
    public void onCategoryCreated(CategoryCreatedEvent event) {
        smsService.sendNotification("New category: " + event.getName());
    }
}

// ✅ KHÔNG cần sửa:
// - CategoryCommandService (vẫn nguyên)
// - Tests cũ (vẫn pass)
// - Chỉ thêm listener mới
```

---

### 5. Performance Comparison

**Trước (Synchronous):**
```
User clicks "Create" → Waits 3200ms → Sees response

Timeline:
├─ DB Save:        150ms
├─ Cache:          100ms
├─ Email:         2000ms ⏰ (User waits!)
├─ Search Index:   500ms ⏰ (User waits!)
├─ Analytics:      200ms
├─ Notification:   300ms
└─ Audit:          100ms
   ──────────────────────
   Total:         3350ms

User experience: 😱 SLOW!
```

**Sau (Asynchronous with Events):**
```
User clicks "Create" → Waits 150ms → Sees response ✅

Main Thread:
├─ DB Save:        150ms
├─ Publish Event:   10ms
└─ Response:       ✅ DONE!
   ──────────────────────
   User sees:      160ms 🚀

Background (Parallel):
├─ Cache:          100ms (sync, must complete)
├─ Email:         2000ms (async, parallel)
├─ Search Index:   500ms (async, parallel)
├─ Analytics:      200ms (async, parallel)
├─ Notification:   300ms (async, parallel)
└─ Audit:          100ms (sync)

Background completes in: ~2000ms (parallel)

User experience: 😊 FAST!
Improvement: 3200ms → 160ms = 20x faster! 🚀
```

---

### 6. Code Maintainability

**Trước:**
```
AdminCategoryService.java (250 lines)
├─ createCategory()      [80 lines] 😱
├─ updateCategory()      [75 lines] 😱
├─ deleteCategory()      [60 lines] 😱
└─ Private helpers       [35 lines]

Complexity: HIGH
Readability: LOW
Maintainability: HARD
```

**Sau:**
```
CategoryCommandService.java (100 lines)
├─ createCategory()      [20 lines] 😊
├─ updateCategory()      [18 lines] 😊
└─ deleteCategory()      [15 lines] 😊

CategoryCacheListener.java (15 lines)
CategoryEmailListener.java (15 lines)
CategorySearchListener.java (15 lines)
...

Complexity: LOW
Readability: HIGH
Maintainability: EASY
```

---

## 🎯 Kết luận

### Metrics Comparison:

| Metric | Trước | Sau | Improvement |
|--------|-------|-----|-------------|
| **Dependencies** | 7 | 2 | -71% 😊 |
| **Lines of Code** | 80 | 20 | -75% 😊 |
| **Response Time** | 3200ms | 160ms | **20x faster** 🚀 |
| **Test Complexity** | Mock 7 | Mock 2 | -71% 😊 |
| **Add Feature** | Modify service | Add listener | **No changes** ✅ |
| **Error Impact** | May rollback all | Isolated | **Better** ✅ |
| **Maintainability** | Hard | Easy | **Much better** 😊 |

### Win-Win Situation:

✅ **Developer wins:**
- Cleaner code
- Easier to test
- Easier to extend
- Easier to maintain

✅ **User wins:**
- Faster response (20x!)
- Better experience
- More reliable

✅ **Business wins:**
- Faster development
- Less bugs
- More features
- Better quality

---

## 🚀 Bạn nên dùng Events!

**Đơn giản:** Thay vì service gọi 7 services khác → chỉ publish 1 event!

**Nhanh:** User chỉ đợi 160ms thay vì 3200ms!

**Dễ bảo trì:** Mỗi listener làm 1 việc, tách biệt rõ ràng!

**Dễ mở rộng:** Thêm tính năng = thêm listener mới!

---

**Bây giờ bạn đã thấy rõ sự khác biệt chưa?** 😊

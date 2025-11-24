# 🏗️ Kiến trúc sau khi cải tiến

## 📐 Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  admin/controllers/CategoryController.java           │   │
│  │  - Handles HTTP requests                             │   │
│  │  - Validates DTOs                                    │   │
│  │  - Returns responses                                 │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  admin/services/AdminCategoryService.java            │   │
│  │  - Orchestrates use cases                            │   │
│  │  - Transforms DTOs ↔ Entities                        │   │
│  │  - Delegates to Domain Services                      │   │
│  │  - Transaction boundaries                            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  ┌───────────────────────────┬──────────────────────────┐   │
│  │ CategoryQueryService      │ CategoryCommandService   │   │
│  │ - Read operations         │ - Write operations       │   │
│  │ - @Transactional(readOnly)│ - @Transactional         │   │
│  │ - findAll()              │ - createCategory()       │   │
│  │ - findById()             │ - updateCategory()       │   │
│  │ - searchByKeyword()      │ - deleteCategory()       │   │
│  │                          │ - Publishes events       │   │
│  └───────────────────────────┴──────────────────────────┘   │
│                            ⬇️                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Category.java (Rich Domain Model)                   │   │
│  │  - Business logic                                    │   │
│  │  + create(name, desc, slug)   [Factory]             │   │
│  │  + updateInfo(...)            [Business method]      │   │
│  │  + isValid()                  [Business rule]        │   │
│  │  - validateName()             [Domain validation]    │   │
│  │  - validateSlug()             [Domain validation]    │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Value Objects                                       │   │
│  │  - CategoryName.java                                │   │
│  │  - Slug.java                                        │   │
│  │  (Immutable, self-validating)                       │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Domain Events                                       │   │
│  │  - CategoryCreatedEvent                              │   │
│  │  - CategoryEventListener                             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ⬇️
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  CategoryRepository.java (Spring Data JPA)           │   │
│  │  - extends JpaRepository                             │   │
│  │  - Custom queries                                    │   │
│  └──────────────────────────────────────────────────────┘   │
│                            ⬇️                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Database (PostgreSQL/MySQL)                         │   │
│  │  - categories table                                  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow (Create Category)

```
1. User submits form
   ⬇️
2. CategoryController.create(@RequestBody FormCreateCategoryDTO dto)
   ⬇️
3. AdminCategoryService.createCategory(dto)
   ⬇️
4. CategoryCommandService.createCategory(name, desc, slug)
   ├─➡️ Check uniqueness (via QueryService)
   ├─➡️ Category.create(name, desc, slug)  [Factory + Validation]
   ├─➡️ categoryRepository.save(category)
   └─➡️ eventPublisher.publishEvent(CategoryCreatedEvent)
   ⬇️
5. CategoryEventListener.handleCategoryCreated(event)
   ├─➡️ Clear cache
   ├─➡️ Send notification
   └─➡️ Log analytics
   ⬇️
6. Return to controller → Response
```

---

## 🔍 Query Flow (Search Categories)

```
1. User searches
   ⬇️
2. CategoryController.search(@RequestParam keyword)
   ⬇️
3. AdminCategoryService.queryCategoriesByKeyword(keyword)
   ⬇️
4. CategoryQueryService.searchByKeyword(keyword)
   ⬇️
5. CategoryRepository.searchByKeyword(keyword)
   ⬇️
6. Database query
   ⬇️
7. Return List<Category>
   ⬇️
8. Map to List<ListCategoryDTO>
   ⬇️
9. Return to controller → Response
```

---

## 📦 Module Structure

```
module/category/
├── 📄 Category.java                    # Rich Domain Entity
│   ├── Fields: name, description, slug
│   ├── Factory: create()
│   ├── Business: updateInfo(), isValid(), hasName()
│   └── Validation: validateName(), validateSlug()
│
├── 📄 CategoryRepository.java          # Data Access Interface
│   ├── extends JpaRepository
│   ├── Custom queries
│   └── Spring Data JPA
│
├── 📄 CategoryQueryService.java        # Read Operations
│   ├── @Transactional(readOnly = true)
│   ├── findAll(), findById()
│   ├── existsByName(), existsBySlug()
│   └── searchByKeyword()
│
├── 📄 CategoryCommandService.java      # Write Operations
│   ├── @Transactional
│   ├── createCategory() → validates → publishes event
│   ├── updateCategory() → validates
│   └── deleteCategory()
│
├── 📄 CategoryService.java             # DEPRECATED (backward compat)
│   └── Delegates to Query/Command services
│
├── 📁 valueobject/
│   ├── 📄 CategoryName.java           # Value Object
│   │   ├── Immutable
│   │   ├── Self-validating
│   │   └── of(String value)
│   │
│   └── 📄 Slug.java                   # Value Object
│       ├── Immutable
│       ├── Self-validating
│       ├── of(String value)
│       └── fromText(String text)      # Auto-generate
│
└── 📁 event/
    ├── 📄 CategoryCreatedEvent.java    # Domain Event
    │   └── Published when category created
    │
    └── 📄 CategoryEventListener.java   # Event Handler
        └── Handles CategoryCreatedEvent
```

---

## 🎯 Dependency Flow

```
┌─────────────────────────────────────────────┐
│         AdminCategoryService                │
│         (Application Layer)                 │
└────────────┬────────────────────┬───────────┘
             │                    │
             ▼                    ▼
    ┌────────────────┐   ┌────────────────┐
    │ QueryService   │   │ CommandService │
    │ (Domain Layer) │   │ (Domain Layer) │
    └────────┬───────┘   └────────┬───────┘
             │                    │
             │      ┌─────────────┤
             │      │             │
             ▼      ▼             ▼
        ┌────────────────┐   ┌──────────────┐
        │  Repository    │   │  Category    │
        │(Infrastructure)│   │  (Domain)    │
        └────────────────┘   └──────────────┘
```

### Dependency Rules:
- ✅ Application Layer → Domain Layer
- ✅ Domain Layer → Infrastructure Layer (via interfaces)
- ❌ Infrastructure Layer → Domain Layer (NO!)
- ❌ Domain Layer → Application Layer (NO!)

---

## 🔐 Transaction Boundaries

```
@Service
@Transactional                          ← Write Transaction
public class CategoryCommandService {
    public Category createCategory(...) {
        // Entire method in ONE transaction
        // - Validation
        // - Save
        // - Event publishing
    }
}

@Service
@Transactional(readOnly = true)         ← Read-Only Transaction
public class CategoryQueryService {
    public List<Category> findAll() {
        // Read-only, can be optimized
    }
}
```

**Benefits:**
- 🚀 Read queries can use read replicas
- 🚀 Write transactions isolated
- 🚀 Better performance monitoring
- 🚀 Clear transaction scope

---

## 📡 Event Flow

```
CommandService.createCategory()
    │
    ├─➡️ Create entity
    ├─➡️ Save to DB
    └─➡️ Publish event ────────┐
                                │
                                ▼
                    ApplicationEventPublisher
                                │
                    ┌───────────┼───────────┐
                    ▼           ▼           ▼
            Listener 1    Listener 2    Listener N
            (Cache)       (Email)       (Analytics)
```

**Async vs Sync:**
```java
// Sync (default)
@EventListener
public void handle(CategoryCreatedEvent event) { }

// Async (if needed)
@Async
@EventListener
public void handleAsync(CategoryCreatedEvent event) { }
```

---

## 🧪 Testing Strategy

```
┌─────────────────────────────────────────────┐
│ Unit Tests                                  │
├─────────────────────────────────────────────┤
│ CategoryTest.java                           │
│ - Test factory methods                      │
│ - Test validation                           │
│ - Test business methods                     │
├─────────────────────────────────────────────┤
│ SlugTest.java                               │
│ - Test value object validation              │
│ - Test fromText() generation                │
├─────────────────────────────────────────────┤
│ CategoryCommandServiceTest.java             │
│ - Mock repository                           │
│ - Test business rules                       │
│ - Test event publishing                     │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ Integration Tests                           │
├─────────────────────────────────────────────┤
│ CategoryIntegrationTest.java                │
│ - Test full flow                            │
│ - Test transactions                         │
│ - Test event handling                       │
└─────────────────────────────────────────────┘
```

---

## 🎨 Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Factory Method** | `Category.create()` | Encapsulate creation logic |
| **Value Object** | `Slug`, `CategoryName` | Immutable, self-validating |
| **CQRS** | Query/Command Services | Separate read/write |
| **Repository** | `CategoryRepository` | Abstract data access |
| **Service Layer** | All Services | Business logic layer |
| **Domain Events** | `CategoryCreatedEvent` | Decouple side effects |
| **Observer** | Event Listeners | React to domain events |
| **Strategy** | Validation methods | Different validation rules |

---

## 📊 Comparison: Before vs After

### Architecture Complexity
```
Before: ████░░░░░░ 40%  (Simple but limited)
After:  ███████░░░ 70%  (More structure, better organized)
```

### Code Quality
```
Before: █████░░░░░ 50%  (Basic validation, mixed concerns)
After:  █████████░ 90%  (Rich domain, clear separation)
```

### Testability
```
Before: ████░░░░░░ 40%  (Hard to mock, coupled)
After:  █████████░ 90%  (Easy to test each layer)
```

### Maintainability
```
Before: █████░░░░░ 50%  (Logic scattered)
After:  ████████░░ 80%  (Clear responsibilities)
```

### Scalability
```
Before: ████░░░░░░ 40%  (Single service bottleneck)
After:  ████████░░ 80%  (Can scale read/write separately)
```

---

## 🚀 Performance Considerations

### Before:
```java
@Service
public class CategoryService {
    @Transactional  // Always write transaction!
    public List<Category> findAll() {
        return repository.findAll();
    }
}
```

### After:
```java
@Service
@Transactional(readOnly = true)  // Read-only optimization!
public class CategoryQueryService {
    public List<Category> findAll() {
        return repository.findAll();
    }
}
```

**Benefits:**
- 🚀 Database can optimize read-only queries
- 🚀 No unnecessary locking
- 🚀 Can use read replicas
- 🚀 Better connection pool usage

---

## 🔒 Security Benefits

### Clear Separation:
```java
// Admin can use both
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryService {
    private final CategoryQueryService queryService;      // Read
    private final CategoryCommandService commandService;  // Write
}

// Public API - only read
@RestController
public class PublicCategoryController {
    private final CategoryQueryService queryService;  // Read only!
    // No access to CommandService
}
```

**Security wins:**
- ✅ Granular permissions (read vs write)
- ✅ Harder to accidentally expose write operations
- ✅ Audit trail via Domain Events
- ✅ Validation at domain level (can't bypass)

---

**Visual Guide Version:** 1.0
**Created:** November 24, 2025

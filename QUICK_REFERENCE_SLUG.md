# 📖 Quick Reference: Slug & Validation

## 🚀 Quick Start

### **Generate Slug**
```java
import asterisk.sun.booking_tours.common.helper.SlugifyHelper;

String slug = SlugifyHelper.toSlug("Hello World");
// Result: "hello-world"
```

### **Validate Slug**
```java
SlugifyHelper.validateSlug("my-slug"); // Throws if invalid
```

### **Check if Valid**
```java
boolean valid = SlugifyHelper.isValidSlug("my-slug"); // true/false
```

---

## 📋 Validation Rules

### **Slug Rules**
- ✅ Min length: **3** characters
- ✅ Max length: **100** characters
- ✅ Pattern: `^[a-z0-9]+(?:-[a-z0-9]+)*$`
- ✅ Lowercase only
- ✅ Numbers allowed
- ✅ Hyphens as separators
- ❌ No uppercase
- ❌ No spaces
- ❌ No special characters
- ❌ Cannot start/end with hyphen

### **Name Rules**
- ✅ Min length: **3** characters
- ✅ Max length: **100** characters

### **Description Rules**
- ✅ Min length: **10** characters
- ✅ Max length: **500** characters

---

## 🎯 Usage in DTOs

```java
import asterisk.sun.booking_tours.common.constants.ValidationConstants;
import jakarta.validation.constraints.*;

@NotBlank(message = "Slug is required")
@Size(
    min = ValidationConstants.SLUG_MIN_LENGTH,
    max = ValidationConstants.SLUG_MAX_LENGTH,
    message = ValidationConstants.SLUG_LENGTH_MESSAGE
)
@Pattern(
    regexp = ValidationConstants.SLUG_PATTERN,
    message = ValidationConstants.SLUG_FORMAT_MESSAGE
)
private String slug;
```

---

## 🏗️ Usage in Entities

```java
import asterisk.sun.booking_tours.common.helper.SlugifyHelper;

private static void validateSlug(String slug) {
    SlugifyHelper.validateSlug(slug);
}
```

---

## 📦 Where to Use

### **SlugifyHelper** - Dùng ở:
- ✅ Category slugs
- ✅ Product slugs
- ✅ Tour slugs
- ✅ Blog post slugs
- ✅ Any URL-friendly identifiers

### **ValidationConstants** - Dùng ở:
- ✅ All DTOs (FormCreateXXX, FormEditXXX)
- ✅ All Entities
- ✅ Custom validators

---

## ✅ Valid Examples

```java
SlugifyHelper.isValidSlug("hello-world")        // ✅ true
SlugifyHelper.isValidSlug("category-123")       // ✅ true
SlugifyHelper.isValidSlug("my-awesome-tour")    // ✅ true
SlugifyHelper.isValidSlug("abc")                // ✅ true (min 3)
```

## ❌ Invalid Examples

```java
SlugifyHelper.isValidSlug("Hello World")        // ❌ false (uppercase, space)
SlugifyHelper.isValidSlug("test_slug")          // ❌ false (underscore)
SlugifyHelper.isValidSlug("-invalid-")          // ❌ false (starts/ends with -)
SlugifyHelper.isValidSlug("test--slug")         // ❌ false (double hyphen)
SlugifyHelper.isValidSlug("ab")                 // ❌ false (too short)
SlugifyHelper.isValidSlug("Slug!")              // ❌ false (special char)
```

---

## 🔧 Common Patterns

### **Pattern 1: Auto-generate slug from title**
```java
String title = "My Awesome Product";
String slug = SlugifyHelper.toSlug(title);
// Result: "my-awesome-product"
```

### **Pattern 2: Validate user input**
```java
public void setSlug(String userInput) {
    SlugifyHelper.validateSlug(userInput); // Throws if invalid
    this.slug = userInput;
}
```

### **Pattern 3: Safe validation with boolean**
```java
if (!SlugifyHelper.isValidSlug(userInput)) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Invalid slug format"
    );
}
```

### **Pattern 4: DTO validation (automatic)**
```java
@PostMapping("/create")
public String create(@Valid FormCreateDTO dto) {
    // @Valid automatically validates @Pattern and @Size
    // No need to manually call SlugifyHelper here
}
```

---

## 🎨 File Locations

```
src/main/java/asterisk/sun/booking_tours/
├── common/
│   ├── constants/
│   │   └── ValidationConstants.java    ← Constants here
│   └── helper/
│       └── SlugifyHelper.java          ← Slug logic here
├── admin/
│   └── dto/
│       └── category/
│           ├── FormCreateCategoryDTO.java  ← Use constants
│           └── FormEditCategoryDTO.java    ← Use constants
└── module/
    └── category/
        └── Category.java                   ← Use SlugifyHelper
```

---

## 💡 Tips

1. **Always use ValidationConstants** thay vì magic numbers
2. **SlugifyHelper.toSlug()** để auto-generate từ text
3. **SlugifyHelper.validateSlug()** khi cần throw exception
4. **SlugifyHelper.isValidSlug()** khi cần boolean check
5. **@Pattern** annotation trong DTO cho early validation

---

## 🐛 Troubleshooting

### Q: Slug validation fails với slug hợp lệ?
A: Check uppercase - slug phải là lowercase

### Q: Làm sao auto-generate slug từ Vietnamese text?
A: `SlugifyHelper.toSlug()` tự động handle:
```java
SlugifyHelper.toSlug("Đà Nẵng") // → "da-nang"
```

### Q: Validation constants ở đâu?
A: `asterisk.sun.booking_tours.common.constants.ValidationConstants`

### Q: Có thể change min/max length không?
A: Có! Update trong `ValidationConstants.java`

---

**Last Updated:** November 24, 2025

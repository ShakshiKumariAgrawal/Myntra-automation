# 🛒 Myntra Scraper using Playwright & Cucumber BDD (Java)

Automated web scraper to extract discounted products (e.g., T-shirts, shirts) from [Myntra](https://www.myntra.com), built using **Playwright** and **Cucumber BDD** in **Java**.

This scraper:
- ✅ Navigates to Myntra
- 🎯 Applies gender, product type & brand filters
- 💸 Collects discounted items across multiple pages
- 📉 Sorts them by discount (highest first)
- 📤 Prints final results in the console

---

## 🚀 Features

✨ Select:
- Gender (Men/Women/Kids)
- Product category (T-Shirts, Shirts, etc.)
- Specific brand (e.g., H&M, Nike)

🔍 Filters:
- Applies brand filters dynamically

📦 Collects:
- Discounted items only (skips full-price)

📊 Sorts:
- Products by **highest discount first**

🖨️ Prints:
- Original price
- Discount %
- Final price
- Product link

---

## 🧰 Tech Stack

| Tool        | Purpose                    |
|-------------|-----------------------------|
| ☕ Java      | Core programming language   |
| 🧪 Cucumber | BDD Framework (step-defs)   |
| 🎭 Playwright | Browser automation         |
| 🧪 JUnit     | Assertions in tests         |
| 🧾 Maven/Gradle | Dependency Management (Optional) |

---

## 📂 Folder Structure

```bash
project-root/
│
├── features/             # Cucumber feature files
│
├── steps/                # Step definitions (Java)
│   └── Myntra.java       # Main scraper logic
│
├── pom.xml / build.gradle  # Project build file
└── README.md             # You're reading this!

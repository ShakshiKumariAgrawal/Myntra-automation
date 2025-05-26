// package steps;

// import com.microsoft.playwright.*;
// import io.cucumber.java.en.*;
// import java.util.*;

// import org.junit.Assert;

// import static org.junit.Assert.*;

// public class Myntra {
//     private Browser browser;
//     private Page page;
//     private String selectedBrand;
//     private String selectedCategory;
//     private String selectedType;
//     private final List<Map<String, String>> discountedProducts = new ArrayList<>();

//     @Given("I open the Myntra website at {string}")
//     public void openWebPage(String url) {
//         // Validate the URL
//         if (!url.contains("myntra.com")) {
//             Assert.fail("Invalid Myntra URL.");
//         }

//         // Launch Chromium browser with Playwright
//         Playwright playwright = Playwright.create();
//         BrowserType chrome = playwright.chromium();
//         browser = chrome.launch(new BrowserType.LaunchOptions().setHeadless(false));
//         page = browser.newPage();
//         page.navigate(url);
//     }

//     @When("I select {string} as the gender")
//     public void chooseGender(String gender) {
//         // Ensure valid gender category
//         if (!Arrays.asList("Men", "Women", "Kids").contains(gender)) {
//             Assert.fail("Unsupported gender selected.");
//         }

//         selectedCategory = gender;

//         // Hover over the gender section on the homepage
//         page.hover("text=" + gender);
//     }

//     @And("I choose {string} as the product category")
//     public void chooseProductType(String type) {
//         selectedType = type;
//         String categoryPath = selectedCategory.toLowerCase();

//         // Navigate to appropriate category page
//         if (categoryPath.equals("men") || categoryPath.equals("kids")) {
//             page.navigate("https://www.myntra.com/" + categoryPath + "-" + type.toLowerCase());
//         } else if (categoryPath.equals("women")) {
//             page.navigate("https://www.myntra.com/myntra-fashion-store?f=Categories%3A" + type
//                     + "%3A%3AGender%3Amen%20women%2C" + categoryPath);
//         }

//         // Wait for the page to load
//         page.waitForLoadState();
//     }

//     @And("I apply the brand filter for {string}")
//     public void applyBrandFilter(String brand) {
//         selectedBrand = brand;

//         try {
//             // Handle different filter input locations for different categories
//             if (selectedCategory.equalsIgnoreCase("Men")) {
//                 page.click(".filter-search-iconSearch");
//                 page.fill(".filter-search-inputBox", brand);
//                 page.press(".filter-search-inputBox", "Enter");
//                 page.click("label:has(input[type='checkbox'][value='" + brand + "'])");
//             } else {
//                 page.click(".brand-container .filter-search-iconSearch");
//                 page.fill(".brand-container .filter-search-inputBox", brand);
//                 page.press(".brand-container .filter-search-inputBox", "Enter");
//                 page.click("label:has(input[type='checkbox'][value='" + brand + "'])");
//             }
//         } catch (Exception e) {
//             // Fail the test if filtering fails
//             fail("Brand filtering failed for: " + brand);
//         }
//     }

//     @Then("I collect the products that are on discount from maximum {int} pages")
//     public void collectDiscountedProducts(int maxPages) {
//         for (int i = 0; i < maxPages; i++) {
//             // Extract discounted products from current page
//             extractCurrentPageProducts();

//             // Locate the next button for pagination
//             Locator nextBtn = page.locator(".pagination-next");

//             // Stop if next button is disabled or missing
//             if (nextBtn.count() == 0 || nextBtn.getAttribute("class").contains("pagination-disabled")) {
//                 if (i < maxPages - 1) fail("Less pages available than expected.");
//                 break;
//             }

//             // Go to next page and wait for it to load
//             nextBtn.click();
//             page.waitForLoadState();
//         }
//     }

//     private void extractCurrentPageProducts() {
//         // Wait for the product grid to load
//         page.waitForSelector(".product-base");

//         Locator productList = page.locator(".product-base");
//         int total = productList.count();

//         for (int i = 0; i < total; i++) {
//             Locator item = productList.nth(i);
//             Locator originalPriceTag = item.locator(".product-strike");

//             // Check if original price exists to confirm a discount
//             if (originalPriceTag.count() > 0 && originalPriceTag.isVisible()) {
//                 try {
//                     // Extract price and discount information
//                     String original = originalPriceTag.textContent().trim();
//                     String discounted = item.locator(".product-discountedPrice").textContent().trim();
//                     String discount = item.locator(".product-discountPercentage").textContent().trim();
//                     String link = "https://www.myntra.com" + item.locator("a").getAttribute("href");

//                     int discVal = parseDiscount(discount);

//                     // If discount is shown in rupees, convert to percentage
//                     if (discount.charAt(1) == 'R') {
//                         discVal = (discVal * 100) / parseDiscount(original);
//                         discount = "[" + discVal + "%]";
//                     }

//                     // Store product info in a map
//                     Map<String, String> product = new HashMap<>();
//                     product.put("originalPrice", original);
//                     product.put("discountedPrice", discounted);
//                     product.put("discount", discount);
//                     product.put("link", link);

//                     discountedProducts.add(product);
//                 } catch (Exception ignored) {
//                     // Ignore errors for individual products
//                 }
//             }
//         }
//     }

//     private int parseDiscount(String text) {
//         // Extract numeric value from discount string
//         return Integer.parseInt(text.replaceAll("[^0-9]", ""));
//     }

//     @Then("I sort those products based on discount in descending order")
//     public void sortByDiscount() {
//         // Sort the product list by discount value (highest first)
//         discountedProducts.sort((a, b) -> parseDiscount(b.get("discount")) - parseDiscount(a.get("discount")));
//     }

//     @Then("I print the final product list to the console")
//     public void printSortedResults() {
//         // Print all the products in console
//         if (discountedProducts.isEmpty()) {
//             System.out.println("No discounted " + selectedType + " found for brand: " + selectedBrand);
//         } else {
//             System.out.println("Discounted " + selectedType + " for " + selectedBrand + " in " + selectedCategory + " category:");
//             for (Map<String, String> product : discountedProducts) {
//                 System.out.println("Original Price: " + product.get("originalPrice"));
//                 System.out.println("Discount: " + product.get("discount"));
//                 System.out.println("After discount: " + product.get("discountedPrice"));
//                 System.out.println("Link: " + product.get("link"));
//                 System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
//             }
//         }

//         // Close the browser session
//         browser.close();
//     }
// }

package steps;

import com.microsoft.playwright.*;
import io.cucumber.java.en.*;
import java.util.*;
import org.junit.Assert;

import static org.junit.Assert.*;

public class Myntra {
    private Browser browser;
    private Page page;
    private String selectedBrand;
    private String selectedCategory;
    private String selectedType;
    private boolean isDirectCategoryURL = false;

    private final List<Map<String, String>> discountedProducts = new ArrayList<>();

    @Given("I open the Myntra website at {string}")
    public void openWebPage(String url) {
        if (!url.contains("myntra.com")) {
            fail("Invalid Myntra URL.");
        }

        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate(url);

        // Determine if this is a direct category URL
        isDirectCategoryURL = !url.equalsIgnoreCase("https://www.myntra.com");
    }

    @When("I select {string} as the gender")
    public void chooseGender(String gender) {
        if (!Arrays.asList("Men", "Women", "Kids").contains(gender)) {
            fail("Unsupported gender selected.");
        }

        selectedCategory = gender;

        if (!isDirectCategoryURL) {
            page.hover("text=" + gender);
        }
    }
    @And("I choose {string} as the product category")
    public void chooseProductType(String type) {
        selectedType = type;
        String categoryPath = selectedCategory.toLowerCase();

        try {
            // Hover over the category
            page.hover("text=" + selectedCategory);
            page.waitForTimeout(2000); // Wait for dropdown to appear

            // Click on the product type link (e.g., "Saree")
            page.click("a:has-text('" + type + "')");

            // Wait for the page to load
            page.waitForLoadState();

        } catch (Exception e) {
            Assert.fail("Failed to select category: " + type + " under " + selectedCategory);
        }
    }

    @And("I apply the brand filter for {string}")
    public void applyBrandFilter(String brand) {
        selectedBrand = brand;

        try {
            if (selectedCategory.equalsIgnoreCase("Men")) {
                page.click(".filter-search-iconSearch");
                page.fill(".filter-search-inputBox", brand);
                page.press(".filter-search-inputBox", "Enter");
                page.click("label:has(input[type='checkbox'][value='" + brand + "'])");
            } else {
                page.click(".brand-container .filter-search-iconSearch");
                page.fill(".brand-container .filter-search-inputBox", brand);
                page.press(".brand-container .filter-search-inputBox", "Enter");
                page.click("label:has(input[type='checkbox'][value='" + brand + "'])");
            }

            page.waitForTimeout(2000); // Wait for products to reload after filter
        } catch (Exception e) {
            fail("Brand filtering failed for: " + brand);
        }
    }

    @Then("I collect the products that are on discount from maximum {int} pages")
    public void collectDiscountedProducts(int maxPages) {
        for (int i = 0; i < maxPages; i++) {
            extractCurrentPageProducts();

            Locator nextBtn = page.locator(".pagination-next");

            if (nextBtn.count() == 0 || nextBtn.getAttribute("class").contains("pagination-disabled")) {
                System.out.println("Reached last page. Only " + (i + 1) + " pages available.");
                break;
            }

            nextBtn.click();
            page.waitForLoadState();
        }
    }

    private void extractCurrentPageProducts() {
        page.waitForSelector(".product-base");

        Locator productList = page.locator(".product-base");
        int total = productList.count();

        for (int i = 0; i < total; i++) {
            Locator item = productList.nth(i);
            Locator originalPriceTag = item.locator(".product-strike");

            if (originalPriceTag.count() > 0 && originalPriceTag.isVisible()) {
                try {
                    String original = originalPriceTag.textContent().trim();
                    String discounted = item.locator(".product-discountedPrice").textContent().trim();
                    String discount = item.locator(".product-discountPercentage").textContent().trim();
                    String link = "https://www.myntra.com" + item.locator("a").getAttribute("href");

                    int discVal = parseDiscount(discount);

                    if (discount.charAt(1) == 'R') {
                        discVal = (discVal * 100) / parseDiscount(original);
                        discount = "[" + discVal + "%]";
                    }

                    Map<String, String> product = new HashMap<>();
                    product.put("originalPrice", original);
                    product.put("discountedPrice", discounted);
                    product.put("discount", discount);
                    product.put("link", link);

                    discountedProducts.add(product);
                } catch (Exception ignored) {}
            }
        }
    }

    private int parseDiscount(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    @Then("I sort those products based on discount in descending order")
    public void sortByDiscount() {
        discountedProducts.sort((a, b) -> parseDiscount(b.get("discount")) - parseDiscount(a.get("discount")));
    }

    @Then("I print the final product list to the console")
    public void printSortedResults() {
        if (discountedProducts.isEmpty()) {
            System.out.println("No discounted " + selectedType + " found for brand: " + selectedBrand);
        } else {
            System.out.println("Discounted " + selectedType + " for " + selectedBrand + " in " + selectedCategory + " category:");
            for (Map<String, String> product : discountedProducts) {
                System.out.println("Original Price: " + product.get("originalPrice"));
                System.out.println("Discount: " + product.get("discount"));
                System.out.println("After discount: " + product.get("discountedPrice"));
                System.out.println("Link: " + product.get("link"));
                System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
            }
        }

        browser.close();
    }
}

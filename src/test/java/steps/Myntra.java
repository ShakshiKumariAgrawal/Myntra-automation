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

    // List to store discounted product data
    private final List<Map<String, String>> discountedProducts = new ArrayList<>();

    
    // Opens the given Myntra URL in a browser.
    // It initializes Playwright, launches Chromium browser, and navigates to the given URL.
    // Also determines if the URL is directly targeting a category page or homepage.
    
    @Given("I open the Myntra website at {string}")
    public void openWebPage(String url) {
        if (!url.contains("myntra.com")) {
            fail("Invalid Myntra URL.");
        }

        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate(url);

        // Check if the URL is already a direct category page
        isDirectCategoryURL = !url.equalsIgnoreCase("https://www.myntra.com");
    }

    
    // Select gender from main navigation (Men, Women, Kids).
    // Hovers on the gender category if not a direct URL navigation.
    
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

    
    // Choose product type like "Shirts", "Saree", "T-Shirts" etc.
    // It tries to click on the product type in the dropdown menu under the selected category.
    // Fails if not found or clickable.
    
    @And("I choose {string} as the product category")
    public void chooseProductType(String type) {
        selectedType = type;
        String categoryPath = selectedCategory.toLowerCase();

        try {
            page.hover("text=" + selectedCategory);
            page.waitForTimeout(2000); // Wait for dropdown to load

            // Use strict matching using XPath with normalize-space() for exact text match
            Locator exactMatch = page.locator("//a[normalize-space(text())='" + type + "']");
            if (exactMatch.count() > 0) {
                exactMatch.first().click();
            } else {
                Assert.fail("Exact product type '" + type + "' not found in dropdown.");
            }

            page.waitForLoadState(); // Wait for full page load

        } catch (Exception e) {
            Assert.fail("Failed to select category: " + type + " under " + selectedCategory);
        }
}


    
    // Applies brand filter from the filter section.
    // It handles brand filtering UI for both Men and Women categories.
    
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

            page.waitForTimeout(2000); // Let products reload after applying brand filter
        } catch (Exception e) {
            fail("Brand filtering failed for: " + brand);
        }
    }

    
    // Iterates over product listing pages (max given) and collects discounted products.
    
    @Then("I collect the products that are on discount from maximum {int} pages")
    public void collectDiscountedProducts(int maxPages) {
        for (int i = 0; i < maxPages; i++) {
            extractCurrentPageProducts(); // Scrape current page

            // Look for Next button
            Locator nextBtn = page.locator(".pagination-next");

            // Stop if last page reached
            if (nextBtn.count() == 0 || nextBtn.getAttribute("class").contains("pagination-disabled")) {
                System.out.println("Reached last page. Only " + (i + 1) + " pages available.");
                break;
            }

            nextBtn.click(); // Go to next page
            page.waitForLoadState();
        }
    }

    
    // Scrapes all products on the current page and filters those with a visible discount.
    // Adds discounted items to the `discountedProducts` list.
    
    private void extractCurrentPageProducts() {
        page.waitForSelector(".product-base"); // Wait for product cards to load

        Locator productList = page.locator(".product-base");
        int total = productList.count();

        for (int i = 0; i < total; i++) {
            Locator item = productList.nth(i);
            Locator originalPriceTag = item.locator(".product-strike");

            // Only consider products that have an original price (i.e., discounted)
            if (originalPriceTag.count() > 0 && originalPriceTag.isVisible()) {
                try {
                    String original = originalPriceTag.textContent().trim();
                    String discounted = item.locator(".product-discountedPrice").textContent().trim();
                    String discount = item.locator(".product-discountPercentage").textContent().trim();
                    String link = "https://www.myntra.com" + item.locator("a").getAttribute("href");

                    int discVal = parseDiscount(discount);

                    // If discount isn't in percentage, calculate it manually
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

                } catch (Exception ignored) {
                }
            }
        }
    }

    
    // Utility method: Extracts numeric value from discount string (like "20% OFF" -> 20).
    
    private int parseDiscount(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    
    // Sorts the `discountedProducts` list in descending order of discount.
     
    @Then("I sort those products based on discount in descending order")
    public void sortByDiscount() {
        discountedProducts.sort((a, b) -> parseDiscount(b.get("discount")) - parseDiscount(a.get("discount")));
    }

    
    // Prints all the collected and sorted discounted products in a readable format.
    // Closes the browser after printing.
     
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

        browser.close(); // Close browser session
    }
}

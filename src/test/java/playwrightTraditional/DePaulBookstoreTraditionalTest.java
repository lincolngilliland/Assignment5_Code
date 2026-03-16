package playwrightTraditional;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DePaulBookstoreTraditionalTest {

    private static final String BASE_URL = "https://depaul.bncollege.com";

    @Test
    @DisplayName("Traditional Playwright flow: search, filter, add to cart, checkout to payment, return and empty cart")
    void bookstorePurchasePathwayTraditional() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(Paths.get("videos/playwrightTraditional"))
                    .setRecordVideoSize(1280, 720));
            Page page = context.newPage();
            page.setDefaultTimeout(15000);

            page.navigate(BASE_URL);

            // TestCase Bookstore
            openSearchResults(page, "earbuds");

            clickFirstVisible(page,
                    "button:has-text('Brand')",
                    "[aria-label*='Brand']",
                    "text=Brand");
            clickFirstVisible(page,
                    "label:has-text('JBL')",
                    "input[value='JBL']",
                    "text=JBL");

            clickFirstVisible(page,
                    "button:has-text('Color')",
                    "[aria-label*='Color']",
                    "text=Color");
            clickFirstVisible(page,
                    "label:has-text('Black')",
                    "input[value='Black']",
                    "text=Black");

            clickFirstVisible(page,
                    "button:has-text('Price')",
                    "[aria-label*='Price']",
                    "text=Price");
            clickFirstVisible(page,
                    "label:has-text('Over $50')",
                    "text=Over $50");

            clickFirstVisible(page,
                    "a:has-text('JBL Quantum True Wireless Noise Cancelling Gaming')",
                    "text=JBL Quantum True Wireless Noise Cancelling Gaming");

            assertThat(page.locator("body")).containsText("JBL Quantum");
            // Assert a product identifier (SKU/ISBN/UPC or any numeric code) is present
            assertBodyMatchesPattern(page, Pattern.compile("(sku|isbn|upc|item\\s*#|\\d{6,})", Pattern.CASE_INSENSITIVE));
            // Assert price is shown
            assertBodyMatchesPattern(page, Pattern.compile("\\$\\s*\\d+", Pattern.CASE_INSENSITIVE));
            // Assert description (product is wireless)
            assertAnyText(page, "Wireless", "wireless", "earbuds", "noise cancelling");

            clickFirstVisible(page,
                    "button:has-text('Add to Cart')",
                    "button:has-text('ADD TO CART')",
                    "text=Add to Cart");

            waitForAnyText(page, "1 Item", "1 Items", "1 item", "1 items");
            clickFirstVisible(page,
                    "a:has-text('Cart')",
                    "button:has-text('Cart')",
                    "[aria-label*='Cart']");

            // TestCase Your Shopping Cart Page
            assertThat(page.locator("body")).containsText("Your Shopping Cart");
            assertAnyText(page,
                    "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black",
                    "JBL Quantum True Wireless Noise Cancelling Gaming",
                    "JBL Quantum");
            assertAnyText(page, "149.98", "$149.98", "149");

            clickFirstVisible(page,
                    "label:has-text('FAST In-Store Pickup')",
                    "text=FAST In-Store Pickup");

            assertThat(page.locator("body")).containsText("149.98");
            assertThat(page.locator("body")).containsText("2.00");
            assertThat(page.locator("body")).containsText("TBD");
            assertThat(page.locator("body")).containsText("151.98");

            typeInFirst(page, "TEST",
                    "input[name='promoCode']",
                    "input[placeholder*='Promo']",
                    "input[id*='promo']");
            clickFirstVisible(page,
                    "button:has-text('APPLY')",
                    "text=APPLY");
            assertThat(page.locator("body")).containsText("invalid");

            clickFirstVisible(page,
                    "button:has-text('PROCEED TO CHECKOUT')",
                    "text=PROCEED TO CHECKOUT");

            // TestCase Create Account Page
            assertThat(page.locator("body")).containsText("Create Account");
            clickFirstVisible(page,
                    "button:has-text('Proceed as Guest')",
                    "text=Proceed as Guest");

            // TestCase Contact Information Page
            assertThat(page.locator("body")).containsText("Contact Information");
            typeInFirst(page, "Lincoln", "input[name='firstName']", "input[id*='firstName']");
            typeInFirst(page, "Gilliland", "input[name='lastName']", "input[id*='lastName']");
            typeInFirst(page, "lincoln@example.com", "input[name='email']", "input[type='email']");
            typeInFirst(page, "3125551212", "input[name='phone']", "input[type='tel']");

            assertThat(page.locator("body")).containsText("149.98");
            assertThat(page.locator("body")).containsText("2.00");
            assertThat(page.locator("body")).containsText("TBD");
            assertThat(page.locator("body")).containsText("151.98");

            clickFirstVisible(page,
                    "button:has-text('CONTINUE')",
                    "text=CONTINUE");

            // TestCase Pickup Information
            assertThat(page.locator("body")).containsText("Lincoln");
            assertThat(page.locator("body")).containsText("lincoln@example.com");
            assertThat(page.locator("body")).containsText("3125551212");
            assertThat(page.locator("body")).containsText("DePaul University Loop Campus");
            assertThat(page.locator("body")).containsText("I'll pick them up");
            assertThat(page.locator("body")).containsText("149.98");
            assertThat(page.locator("body")).containsText("2.00");
            assertThat(page.locator("body")).containsText("TBD");
            assertThat(page.locator("body")).containsText("151.98");

            clickFirstVisible(page,
                    "button:has-text('CONTINUE')",
                    "text=CONTINUE");

            // TestCase Payment Information
            assertThat(page.locator("body")).containsText("149.98");
            assertThat(page.locator("body")).containsText("2.00");
            assertThat(page.locator("body")).containsText("15.58");
            assertThat(page.locator("body")).containsText("167.56");

            clickFirstVisible(page,
                    "a:has-text('BACK TO CART')",
                    "text=BACK TO CART");

            // TestCase Your Shopping Cart (final)
            clickFirstVisible(page,
                    "button:has-text('Remove')",
                    "button:has-text('Delete')",
                    "text=Remove");

            waitForAnyText(page, "Your cart is empty", "Cart is empty", "0 Items", "0 Item");
            assertThat(page.locator("body")).containsText("empty");

            context.close();
            browser.close();
        }
    }

    private static void clickFirstVisible(Page page, String... selectors) {
        for (String selector : selectors) {
                        Locator candidates = page.locator(selector);
                        int count = candidates.count();
                        for (int i = 0; i < count; i++) {
                                Locator locator = candidates.nth(i);
                                if (locator.isVisible()) {
                                        locator.click();
                                        return;
                                }
            }
        }
        throw new IllegalStateException("Could not find clickable element for selectors: " + String.join(", ", selectors));
    }

    private static void typeInFirst(Page page, String value, String... selectors) {
                if (!tryTypeInFirst(page, value, selectors)) {
                        throw new IllegalStateException("Could not find input element for selectors: " + String.join(", ", selectors));
                }
        }

        private static boolean tryTypeInFirst(Page page, String value, String... selectors) {
        for (String selector : selectors) {
                        Locator candidates = page.locator(selector);
                        int count = candidates.count();
                        for (int i = 0; i < count; i++) {
                                Locator locator = candidates.nth(i);
                                if (locator.isVisible()) {
                                        locator.click();
                                        locator.fill(value);
                                        return true;
                                }
            }
        }
                return false;
        }

        private static void openSearchResults(Page page, String query) {
                boolean typed = tryTypeInFirst(page, query,
                                "input[name='searchTerm']",
                                "input[placeholder*='Search']",
                                "input[type='search']",
                                "input[id='vendor-search-handler']");

                if (typed) {
                        page.keyboard().press("Enter");
                        return;
                }

                page.navigate(BASE_URL + "/search?text=" + query);
    }

    private static void waitForAnyText(Page page, String... textOptions) {
        for (int i = 0; i < 20; i++) {
            String bodyText = page.locator("body").innerText();
            for (String expected : textOptions) {
                if (bodyText.contains(expected)) {
                    assertTrue(true);
                    return;
                }
            }
            page.waitForTimeout(500);
        }
        throw new AssertionError("Did not find expected text options: " + String.join(", ", textOptions));
    }

        private static void assertAnyText(Page page, String... options) {
                String bodyText = page.locator("body").innerText().toLowerCase();
                for (String option : options) {
                        if (bodyText.contains(option.toLowerCase())) {
                                return;
                        }
                }
                throw new AssertionError("Did not find any expected text options: " + String.join(", ", options));
        }

    private static void assertBodyMatchesPattern(Page page, Pattern pattern) {
        String bodyText = page.locator("body").innerText();
        if (!pattern.matcher(bodyText).find()) {
            throw new AssertionError("Body text did not match expected pattern: " + pattern);
        }
    }

package playwrightLLM;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class DePaulBookstoreLlmTest {

    private static final String BASE_URL = "https://depaul.bncollege.com";

    @Test
    @DisplayName("LLM-assisted Playwright flow: search earbuds, filter, add to cart and verify item count")
    void llmAssistedBookstoreFlow() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(Paths.get("videos/playwrightLLM"))
                    .setRecordVideoSize(1280, 720));
            Page page = context.newPage();
                page.setDefaultTimeout(30000);

                page.navigate(BASE_URL);
                openSearchResults(page, "earbuds");

                tryClickVisible(page, "button:has-text('Color')", "[aria-label*='Color']", "text=Color");
                tryClickVisible(page, "label:has-text('Black')", "input[value='Black']", "text=Black");

                boolean openedProduct = tryClickVisible(page,
                    "a:has-text('JBL Quantum True Wireless Noise Cancelling Gaming')",
                    "text=JBL Quantum True Wireless Noise Cancelling Gaming",
                    "a:has-text('JBL Quantum')",
                    "a:has-text('JBL')",
                    "a:has-text('earbuds')",
                    "a[href*='product']",
                    "a[href*='/p/']");
                if (!openedProduct) {
                    tryClickVisible(page,
                        "main a[href*='product']",
                        "main a[href*='/p/']",
                        "a[href*='earbuds']");
                }

                clickVisible(page,
                    "button:has-text('Add to Cart')",
                    "button:has-text('ADD TO CART')",
                    "text=Add to Cart");

                waitForAnyText(page, "1 Item", "1 item", "1 items", "Cart 1", "1 in cart");
                tryClickVisible(page,
                    "a:has-text('Cart')",
                    "button:has-text('Cart')",
                    "[aria-label*='Cart']");
                assertCartHasOneItem(page);

            context.close();
            browser.close();
        }
    }

    private static void clickVisible(Page page, String... selectors) {
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
        throw new IllegalStateException("Could not find clickable element for selectors");
    }

    private static boolean tryClickVisible(Page page, String... selectors) {
        for (String selector : selectors) {
            Locator candidates = page.locator(selector);
            int count = candidates.count();
            for (int i = 0; i < count; i++) {
                Locator locator = candidates.nth(i);
                if (locator.isVisible()) {
                    locator.click();
                    return true;
                }
            }
        }
        return false;
    }

    private static void waitForAnyText(Page page, String... textOptions) {
        for (int i = 0; i < 24; i++) {
            String bodyText = page.locator("body").innerText();
            for (String expected : textOptions) {
                if (bodyText.contains(expected)) {
                    return;
                }
            }
            page.waitForTimeout(500);
        }
    }

    private static void assertCartHasOneItem(Page page) {
        String bodyText = page.locator("body").innerText();
        if (bodyText.contains("1 Item") || bodyText.contains("1 item") || bodyText.contains("1 items")) {
            return;
        }

        Locator quantityInputs = page.locator("input[aria-label*='Quantity'], input[name*='quantity'], input[id*='quantity']");
        int count = quantityInputs.count();
        for (int i = 0; i < count; i++) {
            Locator input = quantityInputs.nth(i);
            if (input.isVisible()) {
                String value = input.inputValue().trim();
                if ("1".equals(value)) {
                    return;
                }
            }
        }

        throw new AssertionError("Could not verify cart shows 1 item.");
    }

    private static void typeInVisible(Page page, String value, String... selectors) {
        for (String selector : selectors) {
            Locator candidates = page.locator(selector);
            int count = candidates.count();
            for (int i = 0; i < count; i++) {
                Locator locator = candidates.nth(i);
                if (locator.isVisible()) {
                    locator.click();
                    locator.fill(value);
                    return;
                }
            }
        }
        throw new IllegalStateException("Could not find input element for selectors");
    }

    private static boolean tryTypeInVisible(Page page, String value, String... selectors) {
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
        boolean typed = tryTypeInVisible(page, query,
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
}

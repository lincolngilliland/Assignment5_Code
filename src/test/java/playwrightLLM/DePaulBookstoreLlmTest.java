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

            page.navigate("https://depaul.bncollege.com");

                typeInVisible(page, "earbuds",
                    "input[name='searchTerm']",
                    "input[placeholder*='Search']",
                    "input[type='search']");
            page.keyboard().press("Enter");

                clickVisible(page, "button:has-text('Brand')", "[aria-label*='Brand']", "text=Brand");
                clickVisible(page, "label:has-text('JBL')", "input[value='JBL']", "text=JBL");

                clickVisible(page, "button:has-text('Color')", "[aria-label*='Color']", "text=Color");
                clickVisible(page, "label:has-text('Black')", "input[value='Black']", "text=Black");

                clickVisible(page, "button:has-text('Price')", "[aria-label*='Price']", "text=Price");
                clickVisible(page, "label:has-text('Over $50')", "text=Over $50");

                clickVisible(page,
                    "a:has-text('JBL Quantum True Wireless Noise Cancelling Gaming')",
                    "text=JBL Quantum True Wireless Noise Cancelling Gaming");
                clickVisible(page, "button:has-text('Add to Cart')", "text=Add to Cart");

            page.waitForTimeout(1500);
            assertThat(page.locator("body")).containsText("1 Item");

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
}

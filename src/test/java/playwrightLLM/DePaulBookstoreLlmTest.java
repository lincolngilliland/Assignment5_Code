package playwrightLLM;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
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

            page.navigate("https://depaul.bncollege.com");

            page.locator("input[name='searchTerm'], input[placeholder*='Search'], input[type='search']").first().fill("earbuds");
            page.keyboard().press("Enter");

            page.locator("button:has-text('Brand'), [aria-label*='Brand'], text=Brand").first().click();
            page.locator("label:has-text('JBL'), input[value='JBL'], text=JBL").first().click();

            page.locator("button:has-text('Color'), [aria-label*='Color'], text=Color").first().click();
            page.locator("label:has-text('Black'), input[value='Black'], text=Black").first().click();

            page.locator("button:has-text('Price'), [aria-label*='Price'], text=Price").first().click();
            page.locator("label:has-text('Over $50'), text=Over $50").first().click();

            page.locator("a:has-text('JBL Quantum True Wireless Noise Cancelling Gaming'), text=JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
            page.locator("button:has-text('Add to Cart'), text=Add to Cart").first().click();

            page.waitForTimeout(1500);
            assertThat(page.locator("body")).containsText("1 Item");

            context.close();
            browser.close();
        }
    }
}

package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CodegenBookstoreTest {
  public static void main(String[] args) {
    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(true));
      BrowserContext context = browser.newContext(new Browser.NewContextOptions().setRecordVideoDir(Paths.get("videos/codegen")).setRecordVideoSize(1280, 720));
      Page page = context.newPage();
      page.navigate("https://depaul.bncollege.com/");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("earbuds");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).press("Enter");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
      page.locator(".facet__list.js-facet-list.js-facet-top-values > li:nth-child(3) > form > label > .facet__list__label > .facet__list__mark > .facet-unchecked > svg").first().click();
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
      page.locator("#facet-Color > .facet__values > .facet__list > li > form > label > .facet__list__label > .facet__list__mark > .facet-unchecked > svg").first().click();
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
      page.locator("#facet-price > .facet__values > .facet__list > li > form > label > .facet__list__label > .facet__list__mark > .facet-unchecked > svg").click();
      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
      assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING)).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
      assertThat(page.getByLabel("main")).containsText("668972707");
      assertThat(page.getByLabel("main")).containsText("Adaptive noise cancelling allows awareness of environment when gaming on the go. Light weight, durable, water resist. USB-C dongle for low latency connection < than 30ms.");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).click();
      assertThat(page.locator("#headerDesktopView")).containsText("1 items");
      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
      assertThat(page.getByLabel("main")).containsText("Your Shopping Cart");
      assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
      assertThat(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Quantity, edit and press"))).hasValue("1");
      assertContainsCurrency(page.getByLabel("main"));
      page.locator(".sub-check").first().click();
      assertContainsCurrency(page.getByLabel("main"));
      assertThat(page.getByLabel("main")).containsText("TBD");
      assertContainsCurrency(page.getByLabel("main"));
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).fill("TEST");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Promo Code")).click();
      assertThat(page.locator("#js-voucher-result")).containsText("The coupon code entered is not valid.");
      page.getByLabel("Proceed To Checkout").click();
      assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Create Account"))).isVisible();
      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
      assertThat(page.getByLabel("main")).containsText("Contact Information");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).fill("John");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).fill("Doe");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).fill("bsemail@gmail.com");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).fill("8165559014");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
      assertContainsCurrency(page.getByLabel("main"));
      assertThat(page.getByLabel("main")).containsText("TBD");
      assertContainsCurrency(page.getByLabel("main"));
      assertThat(page.locator("#bnedPickupPersonForm")).containsText("I'll pick them up");
      page.locator(".sub-check").first().click();
      assertThat(page.locator("#bnedPickupPersonForm")).containsText("DePaul University Loop Campus & SAIC");
      assertThat(page.getByLabel("main")).containsText("John Doe");
      assertThat(page.getByLabel("main")).containsText("bsemail@gmail.com");
      assertThat(page.getByLabel("main")).containsText("18165559014");
      assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
      assertContainsCurrency(page.getByLabel("main"));
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
      assertContainsCurrency(page.getByLabel("main"));
      assertThat(page.getByLabel("main")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
      assertContainsCurrency(page.getByLabel("main"));
      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove product JBL Quantum")).click();
      assertThat(page.getByLabel("main").getByRole(AriaRole.HEADING)).containsText("Your cart is empty");
      page.close();
      context.close();
      browser.close();
    }
  }

  private static void assertContainsCurrency(Locator locator) {
    String text = locator.innerText();
    Pattern currency = Pattern.compile("\\$\\s*\\d+(?:\\.\\d{2})?");
    if (!currency.matcher(text).find()) {
      throw new AssertionError("Expected at least one currency amount in page text.");
    }
  }
}
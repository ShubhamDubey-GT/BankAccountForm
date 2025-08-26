package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Map;

public class AccountFormPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AccountFormPage(WebDriver driver, int timeoutSec) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
    }

    private static String asXPathLiteral(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        if (!s.contains("\"")) return "\"" + s + "\"";
        String[] parts = s.split("'");
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }

    private By byInputFollowingLabel(String labelText) {
        String safe = asXPathLiteral(labelText);
        String xp = String.format(
                "//label[normalize-space()=%s]/following::input[1] | //label[normalize-space()=%s]/following::select[1]",
                safe, safe
        );
        return By.xpath(xp);
    }

    private By byIdOrName(String key) {
        return By.xpath(String.format("//*[@id='%1$s' or @name='%1$s']", key));
    }

    private WebElement findField(String label, String fallbackKey) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(byInputFollowingLabel(label)));
        } catch (TimeoutException e) {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(byIdOrName(fallbackKey)));
        }
    }

    public void typeText(String label, String key, String value) {
        WebElement el = findField(label, key);
        if ("select".equalsIgnoreCase(el.getTagName()))
            throw new IllegalStateException("Expected input but found select for label: " + label);
        el.clear();
        if (value != null && !value.isEmpty()) el.sendKeys(value);
    }

    public void selectByVisibleText(String label, String key, String value) {
        WebElement el = findField(label, key);
        if (!"select".equalsIgnoreCase(el.getTagName()))
            throw new IllegalStateException("Expected select but found input for label: " + label);
        if (value != null && !value.isEmpty()) new Select(el).selectByVisibleText(value);
    }

    public void fillForm(Map<String, String> data) {
        data.forEach((key, value) -> {
            switch (key) {
                case "accountType" -> selectByVisibleText("Account Type", key, mapAccountType(value));
                default -> typeText(labelForKey(key), key, value);
            }
        });
    }

    public boolean isFieldInvalid(String label, String key) {
        WebElement el = findField(label, key);
        return !(Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", el);
    }

    public String validationMessage(String label, String key) {
        WebElement el = findField(label, key);
        String msg = el.getAttribute("validationMessage");
        return msg == null ? "" : msg.trim();
    }

    public String getSelectedAccountTypeText() {
        WebElement el = findField("Account Type", "accountType");
        if (!"select".equalsIgnoreCase(el.getTagName())) return "";
        return new Select(el).getFirstSelectedOption().getText().trim();
    }

    private static String labelForKey(String key) {
        return switch (key) {
            case "fullName" -> "Full Name";
            case "fatherName" -> "Father Name";
            case "occupation" -> "Occupation";
            case "companyName" -> "Company Name";
            case "dob" -> "Date of Birth";
            case "email" -> "Email Address";
            case "phone" -> "Mobile Number";
            case "address" -> "Residential Address";
            case "idProof" -> "ID Proof (e.g., Passport/Driver's License)";
            case "accountType" -> "Account Type";
            case "initialDeposit" -> "Initial Deposit Amount ($)";
            default -> key;
        };
    }

    private static String mapAccountType(String v) {
        if (v == null) return "";
        return switch (v.trim().toLowerCase()) {
            case "regular" -> "Regular Savings";
            case "student" -> "Student Account";
            case "senior" -> "Senior Citizen";
            default -> v;
        };
    }

    public boolean isPostSubmitState() {
        return true;
    }

    public void clickSubmit() {
        By submit = By.xpath("//button[normalize-space()='Submit Application']");
        wait.until(ExpectedConditions.elementToBeClickable(submit)).click();
    }
}

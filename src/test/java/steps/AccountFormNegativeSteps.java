package steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.AccountFormPage;
import utils.ConfigReader;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccountFormNegativeSteps {
    private final WebDriver driver;
    private final AccountFormPage page;

    public AccountFormNegativeSteps() {
        this.driver = Hooks.driver;
        int timeout = ConfigReader.get("implicitWait") != null ? Integer.parseInt(ConfigReader.get("implicitWait")) : 10;
        this.page = new AccountFormPage(driver, timeout);
    }

    // Base data in insertion order
    private Map<String, String> baseData() {
        Map<String, String> data = new LinkedHashMap<>();
        String[] keys = {"fullName","fatherName","occupation","companyName","dob","email","phone","address","idProof","accountType","initialDeposit"};
        for (String key : keys) data.put(key, ConfigReader.get(key));
        return data;
    }

    // Generic fill form with overrides
    private void fillFormWithOverrides(Map<String, String> overrides) {
        Map<String, String> data = baseData(); // start with all fields
        if (overrides != null) data.putAll(overrides); // override specific fields
        page.fillForm(data);
    }

    // Steps

    @When("the user fills the form with valid data except {string}")
    public void fillWithMissingField(String missingLabel) {
        Map<String, String> overrides = new LinkedHashMap<>();
        String key = keyFor(missingLabel);
        if (key != null) overrides.put(key, ""); // make field empty
        fillFormWithOverrides(overrides);
        Hooks.scenarioTest.info("Filled form except missing: " + missingLabel);
    }

    @When("the user fills the form with an invalid email {string}")
    public void fillInvalidEmail(String email) {
        fillFormWithOverrides(Map.of("email", email));
        Hooks.scenarioTest.info("Filled form with invalid email: " + email);
    }

    @When("the user fills the form with initial deposit {string}")
    public void fillInitialDeposit(String deposit) {
        fillFormWithOverrides(Map.of("initialDeposit", deposit));
        Hooks.scenarioTest.info("Filled form with initial deposit: " + deposit);
    }

    @When("the user selects account type {string}")
    public void selectAccountType(String visibleText) {
        fillFormWithOverrides(Map.of("accountType", visibleText));
        Hooks.scenarioTest.info("Selected account type: " + visibleText);
    }

    @Then("the browser should show a required validation for {string}")
    public void assertRequired(String label) {
        assertValidation(label, "required");
    }

    @Then("the browser should show a type mismatch validation for {string}")
    public void assertTypeMismatch(String label) {
        assertValidation(label, "typeMismatch");
    }

    @Then("the browser should show a range validation for {string}")
    public void assertRange(String label) {
        assertValidation(label, "range");
    }

    private void assertValidation(String label, String type) {
        String key = keyFor(label);
        boolean invalid = page.isFieldInvalid(label, key);
        String msg = page.validationMessage(label, key);

        if (!invalid || msg == null || msg.isEmpty()) {
            Hooks.scenarioTest.fail("Expected " + type + " validation for: " + label);
            throw new AssertionError("Expected " + type + " validation for: " + label);
        }
        Hooks.scenarioTest.pass(type + " validation present for: " + label + " (message: " + msg + ")");
    }

    @Then("the account type field should reflect the last selected value {string}")
    public void assertAccountTypeSelected(String expected) {
        String actual = page.getSelectedAccountTypeText();
        if (!expected.equals(actual)) {
            Hooks.scenarioTest.fail("Account type mismatch. Expected: " + expected + ", Actual: " + actual);
            throw new AssertionError("Account type mismatch. Expected: " + expected + ", Actual: " + actual);
        }
        Hooks.scenarioTest.pass("Account type correctly set to: " + expected);
    }

    private static String keyFor(String label) {
        return switch (label) {
            case "Full Name" -> "fullName";
            case "Father Name" -> "fatherName";
            case "Occupation" -> "occupation";
            case "Company Name" -> "companyName";
            case "Date of Birth" -> "dob";
            case "Email Address" -> "email";
            case "Mobile Number" -> "phone";
            case "Residential Address" -> "address";
            case "ID Proof (e.g., Passport/Driver's License)" -> "idProof";
            case "Account Type" -> "accountType";
            case "Initial Deposit Amount ($)" -> "initialDeposit";
            default -> null;
        };
    }
}

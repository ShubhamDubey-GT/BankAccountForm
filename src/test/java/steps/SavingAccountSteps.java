package steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.AccountFormPage;
import utils.ConfigReader;

public class SavingAccountSteps {

    private final WebDriver driver;
    private final AccountFormPage page;

    public SavingAccountSteps() {
        this.driver = Hooks.driver;
        int timeout = Integer.parseInt(ConfigReader.get("implicitWait") == null ? "10" : ConfigReader.get("implicitWait"));
        this.page = new AccountFormPage(driver, timeout);
    }

    @Given("the user opens the savings account form")
    public void openForm() {
        String url = ConfigReader.get("url");
        driver.get(url);
        Hooks.scenarioTest.info("Opened URL: " + url);
    }

    @When("the user fills the form with valid data")
    public void fillForm() {
        page.typeText("Full Name", "fullname", ConfigReader.get("fullName"));
        page.typeText("Father Name", "fathername", ConfigReader.get("fatherName"));
        page.typeText("Occupation", "occupation", ConfigReader.get("occupation"));
        page.typeText("Company Name", "companyname", ConfigReader.get("companyName"));
        page.typeText("Date of Birth", "dob", ConfigReader.get("dob"));
        page.typeText("Email Address", "email", ConfigReader.get("email"));
        page.typeText("Mobile Number", "phone", ConfigReader.get("phone"));
        page.typeText("Residential Address", "address", ConfigReader.get("address"));
        page.typeText("ID Proof (e.g., Passport/Driver's License)", "idProof", ConfigReader.get("idProof"));
        page.selectByVisibleText("Account Type", "accountType", mapAccountType(ConfigReader.get("accountType")));
        page.typeText("Initial Deposit Amount ($)", "initialDeposit", ConfigReader.get("initialDeposit"));
        Hooks.scenarioTest.info("Filled form with configuration data");
    }

    @When("the user submits the form")
    public void submitForm() {
        page.clickSubmit();
        Hooks.scenarioTest.info("Clicked Submit");
    }

    @Then("the form should be submitted successfully")
    public void verifySubmit() {
        if (!page.isPostSubmitState()) {
            Hooks.scenarioTest.fail("Form did not submit successfully");
            throw new AssertionError("Form did not submit successfully");
        }
        Hooks.scenarioTest.pass("Form submitted successfully");
    }

    private String mapAccountType(String v) {
        if (v == null) return "";
        switch (v.trim().toLowerCase()) {
            case "regular": return "Regular Savings";
            case "student": return "Student Account";
            case "senior":  return "Senior Citizen";
            default: return v;
        }
    }
}
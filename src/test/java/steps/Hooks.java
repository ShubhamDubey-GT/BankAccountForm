package steps;

import io.cucumber.java.*;
import io.cucumber.java.Scenario;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import utils.ConfigReader;
import utils.DriverFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    public static WebDriver driver;
    public static WebDriverWait wait;

    private static ExtentReports extent;
    public static ExtentTest scenarioTest;

    private static final DateTimeFormatter RUN_TS  = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("HHmmss_SSS");
    private static String runFolderPath;

    private static synchronized String getRunFolder() {
        if (runFolderPath == null) {
            runFolderPath = "Reports" + File.separator + "screenshots" + File.separator
                    + "result_" + LocalDateTime.now().format(RUN_TS);
            File dir = new File(runFolderPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return runFolderPath;
    }

    private static String sanitize(String name) {
        return name == null ? "scenario"
                : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    // Save PNG to file and return absolute path; returns null on failure
    private static String saveScreenshotToFile(WebDriver drv, String prefix, String scenarioName) {
        if (drv == null) return null;
        try {
            String safeName = sanitize(scenarioName);
            String ts = LocalDateTime.now().format(FILE_TS);
            String fileName = prefix + safeName + "_" + ts + ".png";
            String dir = getRunFolder();
            File dest = new File(dir, fileName);

            File src = ((TakesScreenshot) drv).getScreenshotAs(OutputType.FILE);
            Files.createDirectories(dest.getParentFile().toPath());
            Files.copy(src.toPath(), dest.toPath());

            return dest.getAbsolutePath();
        } catch (Throwable t) {
            return null;
        }
    }

    private static byte[] takeBytes(WebDriver drv) {
        if (drv == null) return null;
        try {
            return ((TakesScreenshot) drv).getScreenshotAs(OutputType.BYTES);
        } catch (Throwable t) {
            return null;
        }
    }

    // Compute a path relative to the Reports/ folder (so report is portable)
    private static String toReportsRelative(String absolutePath) {
        if (absolutePath == null) return null;
        try {
            Path reports = Path.of("Reports").toAbsolutePath().normalize();
            Path target  = Path.of(absolutePath).toAbsolutePath().normalize();
            Path rel     = reports.relativize(target);
            return rel.toString().replace("\\", "/");
        } catch (Exception e) {
            return absolutePath.replace("\\", "/");
        }
    }

    @BeforeAll
    public static void beforeAll() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("Reports/SparkExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "Your Name");

        getRunFolder();
    }

    @Before
    public void setUp(Scenario scenario) {
        String browser = ConfigReader.get("browser");

        DriverFactory.setDriver(browser);
        driver = DriverFactory.getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        scenarioTest = extent.createTest(scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        String prefix = scenario.isFailed() ? "FAILED_" : "SUCCESS_";
        String safeScenario = sanitize(scenario.getName());

        byte[] bytes = takeBytes(driver);
        String filePathAbs = saveScreenshotToFile(driver, prefix, scenario.getName());
        String filePathForReport = toReportsRelative(filePathAbs);

        if (bytes != null) {
            try {
                scenario.attach(bytes, "image/png", prefix + safeScenario);
            } catch (Throwable ignored) {}
        }

        if (bytes != null) {
            try {
                String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                scenarioTest.addScreenCaptureFromBase64String("data:image/png;base64," + base64,
                        prefix + safeScenario);
            } catch (Throwable ignored) {}
        }
        if (filePathForReport != null) {
            scenarioTest.addScreenCaptureFromPath(filePathForReport, prefix + safeScenario);
        }

        if (scenario.isFailed()) {
            scenarioTest.fail("Scenario Failed");
        } else {
            scenarioTest.pass("Scenario Passed");
        }

        DriverFactory.quitDriver();
    }

    @AfterAll
    public static void afterAll() {
        if (extent != null) {
            extent.flush();
        }
    }
}

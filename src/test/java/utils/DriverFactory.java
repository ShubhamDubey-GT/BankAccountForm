package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;

            case "brave":
                WebDriverManager.chromedriver().browserVersion("139").setup();  // Brave uses ChromeDriver
                ChromeOptions options = new ChromeOptions();
                // Set path to Brave browser executable - change as per your system
                options.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");
                //  options.addArguments("--headless=new");

                driver.set(new ChromeDriver(options));
                break;

            default:
                throw new IllegalArgumentException("Browser " + browser + " not supported");
        }
        driver.get().manage().window().maximize();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}

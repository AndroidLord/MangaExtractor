package Util;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class WebDriverProvider {

    private static WebDriverProvider instance; // The single instance of WebDriverProvider
    private WebDriver driver;

    // Private constructor to prevent external instantiation
    private WebDriverProvider() {
        initializeWebDriver();
    }

    // Public method to access the single instance of WebDriverProvider
    public static WebDriverProvider getInstance() {
        if (instance == null) {
            instance = new WebDriverProvider();
        }
        return instance;
    }

    private void initializeWebDriver() {
        String path = Credentials.WEBDRIVERPATH;
        System.setProperty("webdriver.chrome.driver", path);

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        options.addArguments("options.add_argument('--blink-settings=imagesEnabled=false')");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--lang=en-US");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-notifications");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disable-network-throttling");
       options.addArguments("--offline");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-data-dir=C:\\Users\\shubh\\AppData\\Local\\BraveSoftware\\Brave-Browser\\UserData\\Profile4");


        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

    }

    // Add methods to get and manage the WebDriver instance as needed

    public WebDriver getDriver() {
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}

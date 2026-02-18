package com.iv1201.recruitment.browser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Base class for cross-browser testing.
 * Handles WebDriver setup and teardown for Chrome, Firefox, and Edge.
 * Supports local mode (Selenium Manager) and Docker/CI mode (remote containers).
 */
public abstract class CrossBrowserTestBase {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        String testMode = System.getProperty("test.mode", "docker").toLowerCase();

        // EdgeDriver on Windows 
        if (System.getenv("SE_MSEDGEDRIVER_MIRROR_URL") == null) {
            System.setProperty("SE_MSEDGEDRIVER_MIRROR_URL",
                    "https://msedgedriver.microsoft.com");
        }

        baseUrl = System.getProperty("app.url", "http://localhost:8080");

        if ("local".equals(testMode)) {
            setupLocalBrowser(browser);
        } else {
            setupDockerBrowser(browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    // CLI mode connect to Selenium Grid
    private void setupDockerBrowser(String browser) {
        String seleniumHost = System.getProperty("selenium.host", "localhost");

        try {
            switch (browser) {
                case "chrome":
                    ChromeOptions chromeOpts = new ChromeOptions();
                    driver = new RemoteWebDriver(
                        new URL("http://" + seleniumHost + ":4444"),
                        chromeOpts
                    );
                    break;

                case "firefox":
                    FirefoxOptions firefoxOpts = new FirefoxOptions(); 
                    driver = new RemoteWebDriver(
                        new URL("http://" + seleniumHost + ":4445"),
                        firefoxOpts
                    );
                    break;

                case "edge":
                    EdgeOptions edgeOpts = new EdgeOptions();
                    driver = new RemoteWebDriver(
                        new URL("http://" + seleniumHost + ":4446"),
                        edgeOpts
                    );
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to Selenium Grid", e);
        }
    }

    //Local mode  
    private void setupLocalBrowser(String browser) {
        switch (browser) {
            case "chrome":
                ChromeOptions chromeOpts = new ChromeOptions();
                chromeOpts.addArguments("--headless=new");
                chromeOpts.addArguments("--no-sandbox");
                chromeOpts.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOpts);
                break;

            case "firefox":
                FirefoxOptions firefoxOpts = new FirefoxOptions();
                firefoxOpts.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOpts);
                break;

            case "edge":
                EdgeOptions edgeOpts = new EdgeOptions();
                edgeOpts.addArguments("--headless=new");
                edgeOpts.addArguments("--no-sandbox");
                edgeOpts.addArguments("--disable-dev-shm-usage");
                driver = new EdgeDriver(edgeOpts);
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    //Close browser after test
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

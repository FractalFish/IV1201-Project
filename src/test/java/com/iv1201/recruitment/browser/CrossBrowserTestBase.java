package com.iv1201.recruitment.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
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
 * 
 * Run tests with: mvn test -Dtest=CrossBrowserTest -Dbrowser=chrome
 * Available browsers: chrome, firefox, edge
 * Test mode: local (uses Docker containers) or installed (uses local browsers)
 */
public abstract class CrossBrowserTestBase {
    
    protected WebDriver driver;
    protected String baseUrl = "http://host.docker.internal:8080";
    
    /**
     * Setup browser based on system properties.
     * 
     * Properties:
     * - browser: chrome, firefox, or edge (default: chrome)
     * - test.mode: docker (use containers) or local (use installed browsers)
     */
    @BeforeEach 
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        String testMode = System.getProperty("test.mode", "docker").toLowerCase();
        
        // Setup WebDriver based on test mode (docker or local)
        if ("docker".equals(testMode)) {
            setupDockerBrowser(browser);
        } else {
            setupLocalBrowser(browser);
        }
        
        // Set implicit wait for elements
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }
    
    /**
     * Setup browser using Docker Selenium containers.
     */
    private void setupDockerBrowser(String browser) {
        try {
            switch (browser) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions(); 
                    driver = new RemoteWebDriver(
                        new URL("http://localhost:4444"),
                        chromeOptions
                    );
                    break;
                    
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    driver = new RemoteWebDriver(
                        new URL("http://localhost:4445"),
                        firefoxOptions
                    );
                    break;
                    
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    driver = new RemoteWebDriver(
                        new URL("http://localhost:4446"),
                        edgeOptions
                    );
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to Selenium Grid", e);
        }
    }
    
    /**
     * Setup browser using locally installed browsers.
     */
    private void setupLocalBrowser(String browser) {
        baseUrl = "http://localhost:8080"; // Use localhost for local testing
        
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless"); 
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage"); 
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless");
                driver = new EdgeDriver(edgeOptions);
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
    
    /**
     * Cleanup after each test.
     */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

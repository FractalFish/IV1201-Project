package com.iv1201.recruitment.browser;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for checking that the application works in Chrome, Firefox, and Edge.
 * 
 * To run these tests:
 * 1. Start the app: mvnw.cmd spring-boot:run
 * 2. Start containers: docker-compose -f docker-compose.selenium.yml up -d
 * 3. Run tests: mvnw.cmd test -Dtest=CrossBrowserTest -Dbrowser=chrome
 * 
 * Change -Dbrowser=chrome to firefox or edge to test other browsers
 */
public class CrossBrowserTest extends CrossBrowserTestBase {
    
    /**
     * Test that the home page loads successfully.
     */
    @Test
    public void testHomePageLoads() {
        driver.get(baseUrl + "/");
        
        // Verify we're not on an error page
        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.contains("/error"), "Should not redirect to error page");
        
        // Verify page title contains expected text
        String title = driver.getTitle();
        assertNotNull(title, "Page title should not be null");
        assertFalse(title.isEmpty(), "Page title should not be empty");
    }
    
    /**
     * Test that the login page loads with all required elements.
     */
    @Test
    public void testLoginPageLoads() {
        driver.get(baseUrl + "/login");
        
        // Verify page title
        String title = driver.getTitle();
        assertTrue(
            title.toLowerCase().contains("login") || 
            title.toLowerCase().contains("recruitment"),
            "Login page title should contain 'login' or 'recruitment'"
        );
        
        // Verify username field exists and is visible
        WebElement usernameField = driver.findElement(By.name("username"));
        assertNotNull(usernameField, "Username field should be present");
        assertTrue(usernameField.isDisplayed(), "Username field should be visible");
        
        // Verify password field exists and is visible
        WebElement passwordField = driver.findElement(By.name("password"));
        assertNotNull(passwordField, "Password field should be present");
        assertTrue(passwordField.isDisplayed(), "Password field should be visible");
        
        // Verify submit button exists and is visible
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertNotNull(submitButton, "Submit button should be present");
        assertTrue(submitButton.isDisplayed(), "Submit button should be visible");
        
        // Verify fields are enabled
        assertTrue(usernameField.isEnabled(), "Username field should be enabled");
        assertTrue(passwordField.isEnabled(), "Password field should be enabled");
        assertTrue(submitButton.isEnabled(), "Submit button should be enabled");
    }
    
    /**
     * Test that the registration page loads with all required form fields.
     */
    @Test
    public void testRegistrationPageLoads() {
        driver.get(baseUrl + "/register");
        
        // Verify page loads (not an error page)
        String currentUrl = driver.getCurrentUrl();
        assertTrue(
            currentUrl.contains("/register"),
            "Should be on registration page"
        );
        
        // Verify all required form fields are present
        assertNotNull(driver.findElement(By.name("username")), "Username field should exist");
        assertNotNull(driver.findElement(By.name("password")), "Password field should exist");
        assertNotNull(driver.findElement(By.name("name")), "Name field should exist");
        assertNotNull(driver.findElement(By.name("surname")), "Surname field should exist");
        assertNotNull(driver.findElement(By.name("email")), "Email field should exist");
        assertNotNull(driver.findElement(By.name("pnr")), "PNR field should exist");
        
        // Verify all fields are visible
        assertTrue(driver.findElement(By.name("username")).isDisplayed());
        assertTrue(driver.findElement(By.name("password")).isDisplayed());
        assertTrue(driver.findElement(By.name("name")).isDisplayed());
        assertTrue(driver.findElement(By.name("surname")).isDisplayed());
        assertTrue(driver.findElement(By.name("email")).isDisplayed());
        assertTrue(driver.findElement(By.name("pnr")).isDisplayed());
    }
    
    /**
     * Test that CSS stylesheets are loaded and applied.
     */
    @Test
    public void testCssLoads() {
        driver.get(baseUrl + "/login");
        
        // Wait for page to fully load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Verify body element has some styling applied
        WebElement body = driver.findElement(By.tagName("body"));
        String backgroundColor = body.getCssValue("background-color");
        
        assertNotNull(backgroundColor, "Body should have a background color");
        assertFalse(backgroundColor.isEmpty(), "Background color should not be empty");
        
        // Verify form elements have styling
        WebElement usernameField = driver.findElement(By.name("username"));
        String display = usernameField.getCssValue("display");
        assertNotNull(display, "Username field should have display property");
    }
    
    /**
     * Test that the layout is responsive and works on different screen sizes.
     */
    @Test
    public void testResponsiveLayout() {
        // Test desktop size (1920x1080)
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        driver.get(baseUrl + "/login");
        
        WebElement usernameField = driver.findElement(By.name("username"));
        assertTrue(usernameField.isDisplayed(), "Form should be visible on desktop");
        
        // Test tablet size (768x1024)
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024));
        driver.get(baseUrl + "/login");
        
        usernameField = driver.findElement(By.name("username"));
        assertTrue(usernameField.isDisplayed(), "Form should be visible on tablet");
        
        // Test mobile size (375x667 - iPhone SE)
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
        driver.get(baseUrl + "/login");
        
        usernameField = driver.findElement(By.name("username"));
        assertTrue(usernameField.isDisplayed(), "Form should be visible on mobile");
    }
    
    /**
     * Test that navigation between pages works.
     */
    @Test
    public void testNavigation() {
        // Start at home page, expect redirect to /login
        driver.get(baseUrl + "/");
        String redirectedUrl = driver.getCurrentUrl();
        assertTrue(redirectedUrl.contains("/login"), "Unauthenticated access to / should redirect to /login");

        // Navigate to register page from login
        driver.get(baseUrl + "/register");
        String registerUrl = driver.getCurrentUrl();
        assertTrue(registerUrl.contains("/register"), "Should be on register page");

        // Navigate back to login
        driver.get(baseUrl + "/login");
        String loginUrl = driver.getCurrentUrl();
        assertTrue(loginUrl.contains("/login"), "Should be on login page");
    }
    
    /**
     * Test that form validation works (submitting empty form should show errors).
     */
    @Test
    public void testFormValidation() {
        driver.get(baseUrl + "/login");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Find and click submit button without filling fields
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
        
        // Form should either:
        // 1. Stay on login page with validation errors
        // 2. Show HTML5 validation (browser native)
        
        // Check if still on login page or if HTML5 validation prevented submission
        String currentUrl = driver.getCurrentUrl();
        boolean stayedOnPage = currentUrl.contains("/login");
        
        // Get username field and check if it has validation attributes
        WebElement usernameField = driver.findElement(By.name("username"));
        String required = usernameField.getAttribute("required");
        
        // Should either stay on page OR have HTML5 validation
        assertTrue(
            stayedOnPage || required != null,
            "Form should have validation (either server-side or HTML5)"
        );
    }
    
    /**
     * Test that error pages render correctly.
     */
    @Test
    public void testErrorPageHandling() {
        // Navigate to a non-existent page
        driver.get(baseUrl + "/this-page-does-not-exist");
        
        // Should either show 404 error page or redirect to home
        String currentUrl = driver.getCurrentUrl();
        
        // Verify we get some valid response (not a browser error)
        WebElement body = driver.findElement(By.tagName("body"));
        String bodyText = body.getText();
        
        assertNotNull(bodyText, "Error page should have content");
        assertFalse(bodyText.isEmpty(), "Error page should not be empty");
        
        // Verify page title exists
        String title = driver.getTitle();
        assertNotNull(title, "Error page should have a title");
    }
    
    /**
     * Test that the application works with browser back/forward navigation.
     */
    @Test
    public void testBrowserNavigation() {
        // Navigate to home page, expect redirect to /login
        driver.get(baseUrl + "/");
        String loginUrl = driver.getCurrentUrl();
        assertTrue(loginUrl.contains("/login"), "Unauthenticated access to / should redirect to /login");

        // Navigate to register page
        driver.get(baseUrl + "/register");
        String registerUrl = driver.getCurrentUrl();
        assertTrue(registerUrl.contains("/register"), "Should be on register page");

        // Go back to login
        driver.navigate().back();
        String backToLoginUrl = driver.getCurrentUrl();
        assertTrue(backToLoginUrl.contains("/login"), "Back button should return to login page");

        // Go forward to register
        driver.navigate().forward();
        String forwardToRegisterUrl = driver.getCurrentUrl();
        assertTrue(forwardToRegisterUrl.contains("/register"), "Forward button should return to register page");
    }
}

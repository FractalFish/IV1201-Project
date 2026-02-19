package com.iv1201.recruitment.browser;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cross-browser tests for Chrome, Firefox, and Edge.
 */
public class CrossBrowserTest extends CrossBrowserTestBase {

    // Verify the home page loads without errors.
    @Test
    public void testHomePageLoads() {
        driver.get(baseUrl + "/");

        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.contains("/error"), "Should not redirect to error page");

        String title = driver.getTitle();
        assertNotNull(title, "Page title should not be null");
        assertFalse(title.isEmpty(), "Page title should not be empty");
    }

    // Verify the login page has username, password, and submit fields.
    @Test
    public void testLoginPageLoads() {
        driver.get(baseUrl + "/login");

        String title = driver.getTitle();
        assertTrue(
            title.toLowerCase().contains("login") ||
            title.toLowerCase().contains("recruitment"),
            "Login page title should contain 'login' or 'recruitment'"
        );

        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        assertTrue(usernameField.isDisplayed(), "Username field should be visible");
        assertTrue(passwordField.isDisplayed(), "Password field should be visible");
        assertTrue(submitButton.isDisplayed(), "Submit button should be visible");

        assertTrue(usernameField.isEnabled(), "Username field should be enabled");
        assertTrue(passwordField.isEnabled(), "Password field should be enabled");
        assertTrue(submitButton.isEnabled(), "Submit button should be enabled");
    }

    // Verify the registration page has all required form fields.
    @Test
    public void testRegistrationPageLoads() {
        driver.get(baseUrl + "/register");
        assertTrue(driver.getCurrentUrl().contains("/register"), "Should be on registration page");

        String[] requiredFields = {"username", "password", "name", "surname", "email", "pnr"};
        for (String field : requiredFields) {
            WebElement element = driver.findElement(By.name(field));
            assertNotNull(element, field + " field should exist");
            assertTrue(element.isDisplayed(), field + " field should be visible");
        }
    }

    // Verify that CSS styles are loaded and applied.
    @Test
    public void testCssLoads() {
        driver.get(baseUrl + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        WebElement body = driver.findElement(By.tagName("body"));
        String backgroundColor = body.getCssValue("background-color");
        assertNotNull(backgroundColor, "Body should have a background color");
        assertFalse(backgroundColor.isEmpty(), "Background color should not be empty");

        WebElement usernameField = driver.findElement(By.name("username"));
        String display = usernameField.getCssValue("display");
        assertNotNull(display, "Username field should have display property");
    }

    // Verify the layout works on desktop, tablet, and mobile sizes.
    @Test
    public void testResponsiveLayout() {
        int[][] sizes = {{1920, 1080}, {768, 1024}, {375, 667}};
        String[] labels = {"desktop", "tablet", "mobile"};

        for (int i = 0; i < sizes.length; i++) {
            driver.manage().window().setSize(
                new org.openqa.selenium.Dimension(sizes[i][0], sizes[i][1])
            );
            driver.get(baseUrl + "/login");

            WebElement usernameField = driver.findElement(By.name("username"));
            assertTrue(usernameField.isDisplayed(), "Form should be visible on " + labels[i]);
        }
    }

    // Verify navigation between login, register, and home pages.
    @Test
    public void testNavigation() {
        driver.get(baseUrl + "/");
        assertTrue(driver.getCurrentUrl().contains("/login"),
            "Unauthenticated access to / should redirect to /login");

        driver.get(baseUrl + "/register");
        assertTrue(driver.getCurrentUrl().contains("/register"), "Should be on register page");

        driver.get(baseUrl + "/login");
        assertTrue(driver.getCurrentUrl().contains("/login"), "Should be on login page");
    }

    // Verify submitting an empty login form triggers validation.
    @Test
    public void testFormValidation() {
        driver.get(baseUrl + "/login");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        boolean stayedOnPage = driver.getCurrentUrl().contains("/login");
        String required = driver.findElement(By.name("username")).getAttribute("required");

        assertTrue(stayedOnPage || required != null,
            "Form should have validation (either server-side or HTML5)");
    }

    // Verify a non-existent URL returns a proper error page.
    @Test
    public void testErrorPageHandling() {
        driver.get(baseUrl + "/this-page-does-not-exist");

        WebElement body = driver.findElement(By.tagName("body"));
        assertNotNull(body.getText(), "Error page should have content");
        assertFalse(body.getText().isEmpty(), "Error page should not be empty");
        assertNotNull(driver.getTitle(), "Error page should have a title");
    }

    // Verify browser back/forward buttons work correctly.
    @Test
    public void testBrowserNavigation() {
        driver.get(baseUrl + "/");
        assertTrue(driver.getCurrentUrl().contains("/login"),
            "Unauthenticated access to / should redirect to /login");

        driver.get(baseUrl + "/register");
        assertTrue(driver.getCurrentUrl().contains("/register"), "Should be on register page");

        driver.navigate().back();
        assertTrue(driver.getCurrentUrl().contains("/login"),
            "Back button should return to login page");

        driver.navigate().forward();
        assertTrue(driver.getCurrentUrl().contains("/register"),
            "Forward button should return to register page");
    }
}

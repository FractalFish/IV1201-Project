package com.iv1201.recruitment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Example test to demonstrate test structure and verify testing infrastructure works.
 * This file can be used as a reference when writing actual tests.
 *
 * To run tests:
 * - Locally: ./mvnw test
 * - In CI/CD: Runs automatically on push via GitHub Actions
 */
public class ExampleTest {

    @Test
    public void testSimpleAssertion() {
        assertEquals(2, 1 + 1, "Simple math should work");
    }

    @Test
    public void testStringConcatenation() {
        String result = "Hello" + " " + "World";
        assertEquals("Hello World", result);
    }

    @Test
    public void testBooleanLogic() {
        assertTrue(true, "True should be true");
        assertFalse(false, "False should be false");
    }

    @Test
    public void testNullChecks() {
        String notNull = "test";
        assertNotNull(notNull, "String should not be null");

        String nullValue = null;
        assertNull(nullValue, "Null value should be null");
    }
}

#!/bin/bash

# CI/CD Local Testing Script for IV1201-Project
# This script tests all components of the CI/CD pipeline locally before pushing

set -e  # Exit on error

echo "========================================="
echo "CI/CD LOCAL TESTING"
echo "========================================="
echo ""

# Step 1: Run Unit Tests with H2 Database
echo "[1/5] Running unit tests with H2 database..."
./mvnw clean test -Dspring.profiles.active=test
echo "[SUCCESS] Unit tests passed!"
echo ""

# Step 2: Generate Test Reports
echo "[2/5] Generating test reports..."
./mvnw surefire-report:report
echo "[SUCCESS] Test reports generated at: target/site/surefire-report.html"
echo ""

# Step 3: Generate Code Coverage Reports
echo "[3/5] Generating code coverage reports..."
./mvnw jacoco:report
echo "[SUCCESS] Coverage report generated at: target/site/jacoco/index.html"
echo ""

# Step 4: Run Static Analysis (Checkstyle)
echo "[4/5] Running Checkstyle static analysis..."
./mvnw checkstyle:checkstyle
echo "[SUCCESS] Checkstyle report generated at: target/checkstyle-result.xml"
echo ""

# Step 5: Build Application JAR
echo "[5/5] Building application JAR..."
./mvnw package -DskipTests
echo "[SUCCESS] Application JAR built at: target/*.jar"
echo ""

echo "========================================="
echo "ALL CI/CD TESTS PASSED LOCALLY!"
echo "========================================="
echo ""
echo "View reports:"
echo "  - Test Results:   target/site/surefire-report.html"
echo "  - Code Coverage:  target/site/jacoco/index.html"
echo "  - Checkstyle:     target/checkstyle-result.xml"
echo "  - Application:    target/*.jar"
echo ""
echo "Next step: Push branch to GitHub to test in CI/CD pipeline"

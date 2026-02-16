# Local Testing Guide

## Quick Reference

### Option 1: Development Mode (App + Database)
**Use when:** Developing features, need live reload for templates/static files
```bash
docker compose -f docker-compose.dev.yml up
```
- ✅ Application runs on http://localhost:8080
- ✅ Templates & static files reload automatically
- ✅ PostgreSQL database with Flyway migrations

### Option 2: Run Tests Locally
**Use when:** Want to run tests quickly without Docker
```bash
./mvnw test
```
- ⚡ Fast (2-3 seconds)
- Uses H2 in-memory database
- No Docker needed
- Runs JUnit tests with JaCoCo coverage

## View Test Results

### In Terminal
When you run `./mvnw test`, results appear immediately:
```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

### Detailed Reports
After tests run, generate detailed reports:
```bash
# Coverage report
./mvnw jacoco:report
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # macOS/Linux

# Test report
./mvnw surefire-report:report
start target/site/surefire-report.html
```

## CI/CD Testing

Tests automatically run in GitHub Actions on every push:
- **Location:** `.github/workflows/main.yml`
- **Stage:** Unit Tests (after Static Analysis, before Build)
- **View Results:** Actions tab → Workflow run → Unit Tests job
- **Downloads:** Test reports and JaCoCo coverage available as artifacts

## Best Practices

1. **Before committing:** Run `./mvnw test checkstyle:checkstyle` to verify locally
2. **After pushing:** GitHub Actions runs full test suite automatically
3. **Check coverage:** Generate JaCoCo reports to see code coverage metrics

## Performance Tips

- First run downloads dependencies (~1-2 min)
- Subsequent runs are fast (~2-3 seconds)
- Tests use H2 in-memory database (no Docker needed)

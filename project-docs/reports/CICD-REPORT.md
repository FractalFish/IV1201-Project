# CI/CD Pipeline - Quick Reference

## What Was Implemented

**GitHub Actions workflow that automatically:**
1. Runs code quality checks (Checkstyle)
2. Runs all tests (JUnit + coverage with JaCoCo)
3. Builds Docker image and pushes to GitHub Container Registry
4. Deploys to Azure (only from main branch)

## When Does It Run?

- **On EVERY branch push** → Runs tests, static analysis, and builds Docker image
- **On pull requests to main/develop** → Same as above
- **Deployment to Azure** → Only when pushing to `main` branch

This means anyone can push to any branch and immediately see if their code passes tests, but production deployment is safely restricted to main.

## What to Remember

### As a Developer:
- Push your code to any branch → CI runs automatically
- Check Actions tab on GitHub to see if tests passed
- Green checkmark = good to merge, red X = fix your code

### Before Merging to Main:
- Make sure all tests pass (green checkmark)
- Check that Checkstyle didn't flag major issues
- PR must be approved before merge

### After Merging to Main:
- Pipeline automatically deploys to Azure
- Wait ~3-5 minutes for deployment
- Check https://iv1201-recruitment.azurewebsites.net/ to verify

## Pipeline Stages (in order)

```
1. Static Analysis (14s)  ← Checkstyle checks code style
2. Unit Tests (27s)       ← JUnit tests with coverage
3. Build Docker (1m24s)   ← Creates container image
4. Deploy Azure (4s)      ← [main branch only]
```

## Common Scenarios

**"I pushed to my feature branch - did it deploy?"**
No. Only tests and builds ran. Deployment happens only from main.

**"Tests failed on my branch - now what?"**
Fix the failing tests locally, push again. CI will re-run automatically.

**"How do I see test coverage?"**
Go to Actions tab → Click your workflow run → Download "coverage-report" artifact.

**"Can I deploy to staging first?"**
Currently no. We only have production. Staging would require Azure paid tier.

## Key Files

- `.github/workflows/ci-cd.yml` - The entire pipeline definition
- `pom.xml` - Maven config with Checkstyle, JaCoCo, test dependencies
- `src/test/resources/application-test.properties` - Test database config (H2)
- `src/test/java/com/iv1201/recruitment/ExampleTest.java` - Example tests

## Azure Configuration

**What's set up:**
- Azure Web App pulls Docker image from ghcr.io
- Database: Azure PostgreSQL Flexible Server with pgcrypto enabled
- Deployment: Webhook-based (simple POST request triggers redeploy)

**GitHub Secret Required:**
- `AZURE_WEBHOOK_URL` - The deployment webhook URL (already configured)

## Database Migrations

Uses **Flyway** - migrations in `src/main/resources/db/migration/`
- Runs automatically when app starts
- Versioned: V1, V2, V3, V4
- Don't modify existing migrations - create new ones

## Troubleshooting

**Pipeline failed on "Build Docker Image"**
- Usually means Docker build failed
- Check the job logs for compilation errors

**Pipeline failed on "Unit Tests"**
- Tests failed or didn't compile
- Download test-results artifact to see which tests failed

**Pipeline failed on "Static Analysis"**
- Checkstyle found issues
- Download checkstyle-result.xml to see violations
- Note: This doesn't block the pipeline, just warns you

**Deployment succeeded but app won't start on Azure**
- Check Azure logs in portal
- Usually database migration issue or missing environment variable

## Testing Locally

**Run tests:**
```bash
./mvnw test
```

**Run Checkstyle:**
```bash
./mvnw checkstyle:check
```

**Run both (what CI does):**
```bash
./mvnw clean test checkstyle:checkstyle
```

## What Changed Recently

- Now runs on ALL branches (used to only run on main/develop/feature/*)
- Deployment restricted to main only (used to also deploy from feature/ci-cd-pipeline)
- Merged logging changes from PR #39

## Documentation

Full technical details in:
- `docs/ADR-003-cicd-pipeline-implementation.md` - Complete architecture decision record
- `docs/ADR-001-azure-deployment-fixes.md` - Azure-specific fixes
- `docs/ADR-002-flyway-database-migrations.md` - Database migration strategy

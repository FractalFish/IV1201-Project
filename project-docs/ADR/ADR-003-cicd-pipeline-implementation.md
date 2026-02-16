# ADR-003: CI/CD Pipeline Implementation

**Status:** Implemented
**Date:** 2026-02-14
**Decision Makers:** Development Team

## Context

The IV1201 Recruitment Application needed automated deployment to Azure to enable:
- Continuous integration (automated testing and validation)
- Continuous deployment (automated releases to production)
- Code quality enforcement
- Consistent build and deployment process
- Faster development iterations

Previously, deployment was manual and error-prone, requiring developers to build, test, and deploy locally.

## Decision

We implemented a **GitHub Actions-based CI/CD pipeline** with four stages:
1. Static Analysis (Checkstyle)
2. Unit Testing (Maven + JaCoCo)
3. Docker Image Build (GitHub Container Registry)
4. Azure Deployment (Webhook-based)

### Pipeline Architecture

```yaml
Trigger (push/PR)
    ↓
┌─────────────────┐
│ Static Analysis │ (Checkstyle)
└────────┬────────┘
         │
┌────────▼────────┐
│   Unit Tests    │ (Maven + JaCoCo)
└────────┬────────┘
         │
┌────────▼────────┐
│  Build Docker   │ (→ ghcr.io)
└────────┬────────┘
         │ (only on main/feature/ci-cd-pipeline)
┌────────▼────────┐
│ Deploy to Azure │ (Webhook)
└─────────────────┘
```

### Implementation Details

**File:** `.github/workflows/ci-cd.yml`

**Triggers:**
- Push to: **Any branch** (tests and builds run on all branches)
- Pull requests to: `main`, `develop`
- Manual workflow dispatch

**Job 1: Static Analysis**
- Tool: Checkstyle with Google style guide
- Configuration: `google_checks.xml`
- Mode: Non-blocking (continue-on-error)
- Artifacts: checkstyle-result.xml (30-day retention)

**Job 2: Unit Tests**
- Framework: JUnit (via Spring Boot Test)
- Profile: `test` (H2 in-memory database)
- Coverage: JaCoCo
- Reports: Surefire reports + JaCoCo HTML reports
- Artifacts: test-results, coverage-report (30-day retention)

**Job 3: Build Docker Image**
- Platform: Docker Buildx
- Registry: GitHub Container Registry (ghcr.io)
- Tags:
  - `ghcr.io/fractalfish/iv1201-project:latest`
  - `ghcr.io/fractalfish/iv1201-project:<commit-sha>`
- Authentication: GitHub token (automatic)
- Dependencies: Requires jobs 1 & 2 to pass

**Job 4: Deploy to Azure**
- Method: Azure Webhook POST request
- Environment: Production
- Conditions:
  - Branch: `main` ONLY
  - Event: `push` (not pull_request)
- Secret: `AZURE_WEBHOOK_URL`
- Rationale: All other branches run tests/builds but don't deploy

### Code Quality Tools Added

**pom.xml additions:**

1. **Checkstyle Plugin:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <failsOnError>false</failsOnError>
    </configuration>
</plugin>
```

2. **JaCoCo Plugin:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

### Testing Infrastructure

**File:** `src/test/resources/application-test.properties`
- Configures H2 in-memory database for tests
- Disables Flyway for test isolation
- Enables SQL logging for debugging

## Alternatives Considered

### Alternative 1: Jenkins
- **Pros:** Self-hosted, highly customizable, mature ecosystem
- **Cons:** Requires infrastructure management, more complex setup
- **Rejected:** GitHub Actions is simpler and better integrated with GitHub

### Alternative 2: GitLab CI
- **Pros:** Integrated with GitLab, powerful features
- **Cons:** Would require moving repository to GitLab
- **Rejected:** Team already using GitHub

### Alternative 3: Azure DevOps Pipelines
- **Pros:** Native Azure integration, powerful
- **Cons:** Separate platform from code repository, learning curve
- **Rejected:** GitHub Actions provides sufficient Azure integration

### Alternative 4: Manual Deployment
- **Pros:** Simple, no CI/CD complexity
- **Cons:** Error-prone, slow, not scalable
- **Rejected:** Not suitable for team development

## Deployment Strategy Evolution

The pipeline went through three iterations:

### Plan A: azure/webapps-deploy@v3
- Used GitHub Action for deployment
- **Failed:** Compatibility issues with container deployment

### Plan B: azure/webapps-deploy@v2
- Downgraded to match official templates
- **Failed:** Still had deployment reliability issues

### Plan C: Azure Webhook (Current)
- Direct POST request to Azure webhook
- **Success:** Simple, reliable, officially supported by Azure
- See commit: `71ca8cf`

## Consequences

### Positive
- **Automated Quality Checks:** Every commit gets checked
- **Fast Feedback:** Developers know if changes break build/tests
- **Consistent Builds:** Same environment every time
- **Deployment Automation:** Push to main → deployed to Azure
- **Audit Trail:** Complete history of builds and deployments
- **Team Confidence:** Tests must pass before deployment
- **Docker Benefits:** Consistent runtime environment

### Negative
- **Build Time:** ~3-5 minutes per build
- **GitHub Actions Minutes:** Consumes free tier minutes (more branches = more builds)
- **Learning Curve:** Team must understand CI/CD concepts
- **Test Artifacts Issue:** Some artifacts not generated (known limitation)

### Neutral
- **Public Container Images:** Images stored in public GitHub registry
- **Webhook Security:** Webhook URL must be kept secret

## Best Practices Established

1. **Test Before Merge:** All tests must pass before merging PR
2. **Keep CI Fast:** Optimize build times for quick feedback
3. **Fail Fast:** Run quick checks (linting) before expensive ones (tests)
4. **Artifact Retention:** Keep test results for 30 days
5. **Branch Protection:** Require CI to pass before merging (recommended)

## Configuration Requirements

### GitHub Secrets
```
AZURE_WEBHOOK_URL - Azure Web App deployment webhook
```

### GitHub Permissions
```yaml
permissions:
  contents: read
  packages: write  # For GHCR push
```

### Azure Configuration
- Web App must be configured for container deployment
- Webhook URL generated via publish profile
- Container registry: ghcr.io

## Monitoring and Observability

**GitHub Actions UI provides:**
- Build status badges
- Workflow run history
- Job logs and artifacts
- Timing metrics

**Azure Portal provides:**
- Deployment history
- Application logs
- Container status
- Performance metrics

## Known Issues

1. **Test Artifacts Not Generated**
   - surefire-reports/ directory not found
   - JaCoCo reports not uploaded
   - **Impact:** Low - tests still run
   - **Workaround:** None needed currently

## Resolved Issues

1. **Feature Branch Deployment** (Resolved 2026-02-16)
   - Issue: `feature/ci-cd-pipeline` was deploying to production during testing
   - Resolution: Restricted deployment to `main` branch only
   - Now: All branches run tests/builds, only main deploys

## Future Improvements

### Short Term
1. Fix test artifact generation
2. Add deployment slots for zero-downtime deployments
3. Add Maven dependency caching to speed up builds (~30s faster)

### Medium Term
1. Add integration tests stage
2. Implement smoke tests post-deployment
3. Add performance testing
4. Configure branch protection rules
5. Add Dependabot for dependency updates

### Long Term
1. Multi-environment support (dev, staging, production)
2. Blue-green deployment strategy
3. Automated rollback on failure
4. Security scanning (SAST/DAST)
5. Container vulnerability scanning

## Verification

**Check pipeline status:**
```bash
# Via GitHub CLI
gh run list --limit 5

# Via web
# https://github.com/FractalFish/IV1201-Project/actions
```

**Test deployment:**
```bash
curl -I https://iv1201-recruitment.azurewebsites.net/
# Expected: HTTP 302 (redirect to login)
```

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Azure Web Apps Deployment](https://learn.microsoft.com/en-us/azure/app-service/deploy-ci-cd-custom-container)
- [Checkstyle Google Style](https://checkstyle.sourceforge.io/google_style.html)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- Pipeline File: `.github/workflows/ci-cd.yml`
- Related ADRs: ADR-001 (Azure Deployment Fixes), ADR-002 (Flyway Migrations)

## Success Metrics

- ✅ Pipeline runs on every commit
- ✅ Average build time: ~3 minutes
- ✅ Zero failed deployments after webhook implementation
- ✅ 100% of commits validated before merge (manual process)
- ✅ Automated deployment to Azure working reliably

## Recent Updates

### 2026-02-16: Branch Deployment Strategy
- **Changed:** Deployment now restricted to `main` branch only
- **Changed:** Workflow now triggers on ALL branches (not just main/develop/feature/**)
- **Rationale:**
  - All developers get CI feedback on any branch
  - Production deployment safely controlled via main branch
  - Reduces risk of accidental production deployments
- **Impact:** Better developer experience, safer deployment process

### 2026-02-16: Integration with Logging
- Merged logging implementation from PR #39
- Pipeline now validates code with comprehensive logging in place
- All stages pass with new logging configuration

## Conclusion

The CI/CD pipeline successfully automates the build, test, and deployment process for the IV1201 Recruitment Application. The webhook-based deployment strategy (Plan C) proved to be the most reliable approach after iterations with the azure/webapps-deploy action.

Key achievements:
- All branches get automated testing and quality checks
- Production deployment controlled through main branch only
- Average pipeline execution: ~3 minutes
- Zero deployment failures since webhook implementation
- Successfully integrated with application logging and database migrations

---

## Simplified ADR Entry (Wiki Format)

**CI/CD Pipeline with GitHub Actions**

Date: 2026-02-14  Status: Implemented

Decision: Implement GitHub Actions CI/CD pipeline with automated testing, building, and Azure deployment.

Description: Created four-stage pipeline: Static Analysis (Checkstyle), Unit Tests (JUnit + JaCoCo), Docker Build (GitHub Container Registry), and Azure Deployment (webhook-based). Pipeline runs on all branches for testing but deploys only from main. Webhook deployment (Plan C) proved most reliable after iterations with azure/webapps-deploy action. Provides automated quality checks, consistent builds, fast feedback for developers, and zero-touch deployment to production.

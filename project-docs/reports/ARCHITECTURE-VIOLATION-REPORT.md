# Architectural Violation Report

**Date:** 2026-02-16
**Severity:** Medium
**Status:** Documented, not yet fixed

## Issue Summary

The application violates the **layered architecture pattern** (Controller → Service → Repository) in at least one location.

**Violation:** `ApplicantController` directly injects and uses `PersonRepository`, bypassing the service layer.

## Details

**File:** `src/main/java/com/iv1201/recruitment/controller/ApplicantController.java`

**Lines:**
- Line 8: `import com.iv1201.recruitment.repository.PersonRepository;`
- Line 37: `private final PersonRepository personRepository;`
- Line 40: Constructor injection of `PersonRepository`

**What's wrong:**
```
Current (Wrong):
Controller → Repository (Direct access)

Expected (Correct):
Controller → Service → Repository
```

## Why This Matters

**Architectural Consistency:**
- Breaks separation of concerns
- Makes testing harder (controller tests need to mock repositories)
- Business logic can leak into controllers
- Reduces maintainability

**Best Practice:**
- Controllers should only handle HTTP concerns (requests, responses, validation)
- Services should contain business logic and orchestrate data access
- Repositories should only handle data persistence

## Root Cause

Likely scenarios:
1. Quick implementation to get feature working
2. Missing service method that controller needs
3. Unclear architectural guidelines during development

## Current State of Enforcement

**No automated checks exist:**
- ❌ Checkstyle doesn't check architectural patterns
- ❌ No ArchUnit tests configured
- ❌ No architectural rules in CI/CD pipeline
- ❌ No documentation of layering requirements

This means similar violations could exist elsewhere or be introduced in the future without detection.

## Other Potential Violations (Not Verified)

Need to check:
- Do other controllers directly access repositories?
- Are there cyclic dependencies between packages?
- Do services access other services correctly?
- Are Spring annotations used correctly (@Controller, @Service, @Repository)?

## Impact Assessment

**Current Impact:**
- Low to Medium - Application functions correctly
- Technical debt accumulating
- Sets bad precedent for team

**Future Impact if Not Fixed:**
- More violations will be introduced
- Harder to refactor later
- Testing becomes more complex
- Code reviews miss these issues

## Recommended Solution

### Short-term (Fix This Violation):
1. Create a new branch: `fix/architecture-violation-applicant-controller`
2. Identify why `ApplicantController` needs `PersonRepository`
3. Options:
   - Add missing method to existing service
   - Create new service if needed
   - Refactor controller to use service layer
4. Remove `PersonRepository` injection from controller
5. Test thoroughly
6. Create PR with detailed explanation

### Long-term (Prevent Future Violations):
1. Add ArchUnit dependency to `pom.xml`
2. Create `ArchitectureTest.java` with rules:
   - Controllers must not access repositories
   - Enforce package structure
   - Check for cyclic dependencies
   - Validate Spring annotations
3. Add architectural tests to CI/CD pipeline
4. Document architectural guidelines in wiki or ADR

## Example ArchUnit Test

```java
@Test
public void controllersShouldNotAccessRepositoriesDirectly() {
    noClasses()
        .that().resideInAPackage("..controller..")
        .should().dependOnClassesThat()
        .resideInAPackage("..repository..")
        .because("Controllers should only use services, not repositories directly")
        .check(importedClasses);
}
```

This test would **fail** on `ApplicantController` and prevent similar violations.

## Action Items

- [ ] Create GitHub issue tracking this violation
- [ ] Create fix branch
- [ ] Investigate why controller needs repository access
- [ ] Implement proper service layer usage
- [ ] Write tests for the fix
- [ ] Consider adding ArchUnit for future prevention
- [ ] Document architectural guidelines

## Related Documentation

- ADR-003: CI/CD Pipeline Implementation (should include ArchUnit)
- Wiki: Architecture Decision Log (should include layering guidelines)

## References

- [Spring Boot Best Practices: Layered Architecture](https://www.baeldung.com/spring-boot-clean-architecture)
- [ArchUnit Documentation](https://www.archunit.org/)
- File: `src/main/java/com/iv1201/recruitment/controller/ApplicantController.java`

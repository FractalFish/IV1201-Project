# Project Cleanup Summary

**Date:** 2026-02-16

## Quick Overview

Two issues discovered that need cleanup:

1. **Architectural Violation** - Controller directly accessing Repository
2. **Documentation Mess** - Duplicates, outdated files, poor organization

## Issue 1: Architectural Violation

**Problem:** `ApplicantController` directly injects `PersonRepository`, bypassing service layer

**Impact:** Breaks layered architecture pattern, sets bad precedent

**Details:** See `ARCHITECTURE-VIOLATION-REPORT.md`

**Fix Required:** Create branch, refactor to use service layer, add ArchUnit tests

## Issue 2: Documentation Organization

**Problems Found:**

| Issue | Files | Action |
|-------|-------|--------|
| Duplicates | migration-application-table.sql, test-data.sql | DELETE - copies of V3, V4 migrations |
| Outdated | The SSR Project Structure (Monolith).txt | DELETE - planning doc, wrong package name |
| Scattered | docs/, project-docs/, root-level reports | REORGANIZE into project-docs/ |

**Details:** See `DOCS-REORGANIZATION-PLAN.md`

**Proposed Structure:**
```
project-docs/
├── ADR/                          ← Architecture Decision Records
│   ├── ADR-001-*.md
│   ├── ADR-002-*.md
│   └── ADR-003-*.md
├── reports/                      ← Standalone reports
│   ├── CICD-REPORT.md
│   ├── ARCHITECTURE-VIOLATION-REPORT.md
│   └── README-TESTING.md
└── Course-Provided-instructions/ ← Keep as-is
```

## Files Can Be Safely Deleted

✅ **Verified safe to delete:**

1. `project-docs/migration-application-table.sql`
   - Exact duplicate of `src/main/resources/db/migration/V3__application_table.sql`
   - Verified with `diff` command - no differences

2. `project-docs/test-data.sql`
   - Exact duplicate of `src/main/resources/db/migration/V4__test_data.sql`
   - Verified with `diff` command - no differences

3. `project-docs/The SSR Project Structure (Monolith).txt`
   - Generic template with wrong package name (`com.example` not `com.iv1201`)
   - Outdated planning document, project already implemented
   - No dependencies, no references in code

## Recommended Action Plan

### Option A: Two Separate Branches
1. Branch `fix/architecture-violation` - Fix controller issue, add ArchUnit
2. Branch `chore/docs-cleanup` - Reorganize and cleanup documentation

### Option B: Single Cleanup Branch
1. Branch `chore/project-cleanup` - Both fixes together
2. Keeps git history clean with single PR

### Option C: Just Documentation (Quick Win)
1. Branch `chore/docs-reorganization` - Just move/delete docs
2. Fix architecture issue later in separate PR

## Related Files

- `ARCHITECTURE-VIOLATION-REPORT.md` - Full details on controller issue
- `DOCS-REORGANIZATION-PLAN.md` - Step-by-step reorganization guide
- `CICD-REPORT.md` - Quick reference for CI/CD pipeline

## No Immediate Action Required

These are documentation and planning issues - they don't affect:
- ✅ Application functionality
- ✅ CI/CD pipeline
- ✅ Current deployments
- ✅ Active development

But should be addressed for:
- Better maintainability
- Cleaner repository
- Architectural consistency
- Team onboarding

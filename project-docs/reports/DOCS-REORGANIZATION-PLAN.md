# Documentation Reorganization Plan

**Date:** 2026-02-16
**Status:** Planned, not executed

## Current Structure (Problematic)

```
IV1201-Project/
├── docs/                               ← Separated from project docs
│   ├── .gitkeep
│   ├── ADR-001-azure-deployment-fixes.md
│   ├── ADR-002-flyway-database-migrations.md
│   └── ADR-003-cicd-pipeline-implementation.md
├── project-docs/
│   ├── Course-Provided-instructions/   ← Keep as-is
│   ├── migration-application-table.sql ← DUPLICATE (old)
│   ├── test-data.sql                   ← DUPLICATE (old)
│   └── The SSR Project Structure (Monolith).txt ← OUTDATED
├── CICD-REPORT.md                      ← Root level docs
├── HANDOVER.md                         ← Root level docs
└── README-TESTING.md                   ← Root level docs
```

## Problems Identified

### 1. Duplicate SQL Files
**Files:** `project-docs/migration-application-table.sql`, `project-docs/test-data.sql`

**Analysis:**
```bash
# Verified identical to Flyway migrations:
diff project-docs/migration-application-table.sql src/main/resources/db/migration/V3__application_table.sql
# No differences

diff project-docs/test-data.sql src/main/resources/db/migration/V4__test_data.sql
# No differences
```

**Conclusion:** These are old files from before Flyway migration was implemented. They were moved to `src/main/resources/db/migration/` with proper Flyway naming conventions (V3, V4).

**Action:** **DELETE** - No longer needed, duplicates of Flyway migrations

### 2. Outdated Structure Document
**File:** `project-docs/The SSR Project Structure (Monolith).txt`

**Analysis:**
- Contains generic project structure template
- Uses wrong package name: `com.example.recruitment` (actual: `com.iv1201.recruitment`)
- Describes planned structure, but project is already implemented
- No longer serves any purpose

**Action:** **DELETE** - Outdated planning document

### 3. Separated Documentation Directories
**Problem:** `docs/` and `project-docs/` are separated at root level

**Current:**
- `docs/` contains ADR files only
- `project-docs/` contains course materials and old files
- Other docs scattered at root (HANDOVER.md, CICD-REPORT.md, README-TESTING.md)

**Action:** **CONSOLIDATE** - Move docs/ into project-docs/ADR/

## Proposed New Structure

```
IV1201-Project/
├── project-docs/
│   ├── ADR/                            ← RENAMED from docs/
│   │   ├── ADR-001-azure-deployment-fixes.md
│   │   ├── ADR-002-flyway-database-migrations.md
│   │   └── ADR-003-cicd-pipeline-implementation.md
│   ├── reports/                        ← NEW - For standalone reports
│   │   ├── CICD-REPORT.md             ← MOVED from root
│   │   ├── ARCHITECTURE-VIOLATION-REPORT.md ← MOVED from root
│   │   └── README-TESTING.md          ← MOVED from root
│   └── Course-Provided-instructions/   ← KEEP AS-IS
│       └── [course materials]
├── HANDOVER.md                         ← KEEP at root (team handover doc)
└── README.md                           ← KEEP at root (project readme)
```

## Reorganization Steps

### Step 1: Create New Structure
```bash
# Create reports directory
mkdir -p project-docs/reports

# Create ADR directory
mkdir -p project-docs/ADR
```

### Step 2: Move ADR Files
```bash
# Move all ADR files from docs/ to project-docs/ADR/
mv docs/ADR-001-azure-deployment-fixes.md project-docs/ADR/
mv docs/ADR-002-flyway-database-migrations.md project-docs/ADR/
mv docs/ADR-003-cicd-pipeline-implementation.md project-docs/ADR/
```

### Step 3: Move Report Files
```bash
# Move standalone reports to project-docs/reports/
mv CICD-REPORT.md project-docs/reports/
mv ARCHITECTURE-VIOLATION-REPORT.md project-docs/reports/
mv README-TESTING.md project-docs/reports/
```

### Step 4: Delete Old/Duplicate Files
```bash
# Remove duplicate SQL files (duplicates of Flyway migrations)
rm project-docs/migration-application-table.sql
rm project-docs/test-data.sql

# Remove outdated structure document
rm "project-docs/The SSR Project Structure (Monolith).txt"
```

### Step 5: Cleanup Empty Directory
```bash
# Remove old docs/ directory (now empty except .gitkeep)
rm -rf docs/
```

### Step 6: Update .gitignore if Needed
```bash
# Check if docs/ is referenced in .gitignore
grep "docs/" .gitignore
# Update if necessary
```

## Files to Keep at Root Level

**HANDOVER.md:**
- Purpose: Team handover documentation
- Justification: Primary document for new team members, should be easily visible
- Location: Root (keep as-is)

**README.md:**
- Purpose: Project overview and setup instructions
- Justification: Standard GitHub practice
- Location: Root (keep as-is)

## Verification After Reorganization

**Check that nothing broke:**
```bash
# Ensure no references to old paths exist
grep -r "docs/ADR" .
grep -r "project-docs/migration-application" .
grep -r "project-docs/test-data.sql" .

# Verify Flyway migrations still work
ls -la src/main/resources/db/migration/
# Should show V1, V2, V3, V4 files
```

## Summary of Changes

| Action | File | Reason |
|--------|------|--------|
| **MOVE** | docs/ADR-*.md → project-docs/ADR/ | Consolidate documentation |
| **MOVE** | CICD-REPORT.md → project-docs/reports/ | Organize standalone reports |
| **MOVE** | README-TESTING.md → project-docs/reports/ | Organize standalone reports |
| **MOVE** | ARCHITECTURE-VIOLATION-REPORT.md → project-docs/reports/ | Organize standalone reports |
| **DELETE** | project-docs/migration-application-table.sql | Duplicate of V3 migration |
| **DELETE** | project-docs/test-data.sql | Duplicate of V4 migration |
| **DELETE** | project-docs/The SSR Project Structure (Monolith).txt | Outdated planning doc |
| **DELETE** | docs/ (directory) | Replaced by project-docs/ADR/ |
| **KEEP** | HANDOVER.md (root) | Primary team handover doc |
| **KEEP** | README.md (root) | Standard project readme |
| **KEEP** | project-docs/Course-Provided-instructions/ | Course materials |

## Final Structure Benefits

✅ **Single documentation directory** - All docs in project-docs/
✅ **Clear organization** - ADR/, reports/, course materials separated
✅ **No duplicates** - Removed redundant SQL files
✅ **No outdated files** - Removed planning documents
✅ **Easy to navigate** - Logical structure for team members
✅ **Standard practice** - Common pattern in professional projects

## Notes

- **No commits required** - This is documentation cleanup only
- **Can be done in one PR** - Simple file moves and deletes
- **Low risk** - Moving documentation doesn't affect application code
- **Consider doing with architecture fix** - Could combine into single cleanup PR

# ADR-002: Flyway Database Migrations

**Status:** Implemented
**Date:** 2026-02-14
**Decision Makers:** Development Team

## Context

The IV1201 Recruitment Application initially used manual SQL scripts located in `src/main/resources/db/` that needed to be executed manually or through custom initialization code. As the application moved towards automated CI/CD and cloud deployment, we needed a robust database migration strategy that would:

- Automatically apply database changes during deployment
- Track which migrations have been applied
- Ensure consistency across environments (dev, staging, production)
- Support rollback capabilities
- Integrate with Spring Boot

## Decision

We adopted **Flyway** as our database migration tool and restructured our database scripts to follow Flyway's versioned migration pattern.

### Implementation Details

**Migration File Structure:**
```
src/main/resources/db/migration/
├── V1__schema.sql              (renamed from 00-schema.sql)
├── V2__password_migration.sql  (renamed from 01-password-migration.sql)
├── V3__application_table.sql   (renamed from 02-application-table.sql)
└── V4__test_data.sql           (renamed from 03-test-data.sql)
```

**Naming Convention:**
- Prefix: `V` (for versioned migrations)
- Version: Sequential number (1, 2, 3, 4)
- Separator: `__` (double underscore)
- Description: Human-readable description with underscores

**Dependencies Added to pom.xml:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**Execution:**
- Flyway runs automatically on Spring Boot application startup
- Creates `flyway_schema_history` table to track migrations
- Only applies migrations that haven't been run yet
- Validates migration checksums to prevent tampering

## Alternatives Considered

### Alternative 1: Liquibase
- **Pros:** XML/YAML/JSON format, more features (conditional logic, preconditions)
- **Cons:** More complex, steeper learning curve, XML verbosity
- **Rejected:** Flyway's SQL-first approach is simpler and more familiar

### Alternative 2: Manual SQL Scripts with Custom Init
- **Pros:** Simple, no additional dependencies
- **Cons:** No version tracking, manual execution required, error-prone
- **Rejected:** Not suitable for automated deployments

### Alternative 3: JPA Schema Generation
- **Pros:** No migration files, automatic schema updates
- **Cons:** No control over schema changes, dangerous for production, no data migrations
- **Rejected:** Too risky for production use

## Consequences

### Positive
- **Automated Migration:** Database updates happen automatically during deployment
- **Version Control:** Clear history of all database changes
- **Environment Consistency:** Same migrations apply to all environments
- **Rollback Support:** Can track and potentially rollback changes
- **Team Collaboration:** Clear understanding of database state across team
- **Azure Compatibility:** Works seamlessly with Azure deployments

### Negative
- **Additional Dependency:** Adds Flyway to the project
- **Learning Curve:** Team must understand Flyway conventions
- **Migration Discipline Required:** Cannot easily modify existing migrations
- **Checksum Validation:** Changes to applied migrations cause errors

### Migration-Specific Considerations

**V1 - Schema:**
- Contains complete database schema
- Includes seed data (users, competencies)
- Fixed for Azure by removing OWNER statements (see ADR-001)

**V2 - Password Migration:**
- One-time BCrypt hashing using pgcrypto
- Requires pgcrypto extension in Azure (see ADR-001)
- Compatible with Spring Security BCryptPasswordEncoder

**V3 - Application Table:**
- Adds application submission functionality

**V4 - Test Data:**
- Adds additional test/seed data

## Best Practices Established

1. **Never modify applied migrations** - Create new versioned migrations instead
2. **Include rollback scripts if needed** - Though Flyway doesn't auto-rollback
3. **Test migrations locally first** - Use `docker-compose up` to verify before pushing
4. **Keep migrations small and focused** - One logical change per migration
5. **Use meaningful descriptions** - Clear migration file names

## Configuration

**Application Properties:**
```properties
# Flyway is enabled by default in Spring Boot
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```

## Verification

After deployment, verify migrations:

```sql
-- Check migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- Should show all 4 migrations as successful
```

## References

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- Migration Files: `src/main/resources/db/migration/`
- Related ADRs:
  - ADR-001 (Azure Deployment Fixes)
  - ADR-003 (CI/CD Pipeline - validates migrations on every build)

## Future Considerations

- Consider adding repeatable migrations for views/procedures (prefix: `R__`)
- Evaluate baseline migrations for existing production databases
- Consider Flyway Teams edition for advanced features (undo migrations, dry runs)

---

## Simplified ADR Entry (Wiki Format)

**Database Migration with Flyway**

Date: 2026-02-14  Status: Implemented

Decision: Use Flyway for versioned database migrations instead of manual SQL scripts.

Description: Migrated from manual SQL scripts in `src/main/resources/db/` to Flyway's versioned migration system. Migrations now run automatically on application startup, providing version control for database changes, consistent schema across environments, and automated deployment compatibility. Files renamed to follow Flyway convention (V1__schema.sql, V2__password_migration.sql, etc.). Integrates seamlessly with Spring Boot and Azure deployments.

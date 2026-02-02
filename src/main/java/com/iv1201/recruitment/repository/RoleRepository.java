package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database layer - queries role table
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}

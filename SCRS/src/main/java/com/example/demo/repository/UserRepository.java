/*
 * File: src/main/java/com/example/demo/repository/UserRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
// Class Summary: Repository class that is the data access layer using Spring Data JPA.
// @Repository marks the data access layer and enables exception translation.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    List<User> findByDepartmentIgnoreCase(String department);
    @Query("select distinct u from User u join u.roles r where r = :role")
    List<User> findByRole(@Param("role") String role);
    @Query("select distinct u from User u join u.roles r where u.department = :department and r = :role")
    List<User> findByDepartmentAndRole(@Param("department") String department, @Param("role") String role);
}

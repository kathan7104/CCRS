/*
 * File: src/main/java/com/example/demo/repository/CourseRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;
import com.example.demo.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdForUpdate(@Param("id") Long id);
    boolean existsByRemainingSeatsGreaterThan(int seats);
    long countByRemainingSeatsGreaterThan(int seats);
}

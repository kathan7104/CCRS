// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Course;
// Import statement: brings a class into scope by name.
import jakarta.persistence.LockModeType;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.Lock;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.Query;
// Import statement: brings a class into scope by name.
import org.springframework.data.repository.query.Param;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Interface declaration: defines a contract of methods.
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Statement: Optional<Course> findByCode(String code);
    Optional<Course> findByCode(String code);

    // Annotation: adds metadata used by frameworks/tools.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Annotation: adds metadata used by frameworks/tools.
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    // Statement: Optional<Course> findByIdForUpdate(@Param("id") Long id);
    Optional<Course> findByIdForUpdate(@Param("id") Long id);
// Closes the current code block.
}

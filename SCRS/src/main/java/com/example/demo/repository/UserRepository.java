// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Repository;

// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Repository for `User` entities. Provides common lookup methods.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Repository
// Interface declaration: defines a contract of methods.
public interface UserRepository extends JpaRepository<User, Long> {

    // Statement: Optional<User> findByEmail(String email);
    Optional<User> findByEmail(String email);

    // Statement: Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByMobileNumber(String mobileNumber);

    // Statement: boolean existsByEmail(String email);
    boolean existsByEmail(String email);

    // Statement: boolean existsByMobileNumber(String mobileNumber);
    boolean existsByMobileNumber(String mobileNumber);
// Closes the current code block.
}

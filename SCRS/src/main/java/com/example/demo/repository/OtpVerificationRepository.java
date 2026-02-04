// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.Modifying;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.Query;
// Import statement: brings a class into scope by name.
import org.springframework.data.repository.query.Param;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Repository;

// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Repository for `OtpVerification` records. Contains queries to find valid OTPs
 // Comment: explains code for readers.
 * and to mark OTPs as used.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Repository
// Interface declaration: defines a contract of methods.
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // Annotation: adds metadata used by frameworks/tools.
    @Query("SELECT o FROM OtpVerification o WHERE o.identifier = :identifier AND o.otp = :otp " +
           // Statement: "AND o.otpType = :type AND o.used = false AND o.expiresAt > :now")
           "AND o.otpType = :type AND o.used = false AND o.expiresAt > :now")
    // Statement: Optional<OtpVerification> findValidOtp(
    Optional<OtpVerification> findValidOtp(
        // Annotation: adds metadata used by frameworks/tools.
        @Param("identifier") String identifier,
        // Annotation: adds metadata used by frameworks/tools.
        @Param("otp") String otp,
        // Annotation: adds metadata used by frameworks/tools.
        @Param("type") OtpVerification.OtpType type,
        // Annotation: adds metadata used by frameworks/tools.
        @Param("now") LocalDateTime now
    // Statement: );
    );

    // Annotation: adds metadata used by frameworks/tools.
    @Modifying
    // Annotation: adds metadata used by frameworks/tools.
    @Query("UPDATE OtpVerification o SET o.used = true WHERE o.identifier = :identifier AND o.otpType = :type")
    // Statement: void markUsedByIdentifierAndType(@Param("identifier") String identifier, @Par...
    void markUsedByIdentifierAndType(@Param("identifier") String identifier, @Param("type") OtpVerification.OtpType type);

    // Statement: void deleteByExpiresAtBefore(LocalDateTime cutOff);
    void deleteByExpiresAtBefore(LocalDateTime cutOff);
// Closes the current code block.
}

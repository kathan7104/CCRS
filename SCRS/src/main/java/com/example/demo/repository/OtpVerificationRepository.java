package com.example.demo.repository;
import com.example.demo.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Spring Data repository for OTP Verification.
 */
@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    @Query("SELECT o FROM OtpVerification o WHERE o.identifier = :identifier AND o.otp = :otp " +
           "AND o.otpType = :type AND o.used = false AND o.expiresAt > :now")
    Optional<OtpVerification> findValidOtp(
        @Param("identifier") String identifier,
        @Param("otp") String otp,
        @Param("type") OtpVerification.OtpType type,
        @Param("now") LocalDateTime now
    );
    @Modifying
    @Query("UPDATE OtpVerification o SET o.used = true WHERE o.identifier = :identifier AND o.otpType = :type")
    void markUsedByIdentifierAndType(@Param("identifier") String identifier, @Param("type") OtpVerification.OtpType type);
    void deleteByExpiresAtBefore(LocalDateTime cutOff);
}

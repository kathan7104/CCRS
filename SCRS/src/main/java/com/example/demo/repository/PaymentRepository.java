/*
 * File: src/main/java/com/example/demo/repository/PaymentRepository.java
 * Role: Repository
 * MVC Fit: Data access layer using Spring Data JPA.
 * Connects To: Service uses Repository for CRUD queries
 */

package com.example.demo.repository;
import com.example.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// Class Summary: Repository class that is the data access layer using Spring Data JPA.
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findTopByInvoiceIdAndGatewayOrderIdOrderByIdDesc(Long invoiceId, String gatewayOrderId);
    List<Payment> findByInvoiceId(Long invoiceId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.invoice.id = :invoiceId and p.status = com.example.demo.entity.Payment$PaymentStatus.SUCCESS")
    BigDecimal getSuccessfulAmountByInvoiceId(@Param("invoiceId") Long invoiceId);
    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = com.example.demo.entity.Payment$PaymentStatus.SUCCESS")
    BigDecimal getTotalSuccessfulAmount();
}

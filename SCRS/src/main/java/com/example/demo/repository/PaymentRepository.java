// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Payment;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;

// Import statement: brings a class into scope by name.
import java.util.Optional;
// Import statement: brings a class into scope by name.
import java.util.List;

// Interface declaration: defines a contract of methods.
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Statement: Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByTransactionId(String transactionId);

    // Statement: List<Payment> findByInvoiceId(Long invoiceId);
    List<Payment> findByInvoiceId(Long invoiceId);
// Closes the current code block.
}

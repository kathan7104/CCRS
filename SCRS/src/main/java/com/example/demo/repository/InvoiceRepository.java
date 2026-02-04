// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.Invoice;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;

// Import statement: brings a class into scope by name.
import java.util.Optional;
// Import statement: brings a class into scope by name.
import java.util.List;

// Interface declaration: defines a contract of methods.
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Statement: Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // Statement: List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStudentId(Long studentId);
// Closes the current code block.
}

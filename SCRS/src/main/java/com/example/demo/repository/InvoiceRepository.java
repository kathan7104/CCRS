package com.example.demo.repository;
import com.example.demo.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

/**
 * Spring Data repository for Invoice.
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStudentId(Long studentId);
}

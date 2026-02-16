package com.example.demo.repository;
import com.example.demo.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    @Query("select coalesce(sum(i.totalAmount), 0) from Invoice i where i.status = com.example.demo.entity.Invoice$InvoiceStatus.PAID")
    BigDecimal getTotalPaidRevenue();
}

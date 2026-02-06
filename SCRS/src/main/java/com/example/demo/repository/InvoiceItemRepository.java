package com.example.demo.repository;
import com.example.demo.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data repository for Invoice Item.
 */
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
}

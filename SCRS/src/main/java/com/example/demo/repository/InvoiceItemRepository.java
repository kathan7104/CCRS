// Package declaration: groups related classes in a namespace.
package com.example.demo.repository;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.InvoiceItem;
// Import statement: brings a class into scope by name.
import org.springframework.data.jpa.repository.JpaRepository;

// Import statement: brings a class into scope by name.
import java.util.List;

// Interface declaration: defines a contract of methods.
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    // Statement: List<InvoiceItem> findByInvoiceId(Long invoiceId);
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
// Closes the current code block.
}

package com.apteka.faktura.core;

import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.model.InvoicePosition;

import java.util.List;

/**
 * Invoice service.
 */
public interface InvoiceService {
    List<Invoice> getActiveInvoices();

    List<InvoicePosition> getInvoicePositions(int dokfId);

    void saveInvoice(InvoicePosition invoicePosition, int amount);
}

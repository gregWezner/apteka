package com.apteka.faktura.model;

import java.util.List;

/**
 * Created by grzegorz.weznerowicz on 2015-08-11.
 */
public interface InvoiceMapper {
    List<Invoice> getAllInvoices(InvoiceParameter invoiceParameter);
    List<InvoicePosition> getInvoicePositions(int dokfId);
}

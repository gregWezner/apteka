package com.apteka.faktura.model;

/**
 * Created by grzegorz.weznerowicz on 2015-08-12.
 */
public interface ProcessedInvoiceMapper {
    ProcessedInvoice getProcessedInvoice(int invoiceParameter);

    int getScannedProductAmount(int id);

    void insertScannedProductAmount(ScannedProduct scannedProduct);
}

package com.apteka.faktura.model;

import javafx.beans.property.*;

import java.util.Date;

public class Invoice implements Comparable<Invoice>{

    private final StringProperty invoiceNo;
    private final IntegerProperty dokfId;
    private final IntegerProperty noOfPositions;
    private final ObjectProperty<Date> issueDate;
    private double amountOfProcessedGoods;
    private int status;

    public Invoice() {
        this(null, 0, 0, null);
    }

    public Invoice(String invoiceNo, Integer dokfId, Integer noOfPositions, Date issueDate) {
        this.invoiceNo = new SimpleStringProperty(invoiceNo);
        this.dokfId = new SimpleIntegerProperty(dokfId);
        this.noOfPositions = new SimpleIntegerProperty(noOfPositions);
        this.issueDate = new SimpleObjectProperty<>(issueDate);
    }

    public String getInvoiceNo() {
        return invoiceNo.get();
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo.set(invoiceNo);
    }

    public StringProperty invoiceNoProperty() {
        return invoiceNo;
    }

    public int getDokfId() {
        return dokfId.get();
    }

    public void setDokfId(int dokfId) {
        this.dokfId.set(dokfId);
    }

    public IntegerProperty dokfIdProperty() {
        return dokfId;
    }

    public int getNoOfPositions() {
        return noOfPositions.get();
    }

    public void setNoOfPositions(int noOfPositions) {
        this.noOfPositions.set(noOfPositions);
    }

    public IntegerProperty noOfPositionsProperty() {
        return noOfPositions;
    }

    public Date getIssueDate() {
        return issueDate.get();
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate.set(issueDate);
    }

    public ObjectProperty<Date> issueDateProperty() {
        return issueDate;
    }

    public Invoice withAmountOfProcessedGoods(long amount) {
        this.amountOfProcessedGoods =amount;
        this.status = (amountOfProcessedGoods==noOfPositions.get() ? 1 : 0);
        return this;
    }

    public boolean isProcessed() {
        return 1 == status;
    }

    public IntegerProperty percentProperty() {
        return new SimpleIntegerProperty(Double.valueOf((amountOfProcessedGoods / noOfPositions.get())*100).intValue());
    }

    public void setAmountOfProcessedGoods(Long amountOfProcessedGoods) {
        this.amountOfProcessedGoods = amountOfProcessedGoods;
        this.status = (amountOfProcessedGoods==noOfPositions.get() ? 1 : 0);
    }

    @Override
    public int compareTo(Invoice o) {
        if(!this.isProcessed()) {
            if (o.isProcessed())
                return o.dokfId.get() - dokfId.get();
            else
                return -1;
        } else{
            if (o.isProcessed())
                return o.dokfId.get() - dokfId.get();
            else
                return 1;
        }
    }
}

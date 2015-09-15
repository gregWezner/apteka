package com.apteka.faktura.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by grzegorz.weznerowicz on 2015-08-11.
 */
public class InvoiceParameter {
    private int numberOfDaysInPast;
    private int idCompany;
    private int idToStartWith;
    private List<Integer> statuses;

    public Date getNumberOfDaysInPast() {
        return Date.from(LocalDate.now().minusDays(numberOfDaysInPast).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public InvoiceParameter(int numberOfDaysInPast, int idCompany, int idToStartWith, List<Integer> statuses) {
        this.numberOfDaysInPast = numberOfDaysInPast;
        this.idCompany = idCompany;
        this.idToStartWith = idToStartWith;
        this.statuses = statuses;
    }
}

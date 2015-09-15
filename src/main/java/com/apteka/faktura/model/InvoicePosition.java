package com.apteka.faktura.model;

import javafx.beans.property.*;

import java.util.Date;

public class InvoicePosition {
    private final StringProperty nazwa;
    private final int idtowr;
    private final StringProperty kodKr;
    private final IntegerProperty ilzkl;
    private final IntegerProperty ilakt;
    private final int MNOZN;
    private final Date datwz;
    private final String seria;
    private final int iddokf;
    private final int id;
    private final IntegerProperty ilosc;
    private final DoubleProperty cena;

    public InvoicePosition(){
        this(null,0,null,0,0,0,null,null,0,0);
    }

    public InvoicePosition(String nazwa, int idtowr, String kodKr, int ilzkl, int ilakt, int MNOZN, Date datwz, String seria, int iddokf, int id) {
        this.nazwa = new SimpleStringProperty(nazwa);
        this.idtowr = idtowr;
        this.kodKr = new SimpleStringProperty(kodKr);
        this.ilzkl = new SimpleIntegerProperty(ilzkl);
        this.ilakt = new SimpleIntegerProperty(ilakt);
        this.MNOZN = MNOZN;
        this.datwz = datwz;
        this.seria = seria;
        this.iddokf = iddokf;
        this.id = id;
        this.ilosc = new SimpleIntegerProperty(0);
        this.cena = new SimpleDoubleProperty(0d);
    }

    public String getNazwa() {
        return nazwa.get();
    }

    public void setNazwa(String nazwa) {
        this.nazwa.set(nazwa);
    }

    public StringProperty nazwaProperty() {
        return nazwa;
    }

    public String getKodKr() {
        return kodKr.get();
    }

    public void setKodKr(String kodkr) {
        this.kodKr.set(kodkr);
    }

    public StringProperty kodKrProperty() {
        return kodKr;
    }

    public int getIlzkl() {
        return ilzkl.get();
    }

    public void setIlzkl(int ilzkl) {
        this.ilzkl.set(ilzkl);
    }

    public IntegerProperty ilzklProperty() {
        return ilzkl;
    }

    public int getIlakt() {
        return ilakt.get();
    }

    public void setIlakt(int ilakt) {
        this.ilakt.set(ilakt);
    }

    public IntegerProperty ilaktProperty() {
        return ilakt;
    }

    public int getIlosc() {
        return ilosc.get();
    }

    public void setIlosc(int ilosc) {
        this.ilosc.set(ilosc);
    }

    public IntegerProperty iloscProperty() {
        return ilosc;
    }

    public double getCena() {
        return cena.get();
    }

    public void setCena(int cena) {
        this.cena.set(cena);
    }

    public DoubleProperty cenaProperty() {
        return cena;
    }

    public int getId() {
        return id;
    }

    public InvoicePosition withIlosc(int scannedProductAmount) {
        ilosc.setValue(scannedProductAmount);
        return this;
    }

    public boolean isCorrect() {
        return ilosc.get()==ilzkl.get() || 0==ilzkl.get();
    }

//    public boolean isIncorrect() {
//        return -9999 != ilosc.get() && ilosc.get()!=ilzkl.get();
//    }

    public boolean isToValidate() {
        return 0 == ilosc.get();
    }

    public void setCorrect() {
        this.ilosc.setValue(ilzkl.get());
    }

    public boolean isOverflow() {
        return ilosc.get()>ilzkl.get();
    }

    public int getIddokf() {
        return iddokf;
    }

    public void setAmount(int amount) {
        this.ilosc.setValue(amount);
    }

    public String getSeria() {
        return seria;
    }

    public Date getData() {
        return datwz;
    }

    public String getStatus() {
        if(isCorrect()){
            return "ok";
        } else if(isToValidate()){
            return "brak";
        } else if(isOverflow()){
            return "nadw";
        } else {
            return "niedobor";
        }
    }

    public void addAmount(int amount) {
        this.ilosc.setValue(ilosc.get() + amount);
    }

    public boolean isOpen() {
        return ilosc.get()<ilzkl.get();
    }
}

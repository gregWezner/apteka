package com.apteka.faktura.model;

/**
 * Created by grzegorz.weznerowicz on 2015-08-14.
 */
public class ScannedProduct {
    private int iddokf;
    private int idkzak;
    private int ilosc;
    private String kodkr;

    public ScannedProduct(int iddokf, int idkzak, int ilosc, String kodkr) {
        this.iddokf = iddokf;
        this.idkzak = idkzak;
        this.ilosc = ilosc;
        this.kodkr = kodkr;
    }
}

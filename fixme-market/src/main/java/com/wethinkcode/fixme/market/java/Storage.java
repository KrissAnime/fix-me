package com.wethinkcode.fixme.market.java;

public class Storage {
    private static Storage ourInstance = new Storage();
    private static Instrument ZAR = new Instrument(10, (float) 1.9);
    private static Instrument USD = new Instrument(5, (float) 11.3);

    public static Storage getInstance() {
        return ourInstance;
    }

    public Instrument getUSD() {
        return USD;
    }

    public Instrument getZAR() {
        return ZAR;
    }
}

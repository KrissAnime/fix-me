package com.wethinkcode.fixme.market.java;

public class Instrument  implements Instruments{
    private int count = 0;
    private float price = 0;

    public Instrument(int count, float price){
        this.count = count;
        this.price = price;
    }


    @Override
    public void add(int num) {
        if (num < 0)
            return;
        this.count += num;
    }

    @Override
    public void sub(int num) {
        this.count = (this.count - num < 0) ? 0 : this.count - num;
    }

    @Override
    public int count() {
        return this.count;
    }

    @Override
    public float price() {
        return this.price;
    }
}

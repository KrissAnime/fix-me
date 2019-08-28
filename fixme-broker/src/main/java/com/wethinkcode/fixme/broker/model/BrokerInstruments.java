package com.wethinkcode.fixme.broker.model;

import lombok.Getter;

public class BrokerInstruments {
    @Getter
    private Double usdInstrument = 0;
    @Getter
    private Float usdPrice;
    @Getter
    private Double zarInstrument = 0;
    @Getter
    private Float zarPrice;

    public BrokerInstruments() {

    }

    public void AddInstrument(Float quantity, Float price, String type){
        if(type == "zar") {
            this.zarInstrument = this.zarInstrument + quantity;
            this.zarPrice = price;
        }

        if(type == "usd") {
            this.usdInstrument = this.usdInstrument + quantity;
            this.usdPrice = price;
        }
    }
}

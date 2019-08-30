package com.wethinkcode.fixme.market.java;

public class MarketMessageHandler {
    int BrokerID;
    float price;
    int qty;
    String instrument;
    int checksum;
    int action;
    String market;
    private Instruments item;
    Reply reply;



    public MarketMessageHandler(String msg, ChannelDetails attachment, Reply reply){

        this.reply = reply;
        String[] MsgSections= msg.split("\\|");
        for (String keyValue: MsgSections) {
            String[] MsgSection = keyValue.split("=");
            if (MsgSection.length == 2){
                System.out.println(MsgSection[0] + " - " + MsgSection[1]);
                switch (MsgSection[0]) {
                    case "55":
//                        System.out.println("instrument " + MsgSection[1]);
                        this.instrument = MsgSection[1];
                        break;
                    case "38":
                        this.qty = Integer.parseInt(MsgSection[1]);
                        break;
                    case "MARKET":
//                        System.out.println("Market " + MsgSection[1]);
                        this.market = MsgSection[1];
                        break;
                    case "44":
//                        System.out.println("price " + MsgSection[1]);
                        this.price = Float.parseFloat(MsgSection[1]);
                        break;
                    case "50":
//                        System.out.println("brokerID " + MsgSection[1]);
                        this.BrokerID = Integer.parseInt(MsgSection[1]);
                        break;
                    case "54":
//                        System.out.println("action " + MsgSection[1]);
                        this.action = Integer.parseInt(MsgSection[1]);
                        break;
                    case "10":
//                        System.out.println("checksum " + MsgSection[1]);
                        this.checksum = Integer.parseInt(MsgSection[1]);
                        break;
                    default:
                }
            }
        }

        System.out.println(Storage.getInstance().getZAR().count());

        instrumentCheck();
        System.out.println(this.item.count() + " for $" + this.item.price());
        Storage.getInstance().getUSD().sub(3);
        System.out.println(this.item.count() + " for $" + this.item.price());
        System.out.println(Storage.getInstance().getZAR().count());
    }

    public int instrumentCheck(){
        if (this.instrument.equals("ZAR"))
            this.item = Storage.getInstance().getZAR();
        else if (this.instrument.equals("USD"))
            this.item = Storage.getInstance().getUSD();


        if (this.qty <= this.item.count() && this.price == this.item.price()){
            this.item.sub(this.qty);
            System.out.println("SUCCESS");


        } else {
            System.out.println("FAILURE");
        }

        return 0;

    }

    public int getStatus() {
        //todo
        return 1;
    }
}
//    String Bmsg = "|50=00001|MARKET=jse|55=ZAR|38=2|44=0.5|54=1|10=066";

//        Message structure:

//        Instrument => 55 = ? (String)
//        Quantity => 38 = ? (Int)
//        Market => MARKET = ? (String)
//        Price => 44 = ? (Float)

//        56 => Destination ID
//        50 => Sender ID
//        39 => Execution Status (Executed = 2 / Rejected = 8 )
//        55 => Instrument (Symbol)
//        54 => Side (Buy = 1 / Sell = 2)

//        Market Response => |56=?|50=?|39=?|10=checksum|
//        Broker Message => |50=?|MARKET=?|55=?|38=?|44=?|54=?|10=checksum| (edited)

//        |50=?|MARKET=?|55=?|38=?|44=?|54=?|10=066

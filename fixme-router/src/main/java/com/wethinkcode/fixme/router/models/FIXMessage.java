package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import org.json.JSONObject;

public class FIXMessage {
    private @Getter String messageTag = "35";
    private @Getter String targetIDTag = "56";
    private @Getter String senderIDTag = "49";
    private @Getter String checkSumTag = "10";
    private @Getter String instrumentTag = "INSTRUMENT";
    private @Getter String marketFieldTag = "MARKET";
    private @Getter String quantityTag = "QUANTITY";
    private @Getter String priceTag = "PRICE";

    private @Getter String message;
    private @Getter String order;
    private @Getter String market;
    private @Getter String targetID;
    private @Getter String senderID;
    private @Getter String checkSum;
    private @Getter String instrument;
    private @Getter double price;
    private @Getter double quantity;

    /** FIX MESSAGE SYSTEM
     * MSGTYPE = 6; SIDE = 1(BUY)/2(SELL)
     *
     *
     */

    public FIXMessage (String line) throws ArrayIndexOutOfBoundsException {
        System.out.println("Original Line " + line);
        String[] lineSplit = line.split("\\|");

        senderID = lineSplit[0].split("=")[1];
        message = lineSplit[1].split("=")[1];
        targetID = lineSplit[2].split("=")[1];
        market = lineSplit[3].split("=")[1];

        if (message.equals("6")) {
            switch (lineSplit[4].split("=")[1]) {
                case "1":
                    order = "BUY";
                    break;
                case "2":
                    order = "SELL";
                    break;
            }
            instrument = lineSplit[5].split("=")[1];
            price = Double.parseDouble(lineSplit[6].split("=")[1]);
            quantity = Double.parseDouble(lineSplit[7].split("=")[1]);
            checkSum = lineSplit[8].split("=")[1];
        } else {
            checkSum = lineSplit[4].split("=")[1];
        }
    }

    @Override
    public String toString() {
        JSONObject fixMessage = new JSONObject();
        fixMessage.put("MessageType", message);
        fixMessage.put("TargetID", targetID);
        fixMessage.put("SenderID", senderID);
        fixMessage.put("Order", order);

        fixMessage.put(instrumentTag, instrument);
        fixMessage.put(marketFieldTag, market);
        fixMessage.put(quantityTag, quantity);
        fixMessage.put(priceTag, price);

        return fixMessage.toString();
    }

    public String MarshallMessage() {
        StringBuilder message = new StringBuilder();

        //0
        message.append(senderIDTag);
        message.append(senderID);

        //1
        message.append(messageTag);
        message.append(this.message);

        //2
        message.append(targetIDTag);
        message.append(targetID);

        //3
        message.append(marketFieldTag);
        message.append(market);



        if (this.message.equals("6")) {
            //4
            message.append(instrumentTag);
            message.append(instrument);

            //5
            message.append(priceTag);
            message.append(price);

            //6
            message.append(quantityTag);
            message.append(quantity);

        }
        message.append(checkSumTag);
        message.append(checkSum);
        return message.toString();
    }
}

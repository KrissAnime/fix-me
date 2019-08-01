package com.wethinkcode.fixme.router.models;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

//MsgType
//        Tag     Value                             Meaning
//        35      D                                 New Order Single
//        35      8                                 Execution Report
//        35      3                                 Session Rejected
//
//        Status
//        Tag     Value                             Meaning
//        39      0                                 New Order Acknowledged
//        39      8                                 Order Rejected
//        39      1                                 Partial Execution
//        39      2                                 Complete Execution
//
//        Side
//        Tag     Value                             Meaning
//        54      1                                 Buy
//        54      2                                 Sell
//
//        OrderQty
//        Tag     Value                             Meaning
//        38      '0' ... '1000000000'(int)         Quantity of product being ordered
//
//        Price
//        Tag     Value                             Meaning
//        44      '0' ... '99999999.9999'(float)    Price per share
//
//        OrderType
//        Tag     Value                             Meaning
//        40      1                                 Market
//        40      2                                 Limit
//
//        Routing
//        Tag     Value                             Meaning
//        50      ?                                 Sender ID
//        49      ?                                 Company ID
//        56      ?                                 Receiver ID
//
//        Symbol
//        Tag     Value   Meaning
//        55      ?       Value is a symbol
//        MsgSeqNum
//        Tag     Value                             Meaning
//        34      int                               Integer message sequence number
//
//        SendingTime
//        Tag     Value                             Meaning
//        52      ?                                 Time of message transmission (always expressed in GMT)

public class FIXMessage {
    private final int SymbolTag = 55;
    private final int RoutingSenderIDTag = 50;
    private final int RoutingCompanyIDTag = 49;
    private final int RoutingReceiverIDTag = 56;
    private final int OrderTypeMarketTag = 40;
    private final int PriceTag = 44;
    private final int OrderQuantityTag = 38;
    private final int SideTag = 54;
    private final int StatusTag = 39;
    private final int MessageTypeTag = 35;
    private final int CheckSumTag = 10;
    private final int MessageSequenceNumberTag = 34;
    private final int SendingTimeTag = 52;

    private @Getter @Setter String Symbol;
    private @Getter @Setter String RoutingSenderID;
    private @Getter @Setter String RoutingCompanyID;
    private @Getter @Setter String RoutingReceiverID;
    private @Getter @Setter String OrderTypeMarket;
    private @Getter @Setter float Price = 0;
    private @Getter @Setter int OrderQuantity = 0;
    private @Getter @Setter String Side;
    private @Getter @Setter String Status;
    private @Getter @Setter String MessageType;
    private @Getter @Setter int MessageSequenceNumber;
    private @Getter String SendingTime;
    private @Getter String CheckSum;

    private @Getter String marshallMessage;

    public FIXMessage(String marshallMessage){
        this.marshallMessage = marshallMessage;
//        ParseMessage(marshallMessage);
//        System.out.println("Header " + ConstructHeader());
//        System.out.println("Body " + ConstructBody());
//        System.out.println("Trailer " + ConstructTrailer());

//        System.out.println("Final constructed message " + MarshallMessage());
    }

    //Example fix message which would request authentication from the server
    //8=FIX.4.4|9=126|35=A|49=theBroker.12345|56=CSERVER|34=1|52=20170117- 08:03:04|57=TRADE|50=any_string|98=0|108=30|141=Y|553=12345|554=passw0rd!|10=131|

    //Example server response to the above FIX message
    //8=FIX.4.4|9=106|35=A|34=1|49=CSERVER|50=TRADE|52=20170117- 08:03:04.509|56=theBroker.12345|57=any_string|98=0|108=30|141=Y|10=066|

    public String MarshallMessage() {
        return (ConstructHeader() + ConstructBody() + ConstructTrailer());
    }

    private void ParseMessage() {
        String[] splitLine = marshallMessage.split("\\|");
        for (String keyValue: splitLine) {
            String[] splitTags = keyValue.split("=");
            switch (splitTags[0]) {
                case "35":
                    MessageType = splitTags[1];
                    break;
                case "55":
                    Symbol = splitTags[1];
                    break;
                case "50":
                    RoutingSenderID = splitTags[1];
                    break;
                case "49":
                    RoutingCompanyID = splitTags[1];
                    break;
                case "56":
                    RoutingReceiverID = splitTags[1];
                    break;
                case "40":
                    OrderTypeMarket = splitTags[1];
                    break;
                case "44":
                    Price = Float.parseFloat(splitTags[1]);
                    break;
                case "38":
                    OrderQuantity = Integer.parseInt(splitTags[1]);
                    break;
                case "54":
                    Side = splitTags[1];
                    break;
                case "39":
                    Status = splitTags[1];
                    break;
                case "34":
                    MessageSequenceNumber = Integer.parseInt(splitTags[1]);
                    break;
                case "52":
                    SendingTime = splitTags[1];
                    break;
            }
        }
    }

    private String ConstructHeader() {
        CalculateBodyLength();
        StringBuilder header = new StringBuilder();

        //First append the fix version used for the messaging
        header.append("8=FIX.4.4|");

        //Append the body length of the message along with its tag
        header.append("9=" + CalculateBodyLength() + "|");

        //Indicate the message type so it can be handled correctly
        header.append("35=" + MessageType + "|");

        return header.toString();
    }

    //8=FIX.4.4|9=106|35=A|34=1|49=CSERVER|50=TRADE|52=20170117- 08:03:04.509|56=theBroker.12345|57=any_string|98=0|108=30|141=Y|10=066|
    private String ConstructBody() {
        StringBuilder body = new StringBuilder();
        body.append(MessageSequenceNumberTag + "=" + MessageSequenceNumber + "|");
        if (RoutingCompanyID != null) {
            body.append(RoutingCompanyIDTag + "=" + RoutingCompanyID + "|");
        }
        if (RoutingSenderID != null) {
            body.append(RoutingSenderIDTag + "=" + RoutingSenderID + "|");
        }

        body.append(SendingTimeTag + "=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|");

        if (RoutingReceiverID != null) {
            body.append(RoutingReceiverIDTag + "=" + RoutingReceiverID + "|");
        }

        if (Status != null) {
            body.append(StatusTag + "=" + Status + "|");
        }

        if (Side != null) {
            body.append(SideTag + "=" + Side + "|");
        }

        if (Symbol != null) {
            body.append(SymbolTag + "=" + Symbol + "|");
        }

        if (OrderTypeMarket != null) {
            body.append(OrderTypeMarketTag + "=" + OrderTypeMarket + "|");
        }

        if (OrderQuantity != 0) {
            body.append(OrderQuantityTag + "=" + OrderQuantity + "|");
        }
        if (Price != 0.0) {
            body.append(PriceTag + "=" + Price + "|");
        }

        return body.toString();
    }


    private String ConstructTrailer() {
        // Perform the calculation for the checksum which is the sum of all the
        // ascii values in the message not including the checksum, all '|' equal
        // 1 according to the documentation of FIX messages, modulo 256
        int sum = 0;

        for (int i = 0; i < marshallMessage.length(); i++) {
            if (marshallMessage.charAt(i) == '|') {
                sum += 1;
            } else {
                sum += (int)marshallMessage.charAt(i);
            }
        }

        CheckSum = String.valueOf(sum % 256);
        for (int i = 3; i > CheckSum.length(); i--) {
            CheckSum = "0" + CheckSum;
        }

        return CheckSumTag + "=" + CheckSum + "|";
    }

    private int CalculateBodyLength() {
        int bodyLength = 0;
        String[] splitMessage = marshallMessage.split("\\|");

        for (int i = 2; i < splitMessage.length; i++) {
            bodyLength += splitMessage[i].length() + 1;
        }
        return bodyLength;
    }

    @Override
    public String toString() {
        JSONObject Data = new JSONObject();

        Data.put(String.valueOf(MessageTypeTag), MessageType)
            .put(String.valueOf(StatusTag), Status)
            .put(String.valueOf(SideTag), Side)
            .put(String.valueOf(OrderQuantityTag), OrderQuantity)
            .put(String.valueOf(PriceTag), Price)
            .put(String.valueOf(OrderTypeMarketTag), OrderTypeMarket)
            .put(String.valueOf(RoutingReceiverIDTag), RoutingReceiverID)
            .put(String.valueOf(RoutingCompanyIDTag), RoutingCompanyID)
            .put(String.valueOf(RoutingSenderIDTag), RoutingSenderID)
            .put(String.valueOf(SymbolTag), Symbol)
            .put(String.valueOf(SendingTimeTag), SendingTime)
            .put(String.valueOf(MessageSequenceNumberTag), MessageSequenceNumber)
            .put(String.valueOf(CheckSumTag), CheckSum);

        return Data.toString(3);
    }
}

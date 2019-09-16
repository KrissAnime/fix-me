package com.wethinkcode.fixme.broker.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

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
    private @Getter
    @Setter
    int RoutingSenderID = 0;
    private @Getter
    @Setter
    int RoutingCompanyID = 0;
    private @Getter
    @Setter
    int RoutingReceiverID = 0;
    private @Getter @Setter String OrderTypeMarket;
    private @Getter @Setter float Price = 0;
    private @Getter @Setter Integer OrderQuantity = 0;
    private @Getter @Setter String Side;
    private @Getter @Setter String Status;
    private @Getter @Setter String MessageType;
    private @Getter @Setter int MessageSequenceNumber;
    private @Getter String SendingTime;
    private @Getter String CheckSum;

    private @Getter String marshallMessage;

     public FIXMessage(String marshallMessage){

            this.marshallMessage = marshallMessage;
            this.ParseMessage();
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
                    RoutingSenderID = Integer.valueOf(splitTags[1]);
                    break;
                case "49":
                    RoutingCompanyID = Integer.valueOf(splitTags[1]);
                    break;
                case "56":
                    RoutingReceiverID = Integer.valueOf(splitTags[1]);
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
                case "10":
                    CheckSum = splitTags[1];
                    break;
                default:
            }
        }
    }


    public String ConstructCheckSum(String message) {

        System.out.println(message);
        String chkmessage = message;
        chkmessage = chkmessage.replace('|', '\u0001');

        int sum = 0;
        int char_val = 0;

        for (int i = 0; i < chkmessage.length(); i++) {
            char_val = chkmessage.charAt(i);
            sum += char_val;
        }

        CheckSum = String.valueOf(sum % 256);
        for (int i = 3; i > CheckSum.length(); i--) {
            CheckSum = "0" + CheckSum;
        }
        return CheckSum;
    }
}

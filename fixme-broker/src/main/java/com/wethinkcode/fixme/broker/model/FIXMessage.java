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
    private @Getter @Setter float OrderQuantity = 0;
    private @Getter @Setter String Side;
    private @Getter @Setter String Status;
    private @Getter @Setter String MessageType;
    private @Getter @Setter int MessageSequenceNumber;
    private @Getter String SendingTime;
    private @Getter String CheckSum;

    private @Getter String marshallMessage;

     public FIXMessage(String marshallMessage){
      String valueToCheck = marshallMessage.split("\\|")[marshallMessage.split("\\|").length - 1].split("=")[1];

        System.out.println("Val to check:\t" + valueToCheck + " checksum val:\t" + !valueToCheck.equals(ConstructCheckSum(marshallMessage)) + " val length:\t");

        if (valueToCheck.length() != 3 && !valueToCheck.equals(ConstructCheckSum(marshallMessage))) {
            this.marshallMessage = null;
        } else {
            this.marshallMessage = marshallMessage;
//            ParseMessage();
        }
//        this.marshallMessage = marshallMessage;
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
//                    System.out.println("Default found in fix message tag:\t" + splitTags[0] + " message:\t" + splitTags[1]);
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
        if (RoutingCompanyID != 0) {
            body.append(RoutingCompanyIDTag + "=" + RoutingCompanyID + "|");
        }
        if (RoutingSenderID != 0) {
            body.append(RoutingSenderIDTag + "=" + RoutingSenderID + "|");
        }

        body.append(SendingTimeTag + "=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|");

        if (RoutingReceiverID != 0) {
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
        CheckSum = ConstructCheckSum(marshallMessage);

        return CheckSumTag + "=" + CheckSum + "|";
    }

    public String ConstructCheckSum(String message) {
        int sum = 0;
        int length;
//        String[] messageSplit = message.split("\\|");

        switch (message.charAt(message.length() - 1)){
            case '|':
                length = message.length() - 7;
                break;
            default:
                length = message.length() - 6;
        }

//        System.out.println("message length " + message.length());
//        System.out.println("Message split " + (message.split("\\|")[message.split("\\|").length - 1].length() - 1));
//        System.out.println("Final char " + message.charAt(length));
        System.out.println(message);
//        for (String splitMessage: messageSplit) {
//            String[] key = splitMessage.split("=");
//            if (!key[0].equals("8") && !key[0].equals("10") && !key[0].equals("9")) {
//                sum += key[0].length() + key[1].length() + 2;
//            }
//        }

        for (int i = 0; i < length; i++) {
            if (message.charAt(i) == '|') {
                sum += 1;
            } else {
                sum += message.charAt(i);
            }
//            System.out.println("Char " + message.charAt(i));
        }
        //8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|
        //0   + 0  +     5  +   10    +   10    +  7   +        21          + 5  +  7   +   0  = 65

        //8=FIX.4.4|9=106|35=A|34=1|49=CSERVER|50=TRADE|52=20170117- 08:03:04.509|56=theBroker.12345|57=any_string|98=0|108=30|141=Y|10=066|
        //                  5   +5  +11         +9      +26                         +19                 +14         +5  +7      +6+5
        CheckSum = String.valueOf(sum % 256);
        for (int i = 3; i > CheckSum.length(); i--) {
            CheckSum = "0" + CheckSum;
        }
       // System.out.println("Sum:\t" + sum +" Checksum:\t" + CheckSum);
        return CheckSum;
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
        return MarshallMessage();
    }

    //8=FIX.4.2|9=49|35=5|34=1|49=ARCA|52=20150916-04:14:05.306|56=TW|10=157|

    public String toJSONString() {
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

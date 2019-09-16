package com.wethinkcode.fixme.router.models;

/*
Quantity => 38 = ? (Int)
Market => MARKET = ? (String)
Price => 44 = ? (Float)
56 => Destination ID
50 => Sender ID
39 => Execution Status (Executed = 2 / Rejected = 8 )
55 => Instrument (Symbol)
54 => Action Side (Buy = 1 / Sell = 2)

Market Response => |56=?|50=?|39=?|10=checksum|
Broker Message => |50=?|MARKET=?|55=?|38=?|44=?|54=?|10=checksum|
*/

public class FIXMessage {
    
    String message;

    int quantity;
    String market;
    float price;
    int destID;
    int senderID;
    int excutionStatus;
    String instrument;
    int action;
    int checksum;

    

    FIXMessage(String message){
        this.message = message;
        parseMessage();
    }

    public void parseMessage(){
        String[] MsgSections= message.split("\\|");
        for (String keyValue: MsgSections) {
            String[] MsgSection = keyValue.split("=");
            if (MsgSection.length == 2){
                switch (MsgSection[0]) {
                    case "38":
                        this.quantity = Integer.parseInt(MsgSection[1]);
                        break;
                    case "MARKET":
//                        System.out.println("Market " + MsgSection[1]);
                        this.market = MsgSection[1];
                        break;
                    case "44":
//                        System.out.println("price " + MsgSection[1]);
                        this.price = Float.parseFloat(MsgSection[1]);
                        break;
                    case "56":
//                        System.out.println("brokerID " + MsgSection[1]);
                        this.destID = Integer.parseInt(MsgSection[1]);
                        break;
                    case "50":
//                        System.out.println("brokerID " + MsgSection[1]);
                        this.senderID = Integer.parseInt(MsgSection[1]);
                        break;
                    case "39":
//                        System.out.println("brokerID " + MsgSection[1]);
                        this.excutionStatus = Integer.parseInt(MsgSection[1]);
                        break;
                    case "55":
//                        System.out.println("brokerID " + MsgSection[1]);
                        this.instrument = MsgSection[1];
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
    }
    
    
    public boolean validateChecksum(){
        String[] splitMessage = message.split("\\|");

        if (splitMessage.length > 3) {
            //add 1 for end pipe
            int len_checksum = splitMessage[splitMessage.length - 1].length() + 1;

            String chkmessage = message.substring(0, message.length() - len_checksum);
            chkmessage = chkmessage.replace('|', '\u0001');

            int sum = 0;
            int char_val = 0;

            for (int i = 0; i < chkmessage.length(); i++) {
                char_val = chkmessage.charAt(i);
                sum += char_val;
            }
//            System.out.println("sum: " + sum + " mod: " + (sum % 256));

            String[] checksumString = splitMessage[splitMessage.length - 1].split("=");
            if (checksum == 0)
                parseMessage();
            //int checkSum = Integer.parseInt(checksumString[checksumString.length - 1]);
//            System.out.println("fix message checksum " + checksum);


            if (checksum == (sum % 256))
                return true;
            else
                return false;
        }
        return false;
    }



}

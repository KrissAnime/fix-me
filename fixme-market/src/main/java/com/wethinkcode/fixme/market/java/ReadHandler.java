package com.wethinkcode.fixme.market.java;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ReadHandler implements CompletionHandler<Integer , ChannelDetails> {

    @Override
    public void completed(Integer result, ChannelDetails attachment) {
        System.out.println("buffer === " + result);
        if (result == -1)
        {
            attachment.mainThread.interrupt();
            System.out.println("error, going offline");
            return ;
        }
        else if (attachment.readStatus == 1){
            String message = this.getBufferMsg(attachment);
            System.out.println("message -> " + message);
            if ((attachment.MarketID = this.setMarketId(attachment, message)) > -1) {
                attachment.readStatus = 0;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
                return;
            } else {
                System.out.println("message : "+ message);
                ////////
//                String Bmsg = "|50=99999|MARKET=jse|55=ZAR|38=2|44=1.9|54=1|10=066";
                if (brokerMessage(message) == 1){
                    String Bmsg = "|50=99999|56=00001|39=2|10=066";
                }


                attachment.byteBuffer.clear();
                attachment.readStatus = 1;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
            }
        } else {
            attachment.byteBuffer.clear();

            System.out.println("go again");
            attachment.readStatus = 1;
            attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
        }

    }

    @Override
    public void failed(Throwable exc, ChannelDetails attachment) {

    }

    public String getBufferMsg(ChannelDetails attachment){
        attachment.byteBuffer.flip();
        int limit = attachment.byteBuffer.limit();
        byte str[] = new byte[limit];
        attachment.byteBuffer.get(str, 0, limit);
        String message = new String(str, Charset.forName("UTF-8"));
        return message;
    }

    public int setMarketId(ChannelDetails attachment, String message){
        if (attachment.MarketID == -1){
            int id = Integer.parseInt(message);
            System.out.println("Server responded with id : " + id);
            return id;
        }
        return -1;
    }

    public int brokerMessage(String Bmsg){
        MarketMessageHandler msg = new MarketMessageHandler(Bmsg);
        return msg.getStatus();
    }
}

//        String Bmsg = "|50=00001|39=2|44=0.5|54=1|10=066";

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

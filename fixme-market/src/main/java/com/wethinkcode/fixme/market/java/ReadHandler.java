package com.wethinkcode.fixme.market.java;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ReadHandler implements CompletionHandler<Integer , ChannelDetails> {
    public static boolean idTest = false;

    @Override
    public void completed(Integer result, ChannelDetails attachment) {
//
        if (result == -1)
        {
            attachment.mainThread.interrupt();
            System.out.println("error, going offline");
            return ;
        }
        else if (attachment.readStatus == 1){
            String message = this.getBufferMsg(attachment);
//            System.out.println("message -> " + message);
            if ( attachment.MarketID == -1) {
                attachment.MarketID = this.setMarketId(attachment, message);
                attachment.readStatus = 0;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
                return;
            } else {
                System.out.println("recieved message : "+ message);
                ////////
//                String Bmsg = "|50=99999|MARKET=jse|55=ZAR|38=2|44=1.9|54=1|10=066";
                Reply reply = new Reply();
                if (brokerMessage(message, attachment, reply) == 1){
                    String Bmsg = "|50=99999|56=00001|39=2|10=066";
                }

                attachment.MarketID = reply.getMarkerId();
                attachment.byteBuffer.clear();
                attachment.readStatus = 1;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
            }
        } else {
            attachment.byteBuffer.clear();
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
            idTest = true;
            return id;
        }
        return -1;
    }

    public int brokerMessage(String Bmsg, ChannelDetails attachment, Reply reply){
        MarketMessageHandler msg = new MarketMessageHandler(Bmsg, attachment, reply);
        System.out.println("Replying with : " + reply.getMessage());
        attachment.socketChannel.write(ByteBuffer.wrap(reply.getMessage().getBytes()));
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

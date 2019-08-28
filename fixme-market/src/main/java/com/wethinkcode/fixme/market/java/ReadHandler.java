package com.wethinkcode.fixme.market.java;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ReadHandler implements CompletionHandler<Integer , ChannelDetails> {

    @Override
    public void completed(Integer result, ChannelDetails attachment) {
        System.out.println("buffer === " + result);
        System.out.println("Read status === " + attachment.readStatus);
        if (result == -1)
        {
            attachment.mainThread.interrupt();
            System.out.println("error, going offline");
            return ;
        }
        else if (attachment.readStatus == 1) {
            String message = this.getBufferMsg(attachment);
            System.out.println("message -> " + message);
            if ((attachment.MarketID = this.setMarketId(attachment, message)) > -1) {
                attachment.readStatus = 0;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
                return;
            } else {
                System.out.println("message : "+ message);
                ////////

                attachment.byteBuffer.clear();
                attachment.readStatus = 1;
                attachment.socketChannel.read(attachment.byteBuffer, attachment, this);
            }
        } else {
            attachment.byteBuffer.clear();
            brokerMessage();

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

    public void brokerMessage(){
        String Bmsg = "|50=00001|MARKET=jse|55=ZAR|38=21|44=1.9|54=1|10=066";
        MarketMessageHandler msg = new MarketMessageHandler(Bmsg);
//        System.out.println(msg.market + " --- " + msg.checksum);
    }
}


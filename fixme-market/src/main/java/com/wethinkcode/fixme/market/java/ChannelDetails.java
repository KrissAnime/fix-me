package com.wethinkcode.fixme.market.java;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class ChannelDetails {
public ByteBuffer byteBuffer;
public Thread mainThread;
public AsynchronousSocketChannel socketChannel;
public int MarketID = -1;
public int readStatus = 0;
        }

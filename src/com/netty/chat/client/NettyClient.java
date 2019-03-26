package com.netty.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * <p>
 *
 * @author Jame
 * @date 2019-03-14 17:04
 */
public class NettyClient {

    /**
     * 对应服务器的公有地址
     */
    public static final String WS_HOST = "47.107.231.65" ;

    /**
     * 服务器的端口
     */
    public static final int WS_PORT = 8212;

    public static void main(String[] args) throws InterruptedException {
        connect();
    }

    public static void connect(){
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer());

        Channel channel = bootstrap.connect(WS_HOST, WS_PORT).channel();
        if (channel != null){
            System.out.println("connect success");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()){
                channel.writeAndFlush(sc.nextLine());
            }
        }else {
            System.out.println("connect fail");
        }
    }
}

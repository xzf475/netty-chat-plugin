package com.netty.chat.client;

import com.intellij.openapi.project.Project;
import com.netty.chat.Chat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 *
 * @author Jame
 * @date 2019-03-14 17:31
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //打印服务端的发送数据
        Chat chat = Chat.getInstance();
        chat.read(s);
    }

}

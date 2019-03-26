package com.netty.chat;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.text.DateFormatUtil;
import com.netty.chat.client.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;

import javax.swing.*;
import java.awt.event.*;
import java.util.Date;

/**
 * <p>
 *
 * @author Jame
 * @date 2019-03-25 16:16
 */
public class Chat  {
    private JButton sendButton;
    private JTextField textField;
    private JScrollPane jScrollPane;
    private javax.swing.JPanel jPanel;
    private JButton refreshButton;
    private JLabel label;
    private JTextArea textArea;

    private static Chat chat ;

    ActionListener writeListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            write();
        }
    };

    ActionListener connectListener =  new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            connect();
        }
    };
    ActionListener disconnectListener =  new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            disconnect();
        }
    };


    public static Chat getInstance(ToolWindow toolWindow){
        if (chat == null){
            chat = new Chat(toolWindow);
        }
        return chat;
    }

    public static Chat getInstance(){
        if (chat == null){
            chat = new Chat();
        }
        return chat;
    }

    /**
     * 对应服务器的公有地址
     */
    public static final String WS_HOST = "localhost" ;

    /**
     * 服务器的端口
     */
    public static final int WS_PORT = 8212;

    private static Channel channel ;

    private Chat(ToolWindow toolWindow) {
        sendButton.addActionListener(writeListener);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    write();
                };
            }
        });
        refreshButton.addActionListener(connectListener);
    }

    private Chat() {
    }

    public JPanel getContent() {
        return jPanel;
    }


    public void connect(){
        if (StringUtil.isNullOrEmpty(label.getText())){
            AskName dialog = new AskName();
            dialog.pack();
            dialog.setVisible(true);
        }
        try {
            if(channel !=null){
                channel.closeFuture();
                channel.close();
            }
            Bootstrap bootstrap = new Bootstrap();
            NioEventLoopGroup group = new NioEventLoopGroup();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer());
            channel = bootstrap.connect(WS_HOST, WS_PORT).channel();
            if (StringUtil.isNullOrEmpty(label.getText())){
                label.setText("User-"+channel.id());
            }
            refreshButton.removeActionListener(connectListener);
            refreshButton.addActionListener(disconnectListener);
            refreshButton.setText("断开");
        }catch (Exception e){
            textArea.append(e.getMessage());
            throw e;
        }

    }

    public void disconnect(){
        if(channel !=null){
            channel.writeAndFlush(formatWrite("离开聊天室。"));
            channel.closeFuture();
            channel.close();
        }
        refreshButton.removeActionListener(disconnectListener);
        refreshButton.addActionListener(connectListener);
        refreshButton.setText("连接");
    }

    public void write(){
        String msg = textField.getText();
        try {
            if (channel != null){
                channel.writeAndFlush(formatWrite(msg));
                textField.setText("");
            }else {
                textArea.append(formatWrite("write fail"));
            }
        }catch (Exception e){
            textArea.append(e.getMessage());
            throw e;
        }
    }

    public void read(String msg){
        if ("connect success".equals(msg)){
            //接收连接成功包
            channel.writeAndFlush(formatWrite("加入聊天室。"));
        }else {
            textArea.append("\n" + msg);
        }
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public String formatWrite(String msg){
        return  DateFormatUtil.formatTime(new Date()) + " " +label.getText() +  ": "+ msg ;
    }

}

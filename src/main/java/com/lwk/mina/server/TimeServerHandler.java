package com.lwk.mina.server;

import java.util.Date;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class TimeServerHandler extends IoHandlerAdapter {
    @Override
    public void sessionCreated(IoSession session) {
        // 显示客户端的ip和端口
        System.out.println(session.getRemoteAddress().toString());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String str = message.toString();
        if (str.trim().equalsIgnoreCase("quit")) {
            session.close();// 结束会话
            return;
        }
        Date date = new Date();
        session.write(date.toString());// 返回当前时间的字符串
        System.out.println("Message written...");
    }
}
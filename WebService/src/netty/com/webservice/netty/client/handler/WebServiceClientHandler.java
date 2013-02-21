package com.webservice.netty.client.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

/** 
 * <p>Description: [客户端接收到服务器端返回的数据时的处理]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceClientHandler extends SimpleChannelUpstreamHandler {
    private Logger logger = Logger.getLogger(WebServiceClientHandler.class);
    /**
     * 保存服务器端返回给客户端的报文消息
     */
    private String msg;
    /**
     * <p>Discription:[客户端与服务端连接断开时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        logger.info("客户端已成功与服务端断开连接");
        if(this.msg == null || "".equals(msg.trim()) || "null".equalsIgnoreCase(msg)){
            logger.error("服务端未响应", new Exception("服务器端未响应"));
        }
        super.channelClosed(ctx, e);
    }

    /**
     * <p>Discription:[客户端与服务端管道连接成功时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        logger.info("客户端已成功与服务端连接");
        super.channelConnected(ctx, e);
    }

    /**
     * <p>Discription:[消息管道出现异常时触发]</p>
     * @param arg0
     * @param arg1
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void exceptionCaught(ChannelHandlerContext arg0, ExceptionEvent e)
            throws Exception {
        logger.error("管道异常！" + e, e.getCause());
        super.exceptionCaught(arg0, e);
    }

    /**
     * <p>Discription:[客户端接收到服务端的消息时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        logger.info("客户端接收到服务端发送的消息：" + e.getMessage());
        //转发服务器端的消息
        this.msg = String.valueOf(e.getMessage());
        //断开连接
        e.getChannel().close();
    }

    /**
     * <p>Discription:[报文传送完成时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
            throws Exception {
        logger.info("客户端已将信息发送至服务端");
        super.writeComplete(ctx, e);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String msg.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param msg The msg to set.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.webservice.netty.server.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/** 
 * <p>Description: [服务器端数据业务处理]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceServerHandler extends SimpleChannelUpstreamHandler {
    public static Logger logger = Logger
            .getLogger(WebServiceServerHandler.class);

    /**
     * <p>Discription:[客户端与服务器端通道关闭时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        logger.info("通道关闭");
        super.channelClosed(ctx, e);
    }

    /**
     * <p>Discription:[客户端与服务器端连接成功时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        logger.info("通道已连接");
        super.channelConnected(ctx, e);
    }

    /**
     * <p>Discription:[客户端与服务器端已断开连接时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        logger.info("通道断开连接");
        super.channelDisconnected(ctx, e);
    }

    /**
     * <p>Discription:[异常时触发]</p>
     * @param arg0
     * @param arg1
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    
    @Override
    public void exceptionCaught(ChannelHandlerContext arg0, ExceptionEvent arg1)
            throws Exception {
        logger.error("服务端发送异常..." + arg1);
        super.exceptionCaught(arg0, arg1);
    }

    /**
     * <p>Discription:[服务器端收到客户端发来的报文时触发，主要业务处理]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        super.messageReceived(ctx, e);
    }
    
}

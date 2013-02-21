package com.webservice.netty.server.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

/** 
 * <p>Description: [空闲时心跳处理器]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class HeartbeatHandler extends IdleStateAwareChannelHandler {
    /**
     * <p>Discription:[服务器端空闲时触发]</p>
     * @param ctx
     * @param e
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
            throws Exception {
        if (e.getState() == IdleState.ALL_IDLE){
            e.getChannel().close();
        }
        super.channelIdle(ctx, e);
    }
}

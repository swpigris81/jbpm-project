package com.webservice.netty.server.factory;

import org.apache.commons.lang.math.NumberUtils;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.webservice.netty.server.handler.HeartbeatHandler;
import com.webservice.netty.server.handler.WebServiceServerHandler;
import com.webservice.netty.util.DelimitersUtil;
import com.webservice.netty.util.NettyProperties;

/** 
 * <p>Description: [服务器端管道]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceServerPipelineFactory implements ChannelPipelineFactory {

    /**
     * <p>Discription:[服务器端管道]</p>
     * @return 服务器端管道
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, false, DelimitersUtil.delimiter()));
        //管道消息解码器
        pipeline.addLast("decoder", new StringDecoder());
        //管道消息编码器
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("idleHandler", new IdleStateHandler(new HashedWheelTimer(), 0, 0, NumberUtils.toInt(NettyProperties.getProperties("nettyTimeout"))));
        pipeline.addLast("heartbeatHandler", new HeartbeatHandler());
        //管道消息事件处理器（业务处理）
        pipeline.addLast("handler", new WebServiceServerHandler());
        return pipeline;
    }

}

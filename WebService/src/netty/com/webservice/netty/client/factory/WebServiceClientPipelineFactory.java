package com.webservice.netty.client.factory;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.webservice.netty.client.handler.WebServiceClientHandler;
import com.webservice.netty.util.DelimitersUtil;
import com.webservice.netty.util.NettyProperties;

/** 
 * <p>Description: [客户端消息渠道管道工厂]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceClientPipelineFactory implements ChannelPipelineFactory {

    /**
     * <p>Discription:[获取客户端渠道管道]</p>
     * @return 渠道管道
     * @throws Exception
     * @author:大牙
     * @update:2013-2-21
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, false, DelimitersUtil.delimiter()));
        //管道消息解码器
        pipeline.addLast("decoder", new StringDecoder(org.jboss.netty.util.CharsetUtil.UTF_8));
        //管道消息编码器
        pipeline.addLast("encoder", new StringEncoder(org.jboss.netty.util.CharsetUtil.UTF_8));
        //管道消息事件处理器（业务处理）
        pipeline.addLast("handler", new WebServiceClientHandler());
        return pipeline;
    }
}

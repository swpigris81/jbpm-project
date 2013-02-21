package com.webservice.netty.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/** 
 * <p>Description: [报文结束符配置]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class DelimitersUtil {
    /**
     * <p>Discription:[获取配置文件中的报文结束符配置]</p>
     * @return 报文结束符
     * @author:大牙
     * @update:2013-2-21
     */
    public static ChannelBuffer[] delimiter() {
        return new ChannelBuffer[] { ChannelBuffers.wrappedBuffer(NettyProperties.getProperties("delimiter")
                .getBytes()) };
    }
}

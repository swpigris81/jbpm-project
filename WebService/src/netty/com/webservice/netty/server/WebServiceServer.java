package com.webservice.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.webservice.netty.server.factory.WebServiceServerPipelineFactory;
import com.webservice.netty.util.NettyProperties;

/** 
 * <p>Description: [服务器端服务]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceServer {
    private static Logger logger = Logger.getLogger(WebServiceServer.class);
    private Channel serverChannel;
    /**
     * <p>Discription:[跟随Spring容器启动Netty服务]</p>
     * @author:大牙
     * @update:2013-2-21
     */
    public void startNettyService(){
        try {
            // 配置多线程服务进程
            ServerBootstrap bootstrap = new ServerBootstrap( 
                    new NioServerSocketChannelFactory( 
                            Executors.newCachedThreadPool(), //boss
                            Executors.newCachedThreadPool(),8)); //worker
            WebServiceServerPipelineFactory serverChannelPipelineFactory = new WebServiceServerPipelineFactory();
            // 设置事件管道 
            bootstrap.setPipeline(serverChannelPipelineFactory.getPipeline()); 
            // 绑定端口
            Channel channel = bootstrap.bind(new InetSocketAddress(NettyProperties.getProperties("serverHostIp"), NumberUtils.toInt(NettyProperties.getProperties("serverHostPort"))));
            setServerChannel(channel);
            logger.info("服务端启动成功...");
        } catch (Exception e) {
            logger.error("服务端启动异常....", e);
            e.printStackTrace();
        }
    }
    /**
     * <p>Discription:[测试时使用]</p>
     * @param args
     * @author:大牙
     * @update:2013-2-21
     */
    public static void main(String[] args) {
        try {
            // 配置多线程服务进程
            ServerBootstrap bootstrap = new ServerBootstrap( 
                    new NioServerSocketChannelFactory( 
                            Executors.newCachedThreadPool(), //boss
                            Executors.newCachedThreadPool(),8)); //worker
            WebServiceServer webService = new WebServiceServer();
            WebServiceServerPipelineFactory serverChannelPipelineFactory = new WebServiceServerPipelineFactory();
            // 设置事件管道 
            bootstrap.setPipeline(serverChannelPipelineFactory.getPipeline()); 
            // 绑定端口
            Channel channel = bootstrap.bind(new InetSocketAddress(NettyProperties.getProperties("serverHostIp"), NumberUtils.toInt(NettyProperties.getProperties("serverHostPort"))));
            webService.setServerChannel(channel);
            logger.info("服务端启动成功...");
        } catch (Exception e) {
            logger.error("服务端启动异常....", e);
            e.printStackTrace();
        }
    }
    
    /**
     * 停止监听指定端口
     */
    public void stop(){
        if(this.serverChannel != null){
            serverChannel.close().awaitUninterruptibly();
        }
    }
    public Channel getServerChannel() {
        return serverChannel;
    }
    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }
}

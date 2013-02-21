package com.webservice.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/** 
 * <p>Description: [应用程序作为Netty客户端向外发送报文请求]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class WebServiceCilent {
    private Logger logger = Logger.getLogger(WebServiceCilent.class);
    
    /**
     * 服务端IP地址
     */
    private String hostIp;
    /**
     * 服务端端口
     */
    private int hostPort;
    /**
     * 联机超时时间，单位：秒
     */
    private int timeout = 10;
    private ChannelFuture future;
    private ClientBootstrap bootstrap;
    
    /**
     * <p>Description:建立管道，创建联机，当完成业务之后，释放资源</p>
     * @author:代超
     * @throws Exception 
     * @update:2012-11-30
     */
    private void createConnectToServer() throws Exception{
        logger.info("准备开始联机");
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool(), 8));
        //设置事件管道工厂
        //bootstrap.setPipelineFactory(new EpayOnlineClientPipelineFactory());
        //开始连接服务器端
        logger.info("开始联机");
        if(hostIp == null || "".equals(hostIp) || hostPort == 0 || "".equals(hostPort)){
            throw new Exception("服务器端IP地址或者是端口不能为空！");
        }
        future = bootstrap.connect(new InetSocketAddress(hostIp, hostPort));
        future.awaitUninterruptibly(timeout, TimeUnit.SECONDS);
    }
    
    /**
     * <p>Description:发送消息到服务端</p>
     * @param msg 消息
     * @author:代超
     * @throws Exception 
     * @update:2012-11-30
     */
    public void sendMessageToServer(String msg) throws Exception{
        //准备工作
        createConnectToServer();
        if(future != null){
            Channel channel = future.getChannel();
            if(channel.isConnected()){
                channel.write(msg);
            }else{
                channel.close();
            }
        }
        //释放资源
        releaseResource();
    }
    
    /**
     * <p>Description:释放资源</p>
     * @author:代超
     * @update:2012-11-30
     */
    public void releaseResource(){
        //当断开连接或者是连接异常时，断开连接，并且释放连接资源
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        logger.info("联机结束，释放资源");
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String hostIp.
     */
    public String getHostIp() {
        return hostIp;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param hostIp The hostIp to set.
     */
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return int hostPort.
     */
    public int getHostPort() {
        return hostPort;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param hostPort The hostPort to set.
     */
    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return int timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param timeout The timeout to set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    
}

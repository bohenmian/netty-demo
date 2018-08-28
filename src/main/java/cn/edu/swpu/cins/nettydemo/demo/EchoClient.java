package cn.edu.swpu.cins.nettydemo.demo;

/**
 * @see io.netty.bootstrap.AbstractBootstrap#channel(java.lang.Class)  channel的初始化
 * @see io.netty.channel.ReflectiveChannelFactory#newChannel()     channel实例化的具体实现(通过反射实例化channel)
 * channel初始化AbstractBootstrap#channel -> ReflectiveChannelFactory#newChannel() -> channelFactory
 * @see io.netty.bootstrap.Bootstrap#connect(java.net.SocketAddress) 调用connect方法
 * @see io.netty.bootstrap.Bootstrap#doResolveAndConnect(java.net.SocketAddress, java.net.SocketAddress)
 * @see io.netty.bootstrap.AbstractBootstrap#initAndRegister
 * Channel.newChannel的调用链:Bootstrap.connect -> Bootstrap.doResolveAndConnect -> AbstractBootstrap.initAndRegister
 * 具体实现: NioSocketChannel -> AbstractNioByteChannel -> AbstractNioChannel -> AbstractChanne,其中在AbstractNioChannel会有参数设置
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;      //主机号
    private final int port;         //端口

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient("127.0.0.1", 8080).start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();      //创建Bootstrap对象
            b.group(group)
                    .channel(NioSocketChannel.class)      //设置channel为适用于NIO传输的channel
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();       //连接到远程节点,阻塞等待知道连接完成
            f.channel().closeFuture().sync();       //阻塞,关闭channel
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

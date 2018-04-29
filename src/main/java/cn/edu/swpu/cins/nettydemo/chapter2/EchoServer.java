package cn.edu.swpu.cins.nettydemo.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Server引导过程需要的流程:
 * 1.创建一个ServerBootStrap的实例引导和绑定服务器
 * 2.创建一个EventLoopGroup实例进行事件的处理(这里因为是采用的NIO的方式,所以是NioEventLoopGroup)
 * 3.制定服务器绑定本地的InetSocketAddress
 * 4.使用EchoServerHandler的实例初始化每一个新的Channel
 * 5.调用ServerBootStrap.bind()方法绑定服务器
 */

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer(8080).start();       //调用服务器的start方法
    }

    public void start() throws InterruptedException {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();     //创建EventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap();      //创建一个ServerBootstrap
            b.group(group)
                    .channel(NioServerSocketChannel.class)      //指定所使用的Nio传输channel
                    .localAddress(new InetSocketAddress(port))      //使用指定端口设置套接字
                    .childHandler(new ChannelInitializer<SocketChannel>() {         //添加一个EchoServerHandler到子Channel的ChannelPipeline
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoServerHandler);       //EchoServerHandler被标注为@Sharable,所以我们总是可以使用同样的实例
                        }
                    });
            ChannelFuture f = b.bind().sync();      //异步的绑定服务器
            f.channel().closeFuture().sync();       //获取Channel的CloseFuture并阻塞当前线程直到它完成
        } finally {
            group.shutdownGracefully().sync();      //关闭EventLoopGroup,释放所有资源
        }

    }
}

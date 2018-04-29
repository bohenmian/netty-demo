package cn.edu.swpu.cins.nettydemo.chapter2;

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

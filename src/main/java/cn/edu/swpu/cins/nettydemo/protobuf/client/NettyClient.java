package cn.edu.swpu.cins.nettydemo.protobuf.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("nettyClient")
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private final static String HOST = "127.0.0.1";
    private final static int PORT = 9876;
    private boolean flag = true;

    @Autowired
    private NettyClientFilter filter;

    public void start() {
        doConnect(new Bootstrap(), group);
    }

    public void doConnect(Bootstrap bootstrap, EventLoopGroup group) {
        ChannelFuture future;
        try {
            if (bootstrap != null) {
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .remoteAddress(HOST, PORT)
                        // client的handler集合
                        .handler(filter);
                future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess()) {
                        System.out.println("与服务器断开连接,在10s后准备尝试重连");
                        eventLoop.schedule(() -> doConnect(new Bootstrap(), eventLoop), 10, TimeUnit.SECONDS);
                    }
                });
                if (flag) {
                    System.out.println("Netty客户端启动成功");
                    flag = false;
                }
                future.channel().closeFuture().sync();
            }
        } catch (Exception e) {
            System.out.println("客户端连接失败" + e.getMessage());
        }
    }
}

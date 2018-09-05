package cn.edu.swpu.cins.nettydemo.protobuf.client;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private final static String HOST = "127.0.0.1";
    private final static int PORT = 8080;
    private boolean flag = true;



    public static void main(String[] args) {
        new NettyClient().start(HOST, PORT);
    }

    private void start(String host, int port) {
        final NettyClientHandler handler = new NettyClientHandler();
        ChannelFuture future;
        try {
                Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS))
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(MessageInfo.Message.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(handler);
                        }
                    });
            future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                final EventLoop eventLoop = futureListener.channel().eventLoop();
                if (!futureListener.isSuccess()) {
                    System.out.println("与服务器断开连接,在10s之后准备尝试重连");
                    eventLoop.schedule(() -> start(HOST, PORT), 10, TimeUnit.SECONDS);
                }
                if (flag) {
                    System.out.println("Netty客户端启动成功");
                    flag = false;
                }
            });
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("客户端连接失败" + e.getMessage());
        }

    }
}

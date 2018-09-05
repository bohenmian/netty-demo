package cn.edu.swpu.cins.nettydemo.protobuf.server;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyServer {

    private static final int PORT = 9876;


    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        final NettyServerHandler handler = new NettyServerHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    // 这里只能处理MessageInfo.Message这一种Protobuf数据结构类型
                                    .addLast(new ProtobufDecoder(MessageInfo.Message.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(handler);
                        }
                    });
            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }
}

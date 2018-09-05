package cn.edu.swpu.cins.nettydemo.protobuf.client;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClientFilter extends ChannelInitializer<SocketChannel> {

    @Autowired
    private NettyClientHandler handler;

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
}

package cn.edu.swpu.cins.nettydemo.protobuf.client;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("nettyClientHandler")
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    // 循环次数
    private int count = 1;

    @Autowired
    private NettyClient nettyClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时间:" + new Date());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时间:" + new Date());
        final EventLoop eventLoop = ctx.channel().eventLoop();
        // 当channel不活跃的时候尝试重连
        nettyClient.doConnect(new Bootstrap(), eventLoop);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageInfo.Message)) {
            System.out.println("未知数据" + msg);
            return;
        }
        try {
            // 拿到protobuf类型的数据
            MessageInfo.Message message = (MessageInfo.Message) msg;
            System.out.println("客户端接受到用户的信息,编号:" + message.getId() + " 姓名:" + message.getName()
                    + " 年龄:" + message.getAge() + " 状态:" + message.getState());
            MessageInfo.Message.Builder messageState = MessageInfo.Message.newBuilder().setState(1);
            ctx.writeAndFlush(messageState);
            System.out.println("成功发送给服务端");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("循环请求时间:" + new Date() + ",次数" + count);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                MessageInfo.Message message = MessageInfo.Message.newBuilder()
                        .setState(2).build();
                ctx.writeAndFlush(message);
                count++;
            }
        }
    }
}

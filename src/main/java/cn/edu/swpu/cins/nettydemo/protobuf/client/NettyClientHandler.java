package cn.edu.swpu.cins.nettydemo.protobuf.client;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private int count = 1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时间:" + new Date());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时间:" + new Date());
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

            System.out.println("客户端接收到用户信息,编号:" + message.getId());
            MessageInfo.Message.Builder messageState = MessageInfo.Message.newBuilder().setState(1);
            ctx.writeAndFlush(messageState);
            System.out.println("成功发送给客户端");
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

package cn.edu.swpu.cins.nettydemo.heartbeat.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("heartbeat_request", CharsetUtil.UTF_8));

    // 空闲次数
    private int idle_count = 1;
    // 发送次数
    private int count = 1;
    // 循环次数
    private int fcount = 1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);

//        System.out.println("第" + count + "次, 客户端发送消息," + msg.toString());
        count++;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时间" + new Date());
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Netty", CharsetUtil.UTF_8));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时间" + new Date());
        super.channelInactive(ctx);
    }

    // 客户端每隔4s发送一次心跳信息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("循环请求时间:" + new Date() + " ,次数" + fcount);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())) {  //如果写通道处于空闲状态
                if (idle_count <= 3) {  //设置发送心跳信息的次数
                    idle_count++;
                    ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                } else {
                    System.out.println("不再发送心跳请求了");
                }
            }
            fcount++;
        }
    }
}

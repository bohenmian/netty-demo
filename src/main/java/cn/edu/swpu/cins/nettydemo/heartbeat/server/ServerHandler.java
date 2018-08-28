package cn.edu.swpu.cins.nettydemo.heartbeat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetAddress;
import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private int idle_count = 1;
    private int count = 1;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(event.state())) {
                System.out.println("已经5秒没收到请求了");
            }
            if (idle_count > 2) {
                System.out.println("关闭这个不活跃的channel");
                ctx.channel().close();
            }
            idle_count++;
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "与服务器连接成功");
        System.out.println("建立连接时间:" + new Date());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第" + count + "次," + "服务端接收到消息:" + msg);
        String message = (String) msg;
        if ("heartbeat_request".equals(message)) {
            ctx.writeAndFlush("服务端成功接收到心跳信息");
        }
        count++;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}

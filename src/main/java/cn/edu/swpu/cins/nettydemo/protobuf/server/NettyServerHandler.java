package cn.edu.swpu.cins.nettydemo.protobuf.server;

import cn.edu.swpu.cins.nettydemo.protobuf.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // 空闲次数
    private int idle_count = 1;
    // 发送次数
    private int count = 1;

    // 建立连接时发送一条消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接客户端的地址为:" + ctx.channel().remoteAddress());
        MessageInfo.Message message = MessageInfo.Message.newBuilder()
                .setId(1)
                .setAge(18)
                .setName("jack")
                .setState(0)
                .build();
        ctx.writeAndFlush(message);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.printf("第%d次, 服务端接受的消息%s", count, msg);
        try {
            if (msg instanceof MessageInfo.Message) {
                MessageInfo.Message message = (MessageInfo.Message) msg;
                if (message.getState() == 1) {
                    System.out.println("客户端处理业务成功");
                } else if (message.getState() == 2) {
                    System.out.println("接受到客户端发送的心跳");
                } else {
                    System.out.println("未知命令");
                }
            } else {
                System.out.println("未知数据" + msg);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
        count++;
    }

    // 超时处理,如果5s没有接受到客户端的心跳就触发,如果超过两次则关闭
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(event.state())) {
                System.out.println("已经5s没有接收到消息了");
                if (idle_count > 1) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
                idle_count++;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

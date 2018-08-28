package cn.edu.swpu.cins.nettydemo.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * netty server handler
 * netty中ChannelHandler是一个接口族的父接口,它的实现负责接收并相应事件通知
 * 在netty中,所有的数据处理逻辑都包含在ChannelHandler的实现类中(Channel实现核心业务逻辑)
 * @see io.netty.channel.ChannelHandler.Sharable 标识一个ChannelHandler可以被多个Channel使用
 */

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    //对每个传入的消息都会调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收发送者发送过来的消息
        ByteBuf byteBuf = (ByteBuf) msg;

        //将消息打印到控制台
        System.out.println("Server received:" + byteBuf.toString(CharsetUtil.UTF_8));

        //将接收到的消息写给发送者
        ctx.write(msg);
    }

    //通知ChannelInboundHandler最后一次的调用是对当前批量的最后一条消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)    //将未决消息冲刷到远程节点,并关闭Channel
                .addListener(ChannelFutureListener.CLOSE);
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();    //打印异常信息
        ctx.close();    //关闭Channel

    }
}

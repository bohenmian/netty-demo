package cn.edu.swpu.cins.nettydemo.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * netty client handler
 */

@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    //在服务器的连接已经建立时被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当被通知的channel是活跃的时候,发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    //当从服务器接收到第一条消息时被调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received:" + msg.toString(CharsetUtil.UTF_8));   //打印到控制台
    }

    //在处理过程中引发异常被调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();        //打印出异常信息
        ctx.close();        //关闭channel
    }
}

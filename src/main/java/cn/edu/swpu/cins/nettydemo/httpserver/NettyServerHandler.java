package cn.edu.swpu.cins.nettydemo.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private String result = "";

    // 当客户端连接是打印信息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "与服务器连接成功");
        super.channelActive(ctx);
    }


    // 处理业务的主要逻辑
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            result = "未知请求";
            send(ctx, result, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {

            String path = httpRequest.getUri();
            String body = getBody(httpRequest);
            HttpMethod method = httpRequest.getMethod();
            if (!"/test".equalsIgnoreCase(path)) {
                result = "非法请求";
                send(ctx, result, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            System.out.println("接收到:" + method + "请求");
            if (HttpMethod.GET.equals(method)) {
                System.out.println("body" + body);
                result = "GET请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            }
            if (HttpMethod.POST.equals(method)) {
                System.out.println("body" + body);
                result = "POST请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            }
            if (HttpMethod.PUT.equals(method)) {
                System.out.println("body" + body);
                result = "PUT请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            }
            if (HttpMethod.DELETE.equals(method)) {
                System.out.println("body" + body);
                result = "DELETE请求";
                send(ctx, result, HttpResponseStatus.OK);
                return;
            }
        } catch (Exception e) {
            System.out.println("处理请求失败");
            e.printStackTrace();
        } finally {
            httpRequest.release();
        }
    }

    // 通知ChannelInboundHandler这是最后一条信息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    // 异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private String getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

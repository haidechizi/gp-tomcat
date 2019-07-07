package com.gupaoedu.tomcat.netty.servlet.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

public class GPResponse {

    private ChannelHandlerContext context;
    private HttpRequest httpRequest;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        this.context = ctx;
        this.httpRequest = httpRequest;
    }

    public void write(String s) {
        if(s == null || s.length() == 0) {
            return;
        }

        try {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(s.getBytes("utf-8")));
            httpResponse.headers().set("Content-Type","text/html;");

            context.writeAndFlush(httpResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            context.close();

        }
    }
}

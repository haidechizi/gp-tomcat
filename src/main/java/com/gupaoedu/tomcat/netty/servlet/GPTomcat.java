package com.gupaoedu.tomcat.netty.servlet;

import com.gupaoedu.tomcat.netty.servlet.http.GPRequest;
import com.gupaoedu.tomcat.netty.servlet.http.GPResponse;
import com.gupaoedu.tomcat.netty.servlet.http.GPServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class GPTomcat {

    private int port;

    private Properties properties = new Properties();

    private Map<String,GPServlet> serviceMapping = new ConcurrentHashMap<>();

    public GPTomcat(int port) {
        this.port = port;
        this.init();
    }

    private void init() {
        InputStream is = this.getClass().getResourceAsStream("/web.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Object k : properties.keySet()) {
            String key = k.toString();
            if(key.endsWith(".url")) {
                String servletName = key.replace(".url","");
                String servletClassName = properties.getProperty(servletName + ".className");
                try {
                    GPServlet servlet = (GPServlet) Class.forName(servletClassName).newInstance();
                    serviceMapping.put(properties.getProperty(key),servlet);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void listen() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new HttpResponseEncoder());
                            pipeline.addLast(new HttpRequestDecoder());
                            pipeline.addLast(new GPTomcatHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_SNDBUF, 3 * 1024)
                    .option(ChannelOption.SO_RCVBUF, 3 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(this.port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();

            workGroup.shutdownGracefully();
        }
    }

    private class GPTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest) {
                HttpRequest httpRequest = (HttpRequest) msg;

                GPRequest request = new GPRequest(ctx,httpRequest);

                GPResponse gpResponse = new GPResponse(ctx,httpRequest);

                String url = request.getUrl();

                if(url == null || !serviceMapping.containsKey(url)) {
                    gpResponse.write("404 not found");
                    return;
                }

                GPServlet servlet = serviceMapping.get(url);

                servlet.service(request,gpResponse);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();

            ctx.close();
        }
    }

    public static void main(String[] args) {
        new GPTomcat(8080).listen();
    }
}

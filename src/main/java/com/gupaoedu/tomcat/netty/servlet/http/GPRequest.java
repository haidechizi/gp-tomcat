package com.gupaoedu.tomcat.netty.servlet.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class GPRequest {

    private ChannelHandlerContext context;

    private HttpRequest httpRequest;

    public GPRequest(ChannelHandlerContext context,HttpRequest httpRequest) {
        this.context = context;

        this.httpRequest = httpRequest;
    }

    public String getUrl() {
        return httpRequest.uri();
    }

    public String getMethod() {
       return httpRequest.method().name();
    }

    public Map<String,List<String>> getParameters() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.uri());
        return queryStringDecoder.parameters();
    }

    public String getParameter(String name) {
        Map<String,List<String>> parameters = getParameters();
        List<String> parameterValue = parameters.get(name);
        if(parameterValue == null) {
            return null;
        } else {
            return parameterValue.get(0);
        }
    }

}

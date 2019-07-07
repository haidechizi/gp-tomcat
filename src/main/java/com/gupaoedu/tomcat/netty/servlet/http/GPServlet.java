package com.gupaoedu.tomcat.netty.servlet.http;

import java.io.IOException;

public abstract class GPServlet {

    public void service(GPRequest request,GPResponse response) throws IOException {
        if("get".equalsIgnoreCase(request.getMethod( ))) {
            this.doGet(request,response);
        } else {
            this.doPost(request,response);
        }
    }

    protected abstract void doPost(GPRequest request, GPResponse response) throws IOException;

    protected abstract void doGet(GPRequest request, GPResponse response) throws IOException;



}

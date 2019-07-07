package com.gupaoedu.tomcat.bio.servlet.http;

import java.io.IOException;

public abstract class GPServlet {
    
    public void service(GPRequest request,GPResponse response) throws IOException {
        if("get".equalsIgnoreCase(request.getMethod())) {
            doGet(request,response);
        } else {
            doPost(request,response);
        }
    }

    protected abstract void doPost(GPRequest request, GPResponse response) throws IOException;

    protected abstract void doGet(GPRequest request, GPResponse response) throws IOException;
}

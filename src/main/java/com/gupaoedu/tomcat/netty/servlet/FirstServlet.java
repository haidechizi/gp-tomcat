package com.gupaoedu.tomcat.netty.servlet;

import com.gupaoedu.tomcat.netty.servlet.http.GPRequest;
import com.gupaoedu.tomcat.netty.servlet.http.GPResponse;
import com.gupaoedu.tomcat.netty.servlet.http.GPServlet;

import java.io.IOException;

public class FirstServlet extends GPServlet {
    @Override
    protected void doPost(GPRequest request, GPResponse response) throws IOException {
        response.write("this is first servlet");
    }

    @Override
    protected void doGet(GPRequest request, GPResponse response) throws IOException {
        this.doPost(request,response);
    }
}

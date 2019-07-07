package com.gupaoedu.tomcat.bio.servlet;

import com.gupaoedu.tomcat.bio.servlet.http.GPRequest;
import com.gupaoedu.tomcat.bio.servlet.http.GPResponse;
import com.gupaoedu.tomcat.bio.servlet.http.GPServlet;

import java.io.IOException;

public class FirstServlet extends GPServlet {
    @Override
    protected void doPost(GPRequest request, GPResponse response) throws IOException {
        response.write("this is the first servlet");
    }

    @Override
    protected void doGet(GPRequest request, GPResponse response) throws IOException {
        doPost(request,response);
    }
}

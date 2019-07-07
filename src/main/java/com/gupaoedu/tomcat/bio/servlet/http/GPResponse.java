package com.gupaoedu.tomcat.bio.servlet.http;

import java.io.IOException;
import java.io.OutputStream;

public class GPResponse {

    private OutputStream os;

    public GPResponse(OutputStream os) {
        this.os = os;
    }

    public void write(String s) throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n").append("Content-Type:text/html;\n").append("\r\n").append(s);

        os.write(sb.toString().getBytes());
    }
}

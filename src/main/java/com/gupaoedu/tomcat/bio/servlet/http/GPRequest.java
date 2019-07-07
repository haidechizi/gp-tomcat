package com.gupaoedu.tomcat.bio.servlet.http;

import java.io.IOException;
import java.io.InputStream;

public class GPRequest {
    
    private String method;
    
    private String url;
    
    public GPRequest(InputStream is) {
        byte[] datas = new byte[1024];
        try {
            int length = is.read(datas);
            if(length > 0) {
                String msg = new String(datas,0,length);

                System.out.println(msg);

                String[] lineData = msg.split("\\n");

                String[] methodAndUrl = lineData[0].split("\\s");
                method = methodAndUrl[0];
                url = methodAndUrl[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }
}

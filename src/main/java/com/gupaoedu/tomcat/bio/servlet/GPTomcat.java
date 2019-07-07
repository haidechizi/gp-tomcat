package com.gupaoedu.tomcat.bio.servlet;

import com.gupaoedu.tomcat.bio.servlet.http.GPRequest;
import com.gupaoedu.tomcat.bio.servlet.http.GPResponse;
import com.gupaoedu.tomcat.bio.servlet.http.GPServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class GPTomcat {

    Properties properties = new Properties();

    private int port;

    private Map<String, GPServlet> servletMapping = new ConcurrentHashMap<>();

    public GPTomcat(int port) {
        this.port = port;
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/web.properties");
        try {
            properties.load(resourceAsStream);

            for (Object k : properties.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replace(".url", "");

                    String servletClassName = properties.getProperty(servletName + ".className");

                    try {
                        GPServlet servlet = (GPServlet) Class.forName(servletClassName).newInstance();

                        servletMapping.put(properties.getProperty(key), servlet);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);

            while (true) {
                Socket socket = serverSocket.accept();

                process(socket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void process(Socket socket) {
        InputStream inputStream = null;

        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();

            outputStream = socket.getOutputStream();

            GPRequest request = new GPRequest(inputStream);

            String url = request.getUrl();
            GPResponse response = new GPResponse(outputStream);

            if (!servletMapping.containsKey(url)) {
                response.write("404 not found");
                return;
            }

            GPServlet servlet = servletMapping.get(url);

            servlet.service(request, response);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static void main(String[] args) {
        new GPTomcat(8080).listen();
    }
}

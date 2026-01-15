package com.fastagi;

import org.asteriskjava.fastagi.DefaultAgiServer;

public class FastAGIServerApp {
    public static void main(String[] args) throws Exception {
        DefaultAgiServer server = new DefaultAgiServer();
        System.out.println("FastAGI Server started on port 4573");
        server.startup();
    }
}

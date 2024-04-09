package com.jfxbase.oopjfxbase.AppLogic;


import com.jfxbase.oopjfxbase.AppLogic.Model.Server;

import java.util.List;

public class Strategy {

    public static Server getMinTimeServer(List<Server> servers) {
        Integer minWaiting = 10000000;
        Server minServer = null;
        for (Server server : servers) {
            if (server.getWaitingtime().get() < minWaiting) {
                minServer = server;
                minWaiting = minServer.getWaitingtime().get();
            }
        }

        return minServer;
    }

    public static Server getShortestServer(List<Server> servers){
        Integer shortestSize=1000000;
        Server shortestServer=null;
        for (Server server : servers) {
            if(server.getClients().size() < shortestSize){
                shortestServer=server;
                shortestSize=server.getClients().size();
            }
        }

        return shortestServer;
    }


}

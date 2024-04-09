package com.jfxbase.oopjfxbase.AppLogic;


import com.jfxbase.oopjfxbase.AppLogic.Model.Server;

import java.util.List;

public class Strategy {

    public static Server getMinServer(List<Server> servers) {
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
}

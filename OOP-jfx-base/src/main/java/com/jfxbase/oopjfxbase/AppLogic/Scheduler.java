package com.jfxbase.oopjfxbase.AppLogic;

import com.jfxbase.oopjfxbase.AppLogic.Model.Client;
import com.jfxbase.oopjfxbase.AppLogic.Model.Server;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    List<Server> servers = new ArrayList<>();

    public List<Server> getServers() {
        return servers;
    }


    public void addClient(StrategyPicked strategyPicked, Client client) {
        if (strategyPicked.equals(StrategyPicked.SHORTEST_TIME)) {
            Strategy.getMinTimeServer(getServers()).addClient(client);
        } else if (strategyPicked.equals(StrategyPicked.SHORTEST_QUEUE)) {
            Strategy.getShortestServer(getServers()).addClient(client);
        }
    }

    public Integer getCurrentClientsInQueues() {
        int currentClients = 0;
        for (Server server : servers) {
            currentClients = currentClients + server.getClients().size();
        }

        return currentClients;
    }
}

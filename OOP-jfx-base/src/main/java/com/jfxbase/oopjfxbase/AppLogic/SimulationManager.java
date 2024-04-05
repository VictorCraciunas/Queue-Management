package com.jfxbase.oopjfxbase.AppLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {

    public LinkedBlockingQueue<Client> clients;
    public AtomicInteger currentTime=new AtomicInteger(0);

    public Scheduler scheduler=new Scheduler();


    public SimulationManager(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax) {
        this.clients=new LinkedBlockingQueue<>();
        generateRandomClients(nrClients,arrivalMin, arrivalMax, serviceMin, serviceMax);
    }

    public void generateRandomClients(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax){

        for(int i=1; i<=nrClients; i++){
            addClient(arrivalMin,arrivalMax,serviceMin,serviceMax);
        }
    }


    private void addClient(Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax){
        Random random=new Random();
        this.clients.add(new Client(random.nextInt(arrivalMin,arrivalMax),random.nextInt(serviceMin,serviceMax)));
    }

    public LinkedBlockingQueue<Client> getClients() {
        return clients;
    }


    public void printClients(){
        for (Client client : clients) {
            System.out.println(client);
        }
    }

    @Override
    public void run() {
        while (!clients.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            List<Client> toRemove = new ArrayList<>();
            for (Client client : clients) {
                if (client.getArrival() == currentTime.get()) {
                    Strategy.getMinServer(scheduler.getServers()).addClient(client);
                    toRemove.add(client);
                }
            }

            clients.removeAll(toRemove);
            currentTime.incrementAndGet();
            System.out.println(currentTime);
        }
    }
}

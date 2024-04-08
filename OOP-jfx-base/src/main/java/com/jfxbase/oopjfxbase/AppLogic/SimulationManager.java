package com.jfxbase.oopjfxbase.AppLogic;

import com.jfxbase.oopjfxbase.controllers.HelloController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {

    public LinkedBlockingQueue<Client> clients;
    public AtomicInteger currentTime = new AtomicInteger(1);

    public Integer totalTime = 0;

    public Scheduler scheduler;

    private CyclicBarrier barrier;


    public SimulationManager(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax, Integer nrQueues, HelloController helloController) {

        this.clients = new LinkedBlockingQueue<>();
        this.scheduler = new Scheduler();

        //create clients
        generateRandomClients(nrClients, arrivalMin, arrivalMax, serviceMin, serviceMax);
        printClients();

        //create the checkpoint for the threads
        this.barrier = new CyclicBarrier(nrQueues + 1, new Runnable() {
            @Override
            public void run() {
                printLog();
            }
        });

        //create the server
        for (int i = 0; i < nrQueues; i++) {
            Server server = new Server(helloController, clients.size(), i + 1, barrier);
            scheduler.getServers().add(server);
            new Thread(server).start();
        }
    }

    public void generateRandomClients(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax) {

        for (int i = 1; i <= nrClients; i++) {
            addClient(arrivalMin, arrivalMax, serviceMin, serviceMax);
        }
    }


    private void addClient(Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax) {
        Random random = new Random();
        int serviceTime = random.nextInt(serviceMin, serviceMax);
        this.clients.add(new Client(random.nextInt(arrivalMin, arrivalMax), serviceTime));
        totalTime = totalTime + serviceTime;

    }

    public void printClients() {
        for (Client client : clients) {
            System.out.println(client);
        }
    }

    @Override
    public void run() {
        while (totalTime != 0) {
            if (!clients.isEmpty()) {
                List<Client> toRemove = new ArrayList<>();
                for (Client client : clients) {
                    if (client.getArrival() == currentTime.get()) {
                        Strategy.getMinServer(scheduler.getServers()).addClient(client);
                        toRemove.add(client);
                    }
                }
                clients.removeAll(toRemove);
            }

            totalTime = totalTime - 1;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void printLog() {
        System.out.println("\nTIME SIMULATION ------------- " + currentTime.get());
        for (Server server : scheduler.getServers()) {
            server.printClients();
        }
        currentTime.incrementAndGet();
    }

}

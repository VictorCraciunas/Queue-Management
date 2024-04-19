package com.jfxbase.oopjfxbase.AppLogic;

import com.jfxbase.oopjfxbase.AppLogic.Model.Client;
import com.jfxbase.oopjfxbase.AppLogic.Model.Server;
import com.jfxbase.oopjfxbase.controllers.GUI;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {

    public LinkedBlockingQueue<Client> clients;

    public AtomicInteger currentTime = new AtomicInteger(0);

    public AtomicInteger MaxSimulationTime = new AtomicInteger();

    public Scheduler scheduler;

    private GUI controller;
    private CyclicBarrier barrier;

    StrategyPicked strategyPicked;

    private BufferedWriter writer;

    private Double averageServiceTime;
    private Integer sumServiceTime = 0;
    private Integer sumWaitingTime = 0;

    private Integer nrClients;
    private AtomicInteger peakHour = new AtomicInteger(0);
    private AtomicInteger maxClientsInQueues = new AtomicInteger(0);


    public SimulationManager(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax, Integer nrQueues, GUI helloController, StrategyPicked strategyPicked, Integer MaxSimulationTime) {

        this.controller = helloController;
        this.clients = new LinkedBlockingQueue<>();
        this.scheduler = new Scheduler();
        this.strategyPicked = strategyPicked;
        this.MaxSimulationTime.set(MaxSimulationTime);
        this.nrClients = nrClients;

        //create the file
        try {
            this.writer = new BufferedWriter(new FileWriter("simulation_output.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create clients
        generateRandomClients(nrClients, arrivalMin, arrivalMax, serviceMin, serviceMax);
        printClients();

        //create the checkpoint for the threads
        this.barrier = new CyclicBarrier(nrQueues + 1, new Runnable() {
            @Override
            public void run() {
                printLog();
                currentTime.incrementAndGet();
            }
        });

        //create the server
        for (int i = 0; i < nrQueues; i++) {
            Server server = new Server(clients.size(), i + 1, barrier);
            scheduler.getServers().add(server);
            new Thread(server).start();
        }
    }

    public void generateRandomClients(Integer nrClients, Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax) {

        for (int i = 1; i <= nrClients; i++) {
            addClient(arrivalMin, arrivalMax, serviceMin, serviceMax);
        }
        this.averageServiceTime = 1.0 * sumServiceTime / nrClients;
    }


    private void addClient(Integer arrivalMin, Integer arrivalMax, Integer serviceMin, Integer serviceMax) {
        Random random = new Random();
        int serviceTime = random.nextInt(serviceMin, serviceMax);
        this.sumServiceTime = this.sumServiceTime + serviceTime;
        this.clients.add(new Client(random.nextInt(arrivalMin, arrivalMax), serviceTime));

    }

    public void printClients() {
        //print in console
        for (Client client : clients) {
            System.out.println(client);
        }

        //print in file
        try {
            for (Client client : clients) {
                writer.write(client.toString());
                writer.newLine();  // Move to the next line
            }
            writer.flush(); // Make sure to flush the writer to ensure all data is written to the file
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (MaxSimulationTime.intValue() + 1 != currentTime.intValue()) {
            if (!clients.isEmpty()) {
                List<Client> toRemove = new ArrayList<>();
                for (Client client : clients) {
                    if (client.getArrival() == currentTime.get()) {
                        scheduler.addClient(strategyPicked, client);
                        sumWaitingTime = sumWaitingTime + client.getWaitingTime().get();
                        toRemove.add(client);
                    }
                }
                clients.removeAll(toRemove);
            }


            updateUI();
            checkPeakHour();

            //checkpoint for all the threads
            try {
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
                return;
            }

            if (clients.isEmpty() && scheduler.getCurrentClientsInQueues() == 0) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //write in console
        System.out.println("Average service Time: " + averageServiceTime); //average service time
        System.out.println("Average waiting Time: " + 1.0 * sumWaitingTime / nrClients); //average waiting time
        System.out.println("Peak hour: " + peakHour + " with " + maxClientsInQueues + " clients "); //peak hour

        //write in file
        try {
            writer.write("Average service Time: " + averageServiceTime);
            writer.newLine();
            writer.flush();
            writer.write("Average waiting Time: " + 1.0 * sumWaitingTime / nrClients);
            writer.newLine();
            writer.flush();
            writer.write("Peak hour: " + peakHour + " with " + maxClientsInQueues + " clients ");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printLog() {
        StringBuilder output = new StringBuilder();
        output.append("Clients: ");
        if (clients.isEmpty()) {
            output.append("empty");
        } else {
            for (Client client : clients) { // we print the clients that have been distributed yet
                output.append(client);
            }
        }

        //print in console
        System.out.println("\nTIME SIMULATION ------------- " + currentTime.get());
        System.out.println(output);
        for (Server server : scheduler.getServers()) {
            server.printClients();
        }


        //print in file
        try {
            writer.write("\nTIME SIMULATION ------------- " + currentTime.get());
            writer.newLine();
            writer.write(output.toString());
            writer.newLine();
            for (Server server : scheduler.getServers()) {
                server.printClientsFile(writer);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkPeakHour() {
        if (scheduler.getCurrentClientsInQueues() > maxClientsInQueues.intValue()) {
            maxClientsInQueues.set(scheduler.getCurrentClientsInQueues());
            peakHour.set(currentTime.intValue());
        }

    }

    public LinkedBlockingQueue<Client> getClients() {
        return clients;
    }

    private void updateUI() {

        // we update the UI Thread
        Platform.runLater(() -> controller.updateServerDisplay(currentTime.get(), scheduler.getServers()));
    }

}

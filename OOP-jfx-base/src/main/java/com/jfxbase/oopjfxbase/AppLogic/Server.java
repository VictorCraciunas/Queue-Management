package com.jfxbase.oopjfxbase.AppLogic;

import com.jfxbase.oopjfxbase.controllers.HelloController;
import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingtime = new AtomicInteger(0);

    public Integer queueNumber;

    private HelloController controller;

    private CyclicBarrier barrier;

    public Server(HelloController controller, Integer MaxClients, Integer queueNumber, CyclicBarrier barrier) {
        this.controller = controller;
        clients = new LinkedBlockingQueue<>(MaxClients);
        this.queueNumber = queueNumber;
        this.barrier = barrier;
    }

    public void addClient(Client client) {
        try {
            this.clients.put(client);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        waitingtime.set(waitingtime.get() + client.getService());

    }

    public void removeClient() {
        try {
            this.clients.take();
        } catch (InterruptedException e) {
            System.out.println("Queue already empty");
        }
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    @Override
    public void run() {

        while (true) {
            if (!clients.isEmpty()) {
                if (clients.peek().getService() != 0) {
                    updateUI();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                waitingtime.set(waitingtime.get() - 1);
                if (clients.peek() != null) {
                    clients.peek().setService(clients.peek().getService() - 1); //decrement the service time of the first client
                }


                if (clients.peek().getService() == 0) {
                    removeClient();
                    updateUI();
                }
            }

            try {
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
                break;
            }

        }
    }

    public AtomicInteger getWaitingtime() {
        return waitingtime;
    }

    public void printClients() {
        if (!clients.isEmpty()) {
            System.out.println("Queue: " + queueNumber);
            for (Client client : clients) {
                System.out.println(client);
            }
        } else {
            System.out.println("Queue " + queueNumber + " closed");
        }
    }

    public BlockingQueue<Client> getClients() {
        return clients;
    }

    private void updateUI() {
        // Use Platform.runLater to update the UI on the JavaFX Application Thread
        Platform.runLater(() -> controller.updateServerDisplay());
    }

}

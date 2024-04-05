package com.jfxbase.oopjfxbase.AppLogic;

import com.jfxbase.oopjfxbase.controllers.HelloController;
import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingtime=new AtomicInteger(0);

    public Integer queueNumber;

    private HelloController controller;

    private Object lock=new Object();


    public Server(HelloController controller, Integer MaxClients, Integer queueNumber) {
        this.controller=controller;
        clients=new LinkedBlockingQueue<>(MaxClients);
        this.queueNumber=queueNumber;
    }

    public void addClient(Client client) {
        synchronized (lock){
            try{
                this.clients.put(client);
                waitingtime.set(waitingtime.get() + client.getService());
                lock.notifyAll();
            }
            catch (InterruptedException e){
                System.out.println("Queue: full");
            }
        }

    }

    public void removeClient(){
        try{
            this.clients.take();
        }
        catch (InterruptedException e){
            System.out.println("Queue already empty");
        }
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    @Override
    public void run() {

        while (true){
            synchronized (lock){
                while (clients.isEmpty()){
                    try {
                        lock.wait();
                    }
                    catch (InterruptedException e){
                        System.out.println("WAITING ISN'T WORKIGN");
                        return;
                    }
                }
            }

        if (!clients.isEmpty()){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            if(clients.peek().getService() != 0){
                updateUI();
            }

            waitingtime.set(waitingtime.get()-1);
            if(clients.peek() != null) {
                    clients.peek().setService(clients.peek().getService()-1); //decrement the service time of the first client
            }


            if(clients.peek().getService() == 0){
                removeClient();
                updateUI();
            }
        }

        }
    }

    public AtomicInteger getWaitingtime() {
        return waitingtime;
    }

    public void printClients(){
        System.out.println("Queue: " + queueNumber);
        for (Client client : clients) {
            System.out.println(client);
        }
        System.out.println("waiting time: " + waitingtime);
    }

    public BlockingQueue<Client> getClients() {
        return clients;
    }

    private void updateUI() {
        // Use Platform.runLater to update the UI on the JavaFX Application Thread
        Platform.runLater(() -> controller.updateServerDisplay());
    }

    private void Test(){

    }
}

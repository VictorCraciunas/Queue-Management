package com.jfxbase.oopjfxbase.AppLogic.Model;

import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    private static Integer countID=1;
    public Integer ID;
    public Integer arrival;
    public Integer service;
    public AtomicInteger waitingTime=new AtomicInteger(0); //waiting time + service

    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Integer waitingTime) {
        this.waitingTime.set(waitingTime);
    }

    public Client(Integer arrival, Integer service) {
        this.ID=countID++;
        this.arrival = arrival;
        this.service = service;
    }

    public Integer getArrival() {
        return arrival;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ID=" + ID +
                ", arrival=" + arrival +
                ", service=" + service +
                '}';
    }
}

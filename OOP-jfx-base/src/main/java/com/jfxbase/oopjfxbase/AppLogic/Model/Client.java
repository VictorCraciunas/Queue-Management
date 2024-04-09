package com.jfxbase.oopjfxbase.AppLogic.Model;

public class Client {

    private static Integer countID=1;
    public Integer ID;
    public Integer arrival;
    public Integer service;


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

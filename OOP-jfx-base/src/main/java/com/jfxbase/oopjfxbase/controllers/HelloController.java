package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.AppLogic.Model.Client;
import com.jfxbase.oopjfxbase.AppLogic.Model.Server;
import com.jfxbase.oopjfxbase.AppLogic.SimulationManager;
import com.jfxbase.oopjfxbase.utils.SceneController;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;


public class HelloController extends SceneController {

    @FXML
    public VBox vBox;

    @FXML
    public Text SimulationTime;

    SimulationManager simulationManager;

    @FXML
    public void initialize() {

    }


    @FXML
    private void initializeSimulation() {
        // Number of servers you want to create
        Integer nrQueues = 2;  // Set this to the number of servers you need


        this.simulationManager = new SimulationManager(4, 1, 5, 1, 5, nrQueues, this);
        // Initialize your SimulationManager with the desired number of clients and other parameters


        new Thread((simulationManager)).start();

    }

    // Method to update the UI
    public void updateServerDisplay(Integer currTime, List<Server> servers) {
        vBox.getChildren().clear(); // Clear the existing content

        for (Server server : servers) {
            HBox serverBox = new HBox();
            serverBox.setSpacing(10); // Adjust spacing as needed

            Text queueNumber = new Text("Queue " + server.getQueueNumber() + " waitingTime- " + server.getWaitingtime() + ": ");
            serverBox.getChildren().add(queueNumber);

            for (Client client : server.getClients()) {
                Text clientText = new Text();
                clientText.setText(client.toString()); // Ensure Client has a meaningful toString method


                serverBox.getChildren().add(clientText);
            }

            if (server.getClients().isEmpty()) {
                Text clientText = new Text("Queue closed");
                serverBox.getChildren().add(clientText);
            }
            vBox.getChildren().add(serverBox);
        }

        SimulationTime.setText("Simulation Time: " + currTime);
    }

}

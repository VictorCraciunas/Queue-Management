package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.AppLogic.Model.Client;
import com.jfxbase.oopjfxbase.AppLogic.Model.Server;
import com.jfxbase.oopjfxbase.AppLogic.SimulationManager;
import com.jfxbase.oopjfxbase.AppLogic.StrategyPicked;
import com.jfxbase.oopjfxbase.utils.SceneController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;


public class HelloController extends SceneController {

    @FXML
    public VBox vBox;

    @FXML
    public Text SimulationTime;

    @FXML
    public TextField nrClients;
    @FXML
    public TextField nrQueues;
    @FXML
    public TextField minArriveTime;
    @FXML
    public TextField maxArriveTime;
    @FXML
    public TextField minServiceTime;
    @FXML
    public TextField maxServiceTime;

    @FXML
    public TextField maxSimulationTime;


    SimulationManager simulationManager;

    Integer shortestTimePressed=0;
    Integer shortestQueuePressed=0;


    @FXML
    private void initializeSimulation() {


        if(shortestQueuePressed == 1 && shortestTimePressed == 0){
            this.simulationManager = new SimulationManager(Integer.parseInt(nrClients.getText()), Integer.parseInt(minArriveTime.getText()), Integer.parseInt(maxArriveTime.getText()), Integer.parseInt(minServiceTime.getText()), Integer.parseInt(maxServiceTime.getText()), Integer.parseInt(nrQueues.getText()), this, StrategyPicked.SHORTEST_QUEUE, Integer.parseInt(maxSimulationTime.getText()));
        }
        else if (shortestTimePressed == 1 && shortestQueuePressed == 0){
            this.simulationManager = new SimulationManager(Integer.parseInt(nrClients.getText()), Integer.parseInt(minArriveTime.getText()), Integer.parseInt(maxArriveTime.getText()), Integer.parseInt(minServiceTime.getText()), Integer.parseInt(maxServiceTime.getText()), Integer.parseInt(nrQueues.getText()), this, StrategyPicked.SHORTEST_TIME, Integer.parseInt(maxSimulationTime.getText()));

        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Strategy Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a strategy.");

            alert.show();
        }


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

    @FXML
    public void shortestTimeButtonPressed(){
        shortestTimePressed=1;
        shortestQueuePressed=0;
    }

    @FXML
    public void shortestQueueButtonPressed(){
        shortestTimePressed=0;
        shortestQueuePressed=1;
    }

}

package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.AppLogic.Model.Client;
import com.jfxbase.oopjfxbase.AppLogic.Model.Server;
import com.jfxbase.oopjfxbase.AppLogic.SimulationManager;
import com.jfxbase.oopjfxbase.AppLogic.StrategyPicked;
import com.jfxbase.oopjfxbase.utils.SceneController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;


public class GUI extends SceneController {

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

    Integer shortestTimePressed = 0;
    Integer shortestQueuePressed = 0;


    @FXML
    private void initializeSimulation() {


        if (shortestQueuePressed == 1 && shortestTimePressed == 0) {
            this.simulationManager = new SimulationManager(Integer.parseInt(nrClients.getText()), Integer.parseInt(minArriveTime.getText()), Integer.parseInt(maxArriveTime.getText()), Integer.parseInt(minServiceTime.getText()), Integer.parseInt(maxServiceTime.getText()), Integer.parseInt(nrQueues.getText()), this, StrategyPicked.SHORTEST_QUEUE, Integer.parseInt(maxSimulationTime.getText()));
        } else if (shortestTimePressed == 1 && shortestQueuePressed == 0) {
            this.simulationManager = new SimulationManager(Integer.parseInt(nrClients.getText()), Integer.parseInt(minArriveTime.getText()), Integer.parseInt(maxArriveTime.getText()), Integer.parseInt(minServiceTime.getText()), Integer.parseInt(maxServiceTime.getText()), Integer.parseInt(nrQueues.getText()), this, StrategyPicked.SHORTEST_TIME, Integer.parseInt(maxSimulationTime.getText()));

        } else {
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
        vBox.setSpacing(10);

        HBox clients = new HBox();
        clients.setSpacing(10);

        if (!simulationManager.getClients().isEmpty()) {
            for (Client client : simulationManager.getClients()) {
                Text clientNotDistributed = new Text(client.toString());
                clientNotDistributed.setFill(Color.WHITE);
                clientNotDistributed.getStyleClass().add("textEmptyClients");
                clients.getChildren().add(clientNotDistributed);
            }
        } else {
            Text emptyClients = new Text("Waiting room is empty");
            emptyClients.setFill(Color.WHITE);
            emptyClients.getStyleClass().add("textEmptyClients");
            clients.getChildren().add(emptyClients);
        }
        vBox.getChildren().add(clients);


        for (Server server : servers) {
            HBox serverBox = new HBox();
            serverBox.setSpacing(10); // Adjust spacing as needed
            serverBox.setPrefWidth(HBox.USE_COMPUTED_SIZE);
            serverBox.setMaxWidth(Double.MAX_VALUE);



            Label queueNumber = new Label("Queue " + server.getQueueNumber() + " waitingTime- " + server.getWaitingtime() + ": ");
            queueNumber.getStyleClass().add("queue");
            queueNumber.setMinWidth(Control.USE_PREF_SIZE);
            serverBox.getChildren().add(queueNumber);

            for (Client client : server.getClients()) {
                Text clientText = new Text();
                clientText.setFill(Color.WHITE);
                clientText.getStyleClass().add("text-size");
                clientText.setText(client.toString()); // Ensure Client has a meaningful toString method


                serverBox.getChildren().add(clientText);
            }

            if (server.getClients().isEmpty()) {
                Text clientText = new Text("Queue closed");
                clientText.setFill(Color.WHITE);
                clientText.getStyleClass().add("text-size");
                serverBox.getChildren().add(clientText);
            }
            vBox.getChildren().add(serverBox);
        }
        SimulationTime.setText("Simulation Time: " + currTime);
    }

    public void showAverageDetails(Double averageService, Double averageWaiting, Double PeakHour, Integer maxClientsInQueues) {
        Text averageServiceText=new Text("Average Service " + averageService.toString());
        Text averageWaitingText=new Text("Average Waiting " + averageWaiting.toString());
        Text averagePeakHourText=new Text("Peak Hour " + PeakHour.toString() + " with " + maxClientsInQueues.toString() + " clients");

        averageServiceText.setFill(Color.WHITE);
        averageServiceText.getStyleClass().add("text-size");
        averageWaitingText.setFill(Color.WHITE);
        averageWaitingText.getStyleClass().add("text-size");
        averagePeakHourText.getStyleClass().add("text-size");
        averagePeakHourText.setFill(Color.WHITE);

        vBox.getChildren().clear();
        vBox.getChildren().add(averageServiceText);
        vBox.getChildren().add(averageWaitingText);
        vBox.getChildren().add(averagePeakHourText);
    }


    @FXML
    public void shortestTimeButtonPressed() {
        shortestTimePressed = 1;
        shortestQueuePressed = 0;
    }

    @FXML
    public void shortestQueueButtonPressed() {
        shortestTimePressed = 0;
        shortestQueuePressed = 1;
    }

    @FXML
    public void clearGUI(){
        vBox.getChildren().clear();
        nrClients.clear();
        nrQueues.clear();
        minArriveTime.clear();
        minServiceTime.clear();
        maxArriveTime.clear();
        maxServiceTime.clear();
        maxSimulationTime.clear();
        shortestQueuePressed=0;
        shortestTimePressed=0;
        simulationManager.getClients().clear();
        simulationManager.scheduler.getServers().clear();
    }
    @FXML
    public void stopSimulation(){
        simulationManager.setRunning(false);
    }

}

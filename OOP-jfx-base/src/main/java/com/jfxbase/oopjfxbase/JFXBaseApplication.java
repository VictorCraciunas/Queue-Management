package com.jfxbase.oopjfxbase;

import com.jfxbase.oopjfxbase.utils.ApplicationHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class JFXBaseApplication extends Application {
    @Override
    public void start(Stage stage) {
        ApplicationHandler.getInstance().startApplication(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
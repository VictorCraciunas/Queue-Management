package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.SceneController;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.fxml.FXML;

public class GoodByeView extends SceneController {
    @FXML
    protected void onBackToHelloClick(){
        this.changeScene(SCENE_IDENTIFIER.HELLO);
    }

    @FXML
    protected void onCloseAppClick(){
        this.closeApplication();
    }
}

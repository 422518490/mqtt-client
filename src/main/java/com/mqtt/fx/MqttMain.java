package com.mqtt.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MqttMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../../../fxml/mqttClient.fxml"));
        primaryStage.setTitle("MQTT");
        primaryStage.setScene(new Scene(root, 1200, 1500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

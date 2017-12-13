package com.mqtt.fx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MqttMain extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("MQTT");
        //取消窗口最大化和最小化
        //primaryStage.initStyle(StageStyle.UTILITY);
        //设置窗口固定大小
        //primaryStage.setResizable(false);
        /*这个必须在FXMLLoader的load方法之前，因为在加载完load方法后
        会通过反射的方式去调用controller中的initialize方法来初始化界面，
        如果不在FXMLLoader的load方法之前就会造成无法初始化Stage，从而获取不到高度和宽度的变化
        */
        StageSelf stageSelf = StageSelf.getInstance();
        stageSelf.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("../../../fxml/mqttClient.fxml"));
        primaryStage.setScene(new Scene(root, 1200, 1500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

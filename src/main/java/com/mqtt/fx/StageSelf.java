package com.mqtt.fx;

import javafx.stage.Stage;

/**
 * @author liaoyubo
 * @version 1.0 2017/12/13
 * @description
 */
public class StageSelf {

    private static StageSelf stageSelf = null;
    private StageSelf(){}
    public static StageSelf getInstance() {
        if (stageSelf == null) {
            synchronized (StageSelf.class) {
                if (stageSelf == null) {
                    stageSelf = new StageSelf();
                }
            }
        }
        return stageSelf;
    }

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

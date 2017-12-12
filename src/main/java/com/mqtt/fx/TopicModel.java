package com.mqtt.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

/**
 * @author liaoyubo
 * @version 1.0 2017/12/6
 * @description 订阅主题字段
 */
public class TopicModel {

    private CheckBox checkBox = new CheckBox();

    //主题
    private StringProperty topic;

    //请求服务质量
    private StringProperty qos;

    //用于标识是否新增
    private IntegerProperty addNewBoolean;

    public TopicModel(){
        this("请修改主题","0");
    }

    public TopicModel(String topic, String qos) {
        this.topic = new SimpleStringProperty(topic);
        this.qos = new SimpleStringProperty(qos);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public String getTopic() {
        return topic.get();
    }

    public StringProperty topicProperty() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic.set(topic);
    }

    public String getQos() {
        return qos.get();
    }

    public StringProperty qosProperty() {
        return qos;
    }

    public void setQos(String qos) {
        this.qos.set(qos);
    }

    public int getAddNewBoolean() {
        return addNewBoolean.get();
    }

    public IntegerProperty addNewBooleanProperty() {
        return addNewBoolean;
    }

    public void setAddNewBoolean(int addNewBoolean) {
        this.addNewBoolean.set(addNewBoolean);
    }
}

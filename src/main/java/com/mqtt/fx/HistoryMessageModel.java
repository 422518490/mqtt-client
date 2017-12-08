package com.mqtt.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

/**
 * @author liaoyubo
 * @version 1.0 2017/12/5
 * @description 历史记录
 */
public class HistoryMessageModel {

    private StringProperty event;

    private StringProperty title;

    private StringProperty message;

    private StringProperty historyQos;

    private ObjectProperty<LocalDateTime> dateTime;

    public HistoryMessageModel(){
        this(null,null,null,null,null);
    }

    public HistoryMessageModel(String event, String title, String message,String historyQos,  LocalDateTime dateTime) {
        this.event = new SimpleStringProperty(event);
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.historyQos = new SimpleStringProperty(historyQos);
        this.dateTime = new SimpleObjectProperty<>(dateTime);
    }

    public String getEvent() {
        return event.get();
    }

    public StringProperty eventProperty() {
        return event;
    }

    public void setEvent(String event) {
        this.event.set(event);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getHistoryQos() {
        return historyQos.get();
    }

    public StringProperty historyQosProperty() {
        return historyQos;
    }

    public void setHistoryQos(String historyQos) {
        this.historyQos.set(historyQos);
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }
}

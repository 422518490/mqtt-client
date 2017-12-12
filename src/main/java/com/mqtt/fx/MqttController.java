package com.mqtt.fx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;


public class MqttController {

    private Logger logger = LoggerFactory.getLogger(MqttController.class);

    //服务端地址
    @FXML
    private TextField serverAddress;

    //客服端标识
    @FXML
    private TextField clientId;

    //用户名
    @FXML
    private TextField userName;

    //密码
    @FXML
    private PasswordField password;

    //心跳时长
    @FXML
    private TextField keepAlive;

    //连接状态
    @FXML
    private TextField connectionStatus;

    //连接按钮
    @FXML
    private Button connectButton;

    //断开连接按钮
    @FXML
    private Button disConnectButton;

    //历史记录表格
    @FXML
    private TableView<HistoryMessageModel> historyTable;

    //历史记录表格事件列
    @FXML
    private TableColumn<HistoryMessageModel,String> eventColumn;

    //历史记录表格主题列
    @FXML
    private TableColumn<HistoryMessageModel,String> titleColumn;

    //历史记录表格消息列
    @FXML
    private TableColumn<HistoryMessageModel,String> messageColumn;

    //历史记录表格请求服务质量列
    @FXML
    private TableColumn<HistoryMessageModel,String> historyQosColumn;

    //历史记录表格时间列
    @FXML
    private TableColumn<HistoryMessageModel,LocalDateTime> dateTimeColumn;

    //订阅主题
    @FXML
    private TableView<TopicModel> topicTable;

    //订阅主题多选框勾选列
    @FXML
    private TableColumn<TopicModel,CheckBox> blankCheckBoxColumn;

    //订阅主题的主题列
    @FXML
    private TableColumn<TopicModel,String> topicColumn;

    //订阅主题的请求服务质量列
    @FXML
    private TableColumn<TopicModel,String> qosColumn;

    //发布的主题
    @FXML
    private TextField publishTitle;

    //发布的服务质量
    @FXML
    private ChoiceBox<Object> publishQos;

    //发布的消息
    @FXML
    private TextArea publishMessage;

    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;


    //历史记录表格集合
    private ObservableList<HistoryMessageModel> historyMessageModelObservableList = FXCollections.observableArrayList();

    //订阅主题集合
    private ObservableList<TopicModel> topicModelObservableList = FXCollections.observableArrayList();

    /**
     * 连接按钮
     */
    @FXML
    public void connect(){
        String text = "已连接";
        // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        try {
            mqttClient = new MqttClient(serverAddress.getText(),clientId.getText(),new MemoryPersistence());
            // MQTT的连接设置
            mqttConnectOptions = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            mqttConnectOptions.setCleanSession(true);
            String userNameText = userName.getText();
            // 设置连接的用户名
            if (!"".equals(userNameText)){
                mqttConnectOptions.setUserName(userNameText);
            }
            String passwordText = password.getText();
            // 设置连接的密码
            if(!"".equals(passwordText)){
                mqttConnectOptions.setPassword(passwordText.toCharArray());
            }
            // 设置超时时间 单位为秒
            mqttConnectOptions.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒
            // 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            mqttConnectOptions.setKeepAliveInterval(Integer.parseInt(keepAlive.getText()));
            // 设置回调
            mqttClient.setCallback(new PushCallback());
            //MqttTopic mqttTopic = mqttClient.getTopic(TOPIC);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知
            //mqttConnectOptions.setWill(mqttTopic,"close".getBytes(),2,true);
            mqttClient.connect(mqttConnectOptions);
            //连接成功后连接按钮不可用，断开连接按钮可用
            connectButton.setDisable(true);
            disConnectButton.setDisable(false);
            //输入框不可用
            serverAddress.setDisable(true);
            clientId.setDisable(true);
            userName.setDisable(true);
            password.setDisable(true);
            keepAlive.setDisable(true);
        } catch (Exception e) {
            text = "连接异常";
            logger.error("连接失败",e);
            e.printStackTrace();
        }finally {
            connectionStatus.setText(text);
            //添加row
            HistoryMessageModel historyMessageModel = new HistoryMessageModel("连接MQTT服务器","连接MQTT服务器",text,"",LocalDateTime.now());
            historyMessageModelObservableList.add(historyMessageModel);
            historyTable.setItems(historyMessageModelObservableList);
        }
    }

    /**
     * 断开连接按钮
     */
    @FXML
    public void disConnect(){
        String text = "已断开连接";
        try {
            mqttClient.disconnect();
            //连接成功后连接按钮可用，断开连接按钮不可用
            connectButton.setDisable(false);
            disConnectButton.setDisable(true);
            //输入框可用
            serverAddress.setDisable(false);
            clientId.setDisable(false);
            userName.setDisable(false);
            password.setDisable(false);
            keepAlive.setDisable(false);
        } catch (Exception e) {
            text = "断开连接异常";
            logger.error("断开连接失败",e);
            e.printStackTrace();
        }finally {
            connectionStatus.setText(text);
            //添加row
            HistoryMessageModel historyMessageModel = new HistoryMessageModel("断开MQTT服务器连接","断开MQTT服务器连接",text,"",LocalDateTime.now());
            historyMessageModelObservableList.add(historyMessageModel);
            historyTable.setItems(historyMessageModelObservableList);
        }
    }

    /**
     * 用于初始化添加行值
     */
    @FXML
    private void initialize(){
        //历史记录表格
        eventColumn.setCellValueFactory(cellData -> cellData.getValue().eventProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        messageColumn.setCellValueFactory(cellData -> cellData.getValue().messageProperty());
        historyQosColumn.setCellValueFactory(cellData -> cellData.getValue().historyQosProperty());
        dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().dateTimeProperty());

        //订阅主题
        PropertyValueFactory<TopicModel,CheckBox> propertyValueFactory = new PropertyValueFactory<TopicModel,CheckBox>("checkBox");
        blankCheckBoxColumn.setCellValueFactory(new PropertyValueFactory<TopicModel,CheckBox>("checkBox"));
        topicColumn.setCellValueFactory(cellData -> cellData.getValue().topicProperty());
        qosColumn.setCellValueFactory(cellData -> cellData.getValue().qosProperty());

        //为下拉框赋值
        publishQos.setItems(FXCollections.observableArrayList("0-至多一次","1-至少一次","2-只有一次","3-保留"));
        //设置默认选中值
        publishQos.setValue("0-至多一次");

    }

    //添加主题
    @FXML
    public void addTopic(){
        TopicModel topic = new TopicModel();
        topicModelObservableList.add(topic);
        topicTable.setItems(topicModelObservableList);
        //让行可以编辑
        topicColumn.setCellFactory(TextFieldTableCell.<TopicModel>forTableColumn());
        //按下回车提交时的事件
        topicColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<TopicModel, String> t) -> {
                    ((TopicModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setTopic(t.getNewValue());
                    //删除原来的信息
                    TopicModel topicModel = t.getRowValue();
                    topicModel.setTopic(t.getOldValue());
                    topicModelObservableList.remove(topicModel);
                    //新增修改后的信息
                    topicModel.setTopic(t.getNewValue());
                    topicModelObservableList.add(topicModel);
                });
        qosColumn.setCellFactory(TextFieldTableCell.<TopicModel>forTableColumn());
        qosColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<TopicModel, String> t) -> {
                    ((TopicModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setQos(t.getNewValue());
                    //删除原来的信息
                    TopicModel topicModel = t.getRowValue();
                    topicModel.setQos(t.getOldValue());
                    topicModelObservableList.remove(topicModel);
                    //新增修改后的信息
                    topicModel.setQos(t.getNewValue());
                    topicModelObservableList.add(topicModel);
                });
    }

    //删除主题
    @FXML
    public void delTopic(){
        topicModelObservableList.forEach(topicModel -> {
            //判断如果选中了则删除，且不在订阅
            if(topicModel.getCheckBox().isSelected()){
                try {
                    mqttClient.unsubscribe(topicModel.getTopic());
                    topicModelObservableList.remove(topicModel);
                } catch (MqttException e) {
                    logger.error("删除主题失败",e);
                    e.printStackTrace();
                }
            }
        });
    }

    //更新主题
    @FXML
    public void updateTopic(){
        //让行可以编辑
        topicColumn.setCellFactory(TextFieldTableCell.<TopicModel>forTableColumn());
        topicColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<TopicModel, String> t) -> {
                    ((TopicModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setTopic(t.getNewValue());
                    //删除原来的信息
                    TopicModel topicModel = t.getRowValue();
                    topicModel.setTopic(t.getOldValue());
                    topicModelObservableList.remove(topicModel);
                    //新增修改后的信息
                    topicModel.setTopic(t.getNewValue());
                    topicModelObservableList.add(topicModel);
                    //不在订阅原来的topic
                    try {
                        mqttClient.unsubscribe(t.getOldValue());
                    } catch (MqttException e) {
                        logger.error("更新主题失败",e);
                        e.printStackTrace();
                    }
                });
        qosColumn.setCellFactory(TextFieldTableCell.<TopicModel>forTableColumn());
        qosColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<TopicModel, String> t) -> {
                    ((TopicModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setQos(t.getNewValue());
                    //删除原来的信息
                    TopicModel topicModel = t.getRowValue();
                    topicModel.setQos(t.getOldValue());
                    topicModelObservableList.remove(topicModel);
                    //新增修改后的信息
                    topicModel.setQos(t.getNewValue());
                    topicModelObservableList.add(topicModel);
                    //不在订阅原来的topic
                    try {
                        mqttClient.unsubscribe(topicModel.getTopic());
                    } catch (MqttException e) {
                        logger.error("更新主题失败",e);
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 订阅主题
     */
    @FXML
    public void subscribeTopics(){
        //订阅消息，取值有0表示消息发送至多一次，1至少一次，2只有一次，3保留
        int topicSize = topicModelObservableList != null ? topicModelObservableList.size():0;
        //用于表示订阅的数量
        int subscribeCount = 0;
        for(TopicModel topicModel :topicModelObservableList){
            if(topicModel.getCheckBox().isSelected()){
                subscribeCount++;
            }
        }
        int [] qos = new int[subscribeCount];
        String[] topics = new String[subscribeCount];
        topicModelObservableList.forEach(topicModel -> {
                    int k = 0;
                    //判断如果选中了则订阅
                    if (topicModel.getCheckBox().isSelected()) {
                        qos[k] = Integer.parseInt(topicModel.getQos());
                        topics[k] = topicModel.getTopic();
                        k++;
                    }
                });
        //如果有订阅
        if(subscribeCount > 0){
            //Qos和topics数组长度必须相等
            String text = "订阅成功";
            try {
                mqttClient.subscribe(topics,qos);
            } catch (Exception e) {
                text = "订阅失败";
                logger.error("订阅失败",e);
                e.printStackTrace();
            }finally {
                //主题buffer
                StringBuffer topicBuffer = new StringBuffer();
                for(String topic :topics){
                    topicBuffer.append(topic);
                }
                //请求服务质量buffer
                StringBuffer qosBuffer = new StringBuffer();
                for (int qosb : qos){
                    qosBuffer.append(qosb);
                }
                HistoryMessageModel historyMessageModel = new HistoryMessageModel("订阅topic",topicBuffer.toString(),text,qosBuffer.toString(),LocalDateTime.now());
                historyMessageModelObservableList.add(historyMessageModel);
                historyTable.setItems(historyMessageModelObservableList);
            }
        }
    }

    /**
     * 取消订阅主题
     */
    @FXML
    public void unsubscribeTopics(){
        int topicSize = topicModelObservableList != null ? topicModelObservableList.size():0;
        //用于表示取消订阅的数量
        int subscribeCount = 0;
        for(TopicModel topicModel :topicModelObservableList){
            if(topicModel.getCheckBox().isSelected()){
                subscribeCount++;
            }
        }
        String[] topics = new String[subscribeCount];
        topicModelObservableList.forEach(topicModel -> {
            int k = 0;
            //判断如果选中了则订阅
            if (topicModel.getCheckBox().isSelected()) {
                topics[k] = topicModel.getTopic();
                k++;
            }
        });
        //如果有取消订阅
        if(subscribeCount > 0){
            String text = "取消订阅成功";
            try {
                mqttClient.unsubscribe(topics);
            } catch (Exception e) {
                text = "取消订阅失败";
                logger.error("取消订阅失败",e);
                e.printStackTrace();
            }finally {
                StringBuffer topicBuffer = new StringBuffer();
                for(String topic :topics){
                    topicBuffer.append(topic);
                }
                HistoryMessageModel historyMessageModel = new HistoryMessageModel("取消订阅topic",topicBuffer.toString(),text,"",LocalDateTime.now());
                historyMessageModelObservableList.add(historyMessageModel);
                historyTable.setItems(historyMessageModelObservableList);
            }
        }
    }

    /**
     * 发布主题
     */
    @FXML
    public void publishTopic(){
        MqttDeliveryToken mqttDeliveryToken = null;
        int qos = -1;
        String text = "发布topic成功";
        try {
            //根据分隔符拆分字符串获取qos
            qos = Integer.parseInt(publishQos.getValue().toString().split("-")[0]);
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(publishMessage.getText().getBytes());
            //为true表示发送后这条消息不会被删除，false则相反
            mqttMessage.setRetained(true);
            MqttTopic mqttTopic = mqttClient.getTopic(publishTitle.getText());
            //发布主题
            mqttDeliveryToken = mqttTopic.publish(mqttMessage);
            mqttDeliveryToken.waitForCompletion();
        } catch (Exception e) {
            text = "发布topic异常";
            logger.error("发布topic失败",e);
            e.printStackTrace();
        }finally {
            HistoryMessageModel historyMessageModel ;
            if(mqttDeliveryToken!= null && !mqttDeliveryToken.isComplete()){
                text = "发布topic失败";
            }
            historyMessageModel = new HistoryMessageModel(text,publishTitle.getText(),publishMessage.getText(),qos+"",LocalDateTime.now());
            historyMessageModelObservableList.add(historyMessageModel);
            historyTable.setItems(historyMessageModelObservableList);
        }
    }



    class PushCallback implements MqttCallback {

        public void connectionLost(Throwable throwable) {
            // 连接丢失后，一般在这里面进行重连
            System.out.println("连接断开，可以做重连");
        }

        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            // subscribe后得到的消息会执行到这里面
            System.out.println("接收消息主题 : " + topic);
            System.out.println("接收消息Qos : " + mqttMessage.getQos());
            System.out.println("接收消息内容 : " + new String(mqttMessage.getPayload()));
            HistoryMessageModel historyMessageModel = new HistoryMessageModel("已接收topic",topic,new String(mqttMessage.getPayload()),mqttMessage.getQos()+"",LocalDateTime.now());
            historyMessageModelObservableList.add(historyMessageModel);
            historyTable.setItems(historyMessageModelObservableList);
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            System.out.println("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
        }
    }
}

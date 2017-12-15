package com.mqtt.fx;


import com.sun.javafx.tk.ScreenConfigurationAccessor;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;


public class MqttController {

    private Logger logger = LoggerFactory.getLogger(MqttController.class);

    @FXML
    private SplitPane mqttSplitPane;

    @FXML
    private TabPane leftTabPane;

    @FXML
    private TabPane rightTabPane;

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

    //订阅主题按钮
    @FXML
    private Button subscribeTopicButton;

    //订阅主题的取消按钮
    @FXML
    private Button unsubscribeTopicButton;

    //发布的主题
    @FXML
    private TextField publishTitle;

    //发布的服务质量
    @FXML
    private ChoiceBox<Object> publishQos;

    //发布的消息
    @FXML
    private TextArea publishMessage;

    //发布按钮
    @FXML
    private Button publish;

    //订阅的title pane
    @FXML
    private TitledPane subscribeTitledPane;

    //连接与订阅间的分割线
    @FXML
    private Separator connectSeparator;

    //发布的title pane
    @FXML
    private TitledPane publishTitledPane;

    //订阅与发布之间的分割线
    @FXML
    private Separator subscribeSeparator;

    //服务端地址
    @FXML
    private Label serverLabel;

    //客户端标识
    @FXML
    private Label clientLabel;

    //用户名label
    @FXML
    private Label userLabel;

    //密码label
    @FXML
    private Label passwordLabel;

    //心跳时长label
    @FXML
    private Label keepAliveLabel;

    //连接状态label
    @FXML
    private Label connectStatusLabel;

    //添加订阅button按钮
    @FXML
    private ImageView addTopicButton;

    //删除订阅button按钮
    @FXML
    private ImageView delTopicButton;

    //修改订阅button按钮
    @FXML
    private ImageView updateTopicButton;

    //主题label
    @FXML
    private Label titleLabel;

    //服务质量label
    @FXML
    private Label qosLabel;

    //服务质量label
    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane leftAnchorPane;

    @FXML
    private AnchorPane rightAnchorPane;

    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;
    private Screen screen;
    private StageSelf stageSelf;
    private Stage stage;

    //存放放大缩小时的窗体宽度大小
    private double maxMinWidthSize;
    //存放首次放大前的窗体宽度大小
    double tempWidthSize;
    //存放放大缩小时的窗体高度大小
    private double maxMinHeightSize;
    //存放首次放大前的窗体高度大小
    double tempHeightSize;
    //判断是否是放大缩小操作
    private boolean maxMinBoolean = false;
    //用于初始化是否是启动加载
    private boolean initLoadBoolean = false;


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
            /*StageSelf stageSelf = StageSelf.getInstance();
            if(stageSelf.getStage() != null){
                stageSelf.getStage().widthProperty().addListener(new WidthChange());
                stageSelf.getStage().heightProperty().addListener(new HeightChange());
            }*/
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

        //设置分割线的左右两边窗体大小
        changePane();
        //获取窗口信息
        stageSelf = StageSelf.getInstance();
        stage = stageSelf.getStage();
        if(stage != null){
            //初始化时默认为true
            initLoadBoolean = true;
            //宽度变化监听
            stage.widthProperty().addListener(new WidthChange());
            //高度变化监听
            stage.heightProperty().addListener(new HeightChange());
            //窗口放大缩小监听
            stage.maximizedProperty().addListener(new ChangeMaxMinStage());
        }

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

    private void changePane(){
        //设置分割线的左右两边窗体大小
        mqttSplitPane.setDividerPositions(0.38);
        leftTabPane.maxWidthProperty().bind(mqttSplitPane.widthProperty().multiply(0.38));
        leftTabPane.minWidthProperty().bind(mqttSplitPane.widthProperty().multiply(0.38));
        rightTabPane.minWidthProperty().bind(mqttSplitPane.widthProperty().multiply(0.62));
        rightTabPane.maxWidthProperty().bind(mqttSplitPane.widthProperty().multiply(0.62));
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

    /**
     * 窗体改变大小
     */
    class WidthChange implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            //如果不是初始化且没有进行放大缩小
            if(!initLoadBoolean && !maxMinBoolean){
                maxMinBoolean = false;
            }
            changeStageShow();
            //防止因为放大缩小造成的拖动不能正常完成显示,这个地方有先后顺序，所以放在了改变高度里面设置
            //maxMinBoolean = false;
        }
    }


    /**
     * 使用放大缩小按钮
     */
    class ChangeMaxMinStage implements ChangeListener<Boolean>{

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            //如果是放大
            if(newValue){
                //获取屏幕的宽度最大值
                maxMinWidthSize = Double.parseDouble(screen.getBounds().getWidth()+"");
                //获取当前pane的位置宽度
                tempWidthSize = mqttSplitPane.widthProperty().getValue();
                //获取屏幕的高度最大值
                maxMinHeightSize = Double.parseDouble(screen.getBounds().getHeight()+"");
                //获取当前pane的位置高度
                tempHeightSize = mqttSplitPane.heightProperty().getValue();
            }else{
                //如果是缩小则将当前的位置宽度赋值给最大值
                maxMinWidthSize = tempWidthSize;
                //将当前位置的高度赋值给最大值
                maxMinHeightSize = tempHeightSize;
            }
            //标明是进行放大缩小功能
            maxMinBoolean = true;
            //因为放大缩小功能也是进行窗口改变，所以注释掉这里的代码
            //changeStageShow();

        }
    }

    private void changeStageShow(){
        //如果是初始化
        if(initLoadBoolean){
            screen = Screen.getPrimary();
            //获取当前窗口的宽度大小同时复制给中间变量
            maxMinWidthSize = mqttSplitPane.widthProperty().getValue();
            tempWidthSize = maxMinWidthSize;
            //获取当前窗口的高度大小同时复制给中间变量
            maxMinHeightSize = mqttSplitPane.heightProperty().getValue();
            tempHeightSize = maxMinHeightSize;
            //标明已经初始化完成
            //initLoadBoolean = false;
        }else {
            //如果已经初始化并且不是放大缩小
            if(!maxMinBoolean){
                //获取当前窗口的宽度大小
                maxMinWidthSize = mqttSplitPane.widthProperty().getValue();
                //获取当前窗口的高度大小
                maxMinHeightSize = mqttSplitPane.heightProperty().getValue();
            }
        }
        //设置订阅标题的长短变化
        subscribeTitledPane.setPrefWidth(maxMinWidthSize * 0.38);

        //设置发布标题长短变化
        publishTitledPane.setPrefWidth(maxMinWidthSize * 0.38);

        //设置订阅分隔符的长短变化
        subscribeSeparator.setPrefWidth(maxMinWidthSize * 0.38);

        //设置连接分隔符的长短变化
        connectSeparator.setPrefWidth(maxMinWidthSize * 0.38);

        //设置订阅表格大小及字段长短变化
        topicTable.setPrefWidth(maxMinWidthSize * 0.38);
        topicColumn.setPrefWidth(topicTable.getPrefWidth()*0.7);
        blankCheckBoxColumn.setPrefWidth(topicTable.getPrefWidth()*0.1);
        qosColumn.setPrefWidth(topicTable.getPrefWidth()*0.2);
        //设置订阅按钮与取消订阅按钮的位置
        subscribeTopicButton.setLayoutX(subscribeTitledPane.getPrefWidth()*0.3);
        unsubscribeTopicButton.setLayoutX(subscribeTitledPane.getPrefWidth()*0.5);

        //历史表格大小及字段长短变化
        historyTable.setPrefWidth(maxMinWidthSize * 0.62);
        eventColumn.setPrefWidth(historyTable.getPrefWidth()*0.15);
        titleColumn.setPrefWidth(historyTable.getPrefWidth()*0.18);
        messageColumn.setPrefWidth(historyTable.getPrefWidth()*0.33);
        historyQosColumn.setPrefWidth(historyTable.getPrefWidth()*0.12);
        dateTimeColumn.setPrefWidth(historyTable.getPrefWidth()*0.22);

    }

    class HeightChange implements ChangeListener<Number>{

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            //如果是初始化
            if(initLoadBoolean){
                screen = Screen.getPrimary();
                //获取当前窗口的高度大小同时复制给中间变量
                maxMinHeightSize = mqttSplitPane.heightProperty().getValue();
                tempHeightSize = maxMinHeightSize;
                //标明已经初始化完成
                initLoadBoolean = false;
            }else {
                //如果已经初始化并且不是放大缩小
                if(!maxMinBoolean){
                    //获取当前窗口的高度大小
                    maxMinHeightSize = mqttSplitPane.heightProperty().getValue();
                }
            }
            //设置连接部分的高度变化
            serverLabel.setLayoutY(maxMinHeightSize * 0.02);
            serverAddress.setLayoutY(maxMinHeightSize * 0.02);
            clientLabel.setLayoutY(maxMinHeightSize * 0.06);
            clientId.setLayoutY(maxMinHeightSize * 0.06);
            userLabel.setLayoutY(maxMinHeightSize * 0.1);
            userName.setLayoutY(maxMinHeightSize * 0.1);
            passwordLabel.setLayoutY(maxMinHeightSize * 0.14);
            password.setLayoutY(maxMinHeightSize * 0.14);
            keepAliveLabel.setLayoutY(maxMinHeightSize * 0.18);
            keepAlive.setLayoutY(maxMinHeightSize * 0.18);
            connectStatusLabel.setLayoutY(maxMinHeightSize * 0.22);
            connectionStatus.setLayoutY(maxMinHeightSize * 0.22);
            connectButton.setLayoutY(maxMinHeightSize * 0.26);
            disConnectButton.setLayoutY(maxMinHeightSize * 0.26);
            connectSeparator.setLayoutY(maxMinHeightSize * 0.29);

            //设置订阅部分的高度变化
            subscribeTitledPane.setLayoutY(maxMinHeightSize * 0.31);
            addTopicButton.setLayoutY(maxMinHeightSize * 0.343);
            delTopicButton.setLayoutY(maxMinHeightSize * 0.345);
            updateTopicButton.setLayoutY(maxMinHeightSize * 0.345);
            topicTable.setLayoutY(maxMinHeightSize * 0.37);
            topicTable.setPrefHeight(maxMinHeightSize * 0.2);
            subscribeTopicButton.setLayoutY(maxMinHeightSize * 0.59);
            unsubscribeTopicButton.setLayoutY(maxMinHeightSize * 0.59);
            subscribeSeparator.setLayoutY(maxMinHeightSize * 0.63);

            //设置发布部分的高度变化
            publishTitledPane.setLayoutY(maxMinHeightSize * 0.65);
            titleLabel.setLayoutY(maxMinHeightSize * 0.68);
            publishTitle.setLayoutY(maxMinHeightSize * 0.68);
            qosLabel.setLayoutY(maxMinHeightSize * 0.72);
            publishQos.setLayoutY(maxMinHeightSize * 0.72);
            messageLabel.setLayoutY(maxMinHeightSize * 0.76);
            publishMessage.setLayoutY(maxMinHeightSize * 0.76);
            publishMessage.setPrefHeight(maxMinHeightSize * 0.06);
            publish.setLayoutY(maxMinHeightSize * 0.83);

            //设置历史table部分的高度变化
            historyTable.setPrefHeight(maxMinHeightSize * 0.8);

            //防止因为放大缩小造成的拖动不能正常完成显示
            maxMinBoolean = false;
        }
    }

}

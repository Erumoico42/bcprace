/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import javafx.scene.control.Button;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import prometheus.Vehicles.Animation;
import prometheus.Vehicles.BotCar;
import prometheus.Vehicles.BotTram;
import prometheus.Vehicles.MyCar;


/**
 *
 * @author Honza
 */
public class Prometheus extends Application {
    private static Group root;
    private static Connect actConnect;
    private static List<Connect> startConnects=new ArrayList<Connect>();
    private static List<Connect> connects=new ArrayList<Connect>();
    private static Usek actUsek;
    private static final List<Button> addCarBtns=new ArrayList<Button>();
    private static Animation a;  
    private static final List<Circle> checkPoints=new ArrayList<Circle>();
    private static final List<Node> hideShowList=new ArrayList<>();
    private Scene scene;
    private static boolean tram=false;
    private static SubScene subScene;
    private static Group rootSS;
    private static Canvas canvas;
    private static ImageView background;
    private static HBox backgroundBox;
    private static Policie actPol;
    private boolean changeSpeedLoop=false;
    double startX;
    double startY;
    double distX;
    double distY;
    double raito=0;
    double layoutX=0, layoutY=0;
    private double myCarSpeedChange=0;
    private final int RESIZE_VALUE=50, MOVE_VALUE=25; 
    private TimerTask timertask, changeSpeedTimerTask;
    private Timer timer, changeSpeedTimer;
    private boolean autoGen=false;
    private int genDeley=1000, genDeleyTram=5000;
    private boolean delCanged=false;
    private static int lastUsekId=0, lastCurveId=0, lastConnId=0, lastPsId=0, lastIdPk=0;
    private static List<Usek> useky=new ArrayList<Usek>();
    private static List<Usek> startUsekyCar=new ArrayList<Usek>();
    private static List<Usek> startUsekyTram=new ArrayList<Usek>();
    private static List<MyCurve> krivky=new ArrayList<MyCurve>();
    private static List<HBox> semaforyImg=new ArrayList<HBox>();
    private static List<Semafor> semafory=new ArrayList<Semafor>();
    private static List<Policie> policie=new ArrayList<Policie>();
    private static String bgSource=null;
    private static final SemaforControl sc=new SemaforControl();
    private static MyCar myCar=null;
    private static Statistiky statistika=new Statistiky();
    private static TextField setPolTime, setWaitTime;
    private static Button autoGenBtn, newKombin, autoGenBtnTram;
    private TextField speedTF;
    private boolean delCangedTram;
    private Timer timerTram;
    private TimerTask timertaskTram;
    private boolean autoGenTram;
    private static double resizeRatio;
    private Button loadBg;
    private static Button lockBg;
    private static boolean locked=true;
  
    @Override
    public void start(Stage primaryStage) {
        
        a=new Animation();
        
        root = new Group();
        scene = new Scene(root, 900,600);
        rootSS=new Group();
        canvas=new Canvas(765,515);
        bgInit();
        rootSS.getChildren().addAll(backgroundBox, canvas);
        subScene=new SubScene(rootSS, 765, 515);
        primaryStage.setTitle("Prometheus");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            a.stop();
            
            if(timer!=null && timertask!=null)
            {
                timer.cancel();
                timertask.cancel();   
            }
            if(timerTram!=null && timertaskTram!=null)
            {
                timerTram.cancel();
                timertaskTram.cancel();   
            }
            sc.end();
        });
        primaryStage.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setWidth(newValue.intValue()-150);
            canvas.setWidth(subScene.getWidth());
        });
        primaryStage.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setHeight(newValue.intValue()-120);
            canvas.setHeight(subScene.getHeight());
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN)
                {
                    changeSpeedLoop=false;
                    if(changeSpeedTimer!=null)
                        changeSpeedTimer.cancel();
                    if(changeSpeedTimerTask!=null)
                        changeSpeedTimerTask.cancel();
                }
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(myCar!=null){
                    if(!changeSpeedLoop){
                        if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN){
                            changeSpeedLoop=true;

                            if(event.getCode()==KeyCode.UP)
                                myCarSpeedChange=0.002;
                            if(event.getCode()==KeyCode.DOWN)
                            {
                                myCarSpeedChange=-0.002;
                                
                            }
                            
                            changeSpeedLoop();
                        }
                    }
                }
                
            }
        });
        initGUI();
    }
    private void changeSpeedLoop()
    {
        changeSpeedTimer = new Timer();
        changeSpeedTimerTask = new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if(myCar!=null){
                        if(myCar.getSpeed()+myCarSpeedChange>=0)
                        {
                            myCar.setSpeed(myCar.getSpeed()+myCarSpeedChange);
                        }
                        else
                        {
                            myCar.setSpeed(0);
                        }
                        speedTF.setText(String.valueOf(Math.round(myCar.getSpeed()*1000)));
                    }
                    
                });
            }
        };
        changeSpeedTimer.schedule(changeSpeedTimerTask, 0, 50);   
    }
    public static void addSemafor(Semafor sem)
    {
        semafory.add(sem);
    }
    public static void addCurve(MyCurve mc)
    {
        krivky.add(mc);
    }
    public static void addUsek(Usek us)
    {
        useky.add(us);
    }
    public static void cleanUseky()
    {
        useky.clear();
        
    }
    public static void setLastPsId(int id)
    {
        lastPsId=id;
    }
    private void hideShowList(boolean show)
    {
        for (Node node : hideShowList) {
            node.setVisible(show);
        }
    }
    public static void addToHideShow(Node node)
    {
        hideShowList.add(node);
    }
    public void removeHideShow(Node node)
    {
        hideShowList.remove(node);
    }
    public static int getLastPsId()
    {
        return lastPsId;
    }
    public static void setlastIdPk(int idPk)
    {
        lastIdPk=idPk;
    }
    public static int getLastIdPk()
    {
        return lastIdPk;
    }
    public static Statistiky getStatistiky()
    {
        return statistika;
    }
    public static void addPolicie(Policie pol)
    {
        policie.add(pol);
    }
    public static void cleanSemafory()
    {
        rootSS.getChildren().removeAll(semaforyImg);
        semaforyImg.clear();
        semafory.clear();
    }
    public static void setBgSource(String source)
    {
        bgSource=source;
    }
    private void autoAddCar()
    {
        if(!autoGen)
        {
            autoGen=true;
            genCar();
            autoGenBtn.setText("Auta off");
        }
        else
        {
            autoGenBtn.setText("Auta on");
            timer.cancel();
            timertask.cancel();
            autoGen=false;
        }
    }
    private void autoAddTram()
    {
        if(!autoGenTram)
        {
            autoGenTram=true;
            genTram();
            autoGenBtnTram.setText("Tram off");
        }
        else
        {
            autoGenBtnTram.setText("Tram on");
            timerTram.cancel();
            timertaskTram.cancel();
            autoGenTram=false;
        }
    }
    public static void setLastId(int lastUId)
    {
        lastUsekId=lastUId;
    }
    private void genCar()
    {
        timer = new Timer();
        timertask=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    
                    generCar();
                    
                    if(delCanged)
                    {
                        delCanged=false;
                        timer.cancel();
                        genCar(); 
                    }
                });
            }
        };
        timer.schedule(timertask, 0, genDeley);    
    }private void genTram()
    {
        timerTram = new Timer();
        timertaskTram=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    
                    generTram();
                    
                    if(delCangedTram)
                    {
                        delCangedTram=false;
                        timerTram.cancel();
                        genTram(); 
                    }
                });
            }
        };
        timerTram.schedule(timertaskTram, 0, genDeleyTram);    
    }
    private void generCar()
    {
        if(!startUsekyCar.isEmpty()){
            Usek rndUsek=startUsekyCar.get((int)(Math.random()*(startUsekyCar.size())));
            boolean free=true;
            if(rndUsek.getVehicle()==null){
            for (Usek uNext : rndUsek.getDalsiUseky()) {
                if(uNext.getVehicle()!=null)
                    free=false;
                }
                if(free){
                    BotCar car=new BotCar(a, rndUsek);
                }
            }
        }
    }
    private void generTram()
    {
        if(!startUsekyTram.isEmpty()){
            Usek rndUsek=startUsekyTram.get((int)(Math.random()*(startUsekyTram.size())));
            if(!rndUsek.getDalsiUseky().isEmpty()){
                rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
                boolean free=true;
                if(rndUsek.getVehicle()==null){
                    for (Usek uNext : rndUsek.getDalsiUseky()) {
                        if(uNext.getVehicle()!=null)
                            free=false;
                    }
                    if(free){
                        BotTram tram=new BotTram(a,rndUsek);
                    }
                }
            }
        }
    }
    private void guiSwitchTramCar()
    {
        ToggleGroup tramCar=new ToggleGroup();
        RadioButton rbCar=new RadioButton("Auto");
        rbCar.setSelected(true);
        rbCar.setToggleGroup(tramCar);
        rbCar.setLayoutX(150);
        rbCar.setLayoutY(10);
        rbCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tram=false;
            }
        });
        RadioButton rbTram=new RadioButton("Tramvaj");
        rbTram.setToggleGroup(tramCar);
        rbTram.setLayoutX(150);
        rbTram.setLayoutY(30);
        rbTram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tram=true;
            }
        });
        root.getChildren().addAll(rbCar, rbTram);
    }
    private void initGUI()
    {
        guiSwitchTramCar();
        boolean statshow=false;
        Button stat=new Button("Statistika");
        stat.setLayoutX(10);
        stat.setLayoutY(530);
        stat.setOnAction((ActionEvent event) -> {
            if(!statshow)
                statistika.show();
            else
               statistika.hide(); 
        });
        
        BorderPane pane = new BorderPane();
        pane.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(5),
            new BorderWidths(2))));
        pane.setCenter(subScene);
        pane.setLayoutX(120);
        pane.setLayoutY(70);

        autoGenBtn=new Button("Auta on");
        autoGenBtn.setLayoutX(250);
        autoGenBtn.setLayoutY(10);
        autoGenBtn.setOnAction((ActionEvent event) -> {
             autoAddCar();
        });
        canvas.setOnMousePressed((MouseEvent t) -> {
            if(t.getButton()==MouseButton.PRIMARY)
                newCurve(new Point((int)t.getX(),(int)t.getY()));
            else if(t.getButton()==MouseButton.SECONDARY)
            {
                if(actPol!=null){
                    PolicieStrana ps=new PolicieStrana(new Point((int)t.getX(), (int)t.getY()), actPol);
                    ps.setId(lastPsId);
                }
            }
        });
        canvas.setOnMouseDragged((MouseEvent t) -> {
            if(t.getButton()==MouseButton.PRIMARY)
            {
                actConnect.setPoint(new Point((int)t.getX(),(int)t.getY()));
                actConnect.moveConnect();
            }
        });
        TextField delTF=new TextField("1000");
        delTF.setLayoutX(350);
        delTF.setLayoutY(10);
        delTF.setMaxWidth(100);
        delTF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER)
                {
                    genDeley=Integer.parseInt(delTF.getText());
                    if(genDeley>5000)
                        genDeley=5000;
                    if(genDeley<200)
                        genDeley=200;
                    delCanged=true;
                }
            }
        }); 
        Button delMinus=new Button("-");
        delMinus.setLayoutX(330);
        delMinus.setLayoutY(10);
        delMinus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(genDeley>200)
                    genDeley-=100;
                delTF.setText(""+genDeley);
                delCanged=true;
            }
        });
        Button delPlus=new Button("+");
        delPlus.setLayoutX(450);
        delPlus.setLayoutY(10);
        delPlus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(genDeley<5000)
                    genDeley+=100;
                delTF.setText(""+genDeley);
                delCanged=true;
            }
        });
        autoGenBtnTram=new Button("Tram on");
        autoGenBtnTram.setLayoutX(250);
        autoGenBtnTram.setLayoutY(40);
        autoGenBtnTram.setOnAction((ActionEvent event) -> {
             autoAddTram();
        });
        TextField delTFTram=new TextField("5000");
        delTFTram.setLayoutX(350);
        delTFTram.setLayoutY(40);
        delTFTram.setMaxWidth(100);
        delTFTram.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER)
                {
                    genDeleyTram=Integer.parseInt(delTFTram.getText());
                    if(genDeleyTram<200)
                        genDeleyTram=200;
                    delCangedTram=true;
                }
            }
        }); 
        Button delMinusTram=new Button("-");
        delMinusTram.setLayoutX(330);
        delMinusTram.setLayoutY(40);
        delMinusTram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(genDeleyTram>200)
                    genDeleyTram-=100;
                delTFTram.setText(""+genDeleyTram);
                delCangedTram=true;
            }
        });
        Button delPlusTram=new Button("+");
        delPlusTram.setLayoutX(450);
        delPlusTram.setLayoutY(40);
        delPlusTram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    genDeleyTram+=100;
                delTFTram.setText(""+genDeleyTram);
                delCangedTram=true;
            }
        });
        Button save=new Button("Uložit");
        save.setLayoutX(780);
        save.setLayoutY(10);
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().saveFile(krivky, useky, connects, semafory, policie, bgSource, backgroundBox);
            }
        });
        Button load=new Button("Otevřít");
        load.setLayoutX(835);
        load.setLayoutY(10);
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().openFile();
            }
        });
        Button clean=new Button("Vyčistit");
        clean.setLayoutX(720);
        clean.setLayoutY(10);
        clean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                celan();
            }
        });

        Button hideShow=new Button("Skrýt");
        hideShow.setLayoutX(480);
        hideShow.setLayoutY(10);
        hideShow.setMinWidth(70);
        hideShow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(hideShow.getText().equals("Skrýt"))
                {
                    hideShowList(false);
                    hideShow.setText("Zobrazit");
                }
                else
                {
                    hideShowList(true);
                    hideShow.setText("Skrýt");
                }
            }
        });
        Button addMyCar=new Button("Vložit vlastní");
        addMyCar.setLayoutX(560);
        addMyCar.setLayoutY(10);
        addMyCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
 
                if(myCar==null)
                {
                    Usek rndUsek;
                    if(actConnect!=null && !actConnect.getStartCurves().isEmpty())
                    {
                        rndUsek=actConnect.getStartCurves().get(0).getFirst();
                    }
                    else
                    {
                        rndUsek=startUsekyCar.get((int)(Math.random()*(startUsekyCar.size())));                     
                    }
                    if(!rndUsek.getDalsiUseky().isEmpty()){
                    rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
                    if(rndUsek.getVehicle()==null){
                        speedTF.setText("0");
                        MyCar car=new MyCar(a, rndUsek);
                        myCar=car;
                    }}
                }
            }
        });
        speedTF=new TextField("0");
        speedTF.setLayoutX(645);
        speedTF.setLayoutY(10);
        speedTF.setMaxWidth(70);
        speedTF.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER)
                {
                    if(myCar!=null)
                    {
                        double newSpeed=Double.parseDouble(speedTF.getText())/1000;
                        if(newSpeed>=0 && newSpeed<1)
                            myCar.setSpeed(newSpeed);
                    }
                }
            }
        });
        Button setSpeedUp=new Button("+");
        setSpeedUp.setLayoutX(715);
        setSpeedUp.setLayoutY(10);
        setSpeedUp.setMinSize(25, 25);
        setSpeedUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(myCar!=null)
                {
                    myCar.updateSpeed(0.003);
                    speedTF.setText(String.valueOf(Math.round(myCar.getSpeed()*1000)));
                }
            }
        });
        Button setSpeedDown=new Button("-");
        setSpeedDown.setLayoutX(740);
        setSpeedDown.setLayoutY(10);
        setSpeedDown.setMinSize(25, 25);
        setSpeedDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(myCar!=null)
                {
                    myCar.updateSpeed(-0.003);
                    speedTF.setText(String.valueOf(Math.round(myCar.getSpeed()*1000)));
                }
            }
        });
        guiPolice();
        root.getChildren().addAll(pane,loadBg, lockBg, autoGenBtn, delMinus, delPlus, delTF, save, load, 
                  hideShow, addMyCar, sc.getRoot(), stat, setSpeedDown, setSpeedUp, speedTF, autoGenBtnTram, delMinusTram, delPlusTram, delTFTram);
    }
    private void guiPolice()
    {
        
        Button policieRun=new Button("Policie on");
        policieRun.setLayoutX(10);
        policieRun.setLayoutY(330);
        policieRun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(policieRun.getText().equals("Policie on")){
                    policieRun.setText("Policie off");
                    for (Policie pol : policie) {
                        pol.play();
                    }
                }
                else
                {
                    policieRun.setText("Policie on");
                    for (Policie pol : policie) {
                        pol.pause();
                    }
                }
            }
        });
        Button policieBtn=new Button("Policie++");
        policieBtn.setLayoutX(10);
        policieBtn.setLayoutY(360);
        policieBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actPol=new Policie();
                policie.add(actPol);
            }
        });
        newKombin=new Button("Směr ++");
        newKombin.setLayoutX(10);
        newKombin.setLayoutY(420);
        newKombin.setVisible(false);
        newKombin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(actPol!=null)
                {
                    if(actPol.getPs1()!=null && actPol.getPs2()!=null)
                    {
                        if(newKombin.getText().equals("Směr ++")){
                            actPol.createKomb();
                        }
                        else
                            actPol.removeKomb();
                    }
                }
            }
        });
        setPolTime=new TextField();
        setPolTime.setLayoutX(10);
        setPolTime.setLayoutY(450);
        setPolTime.setMaxWidth(80);
        setPolTime.setVisible(false);
        setPolTime.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER){
                    if(actPol!=null && actPol.getActPK()!=null)
                    {
                        actPol.getActPK().setTime(Integer.parseInt(setPolTime.getText()));
                    }
                }
            }
        });
        setWaitTime=new TextField();
        setWaitTime.setLayoutX(10);
        setWaitTime.setLayoutY(390);
        setWaitTime.setMaxWidth(80);
        setWaitTime.setVisible(false);
        setWaitTime.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER){
                    if(actPol!=null)
                    {
                        actPol.setWaitTime(Integer.parseInt(setWaitTime.getText()));
                    }
                }
            }
        });
        root.getChildren().addAll(policieBtn,policieRun, newKombin, setPolTime, setWaitTime);
    }
    public static void setVisPol(boolean vis)
    {
        setPolTime.setVisible(vis);
        newKombin.setVisible(vis);
    }
    public static void removeNode(Node node)
    {
        root.getChildren().remove(node);
    }
    public static void setPolTime(int time)
    {
        setPolTime.setText(String.valueOf(time));
    }
    public static void checkKombiPol(boolean add)
    {
        if(add)
        {
            newKombin.setText("Směr ++");
        }
        else
            newKombin.setText("Směr --");
    }
    public static SemaforControl getSC()
    {
        return sc;
    }
    public static void setMyCarNull()
    {
        myCar=null;
    }
  
    private void celan()
    {
        for (MyCurve mc : krivky) {
            rootSS.getChildren().removeAll(
                    mc.getCurve(),mc.getControl1().getCircle(),mc.getControl2().getCircle(),
                    mc.getConnect0().getCircle(), mc.getConnect3().getCircle());
        }
        for (Usek u : useky) {
            rootSS.getChildren().remove(u.getCir());
        }
        for (HBox sem : semaforyImg) {
            rootSS.getChildren().remove(sem);
        }
        
        lastUsekId=0;
        lastCurveId=0;
        krivky.clear();
        useky.clear();
        root.getChildren().removeAll(addCarBtns);
        addCarBtns.clear();
        actConnect=null;
        actUsek=null;
        startConnects.clear();
    }
    private void bgScroll(double delta)
    {
        if(delta<0)
        {
            if(background.getFitHeight()>=100 && background.getFitWidth()>=100)
            {
                background.setFitHeight(background.getFitHeight()-RESIZE_VALUE);
                background.setFitWidth(background.getFitWidth()-RESIZE_VALUE*resizeRatio);
                backgroundBox.setLayoutX(backgroundBox.getLayoutX()+MOVE_VALUE*resizeRatio);
                backgroundBox.setLayoutY(backgroundBox.getLayoutY()+MOVE_VALUE);
            }
        }
        else
        {
            background.setFitHeight(background.getFitHeight()+RESIZE_VALUE);
            background.setFitWidth(background.getFitWidth()+RESIZE_VALUE*resizeRatio);
            backgroundBox.setLayoutX(backgroundBox.getLayoutX()-MOVE_VALUE*resizeRatio);
            backgroundBox.setLayoutY(backgroundBox.getLayoutY()-MOVE_VALUE);
        }
    }
    private void bgClick(double x, double y)
    {
        startX = x;
        startY = y;
        distX = startX - backgroundBox.getLayoutX();
        distY = startY - backgroundBox.getLayoutY();
        
    }
    private void bgDrag(double x, double y)
    {
        layoutX = x - distX;
        layoutY = y - distY;
        backgroundBox.setLayoutX(layoutX);
        backgroundBox.setLayoutY(layoutY);
        distX = startX - layoutX;
        distY = startY - layoutY;
    }
    private void bgInit()
    {
        background=new ImageView();
        backgroundBox=new HBox(background);
        loadBg=new Button("Vložit pozadi");
        loadBg.setLayoutX(10);
        loadBg.setLayoutY(10);
        loadBg.setOnAction((ActionEvent event) -> {
            loadImage();
        });
        lockBg=new Button("Odemknout");
        lockBg.setLayoutX(10);
        lockBg.setLayoutY(40);
        lockBg.setOnAction((ActionEvent event) -> {
            if(!locked)
            {
                unlockBG();
            }else
            {
                lockBG();
            }
        });
        backgroundBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!locked)
                    bgClick(event.getX(),event.getY());
            }
        });
         backgroundBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!locked)
                    bgDrag(event.getX(),event.getY());
            }
        });
        
        backgroundBox.setOnScroll((ScrollEvent event) -> {
            if(!locked)
                bgScroll(event.getDeltaY());
        });
    }
    public static void unlockBG()
    {
        locked=true;
        lockBg.setText("Odemknout");
        canvas.setVisible(true);
    }
    public static void lockBG()
    {
        locked=false;
        lockBg.setText("Uzamknout");
        canvas.setVisible(false);
    }
    private void loadImage()
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) { 
            bgSource=file.toURI().toString();
            Image image = new Image(bgSource);
            loadBackground(image);   
            moveDefBg(-(image.getWidth()-subScene.getWidth())/2, -(image.getHeight()-subScene.getHeight())/2, image.getWidth(), image.getHeight());
        }
    }
    public static void moveDefBg(double layoutX, double layoutY, double width, double height)
    {
        resizeRatio=width/height;
        background.setFitHeight(height);
        background.setFitWidth(width);
        backgroundBox.setLayoutX(layoutX);
        backgroundBox.setLayoutY(layoutY);
    }
    public static void loadBackground(Image image)
    {
        background.setImage(image);
        lockBG();
    }
    public static void addNode(Node node)
    {
        rootSS.getChildren().add(node);
    }
    public static void removeNodeSS(Node node)
    {
        rootSS.getChildren().remove(node);
    }
    public static void addNode(int i, Node node)
    {     
        rootSS.getChildren().add(i,node);
    }
    public static void addCircle(Circle cir)
    {
        checkPoints.add(cir);
    }
    public static void removeCircles()
    {
        rootSS.getChildren().removeAll(checkPoints);
        checkPoints.clear();
    }
    public static int getLastUsekId()
    {
        return lastUsekId;
    }
    public static void setLastUsekId(int lui)
    {
        lastUsekId=lui;
    }
    public static Policie getActPol()
    {
        return actPol;
    }
    public static void setActPol(Policie ap)
    {
        actPol=ap;
        if(ap!=null){
            setWaitTime.setVisible(true);
            setWaitTime.setText(String.valueOf(ap.getWaitTime()));
        }
        else
        {
            setWaitTime.setVisible(false);
        }
    }
    public static int getLastConnId()
    {
        return lastConnId;
    }
    public static void setLastConnId(int lci)
    {
        lastConnId=lci;
    }
    public static int getLastCurveId()
    {
        return lastCurveId;
    }
    public static void setLasCurveId(int lci)
    {
        lastCurveId=lci;
    }
    public static void addConnect(Connect con)
    {
        connects.add(con);
    }
    public static List<Connect> getConnect()
    {
        return connects;
    }
    public static void cleanConnect()
    {
        connects.clear();
    }
    public static MyCurve newCurve(Point t)
    {
        if(actConnect==null || (actConnect!=null && (actConnect.isTram() ^ tram)))
        {
            Connect con=new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()), tram);
            startConnects.add(con);
            con.setStart(true);
            con.select();
        }
        Connect con = new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()), tram);
        MyCurve mc=new MyCurve(actConnect, con); 
        con.select();
        return mc;
    }
    public static void rozdel()
    {
        hideShowList.removeAll(checkPoints);
        //removeCircles();
        setLastUsekId(0);
        //cleanUseky();
        
        Split s=new Split(connects);
        startUsekyCar=s.getStartUsekyCar();
        startUsekyTram=s.getStartUsekyTram();
    }
    public static void setConnect(Connect con)
    {
        actConnect=con;
    }
    public static void setStartUsekyCar(List<Usek> start)
    {
        startUsekyCar=start;
    }
    public static void setStartUsekyTram(List<Usek> start)
    {
        startUsekyTram=start;
    }
    public static void setstartConnects(List<Connect> start)
    {
        startConnects=start;
    }
    public static void setActUsek(Usek u)
    {
        actUsek=u;
    }
    public static Usek getActUsek()
    {
        return actUsek;
    }
    public static Connect getActConn()
    {
        return actConnect;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

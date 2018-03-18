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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


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
    private static Auto actAuto;
    private static final List<Button> addCarBtns=new ArrayList<Button>();
    private static Animace a;  
    private static final List<Circle> checkPoints=new ArrayList<Circle>();
    private static final List<Node> hideShowList=new ArrayList<>();
    private Scene scene;
    private static SubScene subScene;
    private static Group rootSS;
    private static Canvas canvas;
    private static ImageView bgIv;
    private static Policie actPol;
    private boolean changeSpeedLoop=false;
    double startX;
    double startY;
    double distX;
    double distY;
    double raito=0;
    private double myCarSpeedChange=0;
    private final double RESIZE=50; 
    private final double MOVE=25; 
    private TimerTask timertask, changeSpeedTimerTask;
    private Timer timer, changeSpeedTimer;
    private boolean autoGen=false;
    private int genDeley=1000;
    private boolean delCanged=false;
    private static int lastUsekId=0, lastCurveId=0, lastConnId=0, lastPsId=0, lastIdPk=0;
    private static List<Usek> useky=new ArrayList<Usek>();
    private static List<Usek> startUseky=new ArrayList<Usek>();
    private static List<MyCurve> krivky=new ArrayList<MyCurve>();
    private static List<HBox> semaforyImg=new ArrayList<HBox>();
    private static List<Semafor> semafory=new ArrayList<Semafor>();
    private static List<Policie> policie=new ArrayList<Policie>();
    private static String bgSource=null;
    private static final SemaforControl sc=new SemaforControl();
    private static Auto myCar=null;
    private static Statistiky statistika=new Statistiky();
    private static TextField setPolTime, setWaitTime;
    private static Button autoGenBtn, newKombin;
    private TextField speedTF;
  
    @Override
    public void start(Stage primaryStage) {
        
        a=new Animace();
        
        root = new Group();
        scene = new Scene(root, 900,600);
        rootSS=new Group();
        canvas=new Canvas(765,535);
        rootSS.getChildren().add(canvas);
        subScene=new SubScene(rootSS, 765, 535);
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
            sc.end();
        });
        primaryStage.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setWidth(newValue.intValue()-150);
            canvas.setWidth(subScene.getWidth());
        });
        primaryStage.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setHeight(newValue.intValue()-100);
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
                            myCar.setRychlost(myCar.getSpeed()+myCarSpeedChange);
                        }
                        else
                        {
                            myCar.setRychlost(0);
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
            autoGenBtn.setText("Gener off");
        }
        else
        {
            autoGenBtn.setText("Gener on");
            timer.cancel();
            timertask.cancel();
            autoGen=false;
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
    }
    private void generCar()
    {
        Usek rndUsek=startUseky.get((int)(Math.random()*(startUseky.size())));
        if(!rndUsek.getDalsiUseky().isEmpty()){
            rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
            boolean free=true;
            if(rndUsek.getCar()==null){
                for (Usek uNext : rndUsek.getDalsiUseky()) {
                    if(uNext.getCar()!=null)
                        free=false;
                }
                if(free){
                    Auto car=new Auto(rndUsek, a);
                }
            }
        }
    }
    private void initGUI()
    {
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
        pane.setLayoutY(50);
        Button loadImage=new Button("Vložit pozadi");
        loadImage.setLayoutX(10);
        loadImage.setLayoutY(20);
        final FileChooser fileChooser = new FileChooser();
        loadImage.setOnAction((ActionEvent event) -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) { 
                loadImage(file);
            }
        });
        Button setbg=new Button("Nastavit pozadi");
        setbg.setLayoutX(125);
        setbg.setLayoutY(20);
        setbg.setOnAction((ActionEvent event) -> {
             setBg(bgIv);
        });
        autoGenBtn=new Button("Gener on");
        autoGenBtn.setLayoutX(250);
        autoGenBtn.setLayoutY(20);
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
        delTF.setLayoutY(20);
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
        delMinus.setLayoutY(20);
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
        delPlus.setLayoutY(20);
        delPlus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(genDeley<5000)
                    genDeley+=100;
                delTF.setText(""+genDeley);
                delCanged=true;
            }
        });
        
        Button save=new Button("Uložit");
        save.setLayoutX(780);
        save.setLayoutY(20);
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().saveFile(krivky, useky, connects, semafory, policie, bgSource, bgIv);
            }
        });
        Button load=new Button("Otevřít");
        load.setLayoutX(835);
        load.setLayoutY(20);
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().openFile();
            }
        });
        Button clean=new Button("Vyčistit");
        clean.setLayoutX(720);
        clean.setLayoutY(20);
        clean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                celan();
            }
        });

        Button hideShow=new Button("Skrýt");
        hideShow.setLayoutX(480);
        hideShow.setLayoutY(20);
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
        addMyCar.setLayoutY(20);
        addMyCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
 
                if(myCar==null)
                {
                    Usek rndUsek;
                    if(actConnect!=null && !actConnect.getStartCurves().isEmpty())
                    {
                        rndUsek=actConnect.getStartCurves().get(0).getPrvni();
                    }
                    else
                    {
                        rndUsek=startUseky.get((int)(Math.random()*(startUseky.size())));                     
                    }
                    if(!rndUsek.getDalsiUseky().isEmpty()){
                    rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
                    if(rndUsek.getCar()==null){
                        speedTF.setText("0");
                        Auto car=new Auto(rndUsek, a);
                        myCar=car;
                        car.setMyCar();
                    }}
                }
            }
        });
        speedTF=new TextField("0");
        speedTF.setLayoutX(645);
        speedTF.setLayoutY(20);
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
                            myCar.setRychlost(newSpeed);
                    }
                }
            }
        });
        Button setSpeedUp=new Button("+");
        setSpeedUp.setLayoutX(715);
        setSpeedUp.setLayoutY(20);
        setSpeedUp.setMinSize(25, 25);
        setSpeedUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(myCar!=null)
                {
                    myCar.zvysitRychlost(0.003);
                    speedTF.setText(String.valueOf(Math.round(myCar.getSpeed()*1000)));
                }
            }
        });
        Button setSpeedDown=new Button("-");
        setSpeedDown.setLayoutX(740);
        setSpeedDown.setLayoutY(20);
        setSpeedDown.setMinSize(25, 25);
        setSpeedDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(myCar!=null)
                {
                    myCar.zvysitRychlost(-0.003);
                    if(myCar.getSpeed()<0)
                        myCar.setRychlost(0);
                    speedTF.setText(String.valueOf(Math.round(myCar.getSpeed()*1000)));
                }
            }
        });
        guiPolice();
        root.getChildren().addAll(pane,setbg, loadImage, autoGenBtn, delMinus, delPlus, delTF, save, load, 
                  hideShow, addMyCar, sc.getRoot(), stat, setSpeedDown, setSpeedUp, speedTF);
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
    public static void setBg(ImageView imgv)
    {
        Image newImg = imgv.snapshot(null, null);
        Rectangle2D croppedPortion = new Rectangle2D(-(int)imgv.getLayoutX(), -(int)imgv.getLayoutY(), (int)subScene.getWidth(), (int)subScene.getHeight());

        ImageView newIv = new ImageView(newImg);
        newIv.setViewport(croppedPortion);
        newIv.setFitWidth(imgv.getFitWidth());
        newIv.setFitHeight(imgv.getFitHeight());
        newIv.setSmooth(false);
        WritableImage croppedImage = newIv.snapshot(null, null);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(croppedImage, 0, 0, canvas.getWidth(), canvas.getHeight()); 
        rootSS.getChildren().remove(imgv);
    }
    public static void loadBg(ImageView img)
    {
        bgIv=img;
    }
    private void loadImage(File file)
    {
        if(bgIv!=null)
            rootSS.getChildren().remove(bgIv);
        bgSource=file.toURI().toString();
        Image image = new Image(bgSource);
        bgIv = new ImageView(image);
        bgIv.setFitHeight(image.getHeight());
        bgIv.setFitWidth(image.getWidth());
        raito=bgIv.getFitWidth()/bgIv.getFitHeight();
        bgIv.setLayoutX(-(image.getWidth()-subScene.getWidth())/2);
        bgIv.setLayoutY(-(image.getHeight()-subScene.getHeight())/2);
        bgIv.setOnMousePressed((MouseEvent event1) -> {
            startX = event1.getX();
            startY = event1.getY();
            distX = startX - bgIv.getLayoutX();
            distY = startY - bgIv.getLayoutY();
        });
        bgIv.setOnMouseDragged((MouseEvent event1) -> {
            bgIv.setLayoutX(event1.getX() - distX);
            bgIv.setLayoutY(event1.getY() - distY);
            distX = startX - bgIv.getLayoutX();
            distY = startY - bgIv.getLayoutY();
        });
        bgIv.setOnScroll((ScrollEvent event) -> {
            if(event.getDeltaY()<0)
            {
                if(bgIv.getFitHeight()>=100 && bgIv.getFitWidth()>=100)
                {
                    bgIv.setFitHeight(bgIv.getFitHeight()-RESIZE);
                    bgIv.setFitWidth(bgIv.getFitWidth()-RESIZE*raito);
                    bgIv.setLayoutX(bgIv.getLayoutX()+MOVE*raito);
                    bgIv.setLayoutY(bgIv.getLayoutY()+MOVE);
                }
            }
            else
            {
                bgIv.setFitHeight(bgIv.getFitHeight()+RESIZE);
                bgIv.setFitWidth(bgIv.getFitWidth()+RESIZE*raito);
                bgIv.setLayoutX(bgIv.getLayoutX()-MOVE*raito);
                bgIv.setLayoutY(bgIv.getLayoutY()-MOVE);
            }
        });
        addNode(bgIv);
    }
    public static void addNode(Node node)
    {
        rootSS.getChildren().add(node);
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
        if(actConnect==null)
        {
            Connect con=new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()));
            startConnects.add(con);
            con.setStart(true);
            con.select();
        }
        Connect con = new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()));
        MyCurve mc=new MyCurve(actConnect, con); 
        con.select();
        return mc;
    }
    public static void rozdel()
    {
        hideShowList.removeAll(checkPoints);
        removeCircles();
        setLastUsekId(0);
        cleanUseky();
        startUseky=new Rozdeleni(startConnects).getStartUseky();
    }
    public static void setConnect(Connect con)
    {
        actConnect=con;
    }
    public static void setStartUseky(List<Usek> start)
    {
        startUseky=start;
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
    public static Auto getActAuto()
    {
        return actAuto;
    }
    public static void setActAuto(Auto au)
    {
        actAuto=au;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

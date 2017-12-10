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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final List<Connect> startConnects=new ArrayList<Connect>();
    private static Usek actUsek;
    private static Auto actAuto;
    private static final List<Button> addCarBtns=new ArrayList<Button>();
    private static int pozYAdd=1;
    private static Animace a;
    private static Rozdeleni r;
    private static final List<Circle> checkPoints=new ArrayList<Circle>();
    private Scene scene;
    private SubScene subScene;
    private static Group rootSS;
    private static Canvas canvas;
    private ImageView bgIv;
    double startX;
    double startY;
    double distX;
    double distY;
    double raito=0;
    private final double RESIZE=50; 
    private final double MOVE=25; 
    private TimerTask timertask;
    private Timer timer;
    private boolean autoGen=false;
    private int genDeley=1000;
    private boolean delCanged=false;
    private static int lastUsekId=0, lastCurveId=0;
    private static List<Usek> useky=new ArrayList<Usek>();
    private static List<MyCurve> krivky=new ArrayList<MyCurve>();
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
        });
        primaryStage.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setWidth(newValue.intValue()-150);
            canvas.setWidth(subScene.getWidth());
        });
        primaryStage.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            subScene.setHeight(newValue.intValue()-100);
            canvas.setHeight(subScene.getHeight());
        });
        initGUI();
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
    private void autoAddCar()
    {
        if(!autoGen)
        {
            autoGen=true;
            genCar();
        }
        else
        {
            timer.cancel();
            timertask.cancel();
            autoGen=false;
        }
    }
    public static void setLastId(int lastUsekId)
    {
        lastUsekId=lastUsekId;
    }
    private void genCar()
    {
        timer = new Timer();
        timertask=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if(Math.random()<0.8){
                        Usek rndUsek=r.getStartUseky().get((int)(Math.random()*(r.getStartUseky().size())));
                        rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
                        if(rndUsek.getCar()==null){
                            Auto car=new Auto(rndUsek, a);
                        }
                    }
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
    private void initGUI()
    {
        
        BorderPane pane = new BorderPane();
        pane.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(5),
            new BorderWidths(2))));
        pane.setCenter(subScene);
        pane.setLayoutX(120);
        pane.setLayoutY(50);
        Button loadImage=new Button("Vložit pozadi");
        loadImage.setLayoutX(25);
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
        Button autoGen=new Button("Generování aut");
        autoGen.setLayoutX(250);
        autoGen.setLayoutY(20);
        autoGen.setOnAction((ActionEvent event) -> {
             autoAddCar();
        });
        canvas.setOnMousePressed((MouseEvent t) -> {
            newCurve(new Point((int)t.getX(),(int)t.getY()));
        });
        canvas.setOnMouseDragged((MouseEvent t) -> {
            actConnect.setPoint(new Point((int)t.getX(),(int)t.getY()));
            actConnect.moveConnect();
        });
        TextField delTF=new TextField("1000");
        delTF.setLayoutX(400);
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
        delMinus.setLayoutX(380);
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
        delPlus.setLayoutX(500);
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
        
        Button save=new Button("Save");
        save.setLayoutX(800);
        save.setLayoutY(20);
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().writer(krivky, useky);
            }
        });
        Button load=new Button("Load");
        load.setLayoutX(850);
        load.setLayoutY(20);
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new XMLStore().reader();
            }
        });
        root.getChildren().addAll(pane,setbg, loadImage, autoGen, delMinus, delPlus, delTF, save, load);
    }
    private void setBg(ImageView imgv)
    {
        Image newImg = imgv.snapshot(null, null);
        Rectangle2D croppedPortion = new Rectangle2D(-(int)bgIv.getLayoutX(), -(int)bgIv.getLayoutY(), (int)subScene.getWidth(), (int)subScene.getHeight());

        ImageView newIv = new ImageView(newImg);
        newIv.setViewport(croppedPortion);
        newIv.setFitWidth(imgv.getFitWidth());
        newIv.setFitHeight(imgv.getFitHeight());
        newIv.setSmooth(false);
        WritableImage croppedImage = newIv.snapshot(null, null);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(croppedImage, 0, 0, canvas.getWidth(), canvas.getHeight()); 
        rootSS.getChildren().remove(bgIv);
    }
    private void loadImage(File file)
    {
        if(bgIv!=null)
            rootSS.getChildren().remove(bgIv);
        Image image = new Image(file.toURI().toString());
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
    public static int getLastCurveId()
    {
        return lastCurveId;
    }
    public static void setLasCurveId(int lci)
    {
        lastCurveId=lci;
    }
    public static MyCurve newCurve(Point t)
    {
        if(actConnect==null)
        {
            Connect con=new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()));
            startConnects.add(con);
            con.select();
            final Button addCar=new Button("Přidat auto");
            addCar.setLayoutX(25);
            addCar.setLayoutY((pozYAdd+1)*26);
            addCarBtns.add(addCar);
            addCar.setOnAction((ActionEvent t1) -> {
                Usek rndUsek=r.getStartUseky().get(addCarBtns.indexOf(addCar));
                rndUsek=rndUsek.getDalsiUseky().get((int)(Math.random()*(rndUsek.getDalsiUseky().size())));
                if(rndUsek.getCar()==null){
                    final Auto car=new Auto(rndUsek, a);
                }
                
            });
            pozYAdd++;
            root.getChildren().add(addCar);
        }
        Connect con = new Connect(new Point((int)t.getX()+(int)canvas.getLayoutX(),(int)t.getY()+(int)canvas.getLayoutY()));
        MyCurve mc=new MyCurve(actConnect, con); 
        con.select();
        return mc;
    }
    public static void rozdel()
    {
        removeCircles();
        r=new Rozdeleni(startConnects);
    }
    public static void setConnect(Connect con)
    {
        actConnect=con;
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

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
import javafx.scene.control.CheckBox;
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
    private static Group root, drawRoot;
    private static Canvas canvas;
    private static DrawControll drawControll;
    private static CarControll carControll;
    private static PoliceControll policeControll;
    private static GuiControll gui;
    private static LightsControll lightsControll;
    private static StoreControll storeControll;
    private static boolean run=false;
    @Override
    public void start(Stage primaryStage) {
        gui=new GuiControll(primaryStage);
        root=gui.getRoot();
        drawRoot=gui.getDrawRoot();
        Scene scene=gui.getScene();
        
        canvas=gui.getCanvas();
        drawControll=new DrawControll(canvas, drawRoot, gui);
        policeControll=new PoliceControll(drawControll, gui);
        carControll=new CarControll(drawControll, gui);
        lightsControll=new LightsControll(drawControll, gui);
        storeControll=new StoreControll();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                cancel();
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                carControll.myCarHandlerDown(event);
                if(event.getCode()==KeyCode.DELETE)
                {
                    if(lightsControll.getSemPrim()!=null)
                        lightsControll.removePrim();
                    if(policeControll.getActualPolice()!=null)
                        policeControll.removePolice();
                    if(policeControll.getActualPolSide()!=null)
                        policeControll.removePolSide();
                    if(drawControll.getActualConnect()!=null)
                        drawControll.removeConnect();
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                carControll.myCarHandlerUp(event);
            }
        });
        initEvents();
        
        
    }

    public static DrawControll getDrawControll() {
        return drawControll;
    }

    public static CarControll getCarControll() {
        return carControll;
    }

    public static PoliceControll getPoliceControll() {
        return policeControll;
    }

    public static LightsControll getLightsControll() {
        return lightsControll;
    }
    
    private void initEvents()
    {
        Button loadTemp=gui.getLoadTemp();
        loadTemp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                storeControll.openFile();
            }
        });
        
        Button saveTemp=gui.getSaveTemp();
        saveTemp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                storeControll.saveFile(drawControll.getCurves(), drawControll.getConnects(), lightsControll.getLights(), policeControll.getPolices(), drawControll.getBgSource(), drawControll.getBG(), drawControll.getStartTram(), drawControll.getStartCar());
            }
        });
        Button newTemp=gui.getNewTemp();
        newTemp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawControll.clean();
            }
        });
        
        Button playBtn=gui.getPlay();
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!run)
                {
                    //playBtn.setText("Pause");
                    gui.changPlay(false);
                    play();
                }
                else
                {
                   // playBtn.setText("Play");
                    pause();
                    gui.changPlay(true);
                    
                }
            }
        });
        CheckBox hide=gui.getHide();
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawControll.showObjects(hide.isSelected());
            }
        });
        
    }
    private void play()
    {
        run=true;
        carControll.play();
        policeControll.play();
        lightsControll.play();
    }
    public static void pause()
    {
        run=false;
        carControll.stop();
        policeControll.stop();
        lightsControll.stop();
    }
    private void cancel()
    {
        pause();
        carControll.cancel();;
    }
    public static void drawNode(Node ... nodes)
    {
        drawRoot.getChildren().addAll(nodes);
    }
    public static void drawNode(int pos, Node node)
    {
        drawRoot.getChildren().add(pos+1,node);
    }
    public static void removeNode(Node ... nodes)
    {
        drawRoot.getChildren().removeAll(nodes);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import prometheus.Street.StreetSegment;
import prometheus.Vehicles.Animation;
import prometheus.Vehicles.BotCar;
import prometheus.Vehicles.BotTram;
import prometheus.Vehicles.MyCar;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 * @author Honza
 */
public class CarControll {
    private Animation animation;
    private DrawControll drawing;
    private TimerTask generTimerTaskCar, changeSpeedTimerTask, generTimerTaskTram;
    private Timer generTimerCar, changeSpeedTimer, generTimerTram;
    private boolean deleyChangedCar=false, carGeneratorRun=false, changeSpeedLoop=false, deleyChangedTram=false, tramGeneratorRun=false;
    private int generDeleyCar=1000, generDeleyTram=1000;
    private int carGenerCount=60, tramGenerCount=2;
    private double myCarSpeedChange;
    private final int MAX_COUNT=120;
    private TextField deleyCar, deleyTram;
    private static MyCar myCar;
    private GuiControll gui;
    private Button delTramMin;
    private Button delTramPlus;
    private static  Button upMyCarSpeed, downMyCarSpeed, delCarPlus, delCarMin;
    private static TextField tfMyCarSpeed;
    private static Button insertMyCar;
    public CarControll(DrawControll draw, GuiControll gui) {
        this.gui=gui;
        animation=new Animation();
        drawing=draw;
        initEvents();
        enableCarDel(false);
        enableTramDel(false);
        changeDeleyCar(carGenerCount);
        changeDeleyTram(tramGenerCount);
        setMyCarNull();
    }
    public Animation getAnimation()
    {
        return animation;
    }
    private void enableCarDel(boolean enab)
    {
        deleyCar.setDisable(!enab);
        delCarPlus.setDisable(!enab);
        delCarMin.setDisable(!enab);
    }
    private void enableTramDel(boolean enab)
    {
        deleyTram.setDisable(!enab);
        delTramPlus.setDisable(!enab);
        delTramMin.setDisable(!enab);
    }
    public void stop()
    {
        generStopCar();
        generStopTram();
    }
    private void initEvents()
    {
        CheckBox carGener=gui.getGenerCar();
        carGener.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!carGeneratorRun){
                    enableCarDel(true);
                    carGeneratorRun=true;
                }
                else
                {
                    enableCarDel(false);
                    carGeneratorRun=false;
                }
            }
        });
        deleyCar=gui.getTimeGenCar();
        deleyCar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    deleyCar.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    changeDeleyCar(Integer.valueOf(newValue.toString()));
                }
            }
        });
        deleyCar.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if(event.getDeltaY()>0)
                    changeDeleyCar(carGenerCount+1);
                else
                    changeDeleyCar(carGenerCount-1);
            }
        });
        delCarPlus=gui.getTimeGenCarPl();
        delCarPlus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                changeDeleyCar(carGenerCount+1);
            }
        });
        delCarMin=gui.getTimeGenCarMi();
        delCarMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeDeleyCar(carGenerCount-1);
            }
        });
        
        CheckBox tramGener=gui.getGenerTram();
        
        tramGener.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!tramGeneratorRun){
                    enableTramDel(true);
                    
                    tramGeneratorRun=true;
                }
                else
                {
                    enableTramDel(false);
                    
                    tramGeneratorRun=false;
                }
            }
        });
        deleyTram=gui.getTimeGenTram();
        deleyTram.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    deleyTram.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    changeDeleyTram(Integer.valueOf(newValue.toString()));
                }
            }
        });
        deleyTram.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if(event.getDeltaY()>0)
                    changeDeleyTram(tramGenerCount+1);
                else
                    changeDeleyTram(tramGenerCount-1);
            }
        });
        delTramPlus=gui.getTimeGenTramPl();
        delTramPlus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                changeDeleyTram(tramGenerCount+1);
            }
        });
        delTramMin=gui.getTimeGenTramMi();
        delTramMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeDeleyTram(tramGenerCount-1);
            }
        });
        insertMyCar=gui.getAddOwn();
        insertMyCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newMyCar();
            }
        });
        tfMyCarSpeed=gui.getSpeedOwn();
        tfMyCarSpeed.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER){
                    setMyCarSpeed(Integer.valueOf(tfMyCarSpeed.getText()));
                }
            }
        });
        upMyCarSpeed=gui.getSpeedOwnPl();
        upMyCarSpeed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                changeMyCarSpeed(0.002);
                tfMyCarSpeed.setText(String.valueOf(myCar.getSpeed()*1000));
            }
        });
        downMyCarSpeed=gui.getSpeedOwnMi();
        downMyCarSpeed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeMyCarSpeed(-0.002);
                tfMyCarSpeed.setText(String.valueOf(myCar.getSpeed()*1000));
            }
        });
        
    }
    public void play()
    {
        if(tramGeneratorRun)
            tramGenerator();
        if(carGeneratorRun)
            carGenerator();
    }
    public void myCarHandlerUp(KeyEvent event)
    {
        if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN)
        {
            changeSpeedLoop=false;
            cancelLoop();
        }
        
    }
    public void myCarHandlerDown(KeyEvent event)
    {
        if(!changeSpeedLoop){
            if(event.getCode()==KeyCode.UP || event.getCode()==KeyCode.DOWN){
                changeSpeedLoop=true;
                
                if(event.getCode()==KeyCode.UP)
                    myCarSpeedChange=0.002;
                if(event.getCode()==KeyCode.DOWN)
                    myCarSpeedChange=-0.002;
                changeSpeedLoop();
            }
        }
    }
    private void changeSpeedLoop()
    {
        changeSpeedTimer = new Timer();
        changeSpeedTimerTask=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    changeMyCarSpeed(myCarSpeedChange);
                    
                });
            }
        };
        changeSpeedTimer.schedule(changeSpeedTimerTask, 0, 50);   
    }
    public void cancelLoop()
    {
        if(changeSpeedTimer!=null)
            changeSpeedTimer.cancel();
        if(changeSpeedTimerTask!=null)
            changeSpeedTimerTask.cancel();
    }
    public static void setMyCarNull()
    {
        myCar=null;
        tfMyCarSpeed.setDisable(true);
        upMyCarSpeed.setDisable(true);
        downMyCarSpeed.setDisable(true);
        insertMyCar.setDisable(false);
    }
    private void newMyCar()
    {
        if(myCar==null){
            StreetSegment newSeg=drawing.getRandomStart(false);
            if(newSeg!=null){
                myCar=new MyCar(animation,newSeg);
                tfMyCarSpeed.setText(String.valueOf(myCar.getSpeed()*1000));
                insertMyCar.setDisable(true);
                tfMyCarSpeed.setDisable(false);
                upMyCarSpeed.setDisable(false);
                downMyCarSpeed.setDisable(false);
            }
        }
    }
    private void setMyCarSpeed(double newSpeed)
    {
        if(myCar!=null)
            myCar.setSpeed(newSpeed/1000);
    }
    private void changeMyCarSpeed(double newSpeed)
    {
        if(myCar!=null){
            myCar.updateSpeed(newSpeed);
            tfMyCarSpeed.setText(String.valueOf(myCar.getSpeed()*1000));
        }
    }
    private void changeDeleyCar(int count)
    {
        if(count>MAX_COUNT)
        {
            count=MAX_COUNT;
        }
        if(count<1)
            count=1;
        carGenerCount=count;
        deleyCar.setText(String.valueOf(count));
        generDeleyCar=60000/count;
        deleyChangedCar=true;
    }
    private void carGenerator()
    {
        generTimerCar = new Timer();
        generTimerTaskCar=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    
                    generCar();
                    
                    if(deleyChangedCar)
                    {
                        deleyChangedCar=false;
                        generTimerCar.cancel();
                        carGenerator(); 
                    }
                });
            }
        };
        generTimerCar.schedule(generTimerTaskCar, 0, generDeleyCar);    
    }
    private void generCar()
    {
        StreetSegment newSeg=drawing.getRandomStart(false);
        if(newSeg!=null)
            new BotCar(animation,newSeg);
    }
    
    private void changeDeleyTram(int count)
    {
        if(count>MAX_COUNT)
        {
            count=MAX_COUNT;
        }
        if(count<1)
            count=1;
        tramGenerCount=count;
        deleyTram.setText(String.valueOf(count));
        generDeleyTram=60000/count;
        deleyChangedTram=true;
    }
    private void tramGenerator()
    {
        generTimerTram = new Timer();
        generTimerTaskTram=new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    
                    generTram();
                    if(deleyChangedTram)
                    {
                        deleyChangedTram=false;
                        generTimerTram.cancel();
                        tramGenerator(); 
                    }
                });
            }
        };
        generTimerTram.schedule(generTimerTaskTram, 0, generDeleyTram);    
    }
    private void generTram()
    {
        StreetSegment newSeg=drawing.getRandomStart(true);
        if(newSeg!=null)
            new BotTram(animation,newSeg);
    }
    private void generStopCar()
    {
        if(generTimerCar!=null)
            generTimerCar.cancel();
        if(generTimerTaskCar!=null)
            generTimerTaskCar.cancel();
    }
    private void generStopTram()
    {
        if(generTimerTram!=null)
            generTimerTram.cancel();
        if(generTimerTaskTram!=null)
            generTimerTaskTram.cancel();
    }
    public void cancel()
    {
        stop();
        cancelLoop();
        animation.stop();
    }
    
}

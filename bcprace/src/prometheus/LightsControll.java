/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import prometheus.TrafficLights.LightsConnect;
import prometheus.TrafficLights.TrafficLight;

/**
 *
 * @author Honza
 */
public class LightsControll {
    private static List<TrafficLight> trafficLightList=new ArrayList<>();
    private boolean run=false;
    private CheckBox connectSems;
    private TrafficLight ligthPrim=null, lightSec=null;
    private Button timeRedPl, timeRedMi, timeOrangePl, timeOrangeMi, timeGreenPl, timeGreenMi;
    private TextField tfRed, tfGreen, tfOrange;
    private static int lastLightId=0, lastIDPrechod=0;
    private boolean runSem=false, connected=false;
    private DrawControll dc;
  
    private GuiControll gui;
    private RadioButton rbPrimRed, rbPrimOrange, rbPrimGreen, rbSecRed, rbSecOrange, rbSecGreen, rbRed, rbOrange, rbGreen;
    private CheckBox enableRed;
    private CheckBox enableGreen;
    public LightsControll(DrawControll dc, GuiControll gui) {
        this.gui=gui;
        this.dc=dc;
        initEvents();
    }
    public int getLastIDPrechod() {
        return lastIDPrechod;
    }
    public void addtLastIDPrechod() {
        this.lastIDPrechod++;
    }
    public void setActualLight(TrafficLight tl)
    {
        ligthPrim=tl;
    }
    public void stop()
    {
        for (TrafficLight trafficLight : trafficLightList) {
            trafficLight.stop();
        }
    }
    public List<TrafficLight> getLights()
    {
        return trafficLightList;
    }
    public void play()
    {
        for (TrafficLight trafficLight : trafficLightList) {
            trafficLight.play();
        }
    }
    private LightsControll getLC()
    {
        return this;
    }
    public static void setLastLightId(int id)
    {
        lastLightId=id;
    }
    public static int getLastLightId()
    {
        return lastLightId;
    }
    public static void setLasConnectId(int id)
    {
        lastIDPrechod=id;
    }
    public static int getLastConnectId()
    {
        return lastIDPrechod;
    }
    public void newLights()
    {
        TrafficLight s=new TrafficLight(getLC());
        trafficLightList.add(s);
        //addSem(s);
    }
    public static void addLight(TrafficLight tl)
    {
        trafficLightList.add(tl);
    }
    public void remove(TrafficLight tl)
    {
        trafficLightList.remove(tl);
    }
    public static void clean()
    {
        List<TrafficLight> toRem=new ArrayList<>();
        for (TrafficLight trafficLight : trafficLightList) {
            
            toRem.add(trafficLight);
        }
        for (TrafficLight trafficLight : toRem) {
            trafficLight.remove();
        }
        trafficLightList.clear();
    }
    private void initEvents()
    {
        gui.getAddSem().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newLights();
            }
        });
        rbRed=gui.getSemColRed();
        rbRed.setDisable(true);
        rbRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ligthPrim.setStatus(0, true);
            }
        });
        rbOrange=gui.getSemColOrange();
        rbOrange.setDisable(true);
        rbOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ligthPrim.setStatus(1, true);
            }
        });
        rbGreen=gui.getSemColGreen();
        rbGreen.setDisable(true);
        rbGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ligthPrim.setStatus(3, true);
            }
        });
        tfRed=gui.getTimeRed();
        tfRed.setDisable(true);
        tfRed.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    tfRed.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    ligthPrim.setTimeRed(Integer.parseInt(newValue.toString()));
                }
            }
        });
        tfOrange=gui.getTimeOrange();
        tfOrange.setDisable(true);
        tfOrange.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    tfOrange.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    ligthPrim.setTimeOrange(Integer.parseInt(newValue.toString()));
                }
            }
        });
        tfGreen=gui.getTimeGreen();
        tfGreen.setDisable(true);
        tfGreen.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    tfGreen.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    ligthPrim.setTimeGreen(Integer.parseInt(newValue.toString()));
                }
            }
        });
        
        timeRedPl=gui.getTimeRedPl();
        timeRedPl.setDisable(true);
        timeRedPl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeRed()+1;
                ligthPrim.setTimeRed(actTime);
                tfRed.setText(String.valueOf(actTime));
            }
        });
        timeRedMi=gui.getTimeRedmi();
        timeRedMi.setDisable(true);
        timeRedMi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeRed()-1;
                ligthPrim.setTimeRed(actTime);
                tfRed.setText(String.valueOf(actTime));
            }
        });
        timeOrangePl=gui.getTimeOrangePl();
        timeOrangePl.setDisable(true);
        timeOrangePl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeOrange()+1;
                ligthPrim.setTimeOrange(actTime);
                tfOrange.setText(String.valueOf(actTime));
            }
        });
        timeOrangeMi=gui.getTimeOrangemi();
        timeOrangeMi.setDisable(true);
        timeOrangeMi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeOrange()-1;
                ligthPrim.setTimeOrange(actTime);
                tfOrange.setText(String.valueOf(actTime));
            }
        });
        timeGreenPl=gui.getTimeGreenPl();
        timeGreenPl.setDisable(true);
        timeGreenPl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeGreen()+1;
                ligthPrim.setTimeGreen(actTime);
                tfGreen.setText(String.valueOf(actTime));
            }
        });
        timeGreenMi=gui.getTimeGreenmi();
        timeGreenMi.setDisable(true);
        timeGreenMi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=ligthPrim.getTimeGreen()-1;
                ligthPrim.setTimeGreen(actTime);
                tfGreen.setText(String.valueOf(actTime));
            }
        });
        connectSems=gui.getConnectSems();
        connectSems.setDisable(true);
        connectSems.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!connected)
                    connectLights();
                else
                    disconnectLights();
            }
        });
        rbPrimRed=gui.getSemPrimRed();
        rbPrimRed.setDisable(true);
        rbPrimRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        rbPrimOrange=gui.getSemPrimOrange();
        rbPrimOrange.setDisable(true);
        rbPrimOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        rbPrimGreen=gui.getSemPrimGreen();
        rbPrimGreen.setDisable(true);
        rbPrimGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        
        rbSecRed=gui.getSemSecRed();
        rbSecRed.setDisable(true);
        rbSecRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        
        rbSecOrange=gui.getSemSecOrange();
        rbSecOrange.setDisable(true);
        rbSecOrange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        rbSecGreen=gui.getSemSecGreen();
        rbSecGreen.setDisable(true);
        rbSecGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkConnection();
            }
        });
        enableRed=gui.getEnableRed();
        enableRed.setDisable(true);
        enableRed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ligthPrim.enableChangeRed(enableRed.isSelected());
            }
        });
        enableGreen=gui.getEnableGreen();
        enableGreen.setDisable(true);
        enableGreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ligthPrim.enableChangeGreen(enableGreen.isSelected());
            }
        });
    }
    public void enableChangeColor(boolean enab)
    {
        rbRed.setDisable(!enab);
        rbOrange.setDisable(!enab);
        rbGreen.setDisable(!enab);
        timeRedMi.setDisable(!enab);
        timeRedPl.setDisable(!enab);
        timeOrangeMi.setDisable(!enab);
        timeOrangePl.setDisable(!enab);
        timeGreenMi.setDisable(!enab);
        timeGreenPl.setDisable(!enab);
        tfRed.setDisable(!enab);
        tfOrange.setDisable(!enab);
        tfGreen.setDisable(!enab);
        enableRed.setDisable(!enab);
        enableGreen.setDisable(!enab);
    }
    private void enableConnection(boolean enab)
    {
        
        rbSecRed.setDisable(!enab);
        rbSecGreen.setDisable(!enab);
        if(enab){
            
            rbSecRed.setSelected(false);
            rbSecGreen.setSelected(false);
            rbPrimRed.setSelected(false);
            rbPrimGreen.setSelected(false);
        }
        else{
            connectSems.setDisable(true);
            connectSems.setSelected(false);
        }
        rbPrimRed.setDisable(!enab);
        rbPrimGreen.setDisable(!enab);
    }
    private void connectLights()
    { 
        connected=true;
        if(rbPrimRed.isSelected()){
            if(rbSecRed.isSelected())
                ligthPrim.addControlRed(lightSec, 1);
            else if(rbSecGreen.isSelected())
                ligthPrim.addControlRed(lightSec, 2);
        }
        if(rbPrimGreen.isSelected()){
            if(rbSecRed.isSelected())
                ligthPrim.addControlGreen(lightSec, 1);
            else if(rbSecGreen.isSelected())
                ligthPrim.addControlGreen(lightSec, 2);
        }
    }
    
    private void disconnectLights()
    {
        connected=false;
    }
    public TrafficLight getSemSec()
    {
        return lightSec;
    }
    public TrafficLight getSemPrim()
    {
        return ligthPrim;
    }
    public void selectSemPrim(TrafficLight prim)
    {
        ligthPrim=prim;
        if(prim==null){
            enableConnection(false);
            enableChangeColor(false);
        }
        else{
            if(lightSec!=null){
                
                enableConnection(true);
            }
            //enableRedChange.setVisible(true);
            //enableGreenChange.setVisible(true);
            enableGreen.setSelected(prim.getEnableGreen());
            enableRed.setSelected(prim.getEnableRed());
            enableChangeColor(true);
            tfRed.setText(String.valueOf(prim.getTimeRed()));
            //enableGreenChange.setSelected(prim.getEnableGreen());
            //enableRedChange.setSelected(prim.getEnableRed());
            tfOrange.setText(String.valueOf(prim.getTimeOrange()));
            tfGreen.setText(String.valueOf(prim.getTimeGreen()));
            setStatus(prim.getStatus());
        }
    }
    private void setStatus(int status)
    {
        switch(status)
        {
            case 0:{
                rbRed.setSelected(true);
                break;
            }
            case 1:{
                rbOrange.setSelected(true);
                break;
            }
            case 3:{
                rbGreen.setSelected(true);
                break;
            }
        }
    }
    private void checkConnection()
    {
        boolean select=false;
        if(rbPrimGreen.isSelected()){
            for (LightsConnect lightsConnect : ligthPrim.getControlGreen()) {
                if(lightsConnect.getSem().equals(lightSec)){
                    if(lightsConnect.getStatus()==1 && rbSecRed.isSelected() || lightsConnect.getStatus()==2 && rbSecGreen.isSelected())
                        select=true;
                }
            }
        }
        if(rbPrimRed.isSelected()){
            for (LightsConnect lightsConnect : ligthPrim.getControlRed()) {
                if(lightsConnect.getSem().equals(lightSec)){
                    if(lightsConnect.getStatus()==1 && rbSecRed.isSelected() || lightsConnect.getStatus()==2 && rbSecGreen.isSelected())
                        select=true;
                }
            }
        }
        if((rbPrimRed.isSelected() || rbPrimOrange.isSelected() || rbPrimGreen.isSelected()) && (rbSecRed.isSelected() || rbSecOrange.isSelected() || rbSecGreen.isSelected()) )
            connectSems.setDisable(false);
        else
            connectSems.setDisable(true);
        connectSems.setSelected(select);
        
    }
    /*
    private void showConnections()
    {
        for (LightsConnect semPrRed : ligthPrim.getControlRed()) {
            if(semPrRed.getSem().equals(lightSec)){
                rbPrimRed.setSelected(true);
                if(semPrRed.getStatus()==1){
                    rbSecRed.setSelected(true);
                }else if(semPrRed.getStatus()==2){
                    rbSecGreen.setSelected(true);
                }
            }
        }
        for (LightsConnect semPrGreen : ligthPrim.getControlGreen()) {
            if(semPrGreen.getSem().equals(lightSec)){
                rbPrimGreen.setSelected(true);
                if(semPrGreen.getStatus()==1){
                    rbSecRed.setSelected(true);
                }else if(semPrGreen.getStatus()==2){
                    rbSecGreen.setSelected(true);
                }
            }
        }
        
    }*/
    public void removePrim()
    {
        ligthPrim.remove();
        selectSemPrim(null);
    }
    
    public void selectSemSec(TrafficLight sec)
    {
        lightSec=sec;
        if(sec==null){
            enableConnection(false);
        }
        else{
            if(ligthPrim==null){
                ligthPrim=sec;
                lightSec=null;
            }
            if(ligthPrim!=null && lightSec!=null){
                enableConnection(true);
            }
        }
    }
    
}

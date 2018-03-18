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

/**
 *
 * @author Honza
 */
public class SemaforControl {
    private Timer timer=new Timer();
    private TimerTask timertask;
    private List<Semafor> semafory=new ArrayList<>();
    private boolean run=false;
    private Group semRoot;
    private RadioButton rbRedPrim=new RadioButton();
    private RadioButton rbGreenPrim=new RadioButton();
    private RadioButton rbRedSec=new RadioButton();
    private RadioButton rbGreenSec=new RadioButton();
    private CheckBox enableRedChange=new CheckBox("Red");
    private CheckBox enableGreenChange=new CheckBox("Green");
    private Semafor semPrim=null, semSec=null;
    private Button vazba, play;
    private TextField tfRed, tfGreen;
    private ComboBox cbColor;
    private int lastID=0, lastIDPrechod=0;
    private boolean runSem=false;
    public SemaforControl() {
        gui();
    }
    private void play()
    {
        if(!runSem){
            runSem=true;
            play.setText("Sem off");
            timertask = new TimerTask() {
                @Override
                public void run(){
                    Platform.runLater(() -> {
                        tick();
                    });
                }
            };
            timer.schedule(timertask, 1000, 1000);
        }
        else
        {
            timertask.cancel();
            runSem=false;
            play.setText("Sem on");      
        }
            
        
    }

    public int getLastIDPrechod() {
        return lastIDPrechod;
    }

    public void addtLastIDPrechod() {
        this.lastIDPrechod++;
    }
    
    public Group getRoot()
    {
        return semRoot;
    }
    public void end()
    {
        if(timertask!=null)
            timertask.cancel();
        if(timer!=null)
            timer.cancel();
    }
    public void removeAll()
    {
        semafory.clear();
    }
    public List<Semafor> getSemafory()
    {
        return semafory;
    }
    private void tick()
    {
        for (Semafor semafor : semafory) {
            semafor.tick();
        }
    }
    private SemaforControl getSC()
    {
        return this;
    }
    public Semafor newSem()
    {
        Semafor s=new Semafor(lastID, getSC());
        lastID++;
        addSem(s);
        return s;
    }
    public void addSem(Semafor s)
    {
        Prometheus.addNode(s.getIMG());
        semafory.add(s);
    }
    private void gui()
    {
        cbColor=new ComboBox();
        cbColor.setLayoutX(10);
        cbColor.setLayoutY(80);
        cbColor.setMaxWidth(80);
        cbColor.setVisible(false);
        cbColor.getItems().addAll("Red", "Orange2Red", "Orange2Green", "Green");
        cbColor.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                semPrim.setStatus(setStatus(newValue.toString()), true);
                
            }
        });
        semRoot=new Group();
        semRoot.setLayoutY(50);
        Button pridat=new Button("Semafor++");
        pridat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newSem();
            }
        });
        pridat.setLayoutX(10);
        pridat.setLayoutY(50);   
        play=new Button("Sem On");
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                play();
            }
        });
        play.setLayoutX(10);
        play.setLayoutY(20); 
        
        vazba=new Button("Propojit");
        vazba.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setControlSem();
            }
        });
        vazba.setLayoutX(10);
        vazba.setLayoutY(210); 
        vazba.setVisible(false);
        enableRedChange.setVisible(false);
        enableGreenChange.setVisible(false);
        enableRedChange.setLayoutX(10);
        enableRedChange.setLayoutY(110);
        enableRedChange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(enableRedChange.isSelected())
                    semPrim.enableChangeRed(true);
                else
                    semPrim.enableChangeRed(false);
            }
        });
        tfRed=new TextField();
        tfRed.setMaxWidth(80);
        tfRed.setLayoutX(10);
        tfRed.setLayoutY(130);
        tfRed.setVisible(false);
        tfRed.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER)
                    semPrim.setTimeRed(Integer.parseInt(tfRed.getText()));
            }
        });
        enableGreenChange.setLayoutX(10);
        enableGreenChange.setLayoutY(160);
        enableGreenChange.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(enableGreenChange.isSelected())
                    semPrim.enableChangeGreen(true);
                else
                    semPrim.enableChangeGreen(false);
            }
        });
        tfGreen=new TextField();
        tfGreen.setMaxWidth(80);
        tfGreen.setLayoutX(10);
        tfGreen.setLayoutY(180);
        tfGreen.setVisible(false);
        tfGreen.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER)
                    semPrim.setTimeGreen(Integer.parseInt(tfGreen.getText()));
            }
        });
        ToggleGroup tgPrim=new ToggleGroup();
        rbRedPrim.setToggleGroup(tgPrim);
        rbRedPrim.setVisible(false);
        rbGreenPrim.setToggleGroup(tgPrim);
        rbGreenPrim.setVisible(false);
        rbRedPrim.setLayoutX(10);
        rbRedPrim.setLayoutY(240);
        rbGreenPrim.setLayoutX(10);
        rbGreenPrim.setLayoutY(260);
        ToggleGroup tgSec=new ToggleGroup();
        rbRedSec.setToggleGroup(tgSec);
        rbRedSec.setVisible(false);
        rbGreenSec.setToggleGroup(tgSec);
        rbGreenSec.setVisible(false);
        rbRedSec.setLayoutX(30);
        rbRedSec.setLayoutY(240);
        rbGreenSec.setLayoutX(30);
        rbGreenSec.setLayoutY(260);
        semRoot.getChildren().addAll(pridat, rbRedPrim, rbGreenPrim, rbRedSec, rbGreenSec, play, vazba, enableRedChange, enableGreenChange, tfGreen, tfRed, cbColor);
    }
    private void setControlSem()
    {
        
        if(rbRedPrim.isSelected()){
            if(rbRedSec.isSelected())
                semPrim.addControlRed(semSec, 1);
            else if(rbGreenSec.isSelected())
                semPrim.addControlRed(semSec, 2);
        }
        if(rbGreenPrim.isSelected()){
            if(rbRedSec.isSelected())
                semPrim.addControlGreen(semSec, 1);
            else if(rbGreenSec.isSelected())
                semPrim.addControlGreen(semSec, 2);
        }
    }
    public Semafor getSemSec()
    {
        return semSec;
    }
    public Semafor getSemPrim()
    {
        return semPrim;
    }
    public void selectSemPrim(Semafor prim)
    {
        semPrim=prim;
        if(prim==null){
            hideControls();
            enableRedChange.setVisible(false);
            enableGreenChange.setVisible(false);
            tfRed.setVisible(false);
            tfGreen.setVisible(false);
            cbColor.setVisible(false);
        }
        else{
            if(semSec!=null){
                showControls();
            }
            enableRedChange.setVisible(true);
            enableGreenChange.setVisible(true);
            tfRed.setVisible(true);
            tfGreen.setVisible(true);
            cbColor.setVisible(true);
            tfRed.setText(""+prim.getTimeRed());
            enableGreenChange.setSelected(prim.getEnableGreen());
            enableRedChange.setSelected(prim.getEnableRed());
            tfGreen.setText(""+prim.getTimeGreen());
            cbColor.setValue(cbColor.getItems().get(prim.getStatus()));
        }
    }
    private void setVazby()
    {
        for (SemPrechod semPrRed : semPrim.getControlRed()) {
            if(semPrRed.getSem().equals(semSec)){
                rbRedPrim.setSelected(true);
                if(semPrRed.getStatus()==1){
                    rbRedSec.setSelected(true);
                }
                if(semPrRed.getStatus()==2){
                    rbGreenSec.setSelected(true);
                }
            }
        }
        for (SemPrechod semPrGreen : semPrim.getControlGreen()) {
            if(semPrGreen.getSem().equals(semSec)){
                rbGreenPrim.setSelected(true);
                if(semPrGreen.getStatus()==1){
                    rbRedSec.setSelected(true);
                }
                if(semPrGreen.getStatus()==2){
                    rbGreenSec.setSelected(true);
                }
            }
        }
        
    }
    /*public String getStatus(Semafor sem)
    {
        switch(sem.getStatus())
        {
            case 0:
            {
                return "Red";
            }
            case 1:
            {
                return "Orange2Red";
            }
            case 2:
            {
                return "Orange2Green";
            }
            case 3:
            {
                return "Green";
            }
            default:
            {
                System.out.println("status error");
                return "Red";
            }
        }
    }*/
    public int setStatus(String st)
    {
        switch(st)
        {
            case "Red":
            {
                return 0;
            }
            case "Orange2Red":
            {
                return 1;
            }
            case "Orange2Green":
            {
                return 2;
            }
            case "Green":
            {
                return 3;
            }
            default:
            {
                System.out.println("status error");
                return 0;
            }
        }
    }
    public void selectSemSec(Semafor sec)
    {
        semSec=sec;
        if(sec==null){
            hideControls();
        }
        else{
            if(semPrim!=null){
                showControls();
            }
        }
    }
    private void hideControls()
    {
        vazba.setVisible(false);
        rbRedSec.setVisible(false);
        rbRedPrim.setVisible(false);
        rbGreenPrim.setVisible(false);
        rbGreenSec.setVisible(false);
    }
    private void showControls()
    {
        setVazby();
        vazba.setVisible(true);
        rbRedPrim.setVisible(true);
        rbGreenPrim.setVisible(true);
        rbRedSec.setVisible(true);
        rbGreenSec.setVisible(true);
    }
}

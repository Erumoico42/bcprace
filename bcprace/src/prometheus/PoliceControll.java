/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import prometheus.Police.Police;
import prometheus.Police.PoliceSide;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Honza
 */
public class PoliceControll {
    private static DrawControll drawing;
    private static PoliceSide actualPolSide;
    private static Police actualPolice;
    private static List<Police> polices=new ArrayList<>();
    private boolean run=false, sideAdded=true;
    private static int lastPoliceId=0, lastPoliceSideId=0, lastPoliceCombinId=0;
    private static Button addSide, sideDeleyPl, sideDeleyMi, deleyPl, deleyMi;
    private static TextField tfDeley, tfSideDeley;
    private GuiControll gui;
    private static CheckBox connectSides;
    public PoliceControll(DrawControll drawing, GuiControll gui) {
        this.gui=gui;
        this.drawing = drawing;
        drawing.setPoliceControll(this);
        initEvents();
    }
    private void initEvents()
    {
        /*policeOn=new Button("Police on");
        policeOn.setLayoutX(10);
        policeOn.setLayoutY(80);
        policeOn.setMinWidth(70);
        policeOn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(run)
                    stop();
                else
                    play();
            }
        });*/
        
        Button addPolice=gui.getAddPolice();
        addPolice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actualPolice=new Police();
                polices.add(actualPolice);
            }
        });
        
        tfDeley=gui.getTimeWait();
        tfDeley.setDisable(true);
        tfDeley.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*") || newValue.length()>4) {
                    tfDeley.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    actualPolice.setDeley(Integer.parseInt(tfDeley.getText()));
                }
            }
        });
        
        addSide=gui.getAddSide();
        addSide.setDisable(true);
        addSide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sideAdded=false;
            }
        });
        connectSides=gui.getConnectSides();
        connectSides.setDisable(true);
        connectSides.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(actualPolice.checkExist())
                    actualPolice.removeKomb();
                else
                    actualPolice.createKomb();
            }
        });
        tfSideDeley=gui.getTimeSide();
        tfSideDeley.setDisable(true);
        tfSideDeley.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")  || newValue.length()>4) {
                    tfSideDeley.setText(oldValue.toString());
                }
                else if(newValue.length()>0)
                {
                    if(actualPolice!=null && actualPolice.getSelectedKombi()!=null)
                        actualPolice.getSelectedKombi().setTime(Integer.parseInt(newValue.toString()));
                }
            }
        });
        deleyPl=gui.getTimeWaiPl();
        deleyPl.setDisable(true);
        deleyPl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actDeley=actualPolice.getDeley()+1;
                actualPolice.setDeley(actDeley);
                changeDeley(actDeley);
            }
        });
        deleyMi=gui.getTimeWaiMi();
        deleyMi.setDisable(true);
        deleyMi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actDeley=actualPolice.getDeley()-1;
                actualPolice.setDeley(actDeley);
                changeDeley(actDeley);
            }
        });
        sideDeleyPl=gui.getTimeSidPl();
        sideDeleyPl.setDisable(true);
        sideDeleyPl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=actualPolice.getSelectedKombi().getTime()+1;
                actualPolice.getSelectedKombi().setTime(actTime);
                changeSideDeley(actTime);
            }
        });
        sideDeleyMi=gui.getTimeSidMi();
        sideDeleyMi.setDisable(true);
        sideDeleyMi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int actTime=actualPolice.getSelectedKombi().getTime()-1;
                actualPolice.getSelectedKombi().setTime(actTime);
                changeSideDeley(actTime);
            }
        });
    }
    public List<Police> getPolices()
    {
        return polices;
    }
    public static void clean()
    {
        for (Police police : polices) {
            police.remove();
        }
        polices.clear();
    }
    public void removePolice()
    {
        actualPolice.remove();
        setActualPolice(null);
    }
    public void removePolSide()
    {
        actualPolSide.remove();
        setActualPolSide(null);
    }
    public void setAddedSide()
    {
        sideAdded=true;
    }
    public boolean sideAdded()
    {
        return sideAdded;
    }
    public static DrawControll getDrawControll()
    {
        return drawing;
    }
    public void play()
    {
        run=true;
        //policeOn.setText("Police off");
        for (Police pol : polices) {
            pol.play();
        }
    }
    public void stop()
    {
        run=false;
        //policeOn.setText("Police on");
        for (Police pol : polices) {
            pol.pause();
        }
    }
    public static void changeSideDeley(int time)
    {
        tfSideDeley.setText(String.valueOf(time));
    }
    public static void changeDeley(int time)
    {
        tfDeley.setText(String.valueOf(time));
    }
    public static void changeCombi(boolean add)
    {
        if(add)
            connectSides.setSelected(false);
        else
            connectSides.setSelected(true);
    }
    public static void showSideDeley(boolean show)
    {
        tfSideDeley.setDisable(!show);
        sideDeleyMi.setDisable(!show);
        sideDeleyPl.setDisable(!show);
    }
    public static void enableSelectedPolice(boolean show)
    {
        addSide.setDisable(!show);
        tfDeley.setDisable(!show);
        deleyMi.setDisable(!show);
        deleyPl.setDisable(!show);
    }
    public static void enableConnectSides(boolean show)
    {
        connectSides.setDisable(!show);
        
    }
    public static Police getActualPolice() {
        return actualPolice;
    }

    public static void setActualPolice(Police actualPolice) {
        PoliceControll.actualPolice = actualPolice;
    }

    
    public static int getLastPolSideId() {
        return lastPoliceSideId;
    }

    public static void setLastPolSideId(int lastPsId) {
        lastPoliceSideId = lastPsId;
    }
    
    public static PoliceSide getActualPolSide() {
        return actualPolSide;
    }

    public static void setActualPolSide(PoliceSide polSide) {
        actualPolSide = polSide;
    }

    public static int getLastPoliceId() {
        return lastPoliceId;
    }

    public static void setLastPoliceId(int lastPoliceId) {
        PoliceControll.lastPoliceId = lastPoliceId;
    }

    public static int getLastPoliceCombinId() {
        return lastPoliceCombinId;
    }

    public static void setLastPoliceCombinId(int lastPoliceCombinId) {
        PoliceControll.lastPoliceCombinId = lastPoliceCombinId;
    }
    
}

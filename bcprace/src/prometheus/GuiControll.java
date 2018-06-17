/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.RenderingHints;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import prometheus.gui.Menu;
import prometheus.gui.MenuFlap;
import prometheus.gui.MenuGroup;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Honza
 */
public class GuiControll {
    private Group root;
    private Stage primaryStage;
    private Menu menu;
    private final Font TEXT_STYLE=new Font(11);
    private final int layout1=5, layout2=28;
    private static MenuFlap selectedFap;
    private Button loadBG, timeGenCarPl, timeGenCarMi, timeGenTramPl, timeGenTramMi, speedOwnPl, speedOwnMi, addOwn, addPolice, addSide, timeSidPl, timeSidMi, timeWaiPl,timeWaiMi,
            addSem, timeRedPl, timeRedmi, timeOrangePl, timeOrangemi, timeGreenPl, timeGreenmi;
    private TextField timeGenCar, timeGenTram, speedOwn, timeSide, timeWait, timeRed, timeOrange, timeGreen;
    private CheckBox generCar, generTram, lockBG, connectSides, connectSems, enableRed, enableGreen, hide, lockWays;
    private RadioButton createCar, createTram, semColOrange, semColRed, semColGreen, semPrimRed, semPrimOrange, semPrimGreen, semSecRed, semSecOrange,semSecGreen;
    private Group drawRoot;
    private SubScene drawScene;
    private Button play;
    private Scene scene;
    private Canvas canvas;
    private static MenuFlap fapSim, fapEdit;
    private Button newTemp, loadTemp, saveTemp;
    private Image imgPlay=new Image("/resources/icons/play.png");
    private Image imgPause=new Image("/resources/icons/pause.png");
    private ImageView ivPlay=new ImageView(imgPlay);
    private static boolean edit=false, saver=false;
    private MenuGroup mgs1, mgs2, mgs3, mgs4, mge1, mge2, mge3, mge4;
    private boolean enableMouseMoveExit=false;
    private BorderPane groupBorder;
    
    public GuiControll(Stage primaryStage, String[] args) {
        
        
        this.primaryStage=primaryStage;
        root = new Group(); 
        scene = new Scene(root, 850, 600);
        primaryStage.setTitle("Bakalarska prace");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        initMenu();
        initDrawingPlace();
        stageControll();
        modSwitch(args);    
    }
    public static boolean editable()
    {
        return edit;
    }
    public boolean isSaver()
    {
        return saver;
    }
    private void modSwitch(String[] args)
    {
        for (String arg : args) {
            boolean found=false;
            if(arg.split("=").length==2){
                found=true;
                String param=arg.split("=")[1];
                switch(param)
                {
                    case "-e":
                    {
                        edit=true;
                        break;
                    }
                    case "-s":
                    {
                        saver=true;
                        runScreenSaver();
                        break;
                    }
                }
            }  
            if(found)
                break;
        }
        if(!edit)
            editDisableMenu();
        
    }
    public void tryLoad()
    {
        if(saver)
        {
            StoreControll.loader(new File("D:\\temp04.xml"));
            Prometheus.play();
            DrawControll.showObjects(false);
        }
    }
    private void runScreenSaver()
    {
        
        root.getChildren().removeAll(menu.getMenu(), saveTemp,loadTemp, newTemp);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        groupBorder.setLayoutY(52);
        drawScene.setWidth(drawScene.getWidth()+16);
        drawScene.setHeight(drawScene.getHeight()+36);
        scene.setFill(Color.BLACK);
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(GuiControll.class.getName()).log(Level.SEVERE, null, ex);
        }
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                if(enableMouseMoveExit)
                    Prometheus.cancel();
                enableMouseMoveExit=true;
            }
        });
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Prometheus.cancel();
            }
        });
        
    }
    private void stageControll()
    {
        primaryStage.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            menu.changeWidth(newValue.intValue());
            drawScene.setWidth(drawScene.getWidth()+(newValue.doubleValue()-oldValue.doubleValue()));
            canvas.setWidth(canvas.getWidth()+(newValue.doubleValue()-oldValue.doubleValue()));
            //drawScene.setWidth(newValue.doubleValue()-22);
        });
        primaryStage.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            drawScene.setHeight(drawScene.getHeight()+(newValue.doubleValue()-oldValue.doubleValue()));
            canvas.setHeight(canvas.getHeight()+(newValue.doubleValue()-oldValue.doubleValue()));
            //drawScene.setHeight(newValue.doubleValue()-149);
        });
    }
    private void initMenu()
    {
        menu=new Menu();
        initEditMenu();
        initSimulMenu();
        
        newTemp=new Button();
        ImageView ivNew=new ImageView(new Image("/resources/icons/new.png"));
        ivNew.setFitWidth(14);
        ivNew.setFitHeight(17);
        newTemp.setGraphic(ivNew);
        newTemp.setMinSize(30, 22);
        newTemp.setMaxSize(30, 22);
        newTemp.setLayoutX(5);
        newTemp.setLayoutY(5);
        
        loadTemp=new Button();
        ImageView ivLoad=new ImageView(new Image("/resources/icons/open.png"));
        ivLoad.setFitWidth(20);
        ivLoad.setFitHeight(21);
        loadTemp.setGraphic(ivLoad);
        loadTemp.setMinSize(30, 22);
        loadTemp.setMaxSize(30, 22);
        loadTemp.setLayoutX(37);
        loadTemp.setLayoutY(5);
        
        saveTemp=new Button();
        ImageView ivSave=new ImageView(new Image("/resources/icons/save.png"));
        ivSave.setFitWidth(18);
        ivSave.setFitHeight(18);
        saveTemp.setGraphic(ivSave);
        saveTemp.setMinSize(30, 22);
        saveTemp.setMaxSize(30, 22);
        saveTemp.setLayoutX(69);
        saveTemp.setLayoutY(5);
        
        root.getChildren().addAll(menu.getMenu(), saveTemp,loadTemp, newTemp);
        menu.changeWidth(primaryStage.getWidth());
    }
    private void editDisableMenu()
    {
        generCar.setDisable(true);
        generTram.setDisable(true);
        canvas.setDisable(true);
        drawRoot.setDisable(true);
        addOwn.setDisable(true);

        root.getChildren().removeAll(saveTemp,newTemp);
        loadTemp.setLayoutX(5);
        menu.getFlapGroup().getChildren().removeAll(fapEdit.getFlap(), fapSim.getFlap());
        fapSim.getContent().getChildren().removeAll(mgs1.getMenuGroup(), mgs2.getMenuGroup());
        mgs3.setLayout(0);
        mgs3.getGroup().getChildren().remove(addOwn);
        mgs4.getGroup().getChildren().remove(hide);
        mgs4.setLayout(90);
        menu.changeContent(fapSim);
        speedOwnPl.setLayoutY(layout1);
        speedOwnMi.setLayoutY(layout1);
        speedOwn.setLayoutY(layout1);
        play.setDisable(true);
        
    }
    private void initSimulMenu()
    {
        fapSim=new MenuFlap("Simulace");
        fapSim.getFlap().setLayoutX(165);
        
        mgs1=new MenuGroup("Automobily");
        mgs1.setWidth(90);
        mgs1.setLblLayout(15);
        
        timeGenCarPl=new Button("+");
        timeGenCarPl.setFont(Font.font(9));
        timeGenCarPl.setLayoutX(5);
        timeGenCarPl.setLayoutY(layout2);
        timeGenCarPl.setMinSize(20, 20);
        timeGenCarPl.setMaxSize(20, 20);
        timeGenCarMi=new Button("-");
        timeGenCarMi.setFont(Font.font(9));
        timeGenCarMi.setLayoutX(65);
        timeGenCarMi.setLayoutY(layout2);
        timeGenCarMi.setMinSize(20, 20);
        timeGenCarMi.setMaxSize(20, 20);
        timeGenCar=new TextField();
        timeGenCar.setFont(TEXT_STYLE);
        timeGenCar.setLayoutX(25);
        timeGenCar.setLayoutY(layout2);
        timeGenCar.setMinSize(40, 20);
        timeGenCar.setMaxSize(40, 20);
        generCar=new CheckBox("Zapnout");
        generCar.setLayoutX(5);
        generCar.setLayoutY(layout1);
        mgs1.addItems(timeGenCarPl, timeGenCarMi, timeGenCar, generCar);
        
        mgs2=new MenuGroup("Tramvaje");
        mgs2.setWidth(90);
        mgs2.setLblLayout(20);
        mgs2.setLayout(90);
        
        timeGenTramPl=new Button("+");
        timeGenTramPl.setFont(Font.font(9));
        timeGenTramPl.setLayoutX(5);
        timeGenTramPl.setLayoutY(layout2);
        timeGenTramPl.setMinSize(20, 20);
        timeGenTramPl.setMaxSize(20, 20);
        timeGenTramMi=new Button("-");
        timeGenTramMi.setFont(Font.font(9));
        timeGenTramMi.setLayoutX(65);
        timeGenTramMi.setLayoutY(layout2);
        timeGenTramMi.setMinSize(20, 20);
        timeGenTramMi.setMaxSize(20, 20);
        timeGenTram=new TextField();
        timeGenTram.setFont(TEXT_STYLE);
        timeGenTram.setLayoutX(25);
        timeGenTram.setLayoutY(layout2);
        timeGenTram.setMinSize(40, 20);
        timeGenTram.setMaxSize(40, 20);
        generTram=new CheckBox("Zapnout");
        generTram.setLayoutX(5);
        generTram.setLayoutY(layout1);
        mgs2.addItems(timeGenTramPl, timeGenTramMi, timeGenTram, generTram);
        
        mgs3=new MenuGroup("Vlastní");
        mgs3.setWidth(90);
        mgs3.setLblLayout(25);
        mgs3.setLayout(180);
        
        speedOwnPl=new Button("+");
        speedOwnPl.setFont(Font.font(9));
        speedOwnPl.setLayoutX(5);
        speedOwnPl.setLayoutY(layout2);
        speedOwnPl.setMinSize(20, 20);
        speedOwnPl.setMaxSize(20, 20);
        speedOwnMi=new Button("-");
        speedOwnMi.setFont(Font.font(9));
        speedOwnMi.setLayoutX(65);
        speedOwnMi.setLayoutY(layout2);
        speedOwnMi.setMinSize(20, 20);
        speedOwnMi.setMaxSize(20, 20);
        speedOwn=new TextField();
        speedOwn.setFont(TEXT_STYLE);
        speedOwn.setLayoutX(25);
        speedOwn.setLayoutY(layout2);
        speedOwn.setMinSize(40, 20);
        speedOwn.setMaxSize(40, 20);
        addOwn=new Button("Vložit");
        addOwn.setFont(TEXT_STYLE);
        addOwn.setMinSize(80, 20);
        addOwn.setMaxSize(80, 20);
        addOwn.setLayoutX(5);
        addOwn.setLayoutY(layout1);
        mgs3.addItems(speedOwnPl, speedOwnMi, speedOwn, addOwn);
        
        mgs4=new MenuGroup("Spustit");
        mgs4.setWidth(110);
        mgs4.setLblLayout(40);
        mgs4.setLayout(270);
        ivPlay.setFitWidth(30);
        ivPlay.setFitHeight(25);
        play=new Button();
        play.setGraphic(ivPlay);
        play.setMinSize(100, 30);
        play.setMaxSize(100, 30);
        play.setLayoutX(layout1);
        play.setLayoutY(layout1);
                
        hide=new CheckBox("Zobrazit silnice");
        hide.setSelected(true);
        hide.setLayoutX(layout1);
        hide.setLayoutY(35);
        mgs4.addItems(play, hide);
        
        
        fapSim.addGroups(mgs1, mgs2, mgs3, mgs4);
        menu.addFlaps(fapSim);
    }
    private void initEditMenu()
    {
        fapEdit=new MenuFlap("Editace");
        fapEdit.getFlap().setLayoutX(105);
        
        //Background
        mge1=new MenuGroup("Pozadí");
        mge1.setWidth(120);
        mge1.setLblLayout(40);
        
        loadBG=new Button("Vložit pozadí");
        loadBG.setMinSize(110, 22);
        loadBG.setMaxSize(110, 22);
        loadBG.setLayoutX(5);
        loadBG.setLayoutY(5);
        loadBG.setFont(TEXT_STYLE);
        lockBG=new CheckBox("Zamnkout pozadí");
        lockBG.setLayoutX(5);
        lockBG.setLayoutY(30);
        lockBG.setFont(TEXT_STYLE);
        mge1.addItems(loadBG, lockBG);
        
        //Street type
        mge2=new MenuGroup("Druh silnice");
        mge2.setWidth(100);
        mge2.setLblLayout(15);
        mge2.setLayout(122);
        ToggleGroup createVehOprion = new ToggleGroup();
        createCar=new RadioButton("Automobil");
        createCar.setFont(TEXT_STYLE);
        createCar.setSelected(true);
        createCar.setToggleGroup(createVehOprion);
        createCar.setLayoutX(5);
        createCar.setLayoutY(layout1);
        createTram=new RadioButton("Tramvaj");
        createTram.setFont(TEXT_STYLE);
        createTram.setLayoutX(5);
        createTram.setLayoutY(20);
        createTram.setToggleGroup(createVehOprion);
         lockWays=new CheckBox("Zamknout");
        lockWays.setLayoutX(5);
        lockWays.setLayoutY(37);
        mge2.addItems(createCar, createTram, lockWays);
        
        
        //Police controll
        mge3=new MenuGroup("Řízení policisty");
        mge3.setLayout(224);
        mge3.setWidth(285);
        mge3.setLblLayout(95);
        addPolice=new Button();
        ImageView ivPol=new ImageView(new Image("/resources/police/head.png"));
        ivPol.setFitWidth(30);
        ivPol.setFitHeight(30);
        addPolice.setGraphic(ivPol);
        addPolice.setLayoutX(5);
        addPolice.setLayoutY(layout1);
        addPolice.setMinSize(35, 45);
        addPolice.setMaxSize(35, 45);
        
        addSide=new Button("Směrový bod");
        addSide.setLayoutX(45);
        addSide.setLayoutY(layout1);
        addSide.setMaxSize(90, 22);
        addSide.setMinSize(90, 22);
        addSide.setFont(TEXT_STYLE);
        connectSides=new CheckBox("Propojit");
        //Button connectSides=new Button("Propojit směry");
        connectSides.setLayoutX(45);
        connectSides.setLayoutY(layout2);
        connectSides.setMaxSize(90, 22);
        connectSides.setMinSize(90, 22);
        connectSides.setFont(TEXT_STYLE);
        
        Label timSideLbl=new Label("Doba jízdy:");
        timSideLbl.setLayoutX(140);
        timSideLbl.setLayoutY(layout1);
        
        timeSidPl=new Button("+");
        timeSidPl.setFont(Font.font(9));
        timeSidPl.setLayoutX(200);
        timeSidPl.setLayoutY(layout1);
        timeSidPl.setMinSize(20, 20);
        timeSidPl.setMaxSize(20, 20);
        
        timeSidMi=new Button("-");
        timeSidMi.setFont(Font.font(9));
        timeSidMi.setLayoutX(260);
        timeSidMi.setLayoutY(layout1);
        timeSidMi.setMinSize(20, 20);
        timeSidMi.setMaxSize(20, 20);
        
        timeSide=new TextField();
        timeSide.setFont(TEXT_STYLE);
        timeSide.setLayoutX(220);
        timeSide.setLayoutY(layout1);
        timeSide.setMinSize(40, 20);
        timeSide.setMaxSize(40, 20);
        
        
        timeWaiPl=new Button("+");
        timeWaiPl.setFont(Font.font(9));
        timeWaiPl.setLayoutX(200);
        timeWaiPl.setLayoutY(layout2);
        timeWaiPl.setMinSize(20, 20);
        timeWaiPl.setMaxSize(20, 20);
        
        timeWaiMi=new Button("-");
        timeWaiMi.setFont(Font.font(9));
        timeWaiMi.setLayoutX(260);
        timeWaiMi.setLayoutY(layout2);
        timeWaiMi.setMinSize(20, 20);
        timeWaiMi.setMaxSize(20, 20);
        
        Label timWaitLbl=new Label("Prodleva:");
        timWaitLbl.setLayoutX(140);
        timWaitLbl.setLayoutY(28);
        timeWait=new TextField();
        timeWait.setFont(TEXT_STYLE);
        timeWait.setLayoutX(220);
        timeWait.setLayoutY(layout2);
        timeWait.setMinSize(40, 20);
        timeWait.setMaxSize(40, 20);
        
        mge3.addItems(addPolice, addSide, connectSides, timSideLbl, timeSide, timWaitLbl, timeWait, timeSidMi, timeSidPl, timeWaiPl, timeWaiMi);
        
        
        mge4=new MenuGroup("Řízení semaforů");
        mge4.setWidth(265);
        mge4.setLayout(511);
        mge4.setLblLayout(90);
        
        addSem=new Button();
        ImageView ivSem=new ImageView(new Image("/resources/trafficLights/all.png"));
        ivSem.setFitWidth(15);
        ivSem.setFitHeight(35);
        addSem.setGraphic(ivSem);
        addSem.setLayoutX(5);
        addSem.setLayoutY(layout1);
        addSem.setMinSize(35, 45);
        addSem.setMaxSize(35, 45);

        ToggleGroup tgSemColor=new ToggleGroup();
        semColRed=new RadioButton();
        semColRed.setToggleGroup(tgSemColor);
        semColRed.setLayoutX(45);
        semColRed.setLayoutY(3); 
        semColOrange=new RadioButton();
        semColOrange.setToggleGroup(tgSemColor);
        semColOrange.setLayoutX(45);
        semColOrange.setLayoutY(19);
        semColGreen=new RadioButton();   
        semColGreen.setToggleGroup(tgSemColor);
        semColGreen.setLayoutX(45);
        semColGreen.setLayoutY(35);
        
        
        timeRedPl=new Button("+");
        timeRedPl.setFont(Font.font(8));
        timeRedPl.setLayoutX(65);
        timeRedPl.setLayoutY(3);
        timeRedPl.setMinSize(18, 18);
        timeRedPl.setMaxSize(18, 18);  
        timeRedmi=new Button("-");
        timeRedmi.setFont(Font.font(8));
        timeRedmi.setLayoutX(123);
        timeRedmi.setLayoutY(3);
        timeRedmi.setMinSize(18, 18);
        timeRedmi.setMaxSize(18, 18);
        timeRed=new TextField();
        timeRed.setFont(TEXT_STYLE);
        timeRed.setLayoutX(83);
        timeRed.setLayoutY(3);
        timeRed.setMinSize(40, 18);
        timeRed.setMaxSize(40, 18);
        
        timeOrangePl=new Button("+");
        timeOrangePl.setFont(Font.font(8));
        timeOrangePl.setLayoutX(65);
        timeOrangePl.setLayoutY(19);
        timeOrangePl.setMinSize(18, 18);
        timeOrangePl.setMaxSize(18, 18);
        timeOrangemi=new Button("-");
        timeOrangemi.setFont(Font.font(8));
        timeOrangemi.setLayoutX(123);
        timeOrangemi.setLayoutY(19);
        timeOrangemi.setMinSize(18, 18);
        timeOrangemi.setMaxSize(18, 18);
        timeOrange=new TextField();
        timeOrange.setFont(TEXT_STYLE);
        timeOrange.setLayoutX(83);
        timeOrange.setLayoutY(19);
        timeOrange.setMinSize(40, 18);
        timeOrange.setMaxSize(40, 18);
        
        timeGreenPl=new Button("+");
        timeGreenPl.setFont(Font.font(8));
        timeGreenPl.setLayoutX(65);
        timeGreenPl.setLayoutY(35);
        timeGreenPl.setMinSize(18, 18);
        timeGreenPl.setMaxSize(18, 18);
        timeGreenmi=new Button("-");
        timeGreenmi.setFont(Font.font(8));
        timeGreenmi.setLayoutX(123);
        timeGreenmi.setLayoutY(35);
        timeGreenmi.setMinSize(18, 18);
        timeGreenmi.setMaxSize(18, 18);
        timeGreen=new TextField();
        timeGreen.setFont(TEXT_STYLE);
        timeGreen.setLayoutX(83);
        timeGreen.setLayoutY(35);
        timeGreen.setMinSize(40, 18);
        timeGreen.setMaxSize(40, 18);
        
        enableRed=new CheckBox();
        enableRed.setLayoutX(150);
        enableRed.setLayoutY(3); 
        enableGreen=new CheckBox();
        enableGreen.setLayoutX(150);
        enableGreen.setLayoutY(35); 

        ToggleGroup tgSemPrim=new ToggleGroup();
        semPrimRed=new RadioButton();
        semPrimRed.setToggleGroup(tgSemPrim);
        semPrimRed.setLayoutX(180);
        semPrimRed.setLayoutY(3); 
        semPrimOrange=new RadioButton();
        semPrimOrange.setToggleGroup(tgSemPrim);
        semPrimOrange.setLayoutX(180);
        semPrimOrange.setLayoutY(19);
        semPrimGreen=new RadioButton();   
        semPrimGreen.setToggleGroup(tgSemPrim);
        semPrimGreen.setLayoutX(180);
        semPrimGreen.setLayoutY(35);
        
        ToggleGroup tgSemSec=new ToggleGroup();
        semSecRed=new RadioButton();
        semSecRed.setToggleGroup(tgSemSec);
        semSecRed.setLayoutX(245);
        semSecRed.setLayoutY(3); 
        semSecOrange=new RadioButton();
        semSecOrange.setToggleGroup(tgSemSec);
        semSecOrange.setLayoutX(245);
        semSecOrange.setLayoutY(19);
        semSecGreen=new RadioButton();   
        semSecGreen.setToggleGroup(tgSemSec);
        semSecGreen.setLayoutX(245);
        semSecGreen.setLayoutY(35);
        
        Label lblConSems=new Label("Propojit");
        lblConSems.setLayoutX(200);
        lblConSems.setLayoutY(7);
        connectSems=new CheckBox();
        connectSems.setLayoutX(212);
        connectSems.setLayoutY(25);
        
        
        mge4.addItems(addSem, semColGreen, semColOrange, semColRed, timeRedPl, timeRedmi, timeRed, timeOrangePl, timeOrangemi, timeOrange, timeGreenPl, timeGreenmi, timeGreen,
                semPrimRed, semPrimOrange, semPrimGreen, lblConSems, connectSems, semSecRed, semSecOrange, semSecGreen, enableGreen, enableRed);
        
        fapEdit.addGroups(mge1, mge2, mge3, mge4);
        
        menu.addFlaps(fapEdit);
        
        menu.changeContent(fapEdit);
    }
    private void initDrawingPlace()
    {
        canvas=new Canvas(844, 491);
        drawRoot=new Group();
        //drawRoot.getChildren().add(canvas);
        drawScene=new SubScene(drawRoot, 844, 491);
        groupBorder = new BorderPane();
        groupBorder.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(2),
            new BorderWidths(1))));
        groupBorder.setCenter(drawScene);
        groupBorder.setLayoutY(105);
        groupBorder.setLayoutX(2);
        root.getChildren().add(groupBorder);
    }
    public void changPlay(boolean chan)
    {
        if(chan)
            ivPlay.setImage(imgPlay);
        else
            ivPlay.setImage(imgPause);
    }
    public SubScene getDrawScene()
    {
        return drawScene;
    }
    public Canvas getCanvas()
    {
        return canvas;
    }
    public Group getDrawRoot()
    {
        return drawRoot;
    }
    public static void setActualMenuFlap(MenuFlap mf)
    {
        selectedFap=mf;
    }
    public static MenuFlap getActualMenuFlap()
    {
        return selectedFap;
    }
    public Button getPlay()
    {
        return play;
    }
    public Group getRoot() {
        return root;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CheckBox getEnableRed() {
        return enableRed;
    }

    public CheckBox getHide() {
        return hide;
    }
    
    public CheckBox getEnableGreen() {
        return enableGreen;
    }
    
    public Menu getMenu() {
        return menu;
    }

    public int getLayout1() {
        return layout1;
    }

    public int getLayout2() {
        return layout2;
    }

    public static MenuFlap getSelectedFap() {
        return selectedFap;
    }

    public Button getTimeGenCarPl() {
        return timeGenCarPl;
    }

    public Button getTimeGenCarMi() {
        return timeGenCarMi;
    }

    public Button getNewTemp() {
        return newTemp;
    }

    public Button getLoadTemp() {
        return loadTemp;
    }

    public Button getSaveTemp() {
        return saveTemp;
    }
    
    public Button getTimeGenTramPl() {
        return timeGenTramPl;
    }

    public Button getTimeGenTramMi() {
        return timeGenTramMi;
    }

    public Button getSpeedOwnPl() {
        return speedOwnPl;
    }

    public Button getSpeedOwnMi() {
        return speedOwnMi;
    }

    public Button getAddOwn() {
        return addOwn;
    }

    public Button getAddPolice() {
        return addPolice;
    }

    public Button getAddSide() {
        return addSide;
    }

    public Button getTimeSidPl() {
        return timeSidPl;
    }

    public Button getTimeSidMi() {
        return timeSidMi;
    }

    public Button getTimeWaiPl() {
        return timeWaiPl;
    }

    public Button getTimeWaiMi() {
        return timeWaiMi;
    }

    public Button getAddSem() {
        return addSem;
    }

    public Button getTimeRedPl() {
        return timeRedPl;
    }

    public Button getTimeRedmi() {
        return timeRedmi;
    }

    public Button getTimeOrangePl() {
        return timeOrangePl;
    }

    public Button getTimeOrangemi() {
        return timeOrangemi;
    }

    public Button getTimeGreenPl() {
        return timeGreenPl;
    }

    public Button getTimeGreenmi() {
        return timeGreenmi;
    }

    public TextField getTimeGenCar() {
        return timeGenCar;
    }

    public TextField getTimeGenTram() {
        return timeGenTram;
    }

    public TextField getSpeedOwn() {
        return speedOwn;
    }

    public TextField getTimeSide() {
        return timeSide;
    }

    public TextField getTimeWait() {
        return timeWait;
    }

    public TextField getTimeRed() {
        return timeRed;
    }

    public TextField getTimeOrange() {
        return timeOrange;
    }

    public TextField getTimeGreen() {
        return timeGreen;
    }

    public CheckBox getGenerCar() {
        return generCar;
    }

    public CheckBox getGenerTram() {
        return generTram;
    }

    public CheckBox getLockBG() {
        return lockBG;
    }

    public CheckBox getConnectSides() {
        return connectSides;
    }

    public CheckBox getConnectSems() {
        return connectSems;
    }

    public RadioButton getCreateCar() {
        return createCar;
    }

    public RadioButton getCreateTram() {
        return createTram;
    }

    public RadioButton getSemColOrange() {
        return semColOrange;
    }

    public RadioButton getSemColRed() {
        return semColRed;
    }

    public RadioButton getSemColGreen() {
        return semColGreen;
    }

    public RadioButton getSemPrimRed() {
        return semPrimRed;
    }

    public RadioButton getSemPrimOrange() {
        return semPrimOrange;
    }

    public RadioButton getSemPrimGreen() {
        return semPrimGreen;
    }

    public RadioButton getSemSecRed() {
        return semSecRed;
    }

    public RadioButton getSemSecOrange() {
        return semSecOrange;
    }

    public RadioButton getSemSecGreen() {
        return semSecGreen;
    }

    public Button getLoadBG() {
        return loadBG;
    }

    public Scene getScene() {
        return scene;
    }

    public CheckBox getLockWays() {
        return lockWays;
    }
    
    
    
}

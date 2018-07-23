/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import prometheus.Police.Police;
import prometheus.Police.PoliceSide;
import prometheus.Street.Connect;
import prometheus.Street.MyCurve;
import prometheus.Street.Split;
import prometheus.Street.StreetSegment;

/**
 *
 * @author Honza
 */
public class DrawControll {
    private static ImageView background;
    private HBox backgroundBox;
    private static Canvas canvas;
    private final int RESIZE_VALUE=50, MOVE_VALUE=25; 
    private double resizeRatio=0;
    private static boolean locked=true, moveAll=false;
    private static Connect endConnect;
    private static MyCurve actualCurve;
    private static Connect actualConnect;
    private static StreetSegment actualStreetSegment;
    private static PoliceControll policeControll;
    private static int lastIdConnect=0, lastIdCurve=0, lastIdSegment=0;
    private static List<Connect> connects=new ArrayList<>();
    private static List<MyCurve> curves=new ArrayList<>();
    private static List<StreetSegment> segments=new ArrayList<>();
    private static List<StreetSegment> startSegmentsCar=new ArrayList<>();
    private static List<StreetSegment> startSegmentsTram=new ArrayList<>();
    private static List<Node> nodesToHide=new ArrayList<>();
    private double startX, startY, distX, distY, layoutX, layoutY;
    private double drstartX, drstartY, drdistX, drdistY, drlayoutX, drlayoutY;
    private static CheckBox lockBg;
    private static Group drawRoot;
    private GuiControll gui;
    private static boolean tram=false, enableSplit=true;
    private String bgSource;
    
    public DrawControll(Canvas canvas, Group drawRoot, GuiControll gui) {
        this.canvas=canvas;
        this.drawRoot=drawRoot;
        this.gui=gui;
                
        
        background=new ImageView();
        backgroundBox=new HBox(background);
        drawRoot.getChildren().add(backgroundBox);
        drawRoot.getChildren().add(canvas);
        initControll();
        initEvents();
    }
    public static void setMoveAll(boolean move)
    {
        moveAll=move;
        if(move)
            lockBackground(true);
    }
    public static void clean()
    {
        lastIdConnect=0;
        lastIdCurve=0;
        lastIdSegment=0;
        
        endConnect=null;
        actualCurve=null;
        actualConnect=null;
        actualStreetSegment=null;
        
        PoliceControll.clean();
        LightsControll.clean();
        drawRoot.getChildren().removeAll(nodesToHide);
        segments=new ArrayList<>();
        curves=new ArrayList<>();
        connects=new ArrayList<>();
        startSegmentsCar=new ArrayList<>();
        startSegmentsTram=new ArrayList<>();
        nodesToHide=new ArrayList<>();
        background.setImage(null);
        CarControll.getAnimation().removeAll();
    }
    public static void addCurve(MyCurve mc)
    {
        curves.add(mc);
    }
    public static void addToHide(Node node)
    {
        nodesToHide.add(node);
    }
    public static void showObjects(boolean show)
    {
        for (Node node : nodesToHide) {
            node.setVisible(show);
        }
    }
    public static void setLastSegmentId(int id)
    {
        lastIdSegment=id;
    }
    public static int getLastSegmentId()
    {
        return lastIdSegment;
    }
    public static void removeCurve(MyCurve curve)
    {
        curves.remove(curve);
    }
    public void removeConnect()
    {
        actualConnect.remove();
        connects.remove(actualConnect);
        setActualConnect(null);
    }
    public static void setEnableSplit(boolean enab)
    {
        enableSplit=enab;
    }
    private void initEvents()
    {
        Button loadBg=gui.getLoadBG();
        loadBg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadImage();
            }
        });
        lockBg=gui.getLockBG();
        lockBg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lockBackground();
                
            }
        });
        RadioButton rbCar=gui.getCreateCar();
        rbCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tram=false;
            }
        });
        RadioButton rbTram=gui.getCreateTram();
        rbTram.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tram=true;
            }
        });
        CheckBox cbLock=gui.getLockWays();
        cbLock.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.getCanvas().setDisable(cbLock.isSelected());
                gui.getCreateCar().setDisable(cbLock.isSelected());
                gui.getCreateTram().setDisable(cbLock.isSelected());
                for (Connect connect : connects) {
                    connect.getCircle().setDisable(cbLock.isSelected());
                }
            }
        });
    }
    public static void lockBackground(boolean lock)
    {
        locked=lock;
        if(canvas!=null)
            canvas.setVisible(lock);
        if(lockBg!=null)
            lockBg.setSelected(lock);
    }
    public static void lockBackground()
    {
        if(!locked)
        {
            lockBackground(true);
        }else
        {
            lockBackground(false);
        }
    }
    public void setPoliceControll(PoliceControll pc)
    {
        policeControll=pc;
    }
    public StreetSegment getRandomStart(boolean tram)
    {
        List<StreetSegment> sstoGen=new ArrayList<>();
        if(!tram)
            sstoGen.addAll(startSegmentsCar);
        else
            sstoGen.addAll(startSegmentsTram);

        while(!sstoGen.isEmpty())
        {
            boolean next=false;
            StreetSegment ret=sstoGen.get((int)(Math.random()*sstoGen.size()));
            sstoGen.remove(ret);
            if(ret!=null)
            {
                for (StreetSegment ss : ret.getDalsiUseky()) {
                    if(ss.getVehicle()!=null)
                        next=true;   
                }
                if(ret.getVehicle()!=null)
                    next=true;
            }
            else
                next=true;
            if(!next){
                return ret;
            }
        }
        return null;
    }
    public List<StreetSegment> getStartCar()
    {
        return startSegmentsCar;
    }
    public List<StreetSegment> getStartTram()
    {
        return startSegmentsTram;
    }
    public void newSplit()
    {
        //cleanSegments();
        if(enableSplit)
            new Split(connects);
    }
    public static void removeConnect(Connect con)
    {
        connects.remove(con);
    }
    public static void split()
    {
        if(enableSplit)
            new Split(connects);
    }
    public void addSegment(StreetSegment ss)
    {
        segments.add(ss);     
    }
    public static void addStartSegmentCar(StreetSegment ss)
    {
        if(!startSegmentsCar.contains(ss)){
            startSegmentsCar.add(ss);     
        }
    }
    public static void addStartSegmentTram(StreetSegment ss)
    {
        if(!startSegmentsTram.contains(ss)){
            startSegmentsTram.add(ss);     
        }
    }
    public static void cleanStartTram()
    {
        startSegmentsTram.clear();
    }
    public static void cleanStartCar()
    {
        startSegmentsCar.clear();
    }
    public static void addConnect(Connect con)
    {
        connects.add(con);
    }
    public static void setActualConnect(Connect actConn)
    {
        actualConnect=actConn;
    }

    public static StreetSegment getActualStreetSegment() {
        return actualStreetSegment;
    }

    public static void setActualStreetSegment(StreetSegment actualSeg) {
        actualStreetSegment = actualSeg;
    }
    
    public int getLastIdConnect() {
        return lastIdConnect;
    }

    public void setLastIdConnect(int lastIdConnect) {
        this.lastIdConnect = lastIdConnect;
    }

    public int getLastIdCurve() {
        return lastIdCurve;
    }
    
    public void setLastIdCurve(int lastIdCurve) {
        this.lastIdCurve = lastIdCurve;
    }

    public int getLastIdSegment() {
        return lastIdSegment;
    }

    public void setLastIdSegment(int lastIdSegment) {
        this.lastIdSegment = lastIdSegment;
    }
    
    public static Connect getActualConnect()
    {
        return actualConnect;
    }
    private void initControll()
    {
        backgroundBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!locked && !moveAll)
                    bgClick(event.getX(),event.getY());
            }
        });
         backgroundBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!locked && !moveAll)
                    bgDrag(event.getX(),event.getY());
            }
        });
        
        backgroundBox.setOnScroll((ScrollEvent event) -> {
            if(!locked)
                bgScroll(event.getDeltaY());
        });
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(locked && !moveAll){
                    if(event.getButton()==MouseButton.PRIMARY)
                    {
                        if(actualConnect==null || (actualConnect!=null && (actualConnect.isTram() ^ tram))){
                            actualConnect=newConnect(event.getX(), event.getY());
                            actualConnect.select();
                            actualCurve=null;
                        }
                        else
                            drawCurve(event.getX(), event.getY());
                    }
                    if(event.getButton()==MouseButton.SECONDARY)
                    {
                        if(!policeControll.sideAdded()){
                            policeControll.setAddedSide();
                            newPoliceSide(event.getX(), event.getY());
                        }
                    }
                    
                }
                if(moveAll)
                    drClick(event.getX(),event.getY());
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(locked && !moveAll)
                {
                    if(event.getButton()==MouseButton.PRIMARY){
                        if(actualConnect!=null){
                            actualConnect.move(event.getX(), event.getY());
                            if(actualCurve==null && !(actualConnect.isTram() ^ tram))
                                drawCurve(event.getX(), event.getY());
                        }
                    }
                }
                if(moveAll)
                {
                    drDrag(event.getX(),event.getY());
                    
                }
            }
        });
       
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
    private void drClick(double x, double y)
    {
        drstartX = x;
        drstartY = y;
        drdistX = drstartX - gui.getXYDrawScene().getX();
        drdistY = drstartY - gui.getXYDrawScene().getY();
    }
    private void drDrag(double x, double y)
    {
        drlayoutX = x - drdistX;
        drlayoutY = y - drdistY;
        gui.setXYDrawScene(drlayoutX, drlayoutY);
        drdistX = drstartX - drlayoutX;
        drdistY = drstartY - drlayoutY;
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
    public void moveDefBg(double layoutX, double layoutY, double width, double height)
    {
        resizeRatio=width/height;
        background.setFitHeight(height);
        background.setFitWidth(width);
        backgroundBox.setLayoutX(layoutX);
        backgroundBox.setLayoutY(layoutY);
    }
    public void setBgSource(String source)
    {
        bgSource=source;
    }
    public void loadBackground(Image image)
    {
        background.setImage(image);
    }
    private void loadImage()
    {
        SubScene subScene=gui.getDrawScene();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) { 
            bgSource=file.toURI().toString();
            Image image = new Image(bgSource);
            
            
            background.setImage(image);
            background.setFitHeight(image.getHeight());
            background.setFitWidth(image.getWidth());
            resizeRatio=image.getWidth()/image.getHeight();
            backgroundBox.setLayoutX(-(image.getWidth()-subScene.getWidth())/2);
            backgroundBox.setLayoutY(-(image.getHeight()-subScene.getHeight())/2);
            canvas.setVisible(false);
            locked=false;
            lockBg.setSelected(false);
            
        }
    }
    public String getBgSource()
    {
        return bgSource;
    }
    public HBox getBG()
    {
        return backgroundBox;
    }
    private void newPoliceSide(double x, double y)
    {
        Police actPolice=PoliceControll.getActualPolice();
        if(actPolice!=null)
            new PoliceSide(new Point((int)x, (int)y), actPolice);
    }
    private void drawCurve(double x, double y)
    {
        
        endConnect=newConnect(x,y);
        newCurve();
    }
    public void deselectAll()
    {
        if(actualConnect!=null)
            actualConnect.deselect();
        if(actualCurve!=null)
            actualCurve.deselectCurve();
        if(actualStreetSegment!=null)
            actualStreetSegment.deselect();
        for (Police p : policeControll.getPolices()) {
            p.deselect();
        }
        LightsControll lc=Prometheus.getLightsControll();
        if(lc.getSemPrim()!=null)
            lc.getSemPrim().setStyle(0);
        if(lc.getSemSec()!=null)
            lc.getSemSec().setStyle(0);
        lc.setActualLight(null);
        lc.selectSemPrim(null);
        lc.selectSemSec(null);
    }
    private void newCurve()
    {
        actualCurve=new MyCurve(actualConnect, endConnect, this);
        actualCurve.setId(lastIdCurve);
        lastIdCurve++;
        actualConnect.addStartCurve(actualCurve);
        endConnect.addEndCurve(actualCurve);
        endConnect.select();
        curves.add(actualCurve);
        newSplit();        
    }
    public void drawCurve(Connect endConnect)
    {
        this.endConnect=endConnect;
        newCurve();
        actualCurve.getC2().adaptControll();
        actualCurve.setChangedControlls();
    }
    private Connect newConnect(double x, double y)
    {
        if(actualConnect!=null)
            actualConnect.setDefSkin();
        Connect newConn=new Connect(new Point((int)x, (int)y), tram);
        newConn.setId(lastIdConnect);
        lastIdConnect++;
        connects.add(newConn);
        return newConn;
    }
    public static boolean drawTram()
    {
        return tram;
    }
    public List<Connect> getConnects()
    {
        return connects;
    }
    public List<MyCurve> getCurves()
    {
        return curves;
    }
    public List<StreetSegment> getSegments()
    {
        return segments;
    }
}

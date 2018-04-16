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
import javafx.scene.layout.HBox;
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
    private ImageView background;
    private HBox backgroundBox;
    private static Canvas canvas;
    private final int RESIZE_VALUE=50, MOVE_VALUE=25; 
    private double resizeRatio=0;
    private boolean locked=true;
    private static Connect endConnect;
    private static MyCurve actualCurve;
    private static Connect actualConnect;
    private static StreetSegment actualStreetSegment;
    private static PoliceControll policeControll;
    private static int lastIdConnect=0, lastIdCurve=0, lastIdSegment=0;
    private static List<Connect> connects=new ArrayList<>();
    private List<MyCurve> curves=new ArrayList<>();
    private static List<StreetSegment> segments=new ArrayList<>();
    private static List<StreetSegment> startSegmentsCar=new ArrayList<>();
    private static List<StreetSegment> startSegmentsTram=new ArrayList<>();
    private static List<Node> nodesToHide=new ArrayList<>();
    private double startX, startY, distX, distY, layoutX, layoutY;
    private CheckBox lockBg;
    private Group drawRoot;
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
    public void clean()
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
        segments.clear();
        curves.clear();
        connects.clear();
        startSegmentsCar.clear();
        startSegmentsTram.clear();
        nodesToHide.clear();
        background.setImage(null);
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
                if(!locked)
                {
                    locked=true;
                    canvas.setVisible(true);
                }else
                {
                    locked=false;
                    canvas.setVisible(false);
                }
                
            }
        });
        RadioButton rbCar=gui.getCreateTram();
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
    }
    public void setPoliceControll(PoliceControll pc)
    {
        policeControll=pc;
    }
    public StreetSegment getRandomStart(boolean tram)
    {
        List<StreetSegment> sstoGen;
        if(!tram)
            sstoGen=startSegmentsCar;
        else
            sstoGen=startSegmentsTram;
        if(!sstoGen.isEmpty()){
            StreetSegment ret=sstoGen.get((int)(Math.random()*sstoGen.size()));
            if(ret!=null && ret.getVehicle()==null)
                return ret;
            else
                return null;
        }
        return null;
    }
    public static void cleanSegments()
    {
        lastIdSegment=0;
        for (StreetSegment segment : segments) {
            Prometheus.removeNode(segment.getCir());
        }
        startSegmentsCar.clear();
        segments.clear();
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
        //new Split(connects);
    }
    public void addSegment(StreetSegment ss)
    {
        segments.add(ss);     
    }
    public static void addStartSegmentCar(StreetSegment ss)
    {
        startSegmentsCar.add(ss);     
    }
    public static void addStartSegmentTram(StreetSegment ss)
    {
        startSegmentsTram.add(ss);     
    }
    public void addCurve(MyCurve mc)
    {
        curves.add(mc);
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
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(locked){
                    if(event.getButton()==MouseButton.PRIMARY)
                    {
                        if(actualConnect==null || (actualConnect!=null && (actualConnect.isTram() ^ tram))){
                            actualConnect=newConnect(event.getX(), event.getY());
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
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(locked)
                {
                    if(event.getButton()==MouseButton.PRIMARY){
                        if(actualConnect!=null){
                            actualConnect.move(event.getX(), event.getY());
                            if(actualCurve==null && !(actualConnect.isTram() ^ tram))
                                drawCurve(event.getX(), event.getY());
                        }
                    }
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

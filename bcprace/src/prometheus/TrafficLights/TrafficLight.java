/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.TrafficLights;

import prometheus.Street.StreetSegment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import prometheus.DrawControll;
import prometheus.LightsControll;

/**
 *
 * @author Honza
 */
public class TrafficLight {
    private int status=0;
    private List<LightsConnect> controlRed=new ArrayList<>();
    private List<LightsConnect> controlGreen=new ArrayList<>();
    private List<StreetSegment> lightSeg=new ArrayList<>();
    private Image red=new Image("resources/trafficLights/red.png");
    private Image green=new Image("resources/trafficLights/green.png");
    private Image orange2green=new Image("/resources/trafficLights/orange2green.png");
    private Image orange2red=new Image("resources/trafficLights/orange2red.png");
    private final String STYLE_SELECT_SEC="-fx-border-color: red;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;";  
    private final String STYLE_SELECT_PRIM="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    private final String STYLE_DEF="-fx-border-color: red;"
            + "-fx-border-width: 0;"
            + "-fx-border-style: solid;"; 
    private ImageView semImg=new ImageView();
    private HBox hbox;
    private double startX, startY, distX, distY;
    
    private int time=0;
    private int defGreen=5;
    private int defRed=5;
    private int defOrange=1;
    private int max=2;
    private boolean runRed=true, runGreen=true;
    private int id;
    private TimerTask timertask;
    private Timer timer;
    private LightsControll lc;
    public TrafficLight(LightsControll lc) {
        this.lc=lc;
        setStatus(status, false);
        imageControl();
        setID(LightsControll.getLastLightId());
    }
    public int getID()
    {
        return id;
    }
    public Point getPoz()
    {
        return new Point((int)hbox.getLayoutX(), (int)hbox.getLayoutY());
    }
    public int getTime()
    {
        return time;
    }
    public void setID(int id)
    {
        this.id=id;
        LightsControll.setLastLightId(id+1);
    }
    public void play()
    {
        timer=new Timer();
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
    private void imageControl()
    {
        semImg.setFitWidth(12);
        semImg.setFitHeight(30);
        hbox=new HBox();
        hbox.setLayoutX(30);
        hbox.setLayoutY(30);
        hbox.getChildren().add(semImg);
        prometheus.Prometheus.drawNode(hbox);
        hbox.setOnMousePressed((MouseEvent event1) -> {
            startX = event1.getX();
            startY = event1.getY();
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
            
        });
        hbox.setOnMouseDragged((MouseEvent event1) -> {
            hbox.setLayoutX(event1.getX() - distX);
            hbox.setLayoutY(event1.getY() - distY);
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
        });
        hbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==MouseButton.PRIMARY){
                    TrafficLight act=lc.getSemPrim();    
                    if(act!=getThis())
                    {
                        
                        if(act!=null)
                        {
                            act.setStyle(0);
                        }
                        
                        lc.selectSemPrim(getThis());
                        setStyle(1);
                    }
                    else
                    {
                        
                        setStyle(0);
                        lc.selectSemPrim(null);
                    } 
                }
                
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    StreetSegment actSegment=DrawControll.getActualStreetSegment();
                    TrafficLight act=lc.getSemSec();
                    if(act!=getThis())
                    {
 
                        if(act!=null)
                        {
                            act.setStyle(0);
                        }
                        if(actSegment!=null){
                            actSegment.addSemafor(getThis());
                            lightSeg.add(actSegment);
                        }
                        lc.selectSemSec(getThis());
                        setStyle(2);
                    }
                    else
                    {
                        if(actSegment!=null){
                            actSegment.getSemafory().remove(getThis());
                            lightSeg.remove(actSegment);
                        }
                        setStyle(0);
                        lc.selectSemSec(null);

                    }
                }
            }
        });
    }
    public void remove()
    {
        controlRed.clear();
        controlGreen.clear();
        prometheus.Prometheus.removeNode(hbox);
        for (StreetSegment streetSegment : lightSeg) {
            streetSegment.getSemafory().remove(this);
        }
        lightSeg.clear();
        lc.remove(this);
    }

    public void stop()
    {
        if(timertask!=null)
            timertask.cancel();
        if(timer!=null)
            timer.cancel();
    }
    public void setStyle(int i)
    {
        if(i==0)
        {
            hbox.setStyle(STYLE_DEF);
        }
        if(i==1)
        {
            hbox.setStyle(STYLE_SELECT_PRIM);
        }
        if(i==2)
        {
            hbox.setStyle(STYLE_SELECT_SEC);
        }
    }
    private TrafficLight getThis(){
        return this;
    }
    public HBox getIMG()
    {
        return hbox;
    }
    public void setTime(int time)
    {
        this.time=time;
    }
    public void moveIMG(Point p)
    {
        hbox.setLayoutX(p.getX());
        hbox.setLayoutY(p.getY());
    }
    public void addControlRed(TrafficLight sem, int stat)
    {
        controlRed.add(new LightsConnect(sem, stat));  
        lc.addtLastIDPrechod();
    }
    public List<LightsConnect> getControlRed()
    {
        return controlRed;
    }
    public void addControlGreen(TrafficLight sem, int stat)
    {
        controlGreen.add(new LightsConnect(sem, stat));   
    }
    public List<LightsConnect> getControlGreen()
    {
        return controlGreen;
    }
    private void changeControlRed()
    {
        for (LightsConnect pre : controlRed) {
            pre.getSem().setStatus(pre.getStatus(), true);
        }
    }
    private void changeControlGreen()
    {
        for (LightsConnect pre : controlGreen) {
            pre.getSem().setStatus(pre.getStatus(), true);
        }
    }
    public void enableChangeRed(boolean en)
    {
        runRed=en;
    }
    public void enableChangeGreen(boolean en)
    {
        runGreen=en;
    }
    public boolean getEnableRed()
    {
        return runRed;
    }
    public boolean getEnableGreen()
    {
        return runGreen;
    }
    public void setStatus(int status, boolean force)
    { 
        if(status==0 || status==3 || force  || ((status==1 && runRed) || (status==2 && runGreen))){
            time=0;
            this.status=status;
            switch(status)
            {
                case 0:
                {
                    //red
                    semImg.setImage(red);
                    max=defRed;
                    break;
                }
                case 1: 
                {
                    //orange to red
                    changeControlRed();
                    semImg.setImage(orange2red);
                    max=defOrange;
                    break;
                }
                case 2:
                {
                    //orange to green
                    changeControlGreen();  
                    semImg.setImage(orange2green);
                    max=defOrange;
                    break;
                }
                case 3:
                {
                    //green
                    semImg.setImage(green);
                    max=defGreen;
                    break;
                }
                default:        
                {
                    System.out.println("error set status");
                    break;
                }
            }
            
        }
    }
    public void setTimeOrange(int time)
    {
        defOrange=time;
    }
    public int getTimeOrange()
    {
        return defOrange;
    }
    private int changeStatus()
    {
        switch(status)
        {
            case 0:
            {
                return 2;
            }
            case 1:
            {
                return 0;
            }
            case 2:
            {
                return 3;
            }
            case 3:
            {
                return 1;
            }
            default:
            {
                System.out.println("error");
                return 0;
            }
        }
    }
    public int getStatus()
    {
        return status;
    }
    public void setTimeRed(int timeRed)
    {
        defRed=timeRed;
    }
    public void setTimeGreen(int timeGreen)
    {
        defGreen=timeGreen;
    }
    public int getTimeRed()
    {
        return defRed;
    }
    public int getTimeGreen()
    {
        return defGreen;
    }
    public void tick()
    {
        if(time==max)
            setStatus(changeStatus(), false);
        time++;
    }
}

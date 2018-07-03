/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Street;

import prometheus.Police.PoliceCombin;
import prometheus.TrafficLights.TrafficLight;
import prometheus.Vehicles.Vehicle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import prometheus.DrawControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class StreetSegment {
    private Point p0, p3, p1=null, p2=null;
    private List<StreetSegment> dalsiUseky=new ArrayList<>();
    private List<StreetSegment> predchoziUseky=new ArrayList<>();
    private Vehicle veh;
    private Circle cir;
    private List<StreetSegment> checkPoints=new ArrayList<>();
    private List<StreetSegment> checkPointsRev=new ArrayList<>();
    private List<TrafficLight> semafory=new ArrayList<>();
    private List<PoliceCombin> policieKombinace=new ArrayList<>();
    private StreetSegment selectedUsek;
    private final Color DEF_COLOR=Color.GREEN;
    private int id;
    private boolean strTram=false;
    private boolean startWinker=false;
    private boolean stopWinker=false;
    private double winkAngle;
    public StreetSegment(Point p0, Point p3) {
        this.p0=p0;
        this.p3=p3;
        setId(DrawControll.getLastSegmentId());
        setCir();
        
    }
    public Point getP0() {
        return p0;
    }
    public Circle getCir() {
        return cir;
    }
    public void setId(int id)
    {
        this.id=id;
        DrawControll.setLastSegmentId(id+1);
    }
    public double getWinkAngle() {
        return winkAngle;
    }
    public void removeNext()
    {
        for (StreetSegment usek : dalsiUseky) {
            usek.getPredchoziUseky().remove(this);
        }
        dalsiUseky.clear();        
    }
    public void removeFront()
    {
        for (StreetSegment usek : predchoziUseky) {
            usek.getDalsiUseky().remove(this);
        }
        predchoziUseky.clear();
    }
    public void removeUsek()
    {
        
        
        Prometheus.removeNode(cir);
        removeNext();
        removeFront();
        deSelectCheckPoints(this);
        /*for (StreetSegment checkPoint : cpToRem) {
            
            removeCheckPoint(checkPoint);
        }*/
    }
    public void moveCircle(Point p)
    {
        cir.setCenterX(p.getX());
        cir.setCenterY(p.getY());
    }
    public void setWinkAngle(double angle) {
        this.winkAngle = angle;
    }
    
    public boolean isStartWinker() {
        return startWinker;
    }

    public void setStartWinker(boolean startWinker) {
        this.startWinker = startWinker;
    }

    public boolean isStopWinker() {
        return stopWinker;
    }

    public void setStopWinker(boolean stopWinker) {
        this.stopWinker = stopWinker;
    }
    
    public int getId() {
        return id;
    }

    
    public void setCir() {
        cir=new Circle(p3.getX(), p3.getY(), 4, Color.GREEN);
        Prometheus.drawNode(cir);
        DrawControll.addToHide(cir);
        cir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                
                selectedUsek=DrawControll.getActualStreetSegment();
                if(t.getButton()==MouseButton.PRIMARY)
                {  
                    
                    if(selectedUsek!=getUsek())
                    {
                        if(selectedUsek!=null)
                        {
                            circleStyle(selectedUsek.getCir(),DEF_COLOR, 4);
                            deSelectCheckPoints(selectedUsek);
                            deselectSemafory(selectedUsek);
                        }
                        DrawControll.setActualStreetSegment(getUsek());
                        //Prometheus.setActUsek(getUsek());
                        circleStyle(cir,Color.MEDIUMVIOLETRED, 6);
                        selectCheckPoints(getUsek());
                        selectSemafory(getUsek());
                    }
                    else
                    {
                        deselect();
                    }
                }
                else if(t.getButton()==MouseButton.SECONDARY)
                {
                   
                    if(selectedUsek!=null)
                    {
                        if(selectedUsek.getCheckPoints().contains(getUsek()))
                        {
                            selectedUsek.removeCheckPoint(getUsek());
                            circleStyle(cir,DEF_COLOR, 4);
                        }
                        else
                        {
                            selectedUsek.addCheckPoint(getUsek());
                            circleStyle(cir,Color.ORANGE, 6);
                        }
                    }
                }  
            }
        });
            cir.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if(cir.getFill().equals(DEF_COLOR))
                        cir.setRadius(5);
                }
            });
            cir.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if(cir.getFill().equals(DEF_COLOR))
                        cir.setRadius(4);
                }
            });
    }
    public void deselect()
    {
        circleStyle(cir,DEF_COLOR, 4);
        deSelectCheckPoints(getUsek());
        deselectSemafory(getUsek());
        DrawControll.setActualStreetSegment(null);
    }
    public void selectCheckPoints(StreetSegment u)
    {
        for (StreetSegment cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),Color.ORANGE, 5);
        }
    }
    public void deSelectCheckPoints(StreetSegment u)
    {
        for (StreetSegment cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),DEF_COLOR, 4);
        }
    }
    public void selectSemafory(StreetSegment u)
    {
        for (TrafficLight sem : u.getSemafory()) {
            sem.setStyle(2);
        }
    }
    public void deselectSemafory(StreetSegment u)
    {
        for (TrafficLight sem : u.getSemafory()) {
            sem.setStyle(0);
        }
    }
    public void addPK(PoliceCombin pk)
    {
        policieKombinace.add(pk);
    }
    public List<PoliceCombin> getPK()
    {
        return policieKombinace;
    }
    public void removePK(PoliceCombin pk)
    {
        policieKombinace.remove(pk);
                
    }
    public void addSemafor(TrafficLight s)
    {
        semafory.add(s);
    }
    public void removeSem(TrafficLight s)
    {
        semafory.remove(s);
    }
    public List<TrafficLight> getSemafory()
    {
        return semafory;
    }
    public void addCheckPoint(StreetSegment u)
    {
        checkPoints.add(u);
        u.addRevCP(this);
    }
    public void addRevCP(StreetSegment u)
    {
        checkPointsRev.add(u);
    }
    public void removeRevCP(StreetSegment u)
    {
        checkPointsRev.remove(u);
    }
    public List<StreetSegment> getRevCP() {
        return checkPointsRev;
    }
    public void removeCheckPoint(StreetSegment u)
    {
        checkPoints.remove(u);
        u.removeRevCP(this);
    }
    public List<StreetSegment> getCheckPoints() {
        return checkPoints;
    }
    
    private void circleStyle(Circle c, Color col, int r)
    {
        c.setFill(col);
        c.setRadius(r);
    }
    private StreetSegment getUsek()
    {
        return this;
    }
    public Point getP3() {
        return p3;
    }

    public void setP0(Point p0) {
        this.p0 = p0;
    }
    public void setP3(Point p3) {
        this.p3 = p3;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }
    
    public Vehicle getVehicle() {
        return veh;
    }

    public void setVehicle(Vehicle veh) {
        this.veh=veh;
    }

    public List<StreetSegment> getDalsiUseky() {
        return dalsiUseky;
    }

    public void setDalsiUseky(StreetSegment dalsi) {
        dalsiUseky.add(dalsi);
    }

    public List<StreetSegment> getPredchoziUseky() {
        return predchoziUseky;
    }

    public void setPredchoziUseky(StreetSegment predchozi) {
        predchoziUseky.add(predchozi);
    }
    
}

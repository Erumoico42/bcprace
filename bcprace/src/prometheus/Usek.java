/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import prometheus.Vehicles.Vehicle;

/**
 *
 * @author Honza
 */
public class Usek {
    private Point p0, p3, p1=null, p2=null;
    private List<Usek> dalsiUseky=new ArrayList<>();
    private List<Usek> predchoziUseky=new ArrayList<>();
    private Vehicle veh;
    private Circle cir;
    private List<Usek> checkPoints=new ArrayList<>();
    private List<Usek> checkPointsRev=new ArrayList<>();
    private List<Semafor> semafory=new ArrayList<>();
    private List<PolKomb> policieKombinace=new ArrayList<>();
    private Usek selectedUsek;
    private final Color DEF_COLOR=Color.GREEN;
    private int id;
    private boolean strTram=false;
    private boolean startWinker=false;
    private boolean stopWinker=false;
    private double winkAngle;
    public Usek(Point p0, Point p3) {
        this.p0=p0;
        this.p3=p3;
        id=Prometheus.getLastUsekId();
        Prometheus.setLastUsekId(id+1);
        Prometheus.addUsek(this);
        setCir();
    }
    public Point getP0() {
        return p0;
    }
    public Circle getCir() {
        return cir;
    }

    public double getWinkAngle() {
        return winkAngle;
    }
    public void removeNext()
    {
        dalsiUseky.clear();        
    }
    public void removeFromt()
    {
        predchoziUseky.clear();
    }
    public void removeUsek()
    {
        for (Usek usek : dalsiUseky) {
            usek.getPredchoziUseky().remove(this);
        }
        for (Usek usek : predchoziUseky) {
            usek.getDalsiUseky().remove(this);
        }
        
        Prometheus.removeNodeSS(cir);
        removeNext();
        removeFromt();
        List<Usek> cpToRem=checkPoints;
        deSelectCheckPoints(this);
        /*for (Usek checkPoint : cpToRem) {
            
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

    public boolean isStrTram() {
        return strTram;
    }

    public void setStrTram(boolean strTram) {
        this.strTram = strTram;
    }
    
    public void setCir() {
        cir=new Circle(p3.getX(), p3.getY(), 4, Color.GREEN);
        Prometheus.addNode(cir);
        Prometheus.addToHideShow(cir);
        Prometheus.addCircle(cir);
        cir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                selectedUsek=Prometheus.getActUsek();
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
                        Prometheus.setActUsek(getUsek());
                        circleStyle(cir,Color.BLUE, 5);
                        selectCheckPoints(getUsek());
                        selectSemafory(getUsek());
                    }
                    else
                    {
                        circleStyle(cir,DEF_COLOR, 4);
                        deSelectCheckPoints(getUsek());
                        deselectSemafory(getUsek());
                        Prometheus.setActUsek(null);
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
                            circleStyle(cir,Color.ORANGE, 5);
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
    public void selectCheckPoints(Usek u)
    {
        for (Usek cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),Color.ORANGE, 5);
        }
    }
    public void deSelectCheckPoints(Usek u)
    {
        for (Usek cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),DEF_COLOR, 4);
        }
    }
    public void selectSemafory(Usek u)
    {
        for (Semafor sem : u.getSemafory()) {
            sem.setStyle(2);
        }
    }
    public void deselectSemafory(Usek u)
    {
        for (Semafor sem : u.getSemafory()) {
            sem.setStyle(0);
        }
    }
    public void addPK(PolKomb pk)
    {
        policieKombinace.add(pk);
    }
    public List<PolKomb> getPK()
    {
        return policieKombinace;
    }
    
    public void addSemafor(Semafor s)
    {
        semafory.add(s);
    }
    public List<Semafor> getSemafory()
    {
        return semafory;
    }
    public void addCheckPoint(Usek u)
    {
        checkPoints.add(u);
        u.addRevCP(this);
    }
    public void addRevCP(Usek u)
    {
        checkPointsRev.add(u);
    }
    public void removeRevCP(Usek u)
    {
        checkPointsRev.remove(u);
    }
    public List<Usek> getRevCP() {
        return checkPointsRev;
    }
    public void removeCheckPoint(Usek u)
    {
        checkPoints.remove(u);
        u.removeRevCP(this);
    }
    public List<Usek> getCheckPoints() {
        return checkPoints;
    }
    
    private void circleStyle(Circle c, Color col, int r)
    {
        c.setFill(col);
        c.setRadius(r);
    }
    private Usek getUsek()
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

    public List<Usek> getDalsiUseky() {
        return dalsiUseky;
    }

    public void setDalsiUseky(Usek dalsi) {
        dalsiUseky.add(dalsi);
    }

    public List<Usek> getPredchoziUseky() {
        return predchoziUseky;
    }

    public void setPredchoziUseky(Usek predchozi) {
        predchoziUseky.add(predchozi);
    }
    
}

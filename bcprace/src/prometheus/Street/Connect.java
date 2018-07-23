/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Street;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
public class Connect {
    private final Point p;
    private List<MyCurve> endCurves, startCurves;
    private final Circle c;
    private boolean selected=false;
    private double xOld, yOld;
    private int id;
    private final boolean tram;
    private Color color=Color.BLUE;
    public Connect(Point p, boolean tram) {
        this.p = p;
        this.tram=tram;
        endCurves=new ArrayList<>();
        startCurves=new ArrayList<>();
        
        if(tram)
            color=Color.PURPLE;
        c=new Circle(p.getX(), p.getY(), 5, color);
        Prometheus.drawNode(c);
        DrawControll.addToHide(c);
        initControll();
    }
    public boolean isTram()
    {
        return tram;
    }
    public Circle getCircle()
    {
        return c;
    }
    public void remove()
    {
        Prometheus.removeNode(c);
        for (MyCurve endCurve : endCurves) {
            endCurve.getC0().getStartCurves().remove(endCurve);
            Prometheus.removeNode(endCurve.getCurve(), endCurve.getC1().getC(), endCurve.getC2().getC(), endCurve.getC1().getControlLine(), endCurve.getC2().getControlLine());
            if(endCurve.getFirst()!=null)
                endCurve.getFirst().removeFront();
            for (StreetSegment segment : endCurve.getSegments()) {
                segment.removeUsek();
            }
            DrawControll.removeCurve(endCurve);
        }
        for (MyCurve startCurve : startCurves) {
            startCurve.getC3().getEndCurves().remove(startCurve);
            
            Prometheus.removeNode(startCurve.getCurve(), startCurve.getC1().getC(), startCurve.getC2().getC(), startCurve.getC1().getControlLine(), startCurve.getC2().getControlLine());
            if(startCurve.getLast()!=null)
                startCurve.getLast().removeUsek();
            for (StreetSegment segment : startCurve.getSegments()) {
                segment.removeUsek();
            }
            DrawControll.removeCurve(startCurve);
        }
        endCurves.clear();
        startCurves.clear();
        DrawControll.removeConnect(this);
        DrawControll.split();
    }
    private void initControll()
    {
        c.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                move(event.getX(),event.getY());
            }
        });
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==MouseButton.PRIMARY){
                    if(!selected)
                    {
                        select();
                    }
                    else
                    {
                        deselect();
                    }
                }
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    if(DrawControll.getActualConnect()!=null && DrawControll.getActualConnect()!=getConnect() && !(isTram() ^ DrawControll.getActualConnect().isTram())){
                        Prometheus.getDrawControll().drawCurve(getConnect());
                    }
                }
            }
        });
        c.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.toFront();
            }
        });
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private Connect getConnect()
    {
        return this;
    }
    public void select()
    {
        Connect actual=DrawControll.getActualConnect();
        if(actual!=null)
            actual.deselect();
        selected=true;
        DrawControll.setActualConnect(this);
        
        c.setStroke(Color.AQUA);
        c.setRadius(7);
    }
    public void deselect()
    {
        DrawControll.setActualConnect(null);
        setDefSkin();
    }
    public void setDefSkin()
    {
        selected=false;
        
        c.setStroke(null);
        c.setRadius(5);
    }
    public void move(double x, double y)
    {
        xOld=p.getX();
        yOld=p.getY();
        p.setLocation(x, y);
        c.setCenterX(x);
        c.setCenterY(y);
        moveConnects(x,y);
    }
    private void moveConnects(double x, double y)
    {
        if(!startCurves.isEmpty() && !endCurves.isEmpty())
        {
            double xNew=(xOld-x);
            double yNew=(yOld-y);
            for (MyCurve startCurve : startCurves) {
                startCurve.setChangedControlls();
                Point origP1=startCurve.getC1().getP();
                startCurve.getC1().move(origP1.getX()-xNew, origP1.getY()-yNew);
            }
            for (MyCurve endCurve : endCurves) {
                endCurve.setChangedControlls();
                Point origP2=endCurve.getC2().getP();
                endCurve.getC2().move(origP2.getX()-xNew, origP2.getY()-yNew);
            }
            
        }
        for (MyCurve startCurve : startCurves) {
            startCurve.moveCurveP0(x, y);
            startCurve.getC1().moveStartLine(x, y);
        }
        for (MyCurve endCurve : endCurves) {  
            endCurve.moveCurveP3(x, y);  
            endCurve.getC2().moveStartLine(x, y);
        }
    }
    public void addStartCurve(MyCurve curve)
    {
        startCurves.add(curve);
    }
    public void addEndCurve(MyCurve curve)
    {
        endCurves.add(curve);
    }

    public List<MyCurve> getEndCurves() {
        return endCurves;
    }

    public List<MyCurve> getStartCurves() {
        return startCurves;
    }
    
    public Point getPoint() {
        return p;
    }
    
    
}

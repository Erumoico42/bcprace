/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class Connect {
    private Point p, lastLoc;
    private List<MyCurve> endCurves=new ArrayList<>();
    private List<MyCurve> startCurves=new ArrayList<>();
    private Circle c;

    public Connect(Point p) {
        this.p = p;
        setCircle();
    }

    public Point getPoint() {
        return p;
    }

    public void setPoint(Point p) {
        this.p = p;
    }

    public List<MyCurve> getStartCurves() {
        return startCurves;
    }

    public void addStartCurve(MyCurve newCurve) {
        startCurves.add(newCurve);
    }
    public List<MyCurve> getEndCurves() {
        return endCurves;
    }

    public void addEndCurve(MyCurve newCurve) {
        endCurves.add(newCurve);
    }

    public Circle getCircle() {
        return c;
    }
    
    private void setCircle() {
        c=new Circle(p.getX(), p.getY(), 6, Color.BLACK);
        c.strokeWidthProperty().set(2);
        c.setOnMouseDragged((MouseEvent t) -> {
            lastLoc=new Point(p);
            p.setLocation(t.getX(), t.getY());
            moveConnect();
            c.toFront();;
        });
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Connect actConn=Prometheus.getActConn();
                if(event.getButton()==MouseButton.PRIMARY)
                    select();
                else if(event.getButton()==MouseButton.SECONDARY && actConn!=null && !actConn.equals(getThis()))
                {
                    MyCurve mc=new MyCurve(actConn,getThis());
                    mc.newRotateControl();
                    mc.moveControls();
                    mc.setJoined();
                    Prometheus.rozdel();
                }
                    
            }
        });
        c.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.toFront();
            }
        });
        c.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.toFront();
            }
        });
        Prometheus.addNode(c);
    }
    public void select()
    {
        Connect actConn=Prometheus.getActConn();
        if(actConn!=null)
        {
            if(actConn.equals(getThis()))
            {
                c.setStroke(null);
                Prometheus.setConnect(null);
            }
            else
            {
                actConn.getCircle().setStroke(null);
                Prometheus.setConnect(getThis());
                c.setStroke(Color.AQUA);
                
            }
        }
        else
        {
            Prometheus.setConnect(getThis());
            c.setStroke(Color.AQUA);
        }
    }
    public void move(Point p)
    {
        lastLoc=new Point(p);
        p.setLocation(p.getX(), p.getY());
        moveConnect();
    }
    private Connect getThis()
    {
        return this;
    }
    private void moveCurves(Point pp)
    {
        if(!startCurves.isEmpty() && !endCurves.isEmpty())
        {
            double dX=(lastLoc.getX()-pp.getX());
            double dY=(lastLoc.getY()-pp.getY());
            for (MyCurve startCurve : startCurves) {
                startCurve.setChanged();
                Point origP1=startCurve.getControl1().getPoint();
                startCurve.getControl1().moveControl(origP1.getX()-dX, origP1.getY()-dY);
            }
            for (MyCurve endCurve : endCurves) {
                endCurve.setChanged();
                Point origP2=endCurve.getControl2().getPoint();
                endCurve.getControl2().moveControl(origP2.getX()-dX, origP2.getY()-dY);
            }
            
        }
        for (MyCurve startCurve : startCurves) {
            startCurve.moveP0(pp.getX(), pp.getY());
        }
        for (MyCurve endCurve : endCurves) {  
            endCurve.moveP3(pp.getX(), pp.getY());     
        }
        
        
    }
    public void moveConnect()
    {
        c.setCenterX(p.getX());
        c.setCenterY(p.getY());
        moveCurves(p);  
    }
    

    
}

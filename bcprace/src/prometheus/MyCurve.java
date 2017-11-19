/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public final class MyCurve {
    private final CubicCurve curve;
    private final Connect conn0;
    private final Connect conn3;
    private final Control con1;
    private final Control con2;
    private Usek prvni, posledni;
    private Point p0;
    private Point p1;
    private Point p2;
    private Point p3;
    private boolean changedCont=false;
    private boolean done=false;
    public MyCurve(Connect conn0, Connect conn3) {    
        this.p0=conn0.getPoint();
        this.p3=conn3.getPoint();
        p1=new Point();
        p2=new Point();
        this.conn0 = conn0;
        this.conn3 = conn3;
        conn0.addStartCurve(getThis());
        conn3.addEndCurve(getThis());
        setAutoControlPoints();
        
        con1=new Control(p1, getThis(), 1, conn0);
        con2=new Control(p2, getThis(), 2, conn3);
        
        curve = new CubicCurve(p0.getX(), p0.getY(),p1.getX(), p1.getY(), p2.getX(), p2.getY(),p3.getX(), p3.getY()); 
        curve.setStrokeWidth(1);
        curve.setStroke(javafx.scene.paint.Color.BROWN);
        curve.setFill(null);
        Prometheus.addNode(1,curve);
        rotateControl();
        moveControls();
        Prometheus.rozdel();
    }
    public void setDone(boolean done)
    {
        this.done=done;
    }
    public boolean getDone()
    {
        return done;
    }
    private MyCurve getThis()
    {
        return this;
    }

    public Usek getPrvni() {
        return prvni;
    }

    public void setPrvni(Usek prvni) {
        this.prvni = prvni;
    }

    public Usek getPosledni() {
        return posledni;
    }

    public void setPosledni(Usek posledni) {
        this.posledni = posledni;
    }
    
    public CubicCurve getCurve() {
        return curve;
    }

    public Connect getConnect0() {
        return conn0;
    }
    public Connect getConnect3() {
        return conn3;
    }
    public Control getControl1()
    {
        return con1;
    }
    public Control getControl2()
    {
        return con2;
    }
    public void moveP0(double x, double y)
    {
        
        p0.setLocation(x,y);
        curve.setStartX(x);
        curve.setStartY(y);
        if(!changedCont)
        {
            setAutoControlPoints();
            moveControls();
        }
        Prometheus.rozdel();
    }
    public void moveP1(double x, double y)
    {
        
        p1.setLocation(x,y);
        curve.setControlX1(x);
        curve.setControlY1(y);
        Prometheus.rozdel();
    }
    public void setChanged()
    {
        changedCont=true;
    }
    public void moveP2(double x, double y)
    {
        
        p2.setLocation(x,y);
        curve.setControlX2(x);
        curve.setControlY2(y);
        Prometheus.rozdel();
    }
    public void moveP3(double x, double y)
    {
        
        p3.setLocation(x,y);
        curve.setEndX(x);
        curve.setEndY(y);
        if(!changedCont)
        {
            setAutoControlPoints();
            rotateControl();  
            moveControls();  
        }
        Prometheus.rozdel();
    }
    private void setAutoControlPoints()
    {
        double x0=p0.getX();
        double y0=p0.getY();
        double x3=p3.getX();
        double y3=p3.getY();
        double x1=(x0+(x3-x0)*1/3);
        double y1=(y0+(y3-y0)*1/3);
        double x2=(x0+(x3-x0)*2/3);
        double y2=(y0+(y3-y0)*2/3);
        p1.setLocation(x1,y1);  
        p2.setLocation(x2,y2);
    }
    private void rotateControl()
    {
        double length= MyMath.length(p0, p3)*1/3;
        if(!conn0.getEndCurves().isEmpty())
        {
            double ang=MyMath.angle(conn0.getPoint(),conn0.getEndCurves().get(0).getControl2().getPoint());
            Point ppp=con1.rotate(conn0.getPoint(), length, ang);
            p1.setLocation(ppp);
        } 
        else if(!conn0.getStartCurves().isEmpty())
        {
            double ang=MyMath.angle(conn0.getPoint(),conn0.getStartCurves().get(0).getControl1().getPoint());
            Point ppp=con1.rotate(conn0.getPoint(),length, Math.PI+ang);
            p1.setLocation(ppp);  
        } 
        double angl=MyMath.angle(p1, p0);
        rotSec(angl, length);
    }
    private void rotSec(double ang, double length)
    {
        double angle=MyMath.angle(conn0.getPoint(),conn3.getPoint());
        Point p=con2.rotate(conn3.getPoint(), length, 2*angle+Math.PI-ang);
        p2.setLocation(p);
    }
    public void newRotateControl()
    {
        if(!conn3.getStartCurves().isEmpty())
        {
            double ang=MyMath.angle(conn3.getPoint(),conn3.getStartCurves().get(0).getControl1().getPoint());
            Point pp=con2.rotate(conn3.getPoint(), MyMath.length(p0, p3)*1/3, ang);
            p2.setLocation(pp); 
        }
    }
    public void moveControls()
    {
        con1.moveControl(p1.getX(),p1.getY());
        con2.moveControl(p2.getX(),p2.getY());  
        
    }
    
}

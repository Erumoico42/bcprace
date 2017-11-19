/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class Control {
    private final Point p;
    private boolean isC1=false;
    private boolean isC2=false;
    private final MyCurve curve;
    private Circle c;
    private final Connect conn;
    public Control(Point p, MyCurve curve, int i, Connect conn) {
        this.p = p;
        this.curve = curve;
        this.conn=conn;
        if(i==1)
            isC1=true;
        else if(i==2)
            isC2=true;   
        setCircle();        
    }
    private void setCircle()
    {
        c=new Circle(p.getX(), p.getY(), 4, Color.RED);
        c.setOnMouseDragged((MouseEvent t) -> {
            moveControl(t.getX(),t.getY());
            curve.setChanged();
            moveControls(p);
        });
        c.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.toFront();
                c.setStroke(Color.RED);
            }
        });
        c.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.setStroke(null);
            }
        });
        Prometheus.addNode(c);
    }
    public void moveControl(double x, double y)
    {
        p.setLocation(x,y);
        if(isC1)
            curve.moveP1(x,y);
        else if(isC2)
            curve.moveP2(x, y);
        c.setCenterX(x);
        c.setCenterY(y); 
    }
    public void moveControls(Point p1)
    {
        double angle=MyMath.angle(conn.getPoint(),p1);
        if(isC2)
            angle+=Math.PI;
        for (MyCurve mc : conn.getStartCurves()) {
            if(!mc.equals(curve))
            {
                double length=MyMath.length(conn.getPoint(), mc.getControl1().getPoint());
                Point pp=rotate(conn.getPoint(), length, angle+Math.PI);
                mc.getControl1().moveControl((int)pp.getX(), (int)pp.getY());
                mc.setChanged();
            }
        }
        for (MyCurve mc : conn.getEndCurves()) {
            double length=MyMath.length(conn.getPoint(), mc.getControl2().getPoint());
            Point pp=rotate(conn.getPoint(), length, angle);
            mc.getControl2().moveControl((int)pp.getX(), (int)pp.getY());
            mc.setChanged();
        }
    }
    
    public Point rotate(Point p1, double length, double angle)
    {
        Point pp=new Point();
        double x=Math.cos(angle)*length;
        double y=Math.sin(angle)*length;
        pp.setLocation(x+p1.getX(),y+p1.getY());
        return pp;
    }
    
    public Circle getCircle()
    {
        return c;
    }
    public Point getPoint()
    {
        return p;
    }
    
}

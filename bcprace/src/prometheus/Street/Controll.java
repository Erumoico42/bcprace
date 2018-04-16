/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Street;

import java.awt.Point;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import prometheus.DrawControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class Controll {
    private final Point p;
    private final Circle c;
    private final MyCurve curve;
    private final boolean c1;
    private Connect conn;
    public Controll(Connect coonn, MyCurve curve, boolean c1) {
        
        this.conn=coonn;
        this.curve=curve;
        this.c1=c1;
        p=new Point(coonn.getPoint());
        this.c = new Circle(p.getX(), p.getY(), 4, Color.RED);
        Prometheus.drawNode(c);
        DrawControll.addToHide(c);
        initControll();
        
    }
    
    public void move(double x, double y)
    {
        
        p.setLocation(x,y);
        c.setCenterX(x);
        c.setCenterY(y);
        if(c1)
            curve.moveCurveP1(x, y);
        else
            curve.moveCurveP2(x, y);
        
    }
    private void initControll()
    {
        c.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                move(event.getX(),event.getY());
                curve.setChangedControlls();
                adaptControlls();
            }
        });
        c.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                c.toFront();
            }
        });
    }
    public void adaptControll()
    {
        List<MyCurve> endCurves=conn.getEndCurves();
        List<MyCurve> startCurves=conn.getStartCurves();
        double angle=-1;
        boolean start=false;
        if(!endCurves.isEmpty() )
        {
            angle=HelpMath.angle(conn.getPoint(),endCurves.get(0).getC2().getP());
        }
        else if(!startCurves.isEmpty())
        {
            start=true;
            angle=HelpMath.angle(conn.getPoint(),startCurves.get(0).getC1().getP());
        }
        
        if(angle>0){
            if(!c1)
                angle+=Math.PI;
            if(start)
                angle+=Math.PI;
            double length=HelpMath.length(conn.getPoint(), p);
            Point pNew=HelpMath.rotate(conn.getPoint(), length,angle);
            move(pNew.getX(),pNew.getY());
            if(!c1 && !startCurves.isEmpty())
                startCurves.get(0).getC1().adaptControlls();;
        }
    }
    private void adaptControlls()
    {
        double angle=HelpMath.angle(conn.getPoint(),p);
        if(!c1)
            angle+=Math.PI;
        for (MyCurve startCurve : conn.getStartCurves()) {
            if(!startCurve.equals(curve))
            {
                double length=HelpMath.length(conn.getPoint(), startCurve.getC1().getP());
                Point pp=HelpMath.rotate(conn.getPoint(), length, angle+Math.PI);
                startCurve.getC1().move(pp.getX(), pp.getY());
                startCurve.setChangedControlls();
            }
        }
        for (MyCurve endCurve : conn.getEndCurves()) {
            double length=HelpMath.length(conn.getPoint(), endCurve.getC2().getP());
            Point pp=HelpMath.rotate(conn.getPoint(), length, angle);
            endCurve.getC2().move(pp.getX(),pp.getY());
            endCurve.setChangedControlls();
        }         
    }
    public Point getP() {
        return p;
    }

    public Circle getC() {
        return c;
    }
    
}

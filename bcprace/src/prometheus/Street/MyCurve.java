/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Street;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import prometheus.DrawControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class MyCurve {
    private final CubicCurve curve;
    private Point p0, p1, p2, p3;
    private StreetSegment first, last;
    private final Connect c0, c3;
    private final Controll c1, c2;
    private boolean changedControlls=false;
    private int id;
    private final DrawControll drawing;
    private List<StreetSegment> segments=new ArrayList<>();
    
    public MyCurve(Connect start, Connect end, DrawControll draw) {
        this.p0=start.getPoint();
        this.p3=end.getPoint();
        p1=new Point();
        p2=new Point();
        this.drawing=draw;
        curve = new CubicCurve(p0.getX(), p0.getY(),p1.getX(), p1.getY(), p2.getX(), p2.getY(),p3.getX(), p3.getY()); 
        curve.setStroke(Color.BLACK);
        curve.setFill(null);
        
        Prometheus.drawNode(0,curve);
        draw.addToHide(curve);
        this.c0 = start;
        this.c3 = end;
        
        c1=new Controll(c0, this, true);
        c2=new Controll(c3, this, false);
        autoMoveP12();
    }
    public void selectCurve()
    {
        curve.setStrokeWidth(2);
        curve.setStroke(Color.FUCHSIA);
    }
    public void deselectCurve()
    {
        curve.setStrokeWidth(1);
        curve.setStroke(Color.BLACK);
    }
    public void addSegment(StreetSegment u)
    {
        segments.add(u);
    }
    public void removeSegment(StreetSegment u)
    {
        segments.remove(u);
    }
    public List<StreetSegment> getSegments()
    {
        return segments;
    }
    public StreetSegment getFirst() {
        return first;
    }

    public void setFirst(StreetSegment first) {
        this.first = first;
    }

    public StreetSegment getLast() {
        return last;
    }

    public void setLast(StreetSegment last) {
        this.last = last;
    }
    
    public CubicCurve getCurve()
    {
        return curve;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
   
    public void setChangedControlls()
    {
        changedControlls=true;
    }
    public Connect getC0() {
        return c0;
    }

    public Connect getC3() {
        return c3;
    }

    public Controll getC1() {
        return c1;
    }

    public Controll getC2() {
        return c2;
    }
    public void setPositionP1(double x, double y)
    {
        p1.setLocation(x, y);
        curve.setControlX1(x);
        curve.setControlY1(y);
    }
    public void setPositionP2(double x, double y)
    {
        p2.setLocation(x, y);
        curve.setControlX2(x);
        curve.setControlY2(y);
    }
    public void moveCurveP0(double x, double y)
    {
        p0.setLocation(x, y);
        curve.setStartX(x);
        curve.setStartY(y);
        if(!changedControlls)
            autoMoveP12();
        drawing.newSplit();
    }
    public void moveCurveP1(double x, double y)
    {
        setPositionP1(x,y);
        drawing.newSplit();
    }
    public void moveCurveP2(double x, double y)
    {
        setPositionP2(x,y);
        drawing.newSplit();
    }
    public void moveCurveP3(double x, double y)
    {
        p3.setLocation(x, y);
        curve.setEndX(x);
        curve.setEndY(y);
        
        if(!changedControlls)
            autoMoveP12();
        drawing.newSplit();
    }
    private void autoMoveP12()
    {
        double x0=p0.getX();
        double y0=p0.getY();
        double x3=p3.getX();
        double y3=p3.getY();
        double x1=(x0+(x3-x0)*1/3);
        double y1=(y0+(y3-y0)*1/3);
        double x2=(x0+(x3-x0)*2/3);
        double y2=(y0+(y3-y0)*2/3);
        c1.move(x1,y1);
        c2.move(x2,y2);
        c1.adaptControll();
    }
    
}

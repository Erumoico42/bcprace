/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.util.List;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public class MyCurve {
    private CubicCurve curve;
    private ConnectPoint cp0;
    private ConnectPoint cp3; 
    private Controller c1;
    private Controller c2;
    private boolean cpChanged=false;
    //private List<MyCurve> dalsi;
    private MyCurve dalsi;
    public MyCurve(CubicCurve curve, ConnectPoint cp0) {
        this.curve = curve;
        this.cp0=cp0;
    }
    public void setNext(MyCurve dalsi)
    {
        this.dalsi=dalsi;          
    }
    public MyCurve getNextCurve()
    {
        return dalsi;
    }
    /*public void setNext(MyCurve next)
    {
        dalsi.add(next);
    }
    public List<MyCurve> getNextCurve()
    {
        return dalsi;
    }*/
    public void setPoint1(Controller c1)
    {
        this.c1=c1;
    }
    public void setPoint2(Controller c2)
    {
        this.c2=c2;
    }
    public void setPoint3(ConnectPoint cp3)
    {
        this.cp3=cp3;
    }
    public void movePoint0(double x, double y)
    {
        curve.setStartX(x);
        curve.setStartY(y);
        if(!cpChanged)
            setAutoControlPoints();
    }
    public void movePoint1(double x, double y)
    {
        curve.setControlX1(x);
        curve.setControlY1(y);
        cpChanged=true;
    }
    public void movePoint2(double x, double y)
    {
        curve.setControlX2(x);
        curve.setControlY2(y);
        cpChanged=true;
    }
    public void movePoint3(double x, double y)
    {
        curve.setEndX(x);
        curve.setEndY(y);
        if(!cpChanged)
            setAutoControlPoints();
    }
    public ConnectPoint getPoint0()
    {
        return cp0;
    }
    public ConnectPoint getPoint3()
    {
        return cp3;
    }
    public void setAutoControlPoints()
    {
        double x0=cp0.getCircle().getCenterX();
        double y0=cp0.getCircle().getCenterY();
        double x3=cp3.getCircle().getCenterX();
        double y3=cp3.getCircle().getCenterY();
        double x1=(x0+(x3-x0)*1/3);
        double y1=(y0+(y3-y0)*1/3);
        double x2=(x0+(x3-x0)*2/3);
        double y2=(y0+(y3-y0)*2/3);
        curve.setControlX1(x1);
        curve.setControlY1(y1);
        curve.setControlX2(x2);
        curve.setControlY2(y2);
        c1.setXY(x1, y1);
        c2.setXY(x2, y2);
    }
    public CubicCurve getCurve()
    {
        return curve;
    }
    
}

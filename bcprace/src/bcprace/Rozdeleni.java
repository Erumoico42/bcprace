/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public class Rozdeleni {

    private List<Usek> useky;
    private Usek u, u2;
    private List<MyCurve> curves;
    private Point p;
    public Rozdeleni(List<MyCurve> curves) {
        useky=new ArrayList<Usek>();
        this.curves=curves;
        split();
    }
    private void split()
    {
        for (MyCurve curve : curves) {
            MyCurve mc=curve;
            CubicCurve cc=mc.getCurve();
            p=new Point((int)cc.getStartX(),(int)cc.getStartY());
            u=new Usek();
            u.setP1(p);
            useky.add(u);
            while (mc!=null) {                
                callBez(mc.getCurve());
                mc=mc.getNextCurve();
            }
        }
    }
    
    public List<Usek> getUseky()
    {
        return useky;
    }
    
    private void callBez(CubicCurve curve)
    {
        bezier(new Point((int)curve.getStartX(), (int)curve.getStartY()), 
            new Point((int)curve.getControlX1(), (int)curve.getControlY1()),
            new Point((int)curve.getControlX2(), (int)curve.getControlY2()),
            new Point((int)curve.getEndX(), (int)curve.getEndY()));
    }
    
    private void newPoint(int x, int y)
    {
        if(length(x, y)>30)
        {
            p=new Point(x,y);
            u.setP2(p);   
            u2=new Usek();
            u.setDalsi(u2);
            u=u2;
            u.setP1(p);
            
        }
    }
    
    private double length(int x, int y)
    {
        double distance = Math.sqrt(Math.pow(p.getX()-x,2) + Math.pow(p.getY()-y,2));
        return distance;
    }
    
    private void bezier(Point p0, Point p1, Point p2, Point p3)
    {
        int x0=(int)p0.getX();
        int y0=(int)p0.getY();
        int x1=3*((int)p1.getX()-x0);
        int y1=3*((int)p1.getY()-y0);
        int x2=3*(x0-2*(int)p1.getX()+(int)p2.getX());
        int y2=3*(y0-2*(int)p1.getY()+(int)p2.getY());
        int x3=3*((int)p1.getX()-(int)p2.getX())+(int)p3.getX()-x0;
        int y3=3*((int)p1.getY()-(int)p2.getY())+(int)p3.getY()-y0;
        int xfirst=(int)p0.getX();
        int yfirst=(int)p0.getY();
        for(float t = 0; t <= 1; t +=0.01) {
            float t2=t*t;
            float t3=t2*t;
            int x = (int)(x0+(t*x1)+(t2*x2)+(t3*x3));
            int y = (int)(y0+(t*y1)+(t2*y2)+(t3*y3));
            line(new Point(xfirst, yfirst),new Point(x,y));
            xfirst=x;
            yfirst=y;   
        }
    }
    
    private void line(Point p1, Point p2)
    {
        int x1=(int)p1.getX(), y1=(int)p1.getY(), x2=(int)p2.getX(), y2=(int)p2.getY();
        double dx = x2-x1;
        double dy = y2-y1;

        if (Math.abs(y2 - y1) <= Math.abs(x2 - x1)) {

            if ((x1 == x2) && (y1 == y2)) {
                newPoint(x1,y1);
            } 
            else {
                if (x2 < x1) {
                    int tmp = x2;
                    x2 = x1;
                    x1 = tmp;

                    tmp = y2;
                    y2 = y1;
                    y1 = tmp;
                }

                double k = (double)dy/dx; 
                int cele_y;           
                double y = (double)y1;

                for (int x = x1 ; x <= x2 ; x++) {
                    cele_y = (int)Math.round(y);
                    y += k;
                    newPoint(x,cele_y);
                }
            }
        } else {

            if (y2 < y1) {
                int tmp = x2;
                x2 = x1;
                x1 = tmp;

                tmp = y2;
                y2 = y1;
                y1 = tmp;
            }

            double k = (double)dx/dy;
            int cele_x;
            double x = (double)x1;
            for (int y = y1; y <= y2; y++) {
                cele_x = (int)Math.round(x);
                x += k;       
                newPoint(cele_x, y);
            }
        }  
    }
}

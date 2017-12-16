/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Honza
 */
public class Rozdeleni {

    private List<Usek> startUseky;
    private Usek u, u2;
    private Point p;
    private Point p12, p21;
    private final int SEG_LENGTH=30;
    public Rozdeleni(List<Connect> connects) {
        startUseky=new ArrayList<Usek>();
        for (Connect con : connects) {
            deDone(con);
        }
        for (Connect connect : connects) {
            p=new Point(connect.getPoint());
            u=new Usek();
            u.setP1(p);
            u.setP2(p);
            startUseky.add(u);
            split(u, connect);
        }
    }
    private void deDone(Connect con)
    {
        for (MyCurve mc : con.getStartCurves()) {
            if(mc.getDone())
            {
                mc.setDone(false);
                deDone(mc.getConnect3());
            }
        }
    }
    private void newPoint(int x, int y)
    {
        if(length(x, y)>SEG_LENGTH)
        {
            u2=new Usek();
            u2.setP1(p);
            p=new Point(x,y);
            u2.setP2(p);
            double angle;
            if(u.getP21()==null || u.getP12()==null)
            {
                angle=MyMath.angle(u2.getP2(), u2.getP1());
                p12=MyMath.rotate(u2.getP1(), 10, angle);
                p21=MyMath.rotate(u2.getP1(), 20, angle);
                u.setP12(p12);
                u.setP21(p21);
            }      
            angle=MyMath.angle(u2.getP1(),u.getP21());
            p12=MyMath.rotate(u2.getP1(), 10, angle);
            u2.setP12(p12);
            
            angle=MyMath.angle(p12,u2.getP2());
            p21=MyMath.rotate(p, 10, angle); 
            u2.setP21(p21);
            
            u2.setCir();
            u.setDalsiUseky(u2);
            u2.setPredchoziUseky(u);
            u=u2;
        }
        
    }
    private void split(Usek start, Connect c)
    {    
        for (MyCurve mc : c.getStartCurves()) { 
            if(!mc.getDone())
            {
                mc.setDone(true);
                p=start.getP2();
                u=start;
                mc.setPrvni(start);
                callBez(mc.getCurve());
                mc.setPosledni(u);
                
                if(!mc.getConnect3().getStartCurves().isEmpty())
                {
                    if(!mc.getConnect3().getStartCurves().get(0).getDone())
                        split(mc.getPosledni(), mc.getConnect3()); 
                    else
                    {  
                        for (Usek usek : mc.getConnect3().getStartCurves().get(0).getPrvni().getDalsiUseky()) {
                            Usek u3;
                            Point p1=mc.getPosledni().getP2();
                            Point p2=usek.getP1();
                            if(MyMath.length(p1.getX(),p1.getY(),p2.getX(),p2.getY())>15)
                            {
                                u3=new Usek();
                                u3.setP1(mc.getPosledni().getP2());
                                u3.setP2(usek.getP1());
                                double angle=MyMath.angle(u3.getP2(), u3.getP1());
                                Point p12=MyMath.rotate(u3.getP1(), 10, angle);
                                Point p21=MyMath.rotate(u3.getP1(), 20, angle);
                                u3.setP12(p12);
                                u3.setP21(p21);
                                mc.getPosledni().setDalsiUseky(u3);
                                u3.setPredchoziUseky(mc.getPosledni());
                            }
                            else
                                u3=mc.getPosledni();
                            
                            u3.setDalsiUseky(usek);
                            if(!usek.getDalsiUseky().contains(mc.getPosledni()))
                                usek.setPredchoziUseky(u3);
                        }
                    }
                }
            }
        }  
    }
    public List<Usek> getStartUseky()
    {
        return startUseky;
    }
    
    private void callBez(CubicCurve curve)
    {
        bezier(new Point((int)curve.getStartX(), (int)curve.getStartY()), 
            new Point((int)curve.getControlX1(), (int)curve.getControlY1()),
            new Point((int)curve.getControlX2(), (int)curve.getControlY2()),
            new Point((int)curve.getEndX(), (int)curve.getEndY()));
    }
    
    
    
    private double length(double x, double y)
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
            line(xfirst, yfirst,x,y);
            xfirst=x;
            yfirst=y;   
        }
    }
    public void line(int xA, int yA, int xB, int yB)
    {       
        int x1=xA;
        int y1=yA;
        int x2=xB;
        int y2=yB;
        int dx = Math.abs(x2-x1);
        int dy = Math.abs(y2-y1); 
        int dxy=dx-dy;    
        if(dx!=0 || dy!=0) 
        {
            int px=-1;
            int py=-1;
            if(x1<x2)px=1;
            if(y1<y2)py=1;
            newPoint(x1, y1);
            while ((x1 != x2) || (y1 != y2))
            {            
                int p = 2 * dxy;
                if (p > -dy) {
                    dxy -= dy;
                    x1 +=px;
                }
                if (p < dx) {
                    dxy += dx;
                    y1 +=py;
                }
                newPoint(x1, y1);
            }
        }    
    }

}

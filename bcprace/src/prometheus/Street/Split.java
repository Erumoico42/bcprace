/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Street;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.CubicCurve;
import prometheus.DrawControll;

/**
 *
 * @author Honza
 */
public class Split {
    private List<Connect> connects=new ArrayList<>();
    private final int SEG_LENGTH=30;
    private Point pOld, pNew;
    private StreetSegment newSS, lastSS, firstSS;;
    private List<StreetSegment> curveUseky;
    private int idCurveUsek=0;
    private boolean newWink=true;
    private double winkAngle;
    public Split(List<Connect> connects) {
        this.connects=connects;
        callSplit();
    }
    private void callSplit()
    {
        for (Connect connect : connects) {
            
            for (MyCurve startCurve : connect.getStartCurves()) { 
                if(connect.getStartCurves().size()>1)
                    newWink=true;
                else
                    newWink=false;
                winkAngle=Math.toDegrees(HelpMath.angle(startCurve.getC0().getPoint(), 
                    startCurve.getC1().getP())-HelpMath.angle(startCurve.getC0().getPoint(), startCurve.getC3().getPoint()));
                
                idCurveUsek=0;
                curveUseky=startCurve.getSegments();
                boolean endExist=false;
                for (MyCurve endCurve : connect.getEndCurves()) {
                    if(endCurve.getLast()!=null)
                    {
                        pOld=endCurve.getLast().getP3();
                        endExist=true;
                        break;
                    }
                }   
                if(!endExist)
                    pOld=connect.getPoint();
                callBez(startCurve.getCurve());
                startCurve.setFirst(firstSS);
                
                if(idCurveUsek<curveUseky.size())
                {
                    int size=curveUseky.size();
                    List<StreetSegment> useky;
                    for (int i = size-1; i > idCurveUsek-1; i--) {
                        useky=curveUseky.get(i).getPredchoziUseky();
                        for (StreetSegment usek : useky) {
                            if(usek.getDalsiUseky().contains(curveUseky.get(i)))
                            {
                                lastSS=usek;
                                lastSS.getDalsiUseky().clear();
                                break;
                            }
                        }
                        curveUseky.get(i).removeUsek();
                        curveUseky.remove(i);
                    }
                }
                
                startCurve.setLast(lastSS);
                
                lastSS=null;
                firstSS=null;
            }  
        }
        for (Connect connect : connects) {
            for (MyCurve startCurve : connect.getStartCurves()) {

                for (MyCurve endCurve : connect.getEndCurves()) {
                    StreetSegment ssEnd=endCurve.getLast();
                    StreetSegment ssStart=startCurve.getFirst();
                    connectSegments(ssEnd,ssStart);  
                      
                }
                
            }
        }
        DrawControll.cleanStartCar();
        DrawControll.cleanStartTram();
        for (Connect connect : connects) {
            if(connect.getEndCurves().isEmpty())
            {
                for (MyCurve startCurve : connect.getStartCurves()) {
                    if(connect.isTram()){
                        DrawControll.addStartSegmentTram(startCurve.getFirst());
                    }
                    else{
                        DrawControll.addStartSegmentCar(startCurve.getFirst());
                    }
                }
                
            }
        }
    }
    private void setWink(StreetSegment us, double angle)
    {
        if(Math.abs(angle)>20 && Math.abs(angle-360)>20){
            if(angle>180)
                angle-=360;
            us.setWinkAngle(angle);
        }
        else
        {
            us.setWinkAngle(0);
        }
    }
    private void connectSegments(StreetSegment oldS, StreetSegment newS)
    {
        
        if(oldS!=null && newS!=null){
            if(!oldS.getDalsiUseky().contains(newS))
                oldS.setDalsiUseky(newS);
            if(!newS.getPredchoziUseky().contains(oldS))
                newS.setPredchoziUseky(oldS);
        }
    }
    private void newSegment(int x, int y)
    {
        if(HelpMath.length(pOld.getX(), pOld.getY(), x, y)>SEG_LENGTH)
        {
            double angle;
            pNew=new Point(x,y);
            idCurveUsek++;
            boolean newUs=true;
            boolean remUs=false;
            if(curveUseky.size()>=idCurveUsek){
                newUs=false; 
                newSS=curveUseky.get(idCurveUsek-1);
                newSS.setP0(pOld);
                newSS.setP3(pNew);
                newSS.moveCircle(pNew);
            }
            else{
                remUs=true;
                if(lastSS!=null)
                    lastSS.removeNext();
                newSS=new StreetSegment(pOld, pNew);
                curveUseky.add(newSS);
            }
            if(newWink)
            {
                setWink(newSS, winkAngle);
                newWink=false;
            }
            else
            {
                setWink(newSS, 0);
            }
            
            if(lastSS==null)
            {
                angle=HelpMath.angle(pNew, pOld);
                Point p12=HelpMath.rotate(pOld, 10, angle);
                Point p21=HelpMath.rotate(pOld, 20, angle);
                
                newSS.setP1(p12);
                newSS.setP2(p21);
                    firstSS=newSS;
            }
            else
            {
                angle=HelpMath.angle(pOld, lastSS.getP2());
                Point p12=HelpMath.rotate(pOld, 10, angle);
                newSS.setP1(p12);
                
                angle=HelpMath.angle(p12, pNew);
                Point p21=HelpMath.rotate(pNew, 10, angle);
                newSS.setP2(p21);  
                if(newUs)
                    connectSegments(lastSS, newSS);
                
            }
            lastSS=newSS;
            pOld=pNew;
        }
        
    }
    private void callBez(CubicCurve curve)
    {
        bezier(new Point((int)curve.getStartX(), (int)curve.getStartY()), 
            new Point((int)curve.getControlX1(), (int)curve.getControlY1()),
            new Point((int)curve.getControlX2(), (int)curve.getControlY2()),
            new Point((int)curve.getEndX(), (int)curve.getEndY()));
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
            newSegment(x1, y1);
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
                newSegment(x1, y1);
            }
        }   
    }
}

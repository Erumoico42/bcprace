/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Honza
 */
public class Auto {
    private ImageView iv;
    private Animace a;
    private Usek u;
    private int xF, yF, xL, yL;
    private int ux, uy;
    private Point p1, p2;
    private double t;
    private double actRychlost=0.1;
    private static final double MAX_RYCHLOST=0.1;
    private boolean pauza=false;
    private double actZrychleni=0;
    private static final double MAX_ZRYCHLENI=0.002;
    private TexturaAuto ta;
    public Auto(Usek u, Animace a) {
        this.a=a;
        this.u=u.getDalsiUseky().get((int)(Math.random()*(u.getDalsiUseky().size())));
        setIv();
        setPoints(); 
    }
    private void setIv()
    {
        ta=new TexturaAuto();
        iv=new ImageView(ta.getDefImg());
        iv.setFitWidth(64);
        iv.setFitHeight(64);
        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(!pauza)
                    pause();
                else
                    play();
                //vybratAuto();
            }
        });
        Prometheus.addNode(iv);
    }
    private void vybratAuto()
    {
        Prometheus.setActAuto(this);
    }
    public void tick()
    {
        t+=actRychlost;
        double x=(xF+(t*ux));
        double y=(yF+(t*uy));
        move(x,y);
        setSpeed(actZrychleni);
        if(actRychlost >=MAX_RYCHLOST)
        {
            actZrychleni=0;
            actRychlost=MAX_RYCHLOST;
        }
        if(actRychlost<0.0005)
        {
            actZrychleni=0;
            actRychlost=0;
        }
        if(!pauza)
        {
            streetDetectCar(); 
        }
        if(t>=1)
        {
            t-=1;
            zmenitUsek();      
        }
    }
    private void removeCar()
    {
        a.removeCar(this);
        u.setCar(null);
        iv.setVisible(false);
    }
    private void zmenitUsek()
    {
        u.setCar(null);
        if(!u.getDalsiUseky().isEmpty())
        {
            u=u.getDalsiUseky().get((int)(Math.random()*(u.getDalsiUseky().size())));
            setPoints();
            u.setCar(this); 
        }
        else
        {
            removeCar();
            
        }
    }
    
    private void setPoints()
    { 
        p1=u.getP1();
        p2=u.getP2();
        xF=(int)p1.getX();
        yF=(int)p1.getY();
        xL=(int)p2.getX();
        yL=(int)p2.getY();
        ux=xL-xF;
        uy=yL-yF;
        iv.setRotate(angle(p1,p2));
    }
    public void setSpeed(double newSpeed)
    {
        actRychlost+=newSpeed;
    }
    private void streetDetectCar()
    {
        Usek uNext=u;
        
        boolean carFound=false;
        //for (Usek usek : uNext.getDalsiUseky()) {
            //Usek uu=uNext;
            double distance=0;
            while(distance<10 && !carFound && !uNext.getDalsiUseky().isEmpty() && (uNext=uNext.getDalsiUseky().get(0))!=null)
            {
                distance++;
                if(uNext.getCar()!=null)
                {
                    double speedNextCar=uNext.getCar().getSpeed();
                    double tNextCar=uNext.getCar().getT();
                    distance=distance+tNextCar-t;
                    double dSpeed=actRychlost-speedNextCar;
                    if(dSpeed>0 || actRychlost<MAX_RYCHLOST)
                    {
                        actZrychleni=-calcSpeed(dSpeed, distance);
                        if(distance<2.5)
                        {
                            actRychlost=0;
                            actZrychleni=0;
                        }
                    }
                    carFound=true;
                } 
            //}
        }
        
        if(!carFound && actRychlost<MAX_RYCHLOST)
        {
            actZrychleni=MAX_ZRYCHLENI;
        }
        crossDetectCar(carFound);
    }
    
    private void crossDetectCar(boolean cb)
    {
        Usek actUsek=u;
        double actDist=0;
        boolean carFound=false;
        while(actDist<4 && !actUsek.getDalsiUseky().isEmpty() && actUsek.getCheckPoints().isEmpty())
        {
            actDist++;
            actUsek=actUsek.getDalsiUseky().get(0);
        } 
        actDist-=t;
        if(actDist<3)
        {
            for (Usek checkPoint : actUsek.getCheckPoints()) {
                if(!carFound){
                    Usek ucp=checkPoint;
                    double nextDist=0;
                    while(nextDist <9 && !carFound && !ucp.getPredchoziUseky().isEmpty())
                    {
                        Auto nextCar=ucp.getCar();
                        if(nextCar!=null)
                        {
                            double nextRychlost=nextCar.getSpeed();
                            double nextT=nextCar.getT();
                            double nextD=nextDist-nextT;
                            double dNext=(nextD+1)/nextRychlost;
                            double dAct=actDist/actRychlost;
                            if((dAct*3)>dNext)
                            {
                                carFound=true;
                                if(cb && actDist>2)
                                    actZrychleni=MAX_ZRYCHLENI;
                                else if(actDist>0.1)
                                    actZrychleni=-MAX_ZRYCHLENI;
                                else
                                {
                                    actZrychleni=0;
                                    actRychlost=0;
                                }
                            }
                        }
                        nextDist++;
                        ucp=ucp.getPredchoziUseky().get(0);
                    }
                }
            }
        }
    }
    private double calcSpeed(double dSpeed, double dist)
    {
        double s=dSpeed/(dist*dist*(dist/2));
        return s;
    }
    private void move(double x, double y)
    {
        iv.setX((x-(iv.getFitHeight()/2)));
        iv.setY((y-(iv.getFitWidth()/2)));
    }
    private float angle(Point p1, Point p2) {
        float angle = (float) Math.toDegrees(Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX()))+180;
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    private void pause()
    {
        pauza=true;
        actZrychleni=-MAX_ZRYCHLENI;
    }
    private void play()
    {
        pauza=false;
        actZrychleni=MAX_ZRYCHLENI;
    }
    private double getSpeed()
    {
        return actRychlost;
    }
    private double getT()
    {
        return t;
    }
}

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
    double x0, y0, x1, y1, x2, y2, x3, y3, xLast, yLast;
    private Point p1, p2, p12, p21;
    private double t;
    private double actRychlost=0.1;
    private static final double MAX_RYCHLOST=0.1;
    private boolean pauza=false;
    private double actZrychleni=0;
    private static final double MAX_ZRYCHLENI=0.002;
    private TexturaAuto ta;
    private final double IMG_SIZE=20;
    private boolean carFoundStreet, semFound;
    private double distNextStreet=0;
    public Auto(Usek u, Animace a) {
        this.a=a;
        this.u=u;
        a.addCar(this);
        u.setCar(this);
        setPoints(); 
        setIv();
        
    }
    private void setIv()
    {
        ta=new TexturaAuto();
        iv=new ImageView(ta.getDefImg());
        iv.setFitWidth(40);
        iv.setFitHeight(40);
        move(0);
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
        
        move(t);
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
            colisionDetect(); 
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
        p12=u.getP12();
        if(p12==null)
            p12=p2;
        p21=u.getP21();
        if(p21==null)
            p21=p1;
        x0=p1.getX();
        y0=p1.getY();
        x1=3*(p12.getX()-x0);
        y1=3*(p12.getY()-y0);
        x2=3*(x0-2*p12.getX()+p21.getX());
        y2=3*(y0-2*p12.getY()+p21.getY());
        x3=3*(p12.getX()-p21.getX())+p2.getX()-x0;
        y3=3*(p12.getY()-p21.getY())+p2.getY()-y0;
        
        xLast=x0;
        yLast=y0;
    }
    public void setSpeed(double newSpeed)
    {
        actRychlost+=newSpeed;
    }
    private void colisionDetect()
    {
        distNextStreet=666;
        carFoundStreet=findStreet(u, 0);
        if(!carFoundStreet)
        {
            actZrychleni=MAX_ZRYCHLENI;  
        }
        
        if(distNextStreet >1)
        {
            carFoundStreet=findSem(u,2);
            if(!semFound)
                findCPCross(u, 1);
            
        }
           
        
    }
    private boolean findSem(Usek us, double d)
    {
        boolean semFound=false;
        for (Usek uNext : us.getDalsiUseky()) {
            if(d<5 && !semFound)
            {
                double dist=d;
                for (Semafor sem : uNext.getSemafory()) {
                    if(sem.getColor().equals("red") || sem.getColor().equals("orange2red"))
                    {
                        semFound=true;
                        this.semFound=false;
                        dist-=t;
                        actZrychleni=-calcSpeed(actRychlost, dist+2);
                        if(dist<1.1)
                        {
                            actRychlost=0;
                            actZrychleni=0;
                        }
                    }
                    else if(!semFound)
                    {
                        this.semFound=true;
                        actZrychleni=MAX_ZRYCHLENI;  
                    }
                }
                if(!semFound)
                {
                    findSem(uNext, d+1);
                }
            }
        }
        return semFound;
    }
    private boolean findStreet(Usek us, int d)
    {
        boolean carFound=false;
        for (Usek uNext : us.getDalsiUseky()) {
            if(d<5 && !carFound)
            {
                double dist=d;
                if(uNext.getCar()!=null)
                {
                    
                    double speedNextCar=uNext.getCar().getSpeed();
                    double tNextCar=uNext.getCar().getT();
                    dist=dist+tNextCar-t;
                    double dSpeed=actRychlost-speedNextCar;
                    distNextStreet=dist; 
                    if(dSpeed>0 || actRychlost<MAX_RYCHLOST)
                    {
                        actZrychleni=-calcSpeed(dSpeed, dist+2);
                        if(dist<0.5)
                        {
                            actRychlost=0;
                            actZrychleni=0;
                        }
                        
                    }
                    carFound=true;
                }
                else
                {
                    carFound=findStreet(uNext, d+1);
                }
            }
        }
        return carFound;
    }
    
    private void findCPCross(Usek us, int dist)
    {
        for (Usek uNext : us.getDalsiUseky()) {
            if(uNext.getCheckPoints().isEmpty())
            {
                if(dist<4)
                    findCPCross(uNext, dist+1);
            }
            else
            {
                for (Usek cp : uNext.getCheckPoints()) {
                    for (Usek uN : cp.getDalsiUseky()) {
                        findCarCross(uN, 1, dist);                        
                    }
                }
                
            }
        }
    }
    private boolean findCarCross(Usek us, int nextDist, double actDist)
    {
        
        boolean carFound=false;
        Auto nextCar=us.getCar();
        for (Usek uNext : us.getPredchoziUseky()) {
            if(!carFound)
            {
                if(nextCar==null)
                    nextCar=uNext.getCar();
                if(nextCar!=null)
                {
                    
                    double dActCar=actDist-t;
                    carFound=true;
                    if(dActCar>1 && actRychlost<MAX_RYCHLOST/1.3)
                        actZrychleni=MAX_ZRYCHLENI;
                    else if(dActCar>0.05)
                        actZrychleni=-MAX_ZRYCHLENI;
                    else
                    {
                        actZrychleni=0;
                        actRychlost=0;
                    }
                }
                else if(nextDist<7)
                {
                    carFound=findCarCross(uNext, nextDist+1, actDist);
                }
            }
        }
        return carFound;
    }
    private double calcSpeed(double dSpeed, double dist)
    {
        double s=dSpeed/(dist*dist*(dist/2));
        return s;
    }
    private void move(double t)
    {
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        
        double angle=Math.toDegrees(MyMath.angle(x, y,xLast, yLast));
        if(angle!=0)
            iv.setRotate(angle);
        xLast=x;
        yLast=y;
        iv.setX(x-IMG_SIZE);
        iv.setY(y-IMG_SIZE);     
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

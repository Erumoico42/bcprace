/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
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
    private double defRychlost=0.1;
    private static final double maxRychlost=0.1;
    private Group root;
    private boolean pauza=false;
    private double defZrychleni=0;
    private static final double zrychleni=0.002;
    private Image defImg=new Image("/resources/car.png");
    public Auto(Group root, Usek u, Animace a) {
        this.a=a;
        this.root=root;
        this.u=u;
        setIv();
        setPoints(); 
    }
    private void setIv()
    {
        iv=new ImageView(defImg);
        iv.setFitWidth(64);
        iv.setFitHeight(64);
        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(!pauza)
                    pause();
                else
                    play();
                vybratAuto();
            }
        });
        root.getChildren().add(iv);
    }
    private void vybratAuto()
    {
        BcPrace.vybratAuto(this);
    }
    public void tick()
    {
        t+=defRychlost;
        
        double x=(xF+(t*ux));
        double y=(yF+(t*uy));
        move(x,y);
        setSpeed(defZrychleni);
        if(defRychlost >=maxRychlost)
        {
            defZrychleni=0;
            defRychlost=maxRychlost;
        }
        if(defRychlost<0.0005)
        {
            defZrychleni=0;
            defRychlost=0;
        }
        if(!pauza)
            detectCar();               
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
        u=u.getDalsi();
        if(u.getDalsi()!=null)
        {
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
        defRychlost+=newSpeed;
    }
    private void detectCar()
    {
        Usek uNext=u;
        double distance=0;
        boolean carFound=false;
        while(distance<10 && !carFound && (uNext=uNext.getDalsi())!=null)
        {
            distance++;
            if(uNext.getCar()!=null)
            {
                double speedNextCar=uNext.getCar().getSpeed();
                double tNextCar=uNext.getCar().getT();
                distance=distance+tNextCar-t;
                double dSpeed=defRychlost-speedNextCar;
                if(dSpeed>0 || defRychlost<maxRychlost)
                {
                    defZrychleni=-calcSpeed(dSpeed, distance);
                    if(distance<2.5)
                    {
                        defRychlost=0;
                        defZrychleni=0;
                    }
                }
                carFound=true;
            } 
        }
        if(!carFound && defRychlost<maxRychlost)
        {
            defZrychleni=zrychleni;
        }
    }
    private double calcSpeed(double dSpeed, double dist)
    {
        double s=dSpeed/(dist*dist*(dist/2));
        return s;
    }
    private void move(double x, double y)
    {
        iv.setX(x-(iv.getFitHeight()/2));
        iv.setY(y-(iv.getFitWidth()/2));
    }
    public float angle(Point p1, Point p2) {
        float angle = (float) Math.toDegrees(Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX()))+180;
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    public void pause()
    {
        pauza=true;
        defZrychleni=-zrychleni;
    }
    public void play()
    {
        pauza=false;
        defZrychleni=zrychleni;
    }
    public double getSpeed()
    {
        return defRychlost;
    }
    public double getT()
    {
        return t;
    }
}

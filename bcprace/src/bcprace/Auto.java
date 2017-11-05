/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private double actRychlost=0.1;
    private static final double maxRychlost=0.1;
    private Group root;
    private boolean pauza=false;
    private double actZrychleni=0;
    private static final double zrychleni=0.002;
    private Image defImg;
    public Auto(Group root, Usek u, Animace a) {
        this.a=a;
        this.root=root;
        this.u=u;
        setIv();
        setPoints(); 
    }
    private void setIv()
    {
        Random rnd=new Random();
        switch(rnd.nextInt(12)+1)
        {  
            case 1:
                defImg=new Image("/resources/cars/01.png");
                break; 
            case 2:
                defImg=new Image("/resources/cars/02.png");
                break; 
            case 3:
                defImg=new Image("/resources/cars/03.png");
                break;     
            case 4:
                defImg=new Image("/resources/cars/04.png");
                break;     
            case 5:
                defImg=new Image("/resources/cars/05.png");
                break; 
            case 6:
                defImg=new Image("/resources/cars/06.png");
                break; 
            case 7:
                defImg=new Image("/resources/cars/07.png");
                break; 
            case 8:
                defImg=new Image("/resources/cars/08.png");
                break; 
            case 9:
                defImg=new Image("/resources/cars/09.png");
                break; 
            case 10:
                defImg=new Image("/resources/cars/10.png");
                break; 
            case 11:
                defImg=new Image("/resources/cars/11.png");
                break; 
            case 12:
                defImg=new Image("/resources/cars/12.png");
                break; 
            default:
                defImg=new Image("/resources/car.png");
        }
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
                //vybratAuto();
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
        t+=actRychlost;
        double x=(xF+(t*ux));
        double y=(yF+(t*uy));
        move(x,y);
        setSpeed(actZrychleni);
        if(actRychlost >=maxRychlost)
        {
            actZrychleni=0;
            actRychlost=maxRychlost;
        }
        if(actRychlost<0.0005)
        {
            actZrychleni=0;
            actRychlost=0;
        }
        if(!pauza)
        {
             
            streetDetectCar();  
            crossDetectCar();  
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
        actRychlost+=newSpeed;
    }
    private void streetDetectCar()
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
                double dSpeed=actRychlost-speedNextCar;
                if(dSpeed>0 || actRychlost<maxRychlost)
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
        }
        if(!carFound && actRychlost<maxRychlost)
        {
            actZrychleni=zrychleni;
        }
    }
    private void crossDetectCar()
    {
        boolean carFound=false;
        double actDist=0;
        Usek x=u;
        while(actDist<4 && !carFound && (x=x.getDalsi())!=null)
        {
            actDist++;
            for (Usek checkPoint : x.getCheckPoints()) {
                Usek ucp=checkPoint;
                double nextDist=0;
                while(nextDist>-3 && ucp!=null)
                {
                    ucp=ucp.getDalsi();
                    nextDist--;
                }
                nextDist=0;
                while(nextDist<10 && !carFound && (ucp=ucp.getPredchozi())!=null)
                {
                    nextDist++;
                    if(ucp.getCar()!=null)
                    {
                        double speedNextCar=ucp.getCar().getSpeed();
                        double tNextCar=ucp.getCar().getT();
                        nextDist+=tNextCar;
                        actDist-=t;
                        double dNext=(nextDist+tNextCar)/speedNextCar;
                        double dAct=actDist/actRychlost;
                        if(dNext/2>dAct)
                        {
                            actZrychleni=-calcSpeed(actRychlost,actDist+2);
                            if(actDist<0.2)
                            {
                                actRychlost=0;
                                actZrychleni=0;
                            }
                        }
                        carFound=true;
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
        actZrychleni=-zrychleni;
    }
    private void play()
    {
        pauza=false;
        actZrychleni=zrychleni;
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

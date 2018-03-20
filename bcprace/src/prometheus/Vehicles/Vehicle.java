/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import java.awt.Point;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import prometheus.MyMath;
import prometheus.Usek;

/**
 *
 * @author Honza
 */
public abstract class Vehicle {
    private ImageView iv;
    private double time, totalTime;
    private Usek actualSegment;
    private final Animation animation;
    private final double MAX_SPEED=0.1, MAX_FORCE=0.002;
    private double speed=MAX_SPEED, force=MAX_FORCE;
    private double x0, y0, x1, y1, x2, y2, x3, y3, xLast, yLast, angle;
    private Rectangle controlRectangle;
    private boolean paused=false;
    private Point p0, p1, p2, p3;
    private int timeToRemove=10;
    private double imgWidth=20, rectWidth;
    private double imgHeight=20, rectHeight;
    public Vehicle(Animation animation, Usek ss) {
        actualSegment=ss;
        actualSegment.setVehicle(this);
        Point pp=actualSegment.getP12();
        actualSegment.setP12(actualSegment.getP1());
        actualSegment.setP1(pp);
        this.animation = animation;
        setPoints();
        animation.addCar(this);
        
    }
    public void setWidth(int imgWidth)
    {
        this.imgWidth=imgWidth;
    }
    public void setHeight(int imgHeight)
    {
        this.imgHeight=imgHeight;
    }
    public double getWidth()
    {
        return imgWidth;
    }
    public int getTimeToRemove()
    {
        timeToRemove--;
        return timeToRemove;
    }
    public Rectangle getRect()
    {
        return controlRectangle;
    }
    public void stop()
    {
        speed=0;
        force=0;
    }
    public void tick()
    {
        totalTime+=20;
        time+=speed;
        move(time);
        updateSpeed(force);
        checkCollAll();
        
        if(speed<0.0005){
           stop();
        }
        if(speed>MAX_SPEED){
           force=0;
           speed=MAX_SPEED;
        }
        if(time>=1){
            time-=1;
            nextSegment();
        }
    }
    public ImageView getIv()
    {
        return iv;
    }
    public double getMaxSpeed()
    {
        return MAX_SPEED;
    }
    public double getMaxForce()
    {
        return MAX_FORCE;
    }
    private void nextSegment()
    {
        actualSegment.setVehicle(null);
        if(!actualSegment.getDalsiUseky().isEmpty())
        {
            actualSegment=actualSegment.getDalsiUseky().get((int)(Math.random()*(actualSegment.getDalsiUseky().size())));

            setPoints();
            actualSegment.setVehicle(this); 
        }
        else
        {
            removeCar();  
        }
    }
    private void setPoints()
    {
        p0=actualSegment.getP1();
        p1=actualSegment.getP12();
        p2=actualSegment.getP21();
        p3=actualSegment.getP2();
        if(p1==null)
            p1=p0;
        if(p2==null)
            p2=p3;
        x0=p0.getX();
        y0=p0.getY();
        x1=3*(p1.getX()-x0);
        y1=3*(p1.getY()-y0);
        x2=3*(x0-2*p1.getX()+p2.getX());
        y2=3*(y0-2*p1.getY()+p2.getY());
        x3=3*(p1.getX()-p2.getX())+p3.getX()-x0;
        y3=3*(p1.getY()-p2.getY())+p3.getY()-y0;
        
        xLast=x0;
        yLast=y0;
    }
    public void removeCar()
    {
        actualSegment.setVehicle(null);
        animation.removeCar(this);
    }
    private void move(double t)
    {
        
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        angle=Math.toDegrees(MyMath.angle(x, y,xLast, yLast));
        if(angle!=0){
            iv.setRotate(angle);
            controlRectangle.setRotate(angle);
        }
        xLast=x;
        yLast=y;
        iv.setX(x-imgWidth);
        iv.setY(y-imgHeight); 
        controlRectangle.setX(x-rectWidth);
        controlRectangle.setY(y-rectHeight);
    }
    public abstract void newImage();
    private void checkCollAll()
    {
        List<Vehicle> auta=animation.getVehicles();
        for (int i = 0; i < auta.size(); i++) {
            Vehicle car=auta.get(i);
            if(car!=this)
            {
                if(checkColl(getRect(),car.getRect()))
                {
                    stop();
                    car.stop();
                    if(!animation.getToRemove().contains(this))
                        animation.addToRemove(this);
                    if(!animation.getToRemove().contains(car))
                        animation.addToRemove(car);
 
                }
            }
        }
    }
    private boolean checkColl(Rectangle r1, Rectangle r2)
    {
         if(Shape.intersect(r1, r2).getBoundsInLocal().getWidth()>10)
             return true;
         else
             return false;
    }
    public void setIv(ImageView iv){
        setIv(iv, new Rectangle(34,14));
    }
    public void setIv(ImageView iv, Rectangle rect)
    {
        this.iv=iv;
        controlRectangle=rect;
        rectWidth=controlRectangle.getWidth()/2;
        rectHeight=controlRectangle.getHeight()/2;
        iv.setFitWidth(imgWidth*2);
        iv.setFitHeight(imgHeight*2);
        time+=speed;
        move(time);
        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(!paused)
                    pause();
                else
                    play();
            }
        });
        prometheus.Prometheus.addNode(iv);
        
    }
    public boolean paused()
    {
        return paused;
    }
    public double getTime()
    {
        return time;
    }
    protected void pause()
    {
        paused=true;
        force=-MAX_FORCE;
    }
    protected void play()
    {
        paused=false;
        force=MAX_FORCE;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void updateSpeed(double speed) {
        this.speed += speed;
        if(this.speed<0)
            this.speed=0;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public Usek getActualSegment() {
        return actualSegment;
    }
    
}

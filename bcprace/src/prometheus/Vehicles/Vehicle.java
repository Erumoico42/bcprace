/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import prometheus.Street.HelpMath;
import prometheus.Street.StreetSegment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public abstract class Vehicle {
    private ImageView iv;
    private double time, totalTime;
    private StreetSegment actualSegment;
    private Animation animation;
    private final double MAX_SPEED=0.07, MAX_FORCE=0.001;
    private double speed=MAX_SPEED, force=MAX_FORCE;
    private double x0, y0, x1, y1, x2, y2, x3, y3, xLast, yLast, angle;
    private Rectangle controlRectangle;
    private TimerTask ttWin;
    private Timer timWin;
    private boolean paused=false;
    private Point p0, p1, p2, p3;
    private int timeToRemove=10;
    private double imgWidth=20, rectWidth;
    private double imgHeight=20, rectHeight;
    private boolean removing=false;
    private boolean isWink=false, winkRun=false;
    private VehicleImages vi;
    private Image imgAlt;
    private List<StreetSegment> street=new ArrayList<>();
    private List<StreetSegment> streetOriginal=new ArrayList<>();
    private boolean slowing=false;
    
    public Vehicle(Animation animation, StreetSegment ss) {
        actualSegment=ss;
        generateStreet(ss);
        
        streetOriginal.addAll(street);
        actualSegment.setVehicle(this);

        this.animation = animation;
        if(street.size()<2){
            removeCar();
        }
        else
        {
            setPoints();
            animation.addVehicle(this);
            winkerTimer();
        }
        
    }
    public List<StreetSegment> getOriginalStreet()
    {
        return streetOriginal;
    }
    private void generateStreet(StreetSegment start)
    {
        StreetSegment newUsek=start;
        StreetSegment lastSplit=newUsek;
        int count=0;
        List<StreetSegment> toAdd=new ArrayList<>();
        boolean stop=false;
        while(!stop && !newUsek.getDalsiUseky().isEmpty())
        {
            
            int size=newUsek.getDalsiUseky().size();
            if(size>1){
                street.addAll(toAdd);
                lastSplit=newUsek;
                toAdd.clear();
                count=0;
            }
            newUsek=newRandomUsek(newUsek);
            
            /*while(street.contains(newUsek)){
                newUsek=newRandomUsek(lastSplit);
            }*/
            if(!street.contains(newUsek) && !toAdd.contains(newUsek)){
                toAdd.add(newUsek);
            }
            else{
                toAdd.clear();
                count++;
                newUsek=lastSplit;
            }
            if(count>10)
                stop=true;
            //street.add(newUsek);
        }
        street.addAll(toAdd);
    }
    private StreetSegment newRandomUsek(StreetSegment u)
    {
        return u.getDalsiUseky().get((int)(Math.random()*(u.getDalsiUseky().size())));
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
    private void winkerTimer()
    {
        isWink=false;
        
            
        ttWin = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    if(isWink)
                    {
                        isWink=false;
                        iv.setImage(vi.getDefImg());
                    }
                    else
                    {
                        isWink=true;
                        iv.setImage(imgAlt);
                    }
                });
            }
        };
        
    }
    public void winkerRun(boolean run)
    {
        
        if(run)
        {
            if(!winkRun)
            {
                timWin=new Timer();
                winkerTimer();
                timWin.schedule(ttWin, 400,400); 
            }
            
            
        }
        else
        {
            iv.setImage(vi.getDefImg());
            if(timWin!=null)
                timWin.cancel();
            if(ttWin!=null)
                ttWin.cancel();    
            
        }
        winkRun=run;
    }
    public List<StreetSegment> getStreet()
    {
        return street;
    }
    public void nextSegment()
    {
        actualSegment.setVehicle(null);
        if(!street.isEmpty())
        {
            actualSegment=street.get(0);
            street.remove(actualSegment);
            
            StreetSegment checkWink=actualSegment;
            int dist=0;
            boolean startWink=false;
            while(!startWink && dist<10 && street.size()>dist)
            {
                
                checkWink=street.get(dist);
                dist++;
                if(checkWink.getWinkAngle()!=0){
                    startWink=true;
                    
                }
                
            }
            if(startWink)
            {
                winkerRun(true);
                if(checkWink.getWinkAngle()<0)
                    imgAlt=vi.getRightImg();
                else if(checkWink.getWinkAngle()>0)
                    imgAlt=vi.getLeftImg();
            }
  
            setPoints();
            actualSegment.setVehicle(this); 

            if(actualSegment.isStopWinker()){
                
                winkerRun(false);
            }
            //}
            //else
                //removeCar(); 
        }
        else
        {
            removeCar();  
        }
    }
    private void setPoints()
    {
        p0=actualSegment.getP0();
        
        p1=actualSegment.getP1();
        p3=actualSegment.getP3();
        p2=actualSegment.getP2();
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
        animation.removeVehicle(this);
    }
    private void move(double t)
    {
        
        double t2=t*t;
        double t3=t2*t;
        double x = (x0+(t*x1)+(t2*x2)+(t3*x3));
        double y = (y0+(t*y1)+(t2*y2)+(t3*y3)); 
        angle=Math.toDegrees(HelpMath.angle(x, y,xLast, yLast));
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
                    removing=true;
                    if(!animation.getToRemove().contains(this))
                        animation.addToRemove(this);
                    if(!animation.getToRemove().contains(car))
                        animation.addToRemove(car);
 
                }
            }
        }
    }
    public boolean removing()
    {
        return removing;
    }
    private boolean checkColl(Rectangle r1, Rectangle r2)
    {
         if(Shape.intersect(r1, r2).getBoundsInLocal().getWidth()>10)
             return true;
         else
             return false;
    }
    public void setIv(VehicleImages vi){
        setIv(vi, new Rectangle(34,14));
    }
    public void setIv(VehicleImages vi, Rectangle rect)
    {
        this.vi=vi;
        this.iv=new ImageView(vi.getDefImg());

        controlRectangle=rect;
        rectWidth=controlRectangle.getWidth()/2;
        rectHeight=controlRectangle.getHeight()/2;
        iv.setFitWidth(imgWidth*2);
        iv.setFitHeight(imgHeight*2);
        move(time);
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
        Prometheus.drawNode(iv);
        
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
        if(speed<this.speed)
            slowing=true;
        else
            slowing=false;
        this.speed = speed;
    }
    public boolean isSlowing()
    {
        return slowing;
    }
    public void updateSpeed(double speed) {
        double newSpeed=this.speed+speed;
        if(this.speed<newSpeed)
            slowing=true;
        else
            slowing=false;
        this.speed=newSpeed;
        if(newSpeed<0)
            this.speed=0;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public StreetSegment getActualSegment() {
        return actualSegment;
    }
    
}

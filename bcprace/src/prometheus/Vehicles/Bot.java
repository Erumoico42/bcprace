/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import prometheus.PolKomb;
import prometheus.Semafor;
import prometheus.Usek;

/**
 *
 * @author Honza
 */
public class Bot extends Vehicle{

    Animation a;
    private boolean carFoundStreet;
    private double distNextStreet;
    private Usek u;
    private boolean semFound;
    private double length;
    public Bot(Animation animation, Usek ss) {
        super(animation, ss);
        a=animation;
    }
    @Override
    public void tick()
    {
        u=getActualSegment();
        super.tick();
        if(!paused())
            colisionDetect();
    }
    public void setLength(double length)
    {
        this.length=length;
    }
    private void colisionDetect()
    {
        distNextStreet=666;
        carFoundStreet=findStreet(u, 0);
        if(!carFoundStreet)
        {
            setForce(getMaxForce());  
        }
        
        if(distNextStreet >1)
        {
            carFoundStreet=findSem(u,2);
            //if(!semFound)
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
                    if(sem.getStatus()==0 || sem.getStatus()==1)
                    {
                        semFound=detection(dist-getTime());
                            
                    }
                    else if(!semFound)
                    {
                        this.semFound=true;
                        setForce(getMaxForce());  
                    }
                }
                dist=d;
                for (PolKomb pk : uNext.getPK()) {
                    if(!pk.getRun())
                    {
                        semFound=detection(dist-getTime());
                            
                    }
                    else if(!semFound)
                    {
                        this.semFound=true;
                        setForce(getMaxForce());  
                    }
                }
                if(!semFound)
                {
                    semFound=findSem(uNext, d+1);
                }
            }
        }
        return semFound;
    }
    private boolean detection(double dist)
    {
        
        semFound=false;
        //dist-=getWidth();
        if(dist<3)
            setForce(-calcSpeed(getSpeed(), dist+2));
        if(dist<1.1)
        {
            setSpeed(0);
            setForce(0);
        }
        return true;
    }
    private boolean findStreet(Usek us, int d)
    {
        boolean carFound=false;
        for (Usek uNext : us.getDalsiUseky()) {
            if(d<5 && !carFound)
            {
                
                if(uNext.getVehicle()!=null && uNext.getVehicle()!=this)
                {
                    double dist=d;
                    double speedNextCar=uNext.getVehicle().getSpeed();
                    double tNextCar=uNext.getVehicle().getTime();
                    dist=dist+tNextCar-getTime()-length;
                    double dSpeed=getSpeed()-speedNextCar;
                    distNextStreet=dist; 
                    
                    if(dSpeed>0 || getSpeed()<getMaxSpeed())
                    {
                        setForce(-calcSpeed(dSpeed, dist+2));
                        if(dist<1)
                        {
                            setForce(0);
                            setSpeed(0);
                        }
                        else if(dist>3)
                            setForce(getMaxForce());
                        
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
        Vehicle nextVeh=us.getVehicle();
        for (Usek uNext : us.getPredchoziUseky()) {
            if(!carFound)
            {
                if(nextVeh==null)
                    nextVeh=uNext.getVehicle();
                if(nextVeh!=null )
                {
                    double dActVeh=actDist-getTime()-length;
                    carFound=true;
                    if((dActVeh>1 && getSpeed()<getMaxSpeed()/1.3) || nextVeh.getSpeed()<0.002)
                        setForce(getMaxForce());
                    else if(dActVeh>0.05)
                        setForce(-getMaxForce());
                    else
                    {
                        setForce(0);
                        setSpeed(0);
                    }
                }
                else if(nextDist<4)
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
    
    @Override
    public void newImage() {
        
    }
}

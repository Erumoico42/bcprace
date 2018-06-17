/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import java.util.List;
import prometheus.Police.PoliceCombin;
import prometheus.Street.StreetSegment;
import prometheus.TrafficLights.TrafficLight;



/**
 *
 * @author Honza
 */
public class Bot extends Vehicle{

    Animation a;
    private boolean carFoundStreet;
    private double distNextStreet;
    private StreetSegment actualSegment;
    private double length;
    private double defDistSpeed=666;
    private boolean ignorCP=false;
    private List<StreetSegment> lastSplit;
    private int distLastSplit=10;
    public Bot(Animation animation, StreetSegment ss) {
        super(animation, ss);
        a=animation;
    }
    @Override
    public void tick()
    {
        actualSegment=getActualSegment();
        super.tick();
        if(!paused())
            colisionDetect();
    }
    @Override
    public void nextSegment()
    {
        
        super.nextSegment();
        checkSplitStreet(getActualSegment());
        
    }
    public void setLength(double length)
    {
        this.length=length;
    }
    private void colisionDetect()
    {
        ignorCP=false;
        distNextStreet=666;
        carFoundStreet=findStreet(actualSegment, 0);
        if(distLastSplit<6)
            findSplitStreet();
        if(!carFoundStreet)
        {
            setForce(getMaxForce());  
        }
        
        if(distNextStreet >1)
        {
            carFoundStreet=findSem(actualSegment,2);
            if(!ignorCP)
                findCPCross(actualSegment, 1);
            
        }
           
        
    }
    private void checkSplitStreet(StreetSegment actSeg)
    {
        
        if(actSeg.getDalsiUseky().size()>1)
        {
            lastSplit=actSeg.getDalsiUseky();
            distLastSplit=0;
            
        }
        else if(distLastSplit<6 && lastSplit!=null )
        {
            distLastSplit++;   
        }
       
    }
    private void findSplit(int dist, StreetSegment ss)
    {
        if(dist<6){
            dist++;
            if(ss.getVehicle()!=null)
            {
                if(!ss.getVehicle().equals(this)){
                    double speedNextCar=ss.getVehicle().getSpeed();
                    double tNextCar=ss.getVehicle().getTime();
                    double distt=dist+tNextCar-length-(distLastSplit+getTime())-2;
                    if(distt>0){
                        setSpeed(newSpeed(getSpeed(), speedNextCar, distt));
                        carFoundStreet=true;
                        
                    }
                    
                }
            }
            else {
                
                for (StreetSegment streetSegment : ss.getDalsiUseky()) {
                    if(!getOriginalStreet().contains(streetSegment)){
                        findSplit(dist, streetSegment);
                    }
                }
            }
        }
    }
    private void findSplitStreet()
    {
        if(lastSplit!=null){
            for (StreetSegment streetSegment : lastSplit) {
                if(!getOriginalStreet().contains(streetSegment))
                    findSplit(1, streetSegment);
            }
        }
    }
    private boolean calcStop(double distNext, double speedNext)
    {
        double ay=((speedNext*10)/distNext);
        if(ay<0.25)
            return false;
        return true;
    }
    private boolean findSem(StreetSegment us, double d)
    {
        boolean semFounded=false;
        for (StreetSegment uNext : us.getDalsiUseky()) {
            if(d<10 && !semFounded)
            {
                double dist=d-getTime();
                for (TrafficLight sem : uNext.getSemafory()) {
                    if(sem.getStatus()==0 || sem.getStatus()==1 || sem.getStatus()==2)
                    {
                        semFounded=true;
                        setSpeed(newSpeed(getSpeed(), 0, dist));  
                        if(dist<1.1)
                            stop();
                        break;
                    }
                    else{
                        ignorCP=true;
                        break;
                    }
                    
                }
                if(!semFounded){
                    for (PoliceCombin pk : uNext.getPK()) {
                        if(!pk.getRun())
                        {
                            semFounded=true;
                            setSpeed(newSpeed(getSpeed(), 0, dist));
                            if(dist<1.1)
                                stop();
                            break;
                        }
                        
                    }
                }
                if(!semFounded)
                {
                    setForce(getMaxForce());  
                    semFounded=findSem(uNext, d+1);
                }
                else
                    break;
            }
        }
        return semFounded;
    }
    private boolean findStreet(StreetSegment us, int d)
    {
        boolean carFound=false;
        for (StreetSegment uNext : us.getDalsiUseky()) {
            if(d<10 && !carFound)
            {
                
                if(uNext.getVehicle()!=null && uNext.getVehicle()!=this)
                {
                    
                    double speedNextCar=uNext.getVehicle().getSpeed();
                    double tNextCar=uNext.getVehicle().getTime();
                    double dist=d+tNextCar-getTime()-length;
                    setSpeed(newSpeed(getSpeed(), speedNextCar, dist));
                    carFound=true;
                    break;
                }
                else
                {
                    carFound=findStreet(uNext, d+1);
                }
            }
        }
        return carFound;
    }
    
    private void findCPCross(StreetSegment us, int dist)
    {
        for (StreetSegment uNext : us.getDalsiUseky()) {
            if(uNext.getCheckPoints().isEmpty())
            {
                if(dist<4)
                    findCPCross(uNext, dist+1);
            }
            else
            {
                for (StreetSegment cp : uNext.getCheckPoints()) {
                    for (StreetSegment uN : cp.getDalsiUseky()) {
                        findCarCross(uN, 1, dist);                        
                    }
                }
                
            }
        }
    }
    private boolean findCarCross(StreetSegment us, int nextDist, double actDist)
    {
        
        boolean carFound=false;
        Vehicle nextVeh=us.getVehicle();
        for (StreetSegment uNext : us.getPredchoziUseky()) {
            if(!carFound)
            {
                if(nextVeh==null)
                    nextVeh=uNext.getVehicle();
                if(nextVeh!=null && nextVeh!=this)
                {
                    double dActVeh=actDist-getTime()-length;
                    boolean stop=calcStop(nextDist+nextVeh.getTime(), nextVeh.getSpeed());
                    if((stop || !nextVeh.isSlowing()) && (nextVeh.getSpeed()>0.01 || nextVeh.removing())){
                        carFound=true;
                        setSpeed(newSpeed(getSpeed(), 0, dActVeh));
                    }
                }
                else if(nextDist<10)
                {
                    carFound=findCarCross(uNext, nextDist+1, actDist);
                }
            }
        }
        return carFound;
    }
    private double newSpeed(double spAct, double spNext, double dist)
    {
        double maxForce=getMaxForce();
        double dSpeed=spAct-spNext;
        distNextStreet=dist;
        double ret=0;
        if(dist>=8 || dSpeed<0)
        {
            if(dSpeed<0)
                ret=spNext;
            else
                ret=spAct+maxForce;
        }
        else if(dist>=4)
        {
            double ya=getMaxSpeed()*((dist-2)/2)-spAct;
            ret=((ya/(dist+1))*dist)*4/5;
            if(ret>spAct)
                ret=spAct;
        }
        else if(dist>1.1)
        {
            double ya=getMaxSpeed()-(spAct*(5-dist)/4);
            ret=((ya/(dist+1))*dist);
            if(ret>spAct)
                ret=spAct;
        }
        else
            ret=0;
        return ret;
                
    }
    
    @Override
    public void newImage() {
        
    }
}

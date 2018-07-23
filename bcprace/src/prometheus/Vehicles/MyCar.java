/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;
import prometheus.Street.StreetSegment;
import prometheus.CarControll;
import prometheus.Player;
import prometheus.Police.PoliceCombin;
import prometheus.TrafficLights.TrafficLight;

/**
 *
 * @author Honza
 */
public class MyCar extends Vehicle{
    private StreetSegment actualSS;
    private Player player;
    public MyCar(Animation animation, StreetSegment ss) {
        super(animation, ss);
        newImage();
        
        actualSS=ss;
        setForce(0);
        setSpeed(0);
    }
    @Override
    public void newImage() {
        setIv(new VehicleImages(666));
    }
    
    @Override
    public boolean checkCollAll()
    {
        boolean crash=super.checkCollAll();
        if(crash)
            addColRun();
        return crash;
    }
    
    @Override
    public void removeCar()
    {
        super.removeCar();
        CarControll.setMyCarNull();  
    }
    
    @Override
    public void nextSegment()
    {
        checkLightsRun();
        checkPolicesRun();
        checkRightsRun();
        super.nextSegment();
    }
    public void setPlayer(Player player)
    {
        this.player=player;
        this.player.cancel();
        this.player.startTimer();
        player.addRun();
    }
    private void checkLightsRun()
    {
        for (TrafficLight tl : getActualSegment().getSemafory()) {
            if((tl.getStatus()==0 || tl.getStatus()==1 || tl.getStatus()==2) && getSpeed()>0.001)
                player.addLightsRuns();
        }
    }
    private void checkPolicesRun()
    {
        for (PoliceCombin pc : getActualSegment().getPK()) {
            if(pc.getRun() && getSpeed()>0.01)
                player.addPolicesRuns();
        }
    }
    private void checkRightsRun()
    {
        for (StreetSegment cp : getActualSegment().getCheckPoints()) {
            if(findCar(cp, 0))
                player.addRightsRuns();
        }
    }
    private boolean findCar(StreetSegment us, int nextDist)
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
                    boolean stop=Bot.calcStop(nextDist+nextVeh.getTime(), nextVeh.getSpeed());
                    if(stop)
                        return true;
                    else
                        return false;
                }
                else if(nextDist<10)
                {
                    carFound=findCar(uNext, nextDist+1);
                }
            }
        }
        return carFound;
    }
    private void addColRun()
    {
        player.addCrash();
    }
}

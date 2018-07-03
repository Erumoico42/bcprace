/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class Animation {
    private List<Vehicle> vehicles=new ArrayList<>();
    private List<Vehicle> toRemove=new ArrayList<>();
    private Timer timer=new Timer();
    private TimerTask timerTask;
    private TimerTask ttRem;
    private Timer timerRem=new Timer();
    public Animation() {
        timer();
        remTimer();;
    }
    public void addVehicle(Vehicle car)
    {
        vehicles.add(car);
    }
    public void removeVehicle(Vehicle car)
    {
        Platform.runLater(
        () -> {
            Prometheus.removeNode(car.getIv());
        });
        vehicles.remove(car);
    }
    public void stop()
    {
        timerTask.cancel();
        timer.cancel();
        ttRem.cancel();
        timerRem.cancel();
        for (Vehicle car : vehicles) {
            car.winkerRun(false);
        }
    }
    private void remTimer()
    {
        ttRem = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    for (int i = 0; i < toRemove.size(); i++) {
                        int ttr=toRemove.get(i).getTimeToRemove();
                        if(ttr%2==0)
                            toRemove.get(i).getIv().setVisible(false);
                        else
                            toRemove.get(i).getIv().setVisible(true);
                        if(ttr<=0){
                            Prometheus.removeNode(toRemove.get(i).getIv());
                            toRemove.get(i).removeCar();
                            toRemove.remove(i);
                            
                        }
                        
                    }
                });
            }
        };
        timerRem.schedule(ttRem, 500, 500);
    }
    public List<Vehicle> getToRemove()
    {
        return toRemove;
    }
    public void addToRemove(Vehicle a)
    {
        toRemove.add(a);
    }
    public List<Vehicle> getVehicles()
    {
        return vehicles;
    }
    private void timer()
    {
        timerTask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tick();
                });
            }
        };
        timer.schedule(timerTask, 20, 20);
    }
    private void tick()
    {
        for (int i = 0; i < vehicles.size(); i++) {
            vehicles.get(i).tick();
        }
    }
    public void removeAll()
    {
        List<Vehicle> toRemove=new ArrayList<>();
        toRemove.addAll(vehicles);
        for (Vehicle veh : toRemove) {
            veh.removeCar();
        }
        vehicles.clear();
    }
}

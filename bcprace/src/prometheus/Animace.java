/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

/**
 *
 * @author Honza
 */
public class Animace {

    private final Timer timer=new Timer();
    private final Timer timerRem=new Timer();
    private List<Auto> cars;
    private List<Auto> toRemove;
    private TimerTask timertask, ttRem;
    private int tim=0;
    public Animace() {
        cars=new ArrayList<Auto>();
        toRemove=new ArrayList<Auto>();
        newAnim();
         remTimer();
    } 
    private void newAnim()
    {
        timertask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tickTack();
                });
            }
        };
        timer.schedule(timertask, 20, 20);
    }
    public void stop()
    {
        ttRem.cancel();
        timertask.cancel();
        timer.cancel();
        timerRem.cancel();
        
    }
    public void addToRemove(Auto a)
    {
        toRemove.add(a);
    }
    private void tickTack()
    {
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).tick();
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
    public List<Auto> getToRemove()
    {
        return toRemove;
    }
    public void addCar(Auto car)
    {
        cars.add(car);
    }
    public void removeCar(Auto car)
    {
        cars.remove(car);
    }
    public List<Auto> getCars()
    {
        return cars;
    }
}

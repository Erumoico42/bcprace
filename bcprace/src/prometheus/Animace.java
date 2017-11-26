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
    private List<Auto> cars;
    private TimerTask timertask;
    public Animace() {
        cars=new ArrayList<Auto>();
        newAnim();
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
        timertask.cancel();
        timer.cancel();
    }
    private void tickTack()
    {
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).tick();
        }
        
    } 
    public void addCar(Auto car)
    {
        cars.add(car);
    }
    public void removeCar(Auto car)
    {
        cars.remove(car);
    }
}

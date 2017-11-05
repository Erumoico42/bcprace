/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Honza
 */
public class Animace {

    private final Timer timer=new Timer();
    private List<Auto> cars;
    private final TimerTask timertask;
    public Animace() {
        cars=new ArrayList<Auto>();
        timertask = new TimerTask() {
            @Override
            public void run(){
                tickTack();
            }
        };
        timer.schedule(timertask, 20, 20);
        
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

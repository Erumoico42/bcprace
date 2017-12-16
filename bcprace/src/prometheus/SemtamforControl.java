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
public class SemtamforControl {
    private Timer timer=new Timer();
    private TimerTask timertask;
    private List<Semafor> semafory=new ArrayList<>();
    private boolean run=false;
    public SemtamforControl() {
        newSC();
    }
    private void newSC()
    {
        timertask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tick();
                });
            }
        };
        timer.schedule(timertask, 1000, 1000);
        
    }
    public void end()
    {
        timertask.cancel();
        timer.cancel();
    }
    public void removeAll()
    {
        semafory.clear();
    }
    public List<Semafor> getSemafory()
    {
        return semafory;
    }
    private void tick()
    {
        for (Semafor semafor : semafory) {
            semafor.tick();
        }
    }

    public void newSem(Semafor s)
    {
        semafory.add(s);
    }
}

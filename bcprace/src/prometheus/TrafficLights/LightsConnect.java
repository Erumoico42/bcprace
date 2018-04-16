/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.TrafficLights;

import prometheus.LightsControll;


/**
 *
 * @author Honza
 */
public class LightsConnect {
    private final TrafficLight sem;
    private final int status;
    private int id;
    public LightsConnect(TrafficLight sem, int status) {
        this.sem = sem;
        this.status = status;
        setId(LightsControll.getLastConnectId());
    }
    public void setId(int id)
    {
        this.id=id;
        LightsControll.setLasConnectId(id+1);
    }
    public int getId()
    {
        return id;
    }
    public TrafficLight getSem() {
        return sem;
    }

    public int getStatus() {
        return status;
    }
    
}

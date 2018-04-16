/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Police;

import prometheus.PoliceControll;

/**
 *
 * @author Honza
 */
public class PoliceCombin {
    private final PoliceSide ps1, ps2;
    private boolean run=false;
    private int time=10;
    private int id;
    private final Police police;

    public PoliceCombin(Police police, PoliceSide ps1, PoliceSide ps2) {
        this.ps1 = ps1;
        this.ps2 = ps2;
        this.police=police;
        setId(PoliceControll.getLastPoliceCombinId());
    }

    public Police getPolice() {
        return police;
    }
    
    public void setId(int id) {
        this.id = id;
        PoliceControll.setLastPoliceCombinId(id+1);
    }
    
    public int getId() {
        return id;
    }
    
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        PoliceControll.changeSideDeley(time);
    }
    
    public boolean getRun() {
        return run;
    }
   
    public PoliceSide getPs1()
    {
        return ps1;
    }
    public PoliceSide getPs2()
    {
        return ps2;
    }
    public void setRun(boolean run) {
        this.run = run;
    }
    public boolean compare(PoliceSide ps1, PoliceSide ps2)
    {
        if((this.ps1.equals(ps1) && this.ps2.equals(ps2))||(this.ps2.equals(ps1) && this.ps1.equals(ps2)))
            return true;
        else
            return false;
                        
    }
}

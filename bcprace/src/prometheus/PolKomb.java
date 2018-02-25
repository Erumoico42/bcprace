/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

/**
 *
 * @author Honza
 */
public class PolKomb {
    private final PolicieStrana ps1, ps2;
    private boolean run=false;
    private int time=10;
    private int id;

    public PolKomb(PolicieStrana ps1, PolicieStrana ps2) {
        this.ps1 = ps1;
        this.ps2 = ps2;
        this.id=id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    
    public boolean getRun() {
        return run;
    }
   
    public PolicieStrana getPs1()
    {
        return ps1;
    }
    public PolicieStrana getPs2()
    {
        return ps2;
    }
    public void setRun(boolean run) {
        this.run = run;
    }
    public boolean compare(PolicieStrana ps1, PolicieStrana ps2)
    {
        if((this.ps1.equals(ps1) && this.ps2.equals(ps2))||(this.ps2.equals(ps1) && this.ps1.equals(ps2)))
            return true;
        else
            return false;
                        
    }
}

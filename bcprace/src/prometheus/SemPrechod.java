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
public class SemPrechod {
    private final Semafor sem;
    private final int status;

    public SemPrechod(Semafor sem, int status) {
        this.sem = sem;
        this.status = status;
    }
    public Semafor getSem() {
        return sem;
    }

    public int getStatus() {
        return status;
    }
    
}

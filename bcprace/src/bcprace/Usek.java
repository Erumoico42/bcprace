/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;

/**
 *
 * @author Honza
 */
public class Usek {
    private Point p1, p2;
    private Usek dalsi=null;
    private Auto car;
    public Usek() {
    }
    public Point getP1() {
        return p1;
    }
    
    public Point getP2() {
        return p2;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }
    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Usek getDalsi() {
        return dalsi;
    }

    public void setDalsi(Usek dalsi) {
        this.dalsi = dalsi;
    }

    public Auto getCar() {
        return car;
    }

    public void setCar(Auto car) {
        this.car=car;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;

/**
 *
 * @author Honza
 */
public class MyMath {
    public static double angle(Point p1, Point p2) {
        double angle = (double) (Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX()));
        if(angle<0)
            angle+=(2*Math.PI);
        return angle;
    }
    public static double length(Point p1,Point p2)
    {
        double distance = Math.sqrt(Math.pow(p1.getX()-p2.getX(),2) + Math.pow(p1.getY()-p2.getY(),2));
        return distance;
    }
}

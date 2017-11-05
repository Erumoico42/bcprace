/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class Controller {
    private Circle circle;
    public Controller(Circle circle) {
        this.circle = circle;
    }
    public void setXY(double x, double y)
    {
        circle.setCenterX(x);
        circle.setCenterY(y);

    }
    public Circle getCircle() {
        return circle;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class ConnectPoint {
    private Circle circle;
    private List<MyCurve> curves =new ArrayList<MyCurve>();
    public ConnectPoint(Circle circle) {
        this.circle=circle;
    }
    public void movePoint(double x, double y)
    {
        circle.setCenterX(x);
        circle.setCenterY(y);
        for (MyCurve curve : curves) {
            if(circle.equals(curve.getPoint0().getCircle()))
            {
                curve.movePoint0(x, y);
            }
            if(circle.equals(curve.getPoint3().getCircle()))
            {
                curve.movePoint3(x, y);
            }
        }
    }
    public void addCurve(MyCurve curve)
    {
        curves.add(curve);
    }

    public List<MyCurve> getCurves() {
        return curves;
    }
    
    public Circle getCircle()
    {
        return circle;
    }
    
}

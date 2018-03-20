/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import prometheus.Usek;

/**
 *
 * @author Honza
 */
public class MyCar extends Vehicle{
    Usek actualSS;
    public MyCar(Animation animation, Usek ss) {
        super(animation, ss);
        newImage();
        
        actualSS=ss;
        setForce(0);
        setSpeed(0);
    }
    @Override
    public void newImage() {
        setIv(new ImageView(new VehicleImages().getMyCar()));
    }

    @Override
    public void removeCar()
    {
        prometheus.Prometheus.setMyCarNull();
        super.removeCar();
    }
    
}

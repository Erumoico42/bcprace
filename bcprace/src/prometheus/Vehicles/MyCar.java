/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;
import prometheus.Street.StreetSegment;
import prometheus.CarControll;

/**
 *
 * @author Honza
 */
public class MyCar extends Vehicle{
    StreetSegment actualSS;
    public MyCar(Animation animation, StreetSegment ss) {
        super(animation, ss);
        newImage();
        
        actualSS=ss;
        setForce(0);
        setSpeed(0);
    }
    @Override
    public void newImage() {
        setIv(new VehicleImages(666));
    }

    @Override
    public void removeCar()
    {
        CarControll.setMyCarNull();
        super.removeCar();
    }
    
}

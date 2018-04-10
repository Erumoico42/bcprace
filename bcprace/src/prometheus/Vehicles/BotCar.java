/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import java.util.Random;
import javafx.scene.image.ImageView;
import prometheus.Usek;

/**
 *
 * @author Honza
 */
public class BotCar extends Bot{
    
    public BotCar(Animation animation, Usek ss) {
        super(animation, ss);
        newImage();
        
    }
    @Override
    public void newImage() {
        int ddd=new Random().nextInt(12)+1;
        super.setIv(new VehicleImages(ddd));
    }
}

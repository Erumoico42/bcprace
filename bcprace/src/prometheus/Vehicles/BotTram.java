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
public class BotTram extends Bot{

    public BotTram(Animation animation, Usek ss) {
        super(animation, ss);
        setLength(1);
        setWidth(35);
        setHeight(20);
        newImage();
        
    }

    @Override
    public void newImage() {
        setIv(new VehicleImages(42), new Rectangle(65, 17));
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Vehicles;

import java.util.Random;
import javafx.scene.image.Image;

/**
 *
 * @author Honza
 */
public class VehicleImages {
    private final Image defImg, imgA, imgL, imgR;


    public VehicleImages(int num) {
        switch(num)
        {  
            case 1:
                defImg=new Image("/resources/cars/auto-01.png");
                imgA = new Image("/resources/cars/auto-01-b.png");
                imgL = new Image("/resources/cars/auto-01-b-l.png");
                imgR = new Image("/resources/cars/auto-01-b-p.png");
                break;        
            case 2:
                defImg=new Image("/resources/cars/auto-02.png");
                imgA=new Image("/resources/cars/auto-02-b.png");
                imgL=new Image("/resources/cars/auto-02-b-l.png");
                imgR=new Image("/resources/cars/auto-02-b-p.png");
                break; 
            case 3:
                defImg=new Image("/resources/cars/auto-03.png");
                imgA=new Image("/resources/cars/auto-03-b.png");
                imgL=new Image("/resources/cars/auto-03-b-l.png");
                imgR=new Image("/resources/cars/auto-03-b-p.png");
                break;     
            case 4:
                defImg=new Image("/resources/cars/auto-04.png");
                imgA=new Image("/resources/cars/auto-04-b.png");
                imgL=new Image("/resources/cars/auto-04-b-l.png");
                imgR=new Image("/resources/cars/auto-04-b-p.png");
                break;     
            case 5:
                defImg=new Image("/resources/cars/auto-05.png");
                imgA=new Image("/resources/cars/auto-05-b.png");
                imgL=new Image("/resources/cars/auto-05-b-l.png");
                imgR=new Image("/resources/cars/auto-05-b-p.png");
                break; 
            case 6:
                defImg=new Image("/resources/cars/auto-06.png");
                imgA=new Image("/resources/cars/auto-06-b.png");
                imgL=new Image("/resources/cars/auto-06-b-l.png");
                imgR=new Image("/resources/cars/auto-06-b-p.png");
                break; 
            case 7:
                defImg=new Image("/resources/cars/auto-07.png");
                imgA=new Image("/resources/cars/auto-07-b.png");
                imgL=new Image("/resources/cars/auto-07-b-l.png");
                imgR=new Image("/resources/cars/auto-07-b-p.png");
                break; 
            case 8:
                defImg=new Image("/resources/cars/auto-08.png");
                imgA=new Image("/resources/cars/auto-08-b.png");
                imgL=new Image("/resources/cars/auto-08-b-l.png");
                imgR=new Image("/resources/cars/auto-08-b-p.png");
                break; 
            case 9:
                defImg=new Image("/resources/cars/auto-09.png");
                imgA=new Image("/resources/cars/auto-09-b.png");
                imgL=new Image("/resources/cars/auto-09-b-l.png");
                imgR=new Image("/resources/cars/auto-09-b-p.png");
                break; 
            case 10:
                defImg=new Image("/resources/cars/auto-10.png");
                imgA=new Image("/resources/cars/auto-10-b.png");
                imgL=new Image("/resources/cars/auto-10-b-l.png");
                imgR=new Image("/resources/cars/auto-10-b-p.png");
                break; 
            case 11:
                defImg=new Image("/resources/cars/auto-11.png");
                imgA=new Image("/resources/cars/auto-11-b.png");
                imgL=new Image("/resources/cars/auto-11-b-l.png");
                imgR=new Image("/resources/cars/auto-11-b-p.png");
                break; 
            case 12:
                defImg=new Image("/resources/cars/auto-12.png");
                imgA=new Image("/resources/cars/auto-12-b.png");
                imgL=new Image("/resources/cars/auto-12-b-l.png");
                imgR=new Image("/resources/cars/auto-12-b-p.png");
                break; 
            case 42:
            {
                defImg=new Image("/resources/cars/tram.png");

                imgA=new Image("/resources/cars/tram.png");
                imgL=new Image("/resources/cars/tram.png");
                imgR=new Image("/resources/cars/tram.png");
                break;
            }
            case 666:
            {
                defImg=new Image("/resources/cars/myCar.png");

                imgA=new Image("/resources/cars/myCar.png");
                imgL=new Image("/resources/cars/myCar.png");
                imgR=new Image("/resources/cars/myCar.png");
                break;
            }
            default:
                defImg=new Image("/resources/cars/auto-01.png");
                imgA=new Image("/resources/cars/auto-01-b.png");
                imgL=new Image("/resources/cars/auto-01-b-l.png");
                imgR=new Image("/resources/cars/auto-01-b-p.png");
        }
    }
    public Image getDefImg() {
        return defImg;
    }
    public Image getRightImg() {
        return imgR;
    }
    public Image getLeftImg() {
        return imgL;
    }
    public Image getAllImg() {
        return imgA;
    }
}

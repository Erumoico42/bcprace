/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.util.Random;
import javafx.scene.image.Image;

/**
 *
 * @author Honza
 */
public class TexturaAuto {
    private Image defImg, leftImg, rightImg;

    public TexturaAuto() {
        Random rnd=new Random();
        switch(rnd.nextInt(12)+1)
        {  
            case 1:
                defImg=new Image("/resources/cars/01.png");
                break; 
            case 2:
                defImg=new Image("/resources/cars/02.png");
                break; 
            case 3:
                defImg=new Image("/resources/cars/03.png");
                break;     
            case 4:
                defImg=new Image("/resources/cars/04.png");
                break;     
            case 5:
                defImg=new Image("/resources/cars/05.png");
                break; 
            case 6:
                defImg=new Image("/resources/cars/06.png");
                break; 
            case 7:
                defImg=new Image("/resources/cars/07.png");
                break; 
            case 8:
                defImg=new Image("/resources/cars/08.png");
                break; 
            case 9:
                defImg=new Image("/resources/cars/09.png");
                break; 
            case 10:
                defImg=new Image("/resources/cars/10.png");
                break; 
            case 11:
                defImg=new Image("/resources/cars/11.png");
                break; 
            case 12:
                defImg=new Image("/resources/cars/12.png");
                break; 
            default:
                defImg=new Image("/resources/cars/00.png");
        }
    }
    public Image getMyCar()
    {
        return new Image("/resources/cars/myCar.png");
    }
    public Image getDefImg() {
        return defImg;
    }
}

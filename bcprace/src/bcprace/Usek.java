/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class Usek {
    private Point p1, p2;
    private Usek dalsi=null;
    private Auto car;
    private Circle cir;
    public Usek() {
    }
    public Point getP1() {
        return p1;
    }

    public Circle getCir() {
        return cir;
    }

    public void setCir(final Circle cir) {
        this.cir = cir;
        cir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Usek actUsek=BcPrace.getActUsek();
                if(actUsek!=getUsek())
                {
                    if(actUsek!=null)
                    {
                        actUsek.getCir().setFill(Color.GREEN);
                        actUsek.getCir().setRadius(3);
                    }
                    BcPrace.vybratUsek(getUsek());
                    cir.setFill(Color.BLUE);
                    cir.setRadius(5);
                }
                else
                {
                    cir.setFill(Color.GREEN);
                    cir.setRadius(3);
                    BcPrace.vybratUsek(null);
                }
            }
        });
    }
    private Usek getUsek()
    {
        return this;
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

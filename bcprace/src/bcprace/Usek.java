/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
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
    private Usek predchozi=null;
    private Auto car;
    private Circle cir;
    private List<Usek> checkPoints=new ArrayList<Usek>();
    private Usek selectedUsek;
    private final Color DEF_COLOR=Color.GREEN;
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
                selectedUsek=BcPrace.getActUsek();
                if(t.getButton()==MouseButton.PRIMARY)
                {  
                    
                    if(selectedUsek!=getUsek())
                    {
                        if(selectedUsek!=null)
                        {
                            circleStyle(selectedUsek.getCir(),DEF_COLOR, 3);
                            deSelectCheckPoints(selectedUsek);
                        }
                        BcPrace.vybratUsek(getUsek());
                        circleStyle(cir,Color.BLUE, 5);
                        selectCheckPoints(getUsek());
                    }
                    else
                    {
                        circleStyle(cir,DEF_COLOR, 3);
                        deSelectCheckPoints(getUsek());
                        BcPrace.vybratUsek(null);
                    }
                }
                else if(t.getButton()==MouseButton.SECONDARY)
                {
                    if(selectedUsek!=null)
                    {
                        if(selectedUsek.getCheckPoints().contains(getUsek()))
                        {
                            selectedUsek.removeCheckPoint(getUsek());
                            circleStyle(cir,DEF_COLOR, 3);
                        }
                        else
                        {
                            selectedUsek.addCheckPoint(getUsek());
                            circleStyle(cir,Color.ORANGE, 5);
                        }
                    }
                }  
            }
        });
        
        
            cir.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if(cir.getFill().equals(DEF_COLOR))
                        cir.setRadius(5);
                }
            });
            cir.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if(cir.getFill().equals(DEF_COLOR))
                        cir.setRadius(3);
                }
            });
    }
    public void selectCheckPoints(Usek u)
    {
        for (Usek cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),Color.ORANGE, 5);
        }
    }
    public void deSelectCheckPoints(Usek u)
    {
        for (Usek cp : u.getCheckPoints()) {
            circleStyle(cp.getCir(),DEF_COLOR, 3);
        }
    }
    public void addCheckPoint(Usek u)
    {
        checkPoints.add(u);
    }
    public void removeCheckPoint(Usek u)
    {
        checkPoints.remove(u);
    }
    public List<Usek> getCheckPoints() {
        return checkPoints;
    }
    
    private void circleStyle(Circle c, Color col, int r)
    {
        c.setFill(col);
        c.setRadius(r);
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

    public Usek getPredchozi() {
        return predchozi;
    }

    public void setPredchozi(Usek predchozi) {
        this.predchozi = predchozi;
    }
    

    public Auto getCar() {
        return car;
    }

    public void setCar(Auto car) {
        this.car=car;
    }
    
}

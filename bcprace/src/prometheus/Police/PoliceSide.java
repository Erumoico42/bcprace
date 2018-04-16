/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Police;


import java.awt.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import prometheus.DrawControll;
import prometheus.PoliceControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class PoliceSide {
    private Circle c;
    private final Point p;
    private boolean selected=false;
    private int id;
    private final Police pol;
    
    public PoliceSide(Point p, Police pol)
    {
        this.pol=pol;
        this.p=p;
        setCircle();
        pol.addSide(this);
        setId(PoliceControll.getLastPolSideId());
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        PoliceControll.setLastPolSideId(id+1);
    }
    public void remove()
    {
        Prometheus.removeNode(c);
        
    }
    
    private void setCircle() {
        c=new Circle(p.getX(), p.getY(), 6, Color.BROWN);
        c.strokeWidthProperty().set(2);
        c.setOnMouseDragged((MouseEvent t) -> {
            p.setLocation(t.getX(), t.getY());
            c.setCenterX(t.getX());
            c.setCenterY(t.getY());
        });
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    if(!selected){
                        select(); 
                    }
                    else
                    {
                        deselect();

                    }
                }
            }
        });
        Prometheus.drawNode(c);
        DrawControll.addToHide(c);
    }
    public void select()
    {
        
        c.setFill(Color.ORANGERED);
        selected=true;
        if(pol.getPs1()==null)
            pol.setPs1(this);
        else{
            if(pol.getPs2()!=null)
                pol.getPs2().deselect();
            pol.setPs2(this);
        }
    }
    public void deselect()
    {
        c.setFill(Color.BROWN);
        selected=false;
        if(pol.getPs1()==this)
            pol.setPs1(null);
        if(pol.getPs2()==this)
            pol.setPs2(null);
    }       
    public Point getPoint()
    {
        return p;
    }
    
}

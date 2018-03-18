/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Honza
 */
public class PolicieStrana {
    private Circle c;
    private final Point p;
    private boolean selected=false;
    private int id;
    private final Policie pol;
    
    public PolicieStrana(Point p, Policie pol)
    {
        this.pol=pol;
        this.p=p;
        setCircle();
        pol.pridatStranu(this);
        Prometheus.addToHideShow(c);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        Prometheus.setLastPsId(Prometheus.getLastPsId()+1);
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
                    if(Prometheus.getActPol()!=null){
                        if(!selected){
                            select(true);
                            
                        }
                        else
                        {
                            select(false);
                            
                        }
                    }
                }
            }
        });
        Prometheus.addNode(c);
    }
    public void deSelect()
    {
        c.setFill(Color.BROWN);
        selected=false;
    }
    public void select(boolean sel)
    {
        if(!sel){
            deSelect();
            if(pol.getPs1()==this)
                pol.setPs1(null);
            if(pol.getPs2()==this)
                pol.setPs2(null);
        }
        else
        {
            c.setFill(Color.ORANGERED);
            selected=true;
            if(pol.getPs1()==null)
                pol.setPs1(this);
            else
                pol.setPs2(this);
        }
    }
    private PolicieStrana getThis()
    {
        return this;
    }
    public Point getPoint()
    {
        return p;
    }
    
}

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
    Circle c;
    Point p;
    private boolean run=false;
    private int id;
    public PolicieStrana(Point p)
    {
        this.p=p;
        setCircle();
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
                    Usek actUs=Prometheus.getActUsek();
                    if(actUs!=null){
                        if(!actUs.getPolicii().contains(getThis())){
                            actUs.addPolicii(getThis());
                            changeColor(1);
                        }
                        else{
                           actUs.getPolicii().remove(getThis());
                           changeColor(0);
                        }
                    }
                    
                }
            }
        });
        Prometheus.addNode(c);
    }
    public void changeColor(int style)
    {
        if(style==0)
            c.setFill(Color.BROWN);
        if(style==1)
            c.setFill(Color.GRAY);
    }
    private PolicieStrana getThis()
    {
        return this;
    }
    public Point getPoint()
    {
        return p;
    }
    public void setRun(boolean run)
    {
        this.run=run;
    }
    public boolean getRun()
    {
        return run;
    }
    
}

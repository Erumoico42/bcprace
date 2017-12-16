/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Honza
 */
public class Semafor {
    private int defGreen=7;
    private int derRed=7;
    private int defOrange=1;
    private ImageView iv;
    private Image red=new Image("/resources/semafor/red.png");
    private Image green=new Image("/resources/semafor/green.png");
    private Image orange2green=new Image("/resources/semafor/orange2green.png");
    private Image orange2red=new Image("/resources/semafor/orange2red.png");
    private int time=0;
    private int max=defGreen;
    private String color;
    private boolean paused=true;
    private double startX, startY, distX, distY;
    private HBox hbox;
    private final String STYLE_SELECT="-fx-border-color: red;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;";  
    private final String STYLE_SELECT_2="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    private final String STYLE_DESELECT="-fx-border-color: red;"
            + "-fx-border-width: 0;"
            + "-fx-border-style: solid;"; 
    public Semafor(String color, SemtamforControl sc) {
        iv=new ImageView();
        iv.setFitWidth(16);
        iv.setFitHeight(40);
        hbox=new HBox();
        hbox.setLayoutX(20);
        hbox.setLayoutY(20);
        sc.newSem(this);
        this.color=color;
        setColor();
        
        hbox.getChildren().add(iv);
        hbox.setOnMousePressed((MouseEvent event1) -> {
            startX = event1.getX();
            startY = event1.getY();
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
            
        });
        hbox.setOnMouseDragged((MouseEvent event1) -> {
            hbox.setLayoutX(event1.getX() - distX);
            hbox.setLayoutY(event1.getY() - distY);
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
        });
        hbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==MouseButton.PRIMARY)
                {
                    Semafor actSem=Prometheus.getActSem();
                    if(actSem==getThis())
                    {
                        deselect();
                        Prometheus.selectSem(null);
                    }else if(actSem!=null)
                    {
                        actSem.deselect();
                        Prometheus.selectSem(getThis());
                        select2();
                        
                    }
                    else{
                        Prometheus.selectSem(getThis());
                        select2();
                    }
                }
                else if(event.getButton()==MouseButton.SECONDARY)
                {
                    Usek actU=Prometheus.getActUsek();
                    if(actU!=null)
                    {
                        List<Semafor> semafory=actU.getSemafory();
                        if(semafory.contains(getThis()))
                        {
                            semafory.remove(getThis());
                            deselect();
                        }
                        else
                        {
                            Prometheus.getActUsek().addSemafor(getThis());
                            select();
                        }
                    }
                }
            }
        });
        Prometheus.addNode(hbox);
    }
    public HBox getImg()
    {
        return hbox;
    }
    public void select()
    {    
        hbox.setStyle(STYLE_SELECT);
    }
    public void deselect()
    {    
        hbox.setStyle(STYLE_DESELECT);
    }
    public void select2()
    {    
        hbox.setStyle(STYLE_SELECT_2);
    }
    public void play()
    {
        paused=false;
    }
    public void pause()
    {
        paused=true;
    }
    public void setPoz(double x, double y)
    {
        hbox.setLayoutX(x);
        hbox.setLayoutY(y);
    }
    public Point getPoz()
    {
        return new Point((int)hbox.getLayoutX(), (int)hbox.getLayoutY());
    }
    public int getTime()
    {
        return time;
    }
    public void setTime(int time)
    {
        this.time=time;
    }
    public void setColor(String color)
    {
        
        if(color.equals("green"))
        {
            color="orange2green";
                
        }else if(color.equals("orange2red"))
        {
            color="green";
        }else if(color.equals("orange2green"))
        {
            color="red";
        }else if(color.equals("red"))
        {
            color="orange2red";
        }
        this.color=color;
        setColor();
    }
    private Semafor getThis()
    {
        return this;
    }
    public boolean getPaused()
    {
        return paused;
    }
    public void pausePlay()
    {
        if(paused)
        {
            paused=false;
            
        }
        else
            paused=true;
    }
    public void tick()
    {
        if(!paused){
            time++;
            if(time==max)
            {
                setColor();
                time=0;
            }
        }
        
    }
    public void setGreen(int green)
    {
        defGreen=green;
    }
    public void setRed(int red)
    {
        derRed=red;
    }
    public void setOrange(int orange)
    {
        defOrange=orange;
    }
    public int getGreen()
    {
        return defGreen;
    }
    public int getRed()
    {
        return derRed;
    }
    public int getOrange()
    {
        return defOrange;
    }
    public String getColor()
    {
        return color;
    }
    private void setColor()
    {
        if(color.equals("green"))
        {
            color="orange2red";
            max=defOrange;
            iv.setImage(orange2red);
                
        }else if(color.equals("orange2red"))
        {
            color="red";
            max=derRed;
            iv.setImage(red);
        }else if(color.equals("orange2green"))
        {
            color="green";
            max=defGreen;
            iv.setImage(green);
        }else if(color.equals("red"))
        {
            color="orange2green";
            max=defOrange;
            iv.setImage(orange2green);
        }

    }
}

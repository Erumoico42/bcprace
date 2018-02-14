/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Honza
 */
public class Policie {
    Timer timer;
    TimerTask timertask;
    ImageView ivPolice, ivRH, ivLH;
    private Point p=new Point();
    private HBox hbox;
    Group rootPolice;
    private PolicieStrana ps1, ps2;
    int poz1=0, poz2=1;
    List<PolicieStrana> strany=new ArrayList<>();
    private double startX, startY, distX, distY;
    private final String STYLE_SELECT="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    public Policie()
    {
        imageControl();
        
        Prometheus.addNode(hbox);
        Prometheus.addNode(ivRH);
        Prometheus.addNode(ivLH);

    }
    public List<PolicieStrana> getStrany(){
        return strany;
    }
    public void move(int x, int y)
    {
        hbox.setLayoutX(x  - distX);
        hbox.setLayoutY(y - distY);
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        ivRH.setLayoutX(p.getX()+10);
        ivRH.setLayoutY(p.getY()-60);
        ivLH.setLayoutX(p.getX()+10);
        ivLH.setLayoutY(p.getY()-60);
    }
    public void pridatStranu(PolicieStrana ps)
    {
        strany.add(ps);
    }
    public void play()
    {
        if(strany.size()>1){
            ps1=strany.get(poz1);
            if(poz2==strany.size())
                poz2=0;
            ps2=strany.get(poz2);
            
            timer=new Timer();
            timertask = new TimerTask() {
                @Override
                public void run(){
                    Platform.runLater(() -> {
                        tick();
                    });
                }
            };
            timer.schedule(timertask, 5000, 5000);  
        }
    }
    public void pause()
    {
        if(timertask!=null)
            timertask.cancel();
        if(timer!=null)
            timer.cancel();
    }
    private void tick()
    {
        if(strany.size()==poz1)
            poz1=0;
        if(strany.size()==poz2)
            poz2=0;
        
        ps1.setRun(false);
        ps1=strany.get(poz1);
        
        ps2.setRun(false);
        ps2=strany.get(poz2);
        ps1.setRun(true);
        ps2.setRun(true);
        
        zmenitStrany();
        poz1++;
        poz2++;
        
    }
    public Point getPoz()
    {
        return p;
    }
    private void zmenitStrany()
    {
        ivRH.setRotate(Math.toDegrees(MyMath.angle(p, ps1.getPoint()))-90);
        ivLH.setRotate(Math.toDegrees(MyMath.angle(p, ps2.getPoint()))-90);
    }
    private void imageControl()
    {
        ivPolice=new ImageView(new Image("resources/police/head.png"));
        ivRH=new ImageView(new Image("resources/police/handR.png"));
        ivLH=new ImageView(new Image("resources/police/handL.png"));
        ivLH.setFitWidth(10);
        ivLH.setFitHeight(150);
        ivRH.setFitWidth(10);
        ivRH.setFitHeight(150);

        
        ivPolice.setFitWidth(40);
        ivPolice.setFitHeight(30);
        hbox=new HBox();
        hbox.setLayoutX(30);
        hbox.setLayoutY(30);
        hbox.setStyle(STYLE_SELECT);
        ivRH.setLayoutX(40);
        ivRH.setLayoutY(-25);
        ivLH.setLayoutX(40);
        ivLH.setLayoutY(-25);
            
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        hbox.getChildren().add(ivPolice);
        hbox.setOnMousePressed((MouseEvent event1) -> {
            startX = event1.getX();
            startY = event1.getY();
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
            Policie actPol=Prometheus.getActPoliceMan();
            if(actPol==getThis())
            {
                hbox.setStyle(null);
                Prometheus.setPoliceMan(null);
            }
            else
            {
                hbox.setStyle(STYLE_SELECT);
                Prometheus.setPoliceMan(getThis());
            }
        });
        hbox.setOnMouseDragged((MouseEvent event1) -> {
            move((int)event1.getX(), (int)event1.getY());
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
        });

    }
    private Policie getThis()
    {
        return this;
    }
}

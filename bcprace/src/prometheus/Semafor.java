/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Honza
 */
public class Semafor {
    private int status=0;
    private List<SemPrechod> controlRed=new ArrayList<>();
    private List<SemPrechod> controlGreen=new ArrayList<>();
    private Image red=new Image("resources/semafor/red.png");
    private Image green=new Image("resources/semafor/green.png");
    private Image orange2green=new Image("/resources/semafor/orange2green.png");
    private Image orange2red=new Image("resources/semafor/orange2red.png");
    private final String STYLE_SELECT_SEC="-fx-border-color: red;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;";  
    private final String STYLE_SELECT_PRIM="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    private final String STYLE_DEF="-fx-border-color: red;"
            + "-fx-border-width: 0;"
            + "-fx-border-style: solid;"; 
    private ImageView semImg=new ImageView();
    private HBox hbox;
    private double startX, startY, distX, distY;
    
    private int time=0;
    private int defGreen=5;
    private int defRed=5;
    private int defOrange=1;
    private int max=2;
    private boolean runRed=false, runGreen=false;
    private SemaforControl sc;
    private int id;
    public Semafor(int id, SemaforControl sc) {
        this.id=id;
        this.sc=sc;
        setStatus(status, false);
        imageControl();
        Prometheus.addSemafor(this);
    }
    public int getID()
    {
        return id;
    }
    public Point getPoz()
    {
        return new Point((int)hbox.getLayoutX(), (int)hbox.getLayoutY());
    }
    public int getTime()
    {
        return time;
    }
    public void setID(int id)
    {
        this.id=id;
    }
    private void imageControl()
    {
        semImg.setFitWidth(12);
        semImg.setFitHeight(30);
        hbox=new HBox();
        hbox.setLayoutX(30);
        hbox.setLayoutY(30);
        hbox.getChildren().add(semImg);
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
                if(event.getButton()==MouseButton.PRIMARY){
                    Semafor act=sc.getSemPrim();    
                    if(act!=getThis())
                    {
                        
                        if(act!=null)
                        {
                            act.setStyle(0);
                        }
                        
                        sc.selectSemPrim(getThis());
                        setStyle(1);
                    }
                    else
                    {
                        
                        setStyle(0);
                        sc.selectSemPrim(null);
                    } 
                }
                
                if(event.getButton()==MouseButton.SECONDARY)
                {
                    Usek actUs=Prometheus.getActUsek();
                    Semafor act=sc.getSemSec();
                    if(act!=getThis())
                    {
 
                        if(act!=null)
                        {
                            act.setStyle(0);
                        }
                        if(actUs!=null)
                            actUs.addSemafor(getThis());
                        sc.selectSemSec(getThis());
                        setStyle(2);
                    }
                    else
                    {
                        if(actUs!=null)
                            actUs.getSemafory().remove(getThis());
                        setStyle(0);
                        sc.selectSemSec(null);

                    }
                }
            }
        });
    }
    public void setStyle(int i)
    {
        if(i==0)
        {
            hbox.setStyle(STYLE_DEF);
        }
        if(i==1)
        {
            hbox.setStyle(STYLE_SELECT_PRIM);
        }
        if(i==2)
        {
            hbox.setStyle(STYLE_SELECT_SEC);
        }
    }
    private Semafor getThis(){
        return this;
    }
    public HBox getIMG()
    {
        return hbox;
    }
    public void setTime(int time)
    {
        this.time=time;
    }
    public void moveIMG(Point p)
    {
        hbox.setLayoutX(p.getX());
        hbox.setLayoutY(p.getY());
    }
    public void addControlRed(Semafor sem, int stat)
    {
        controlRed.add(new SemPrechod(sem, stat));  
        sc.addtLastIDPrechod();
    }
    public List<SemPrechod> getControlRed()
    {
        return controlRed;
    }
    public void addControlGreen(Semafor sem, int stat)
    {
        controlGreen.add(new SemPrechod(sem, stat));   
    }
    public List<SemPrechod> getControlGreen()
    {
        return controlGreen;
    }
    private void changeControlRed()
    {
        for (SemPrechod pre : controlRed) {
            pre.getSem().setStatus(pre.getStatus(), true);
        }
    }
    private void changeControlGreen()
    {
        for (SemPrechod pre : controlGreen) {
            pre.getSem().setStatus(pre.getStatus(), true);
        }
    }
    public void enableChangeRed(boolean en)
    {
        runRed=en;
    }
    public void enableChangeGreen(boolean en)
    {
        runGreen=en;
    }
    public boolean getEnableRed()
    {
        return runRed;
    }
    public boolean getEnableGreen()
    {
        return runGreen;
    }
    public void setStatus(int status, boolean force)
    { 
        if(status==0 || status==3 || force  || ((status==1 && runRed) || (status==2 && runGreen))){
            time=0;
            this.status=status;
            switch(status)
            {
                case 0:
                {
                    //red
                    semImg.setImage(red);
                    max=defRed;
                    break;
                }
                case 1: 
                {
                    //orange to red
                    changeControlRed();
                    semImg.setImage(orange2red);
                    max=defOrange;
                    break;
                }
                case 2:
                {
                    //orange to green
                    changeControlGreen();  
                    semImg.setImage(orange2green);
                    max=defOrange;
                    break;
                }
                case 3:
                {
                    //green
                    semImg.setImage(green);
                    max=defGreen;
                    break;
                }
                default:        
                {
                    System.out.println("error set status");
                    break;
                }
            }
            
        }
    }
    private int changeStatus()
    {
        switch(status)
        {
            case 0:
            {
                return 2;
            }
            case 1:
            {
                return 0;
            }
            case 2:
            {
                return 3;
            }
            case 3:
            {
                return 1;
            }
            default:
            {
                System.out.println("error");
                return 0;
            }
        }
    }
    public int getStatus()
    {
        return status;
    }
    public void setTimeRed(int timeRed)
    {
        defRed=timeRed;
    }
    public void setTimeGreen(int timeGreen)
    {
        defGreen=timeGreen;
    }
    public int getTimeRed()
    {
        return defRed;
    }
    public int getTimeGreen()
    {
        return defGreen;
    }
    public void tick()
    {
        if(time==max)
            setStatus(changeStatus(), false);
        time++;
    }
}

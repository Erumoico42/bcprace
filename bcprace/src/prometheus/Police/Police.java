/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Police;

import prometheus.Street.HelpMath;
import prometheus.Street.StreetSegment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import prometheus.DrawControll;
import prometheus.PoliceControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class Police {
    private Timer timer;
    private TimerTask timertask;
    private ImageView ivPolice, ivRH, ivLH;
    private boolean selected=false;
    private Point p=new Point();
    private HBox hbox;
    private PoliceSide ps1, ps2;
    int poz1=0;
    private List<PoliceSide> sides=new ArrayList<>();
    private List<PoliceCombin> combinations=new ArrayList<>();
    private List<StreetSegment> polSegments=new ArrayList<>();
    private int time=666, maxTime=0;
    private int deley=10;
    private PoliceCombin selectedPK=null;
    private double startX, startY, distX, distY;
    private boolean wait=false;
    private final Image handL=new Image("resources/police/handL.png");
    private final Image handR=new Image("resources/police/handR.png");
    private final Image handLup=new Image("resources/police/handLup.png");
    private final Image handRup=new Image("resources/police/handRup.png");
    private final Image selectedHead=new Image("resources/police/head_selected.png");
    private final Image choosedHead=new Image("resources/police/head_choosed.png");
    private final Image defHead=new Image("resources/police/head.png");
    public Police()
    {
        imageControl();
        Prometheus.drawNode(ivRH, ivLH, hbox);
        select();
    }

    public int getDeley() {
        return deley;
    }

    public void setDeley(int waitTime) {
        this.deley = waitTime;
        PoliceControll.changeDeley(waitTime);
    }
    public boolean checkExist()
    {
        boolean exist=false;
        for (PoliceCombin polKomb : combinations) {
            exist=polKomb.compare(ps1, ps2);
            if(exist){
                setSelectedKombi(polKomb);
                break;
            }
        }
        return exist;
    }
    public void createKomb()
    {        
        PoliceCombin pk=new PoliceCombin(this, ps1, ps2);
        setSelectedKombi(pk);
        combinations.add(pk);
        guiControl();
    }
    public void addKomb(PoliceCombin pk)
    {
        combinations.add(pk);
    }
    public List<PoliceCombin> getPk()
    {
        return combinations;
    }
    public void removeKomb()
    {
        for (PoliceCombin polKomb : combinations) {
            if(polKomb.compare(ps1, ps2)){
                combinations.remove(polKomb);
                break;
            }
        }
        guiControl();
    }
    public PoliceCombin getSelectedKombi() {
        return selectedPK;
    }

    public void setSelectedKombi(PoliceCombin actPK) {
        this.selectedPK = actPK;
        
    }
    public void remove()
    {
        Prometheus.removeNode(ivRH, ivLH, hbox);
        
        for (StreetSegment polSegment : polSegments) {
            for (PoliceCombin combination : combinations) {
                if(polSegment.getPK().contains(combination))
                    polSegment.removePK(combination);
            }
            
        }
        for (PoliceSide policeSide : sides) {
            policeSide.remove();
        }
        sides.clear();
    }
    private void guiControl()
    {
        if(ps1==null || ps2==null){
            PoliceControll.enableConnectSides(false);
            PoliceControll.showSideDeley(false);
        }
        else{
            PoliceControll.enableConnectSides(true);
            if(checkExist()){
                
                PoliceControll.changeSideDeley(selectedPK.getTime());
                PoliceControll.changeCombi(false);
                PoliceControll.showSideDeley(true);
            }
            else{
                PoliceControll.changeCombi(true);
                PoliceControll.showSideDeley(false);
            }
        }
    }

    public PoliceSide getPs1() {
        return ps1;
    }

    public void setPs1(PoliceSide ps1) {
        
        this.ps1 = ps1;
        guiControl();
    }

    public PoliceSide getPs2() {
        return ps2;
        
        
    }

    public void setPs2(PoliceSide ps2) {
        this.ps2 = ps2;
        guiControl();
    }
    
    public List<PoliceSide> getStrany(){
        return sides;
    }
    public void move(int x, int y)
    {
        hbox.setLayoutX(x  - distX);
        hbox.setLayoutY(y - distY);
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        ivLH.setLayoutX(p.getX());
        ivLH.setLayoutY(p.getY()-40);
        ivRH.setLayoutX(p.getX()+15);
        ivRH.setLayoutY(p.getY()-40);

    }
    public void addSide(PoliceSide ps)
    {
        sides.add(ps);
    }
    public void removeSide(PoliceSide ps)
    {
        sides.remove(ps);
        
    }
    public void play()
    {
        if(!combinations.isEmpty()){
            timer=new Timer();
            timertask = new TimerTask() {
                @Override
                public void run(){
                    Platform.runLater(() -> {
                        tick();
                    });
                }
            };
            timer.schedule(timertask, 1000, 1000);  
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
       
        if(time>=maxTime)
        {
            wait=true;
            ivRH.setRotate(0);
            ivLH.setRotate(0);
            ivRH.setImage(handRup);
            ivLH.setImage(handLup);
            PoliceCombin act=combinations.get(poz1);
            act.setRun(false);
            if(deley+maxTime<=time)
            {
                ivRH.setImage(handR);
                ivLH.setImage(handL);
                wait=false;
                if(poz1<combinations.size()-1)
                    poz1++;
                else
                    poz1=0;
                act=combinations.get(poz1);
                zmenitStrany(act.getPs1().getPoint(), act.getPs2().getPoint());
                maxTime=act.getTime();
                act.setRun(true);
                time=0;
            }
        }
        time++;
    }
    
    public Point getPoz()
    {
        return p;
    }
    private void zmenitStrany(Point ps1, Point ps2)
    {
        ivRH.setRotate(Math.toDegrees(HelpMath.angle(p, ps1))-90);
        ivLH.setRotate(Math.toDegrees(HelpMath.angle(p, ps2))-90);
    }
    private void imageControl()
    {
        ivPolice=new ImageView(defHead);
        ivRH=new ImageView(handRup);
        ivLH=new ImageView(handLup);
        ivLH.setFitWidth(15);
        ivLH.setFitHeight(120);
        ivRH.setFitWidth(15);
        ivRH.setFitHeight(120);

        
        ivPolice.setFitWidth(30);
        ivPolice.setFitHeight(30);
        hbox=new HBox();
        hbox.setLayoutX(40);
        hbox.setLayoutY(45);

        ivLH.setLayoutX(40);
        ivLH.setLayoutY(5);
        ivRH.setLayoutX(55);
        ivRH.setLayoutY(5);
            
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        hbox.getChildren().add(ivPolice);
        hbox.setOnMousePressed((MouseEvent event1) -> {
            if(event1.getButton()==MouseButton.PRIMARY){
                startX = event1.getX();
                startY = event1.getY();
                distX = startX - hbox.getLayoutX();
                distY = startY - hbox.getLayoutY();
                if(selected)
                {
                    deselect();
                }
                else
                {
                    Police actPol=PoliceControll.getActualPolice();
                    if(actPol!=null)
                        actPol.deselect();
                    select();
                }
            }
            if(event1.getButton()==MouseButton.SECONDARY){
                StreetSegment actualSS=DrawControll.getActualStreetSegment();
                
                if(actualSS!=null && PoliceControll.getActualPolice()!=null && PoliceControll.getActualPolice().getSelectedKombi()!=null)
                {
                    boolean exist=false;
                    for (PoliceCombin polKomb : actualSS.getPK()) {
                        if(polKomb==selectedPK){
                            polSegments.remove(actualSS);
                            actualSS.removePK(selectedPK);
                            exist=true;
                            unchoose();
                            break;
                        }
                    }
                    if(!exist){
                        choose();
                        polSegments.add(actualSS);
                        actualSS.addPK(selectedPK);
                        
                    }
                }
            }
        });
        hbox.setOnMouseDragged((MouseEvent event1) -> {
            move((int)event1.getX(), (int)event1.getY());
            distX = startX - hbox.getLayoutX();
            distY = startY - hbox.getLayoutY();
        });

    }
    private void select()
    {
        selected=true;
        ivPolice.setImage(selectedHead);
        PoliceControll.setActualPolice(this);
        PoliceControll.enableSelectedPolice(true);
        PoliceControll.changeDeley(deley);
    }
    public void choose()
    {
        ivPolice.setImage(choosedHead);
    }
    public void unchoose()
    {
        if(selected)
            ivPolice.setImage(selectedHead);
        else
            ivPolice.setImage(defHead);
    }
    private void deselect()
    {
        selected=false;
        ivPolice.setImage(defHead);
        PoliceControll.enableSelectedPolice(false);
        PoliceControll.setActualPolice(null);
        if(ps1!=null)
            ps1.deselect();
        if(ps2!=null)
            ps2.deselect();
    }
    private Police getThis()
    {
        return this;
    }
}

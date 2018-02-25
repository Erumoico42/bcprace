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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author Honza
 */
public class Policie {
    private Timer timer;
    private TimerTask timertask;
    private ImageView ivPolice, ivRH, ivLH;
    private Point p=new Point();
    private HBox hbox;
    private PolicieStrana ps1, ps2;
    int poz1=0;
    private List<PolicieStrana> strany=new ArrayList<>();
    private List<PolKomb> kombinace=new ArrayList<>();
    private int time=666, maxTime=0, dealeyTime=10;
    private PolKomb selectedPK=null;
    private double startX, startY, distX, distY;
    private boolean dealey=false;
    private final String STYLE_SELECT="-fx-border-color: blue;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;"; 
    public Policie()
    {
        imageControl();
        Prometheus.addNode(ivRH);
        Prometheus.addNode(ivLH);
        Prometheus.addNode(hbox);
    }

    public int getDealey() {
        return dealeyTime;
    }

    public void setDealey(int dealey) {
        this.dealeyTime = dealey;
    }
    private boolean checkExist()
    {
        boolean exist=false;
        
        for (PolKomb polKomb : kombinace) {
            exist=polKomb.compare(ps1, ps2);
            if(exist){
                setSelPK(polKomb);
                break;
            }
        }
        return exist;
    }
    public void createKomb()
    {
        
        if(!checkExist()){
            int id=Prometheus.getLastIdPk()+1;
            PolKomb pk=new PolKomb(ps1, ps2);
            pk.setId(id);
            Prometheus.setlastIdPk(id);
            setSelPK(pk);
            kombinace.add(pk);
            Prometheus.checkKombiPol(false);
        }
    }
    public void addKomb(PolKomb pk)
    {
        kombinace.add(pk);
    }
    public List<PolKomb> getPk()
    {
        return kombinace;
    }
    public void removeKomb()
    {
        for (PolKomb polKomb : kombinace) {
            if(polKomb.compare(ps1, ps2)){
                kombinace.remove(polKomb);
                Prometheus.checkKombiPol(true);
                break;
            }
        }
    }
    public PolKomb getActPK() {
        return selectedPK;
    }

    public void setSelPK(PolKomb actPK) {
        this.selectedPK = actPK;
        if(selectedPK!=null)
            Prometheus.setPolTime(selectedPK.getTime());
        
    }
    private void guiControl()
    {
        if(ps1==null || ps2==null)
            Prometheus.setVisPol(false);
        else{
            if(checkExist()){
                Prometheus.checkKombiPol(false);
            }
            else
                Prometheus.checkKombiPol(true);
            Prometheus.setVisPol(true);
        }
    }

    public PolicieStrana getPs1() {
        return ps1;
    }

    public void setPs1(PolicieStrana ps1) {
        
        this.ps1 = ps1;
        if(ps1==null || ps2==null)
            setSelPK(null);
        guiControl();
    }

    public PolicieStrana getPs2() {
        return ps2;
        
    }

    public void setPs2(PolicieStrana ps2) {
        if(this.ps2!=null)
        {
            this.ps2.deSelect();
        }
        this.ps2 = ps2;
        guiControl();
        if(ps1==null || ps2==null)
            setSelPK(null);
    }
    
    public List<PolicieStrana> getStrany(){
        return strany;
    }
    public void move(int x, int y)
    {
        hbox.setLayoutX(x  - distX);
        hbox.setLayoutY(y - distY);
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        ivLH.setLayoutX(p.getX()+5);
        ivLH.setLayoutY(p.getY()-40);
        ivRH.setLayoutX(p.getX()+20);
        ivRH.setLayoutY(p.getY()-40);

    }
    public void pridatStranu(PolicieStrana ps)
    {
        strany.add(ps);
    }
    public void play()
    {
        System.out.println(kombinace.size());
        if(kombinace.size()>1){
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
            dealey=true;
            ivRH.setRotate(0);
            ivLH.setRotate(0);
            if(dealeyTime+maxTime<=time)
            {
                dealey=false;
                if(poz1<kombinace.size()-1)
                    poz1++;
                else
                    poz1=0;
                PolKomb act=kombinace.get(poz1);
                zmenitStrany(act.getPs1().getPoint(), act.getPs2().getPoint());
                maxTime=act.getTime();
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
        ivRH.setRotate(Math.toDegrees(MyMath.angle(p, ps1))-90);
        ivLH.setRotate(Math.toDegrees(MyMath.angle(p, ps2))-90);
    }
    private void imageControl()
    {
        ivPolice=new ImageView(new Image("resources/police/head.png"));
        ivRH=new ImageView(new Image("resources/police/handR.png"));
        ivLH=new ImageView(new Image("resources/police/handL.png"));
        ivLH.setFitWidth(15);
        ivLH.setFitHeight(120);
        ivRH.setFitWidth(15);
        ivRH.setFitHeight(120);

        
        ivPolice.setFitWidth(40);
        ivPolice.setFitHeight(40);
        hbox=new HBox();
        hbox.setLayoutX(40);
        hbox.setLayoutY(45);
        hbox.setStyle(STYLE_SELECT);
        ivLH.setLayoutX(45);
        ivLH.setLayoutY(5);
        ivRH.setLayoutX(60);
        ivRH.setLayoutY(5);
            
        p.setLocation(hbox.getLayoutX(), hbox.getLayoutY());
        hbox.getChildren().add(ivPolice);
        hbox.setOnMousePressed((MouseEvent event1) -> {
            if(event1.getButton()==MouseButton.PRIMARY){
                startX = event1.getX();
                startY = event1.getY();
                distX = startX - hbox.getLayoutX();
                distY = startY - hbox.getLayoutY();
                Policie actPol=Prometheus.getActPol();
                if(actPol==getThis())
                {
                    hbox.setStyle(null);
                    Prometheus.setVisPol(false);
                    Prometheus.setActPol(null);
                    if(ps1!=null)
                        ps1.select(false);
                    if(ps2!=null)
                        ps2.select(false);
                }
                else
                {
                    hbox.setStyle(STYLE_SELECT);
                    Prometheus.setActPol(getThis());
                }
            }
            if(event1.getButton()==MouseButton.SECONDARY){
                if(Prometheus.getActUsek()!=null)
                {
                    Usek actUs=Prometheus.getActUsek();
                    boolean exist=false;
                    for (PolKomb polKomb : actUs.getPK()) {
                        if(polKomb==selectedPK){
                            actUs.getPK().remove(selectedPK);
                            exist=true;
                            break;
                        }
                    }
                    if(!exist)
                        actUs.addPK(selectedPK);
                }
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

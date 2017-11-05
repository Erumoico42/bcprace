/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bcprace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.stage.Stage;

/**
 *
 * @author Honza
 */
public class BcPrace extends Application {
    private Group root;
    private List<MyCurve> curves=new ArrayList<MyCurve>();
    private List<Circle> circleUseky=new ArrayList<Circle>();
    private List<Usek> useky;
    private ConnectPoint actCp;
    private List<Button> addCarBtns=new ArrayList<Button>();
    private Animace a;
    private static Auto actAuto;
    private MyCurve actCurve;
    private static Usek actUsek;
    private int pozYAdd=0;
    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Project");
        primaryStage.setScene(scene);
        primaryStage.show();
        initGUI();
        a=new Animace();
    }
    private void toSegments()
    {
        root.getChildren().removeAll(circleUseky);
        circleUseky.clear();
        Rozdeleni r=new Rozdeleni(curves);
        useky=r.getUseky();
        for (Usek u : useky) {
            Usek up=u.getDalsi();
            Circle c;
            while(up!=null)
            {
                c=new Circle(up.getP1().getX(), up.getP1().getY(), 3, Color.GREEN);
                up.setCir(c);
                circleUseky.add(c);
                root.getChildren().add(c);
                up=up.getDalsi();
            }
        }
    }
    private void initGUI()
    {
        Canvas canvas=new Canvas(800,600);
        canvas.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                if(actCp==null)
                    newConnectPoint(t);
                newCurve(t);
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                actCp.movePoint(t.getX(), t.getY());
                toSegments();
            }
        });
        
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent t) {
                if(actAuto!=null)
                {
                    if(t.getDeltaY()>0)
                        actAuto.setSpeed(0.005);
                    else
                        actAuto.setSpeed(-0.005);
                }
            }
        });
        
        root.getChildren().addAll(canvas);
    }
    public static void vybratAuto(Auto auto)
    {
        actAuto=auto;
    }
    public static void vybratUsek(Usek usek)
    {
        actUsek=usek;
    }
    public static Usek getActUsek()
    {
        return actUsek;
    }
    private void newConnectPoint(MouseEvent t)
    {
        Circle circle=new Circle(t.getX(), t.getY(), 5, Color.BLACK);
        final ConnectPoint cp=new ConnectPoint(circle);
        circle.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                cp.movePoint(t.getX(), t.getY());
                toSegments();
                if(actCp==cp)
                {
                    actCurve=null;
                    actCp=null;
                }
                else
                {
                    if(curves.contains(actCurve))
                    {
                        actCurve=cp.getCurves().get(0);
                        actCp=cp;
                    }
                    else
                    {
                        actCurve=null;
                        actCp=null;
                    }
                }
            }
        });
        circle.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                if(actCp==cp)
                {
                    actCurve=null;
                    actCp=null;
                }
                else
                {
                    if(curves.contains(actCurve))
                    {
                        actCurve=cp.getCurves().get(0);
                        actCp=cp;
                    }
                    else
                    {
                        actCurve=null;
                        actCp=null;
                    }
                }
            }
        });
        root.getChildren().add(circle);
        actCp=cp;
    }
    private void newController(final int i, MouseEvent t, final MyCurve curve)
    {
        Circle circle=new Circle(t.getX(), t.getY(), 4, Color.RED);
        final Controller c=new Controller(circle);
        if(i==0)
            curve.setPoint1(c);
        else
            curve.setPoint2(c);
        circle.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                actCurve=curve;
                c.setXY(t.getX(), t.getY());
                toSegments();
                if(i==0)
                    curve.movePoint1(t.getX(), t.getY());
                else
                    curve.movePoint2(t.getX(), t.getY());  
            }
        });
        root.getChildren().add(circle);
    }
    private void newCurve(MouseEvent t)
    {
        Point p=new Point((int)actCp.getCircle().getCenterX(),(int)actCp.getCircle().getCenterY());
        CubicCurve cubicCurve=new CubicCurve(
                p.getX(), p.getY(), 
                p.getX(), p.getY(), 
                t.getX(), t.getY(), 
                t.getX(), t.getY());
        cubicCurve.setStrokeWidth(1);
        cubicCurve.setStroke(Color.BLACK);
        cubicCurve.setFill(null);
        MyCurve mc=new MyCurve(cubicCurve, actCp);
        actCp.addCurve(mc);
        newConnectPoint(t);
        actCp.addCurve(mc);
        mc.setPoint3(actCp);
        newController(0, t, mc);
        newController(1, t, mc);
        if(actCurve==null)
        {
            curves.add(mc);
            final Button addCar=new Button("Přidat auto");
            addCar.setLayoutX(50);
            addCar.setLayoutY((pozYAdd+1)*25);
            addCarBtns.add(addCar);
            addCar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    final Auto car=new Auto(root, useky.get(addCarBtns.indexOf(addCar)), a);
                    a.addCar(car);   
                }
            });
            pozYAdd++;
            root.getChildren().add(addCar);
        }
        else
            actCurve.setNext(mc);
        actCurve=mc;
        root.getChildren().remove(actCp.getCircle());
        root.getChildren().add(actCp.getCircle());
        root.getChildren().add(0, cubicCurve);
        actCp.movePoint(t.getX(), t.getY());
        toSegments();
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
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
    private List<CubicCurve> curves=new ArrayList<CubicCurve>();
    private List<MyCurve> myCurves=new ArrayList<MyCurve>();
    private List<Circle> segmentPoints=new ArrayList<Circle>();
    private List<Usek> useky;
    private ConnectPoint actualCp;
    private List<Auto> cars=new ArrayList<Auto>();
    private Animace a;
    private static Auto vybraneAuto;
    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("V1");
        primaryStage.setScene(scene);
        primaryStage.show();
        initGUI();
        a=new Animace();
    }
    private void toSegments()
    {
        root.getChildren().removeAll(segmentPoints);
        Rozdeleni r=new Rozdeleni(curves);
        useky=r.getUseky();
        for (Usek u : useky) {
            Circle c=new Circle(u.getP1().getX(), u.getP1().getY(), 2, Color.GREEN);
            segmentPoints.add(c);
            root.getChildren().add(c);
        }
        if(useky.size()>1)
        {
            Circle c=new Circle(useky.get(useky.size()-1).getP2().getX(), useky.get(useky.size()-1).getP2().getY(), 2, Color.GREEN);
            segmentPoints.add(c);
            root.getChildren().add(c);
        }
    }
    private void initGUI()
    {
        Canvas canvas=new Canvas(800,600);
        canvas.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                if(actualCp==null)
                    newConnectPoint(t);
                newCurve(t);
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                actualCp.movePoint(t.getX(), t.getY());
                toSegments();
            }
        });
        
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent t) {
                if(t.getDeltaY()>0)
                    vybraneAuto.setSpeed(0.005);
                else
                    vybraneAuto.setSpeed(-0.005);
            }
        });
        
        Button addCar=new Button("Přidat auto");
        addCar.setLayoutX(110);
        addCar.setLayoutY(50);
        addCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                final Auto car=new Auto(root, useky.get(0), a);
                a.addCar(car);
                vybraneAuto=car;
            }
        });
        root.getChildren().addAll(canvas, addCar);
    }
    public static void vybratAuto(Auto auto)
    {
        vybraneAuto=auto;
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
            }
        });
        circle.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t) {
                //actualCp=cp;
            }
        });
        root.getChildren().add(circle);
        actualCp=cp;
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
                toSegments();
                c.setXY(t.getX(), t.getY());
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
        Point p=new Point((int)actualCp.getCircle().getCenterX(),(int)actualCp.getCircle().getCenterY());
        CubicCurve cubicCurve=new CubicCurve(
                p.getX(), p.getY(), 
                p.getX(), p.getY(), 
                t.getX(), t.getY(), 
                t.getX(), t.getY());
        cubicCurve.setStrokeWidth(1);
        cubicCurve.setStroke(Color.BLACK);
        cubicCurve.setFill(null);
        curves.add(cubicCurve);
        MyCurve mc=new MyCurve(cubicCurve, actualCp);
        actualCp.addCurve(mc);
        newConnectPoint(t);
        actualCp.addCurve(mc);
        mc.setPoint3(actualCp);
        newController(0, t, mc);
        newController(1, t, mc);
        root.getChildren().remove(actualCp.getCircle());
        root.getChildren().add(actualCp.getCircle());
        root.getChildren().add(0, cubicCurve);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

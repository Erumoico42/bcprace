/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import prometheus.Vehicles.Vehicle;

/**
 *
 * @author Honza
 */
public class Statistiky {
    private Group root=new Group();
    private Scene scene=new Scene(root, 500,400);
    private Stage stage=new Stage();
    private int carCount=0;
    private double maxSpeed=0;
    private double maxLength=0;
    private double maxTime=0;
    private Label countLab, maxSpeedLab, maxTimeLab, actCountLab, countLabData, 
            maxSpeedLabData, maxTimeLabData, actCountLabData, maxLenghtLabData, maxLenghtLab;
    private List<Vehicle> auta=new ArrayList<>();
    private int actCarCount=0;
    private static final DecimalFormat DF=new DecimalFormat("#.###");
    public Statistiky() {
        stage.setScene(scene);
        gui();
    }
    public void show()
    {
        stage.show();
    }
    public void hide()
    {
        stage.hide();
    }
    public void addCar()
    {
        carCount++;
        actCarCount++;
        showData();
    }
    public void addCar(Vehicle a)
    {
        
        actCarCount--;
        if(maxSpeed<a.getSpeed())
            maxSpeed=a.getSpeed();
        if(maxLength<a.getTime())
            maxLength=a.getTime();
        if(maxTime<a.getTime())
            maxTime=a.getTime();
        showData();
    }
    private void showData()
    {
        countLabData.setText(""+carCount);
        maxSpeedLabData.setText(""+DF.format(maxSpeed));
        maxLenghtLabData.setText(""+DF.format(maxLength*1000)+"us");
        actCountLabData.setText(""+actCarCount);
        maxTimeLabData.setText(""+DF.format(maxTime/60)+"s");
    }
    private void gui()
    {
        countLab=new Label("Celkový pocet aut");
        countLab.setLayoutX(30);
        countLab.setLayoutY(50);
        actCountLab=new Label("Aktuální pocet aut");
        actCountLab.setLayoutX(30);
        actCountLab.setLayoutY(140);
        maxSpeedLab=new Label("Maximální rychlost");
        maxSpeedLab.setLayoutX(30);
        maxSpeedLab.setLayoutY(80);
        maxLenghtLab=new Label("Maximální délka jízdy");
        maxLenghtLab.setLayoutX(30);
        maxLenghtLab.setLayoutY(110);
        maxTimeLab=new Label("Maximální doba jízdy");
        maxTimeLab.setLayoutX(30);
        maxTimeLab.setLayoutY(170);
        countLabData=new Label("0");
        countLabData.setLayoutX(200);
        countLabData.setLayoutY(50);
        maxSpeedLabData=new Label("0");
        maxSpeedLabData.setLayoutX(200);
        maxSpeedLabData.setLayoutY(80);
        maxLenghtLabData=new Label("0");
        maxLenghtLabData.setLayoutX(200);
        maxLenghtLabData.setLayoutY(110);
        actCountLabData=new Label("0");
        actCountLabData.setLayoutX(200);
        actCountLabData.setLayoutY(140);
        maxTimeLabData=new Label("0");
        maxTimeLabData.setLayoutX(200);
        maxTimeLabData.setLayoutY(170);
        root.getChildren().addAll(countLab,maxSpeedLab, maxTimeLab, actCountLab, maxSpeedLabData, maxTimeLabData, actCountLabData, countLabData, maxLenghtLab, maxLenghtLabData);
    }
}

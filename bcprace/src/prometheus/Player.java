/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import prometheus.Police.Police;
import prometheus.Store.BackgroundStore;
import prometheus.Store.ConnectStore;
import prometheus.Store.CurveStore;
import prometheus.Store.GeneratorStore;
import prometheus.Store.LightsStore;
import prometheus.Store.PoliceStore;
import prometheus.Store.SegmentStore;
import prometheus.Street.Connect;
import prometheus.Street.MyCurve;
import prometheus.Street.StreetSegment;
import prometheus.TrafficLights.TrafficLight;

/**
 *
 * @author Honza
 */
public class Player {
    private String name, minTimeStr, maxTimeStr,mark, date;
    private int totalCrashes, totalRuns, time, lightsRuns, policesRuns,rightsRuns, minTime, maxTime;
    private Timer timer;
    private TimerTask timerTask;
    private GuiControll gui;
    private boolean enableCrash=true;
    final int DEF_TIME = 60;
    private List<Integer> times=new ArrayList<>();
    private double avgTime;
    public Player(GuiControll gui) {
        this.gui=gui;
        newDate();
    }
    public Player(String name, int totalCrashes, int totalRuns, int lightsRuns, int policesRuns, int rightsRuns, double avgTime, String minTime, String maxTime, String mark, String date) {
        this.name = name;
        this.totalCrashes = totalCrashes;
        this.totalRuns = totalRuns;
        this.lightsRuns = lightsRuns;
        this.policesRuns = policesRuns;
        this.rightsRuns = rightsRuns;
        this.avgTime = avgTime;
        this.mark=mark;
        minTimeStr=minTime;
        maxTimeStr=maxTime;
        this.date=date;
        
    }
    private void newDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");  
        Date newDate = new Date();  
        date=sdf.format(newDate);
    }
    public String getDate() {
        return date;
    }
    
    public void setName(String name)
    {
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public int getTotalCrashes() {
        return totalCrashes;
    }

    public int getTotalRuns() {
        return totalRuns;
    }
    public int getTime() {
        return time;
    }

    public int getLightsRuns() {
        return lightsRuns;
    }

    public int getPolicesRuns() {
        return policesRuns;
    }

    public int getRightsRuns() {
        return rightsRuns;
    }

    public String getMinTimeStr() {
        return minTimeStr;
    }

    public String getMaxTimeStr() {
        return maxTimeStr;
    }

    public String getMark() {
        return mark;
    }
    
    public void startTimer()
    {
        
        timer=new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run(){
                Platform.runLater(() -> {
                    tick();
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }
    private void tick()
    {
        time++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setActTime(getTime(time));
            }
        });
    }
    public void enableCrash()
    {
        enableCrash=true;
    }
    
    public void addRun()
    {
        totalRuns++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setRunCount(String.valueOf(totalRuns));
            }
        });
    }
    public void addCrash()
    {
        
        if(enableCrash){
            totalCrashes++;
            calcMark();
            enableCrash=false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gui.setCrashes(String.valueOf(totalCrashes));
                }
            });
        }
        
    }
    public void addRightsRuns()
    {
        rightsRuns++;
        calcMark();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setRightsRun(String.valueOf(rightsRuns));
            }
        });
    }
    public void addLightsRuns()
    {
        lightsRuns++;
        calcMark();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setLightsRun(String.valueOf(lightsRuns));
            }  
        });
    }
    public void addPolicesRuns()
    {
        policesRuns++;
        calcMark();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setPolicesRun(String.valueOf(policesRuns));
            }
        });
        
    }
    public void cancel()
    {
        
        if(timer!=null)
            timer.cancel();
        if(timerTask!=null){
            times.add(time);
            calcMinMaxTime();
            time=0;
            calcAvgTime();
            calcMark();
            timerTask.cancel();
        }
    }
    private void calcMinMaxTime()
    {
        if(time<minTime || minTime==0){
            minTime=time;
            minTimeStr=getTime(minTime);
            gui.setMinTime(minTimeStr);
        }
        if(time>maxTime){
            maxTime=time;
            maxTimeStr=getTime(maxTime);
            gui.setMaxTime(maxTimeStr);
        }
        
    }
    private void calcMark()
    {
        double aMinMax=maxTime-minTime;
        double aAvgMin=avgTime-minTime;
        double perc=100/aMinMax*aAvgMin;
        if(aAvgMin==0)
            perc=0;
        perc+=(totalCrashes+rightsRuns/2+lightsRuns/2)*5;
        if(perc<20)
            mark= "A";
        else if(perc<40)
            mark= "B";
        else if(perc<60)
            mark= "C";
        else if(perc<80)
            mark= "D";
        else if(perc<90)
            mark= "E";
        else
            mark= "F";
        System.out.println(perc);
        gui.setMark(mark);
    }
    private void calcAvgTime()
    {
        int sum=0;
        for (Integer time : times) {
            sum+=time;
        }
        avgTime=sum/(double)totalRuns;
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gui.setAvgTime(String.valueOf(Math.round(avgTime*10)/10.0));
            }
        });
    }
    private String getTime(int sec)
    {
        int seconds = sec % DEF_TIME;
        int totalMinutes = sec / DEF_TIME;
        int minutes = totalMinutes % DEF_TIME;
        String min;
        if(minutes<1)
            min="00";
        else if(minutes<10)
            min="0"+minutes;
        else
            min=String.valueOf(minutes);
        String secon;
        if(seconds<1)
            secon="00";
        else if(seconds<10)
            secon="0"+seconds;
        else
            secon=String.valueOf(seconds);
        return min + ":" + secon;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import prometheus.TrafficLights.LightsConnect;
import prometheus.TrafficLights.TrafficLight;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.LightsControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class LightsStore {
    private static Document doc;
    private static Element root;
    private static List<TrafficLight> loadedLights=new ArrayList<>();

    public LightsStore(Document doc, Element root) {
        this.root=root;
        this.doc=doc;
    }
    
    
    public static void loadLights()
    {
        LightsControll sc=Prometheus.getLightsControll();
        NodeList lights=doc.getElementsByTagName("light");
        for (int i = 0; i < lights.getLength(); i++) {
            Node light=lights.item(i);    
            
            String idLight=light.getAttributes().getNamedItem("idLight").getNodeValue();
            TrafficLight semafor=new TrafficLight(sc);
            semafor.setID(Integer.parseInt(idLight));
            String status=light.getAttributes().getNamedItem("status").getNodeValue();
            String timeRed=light.getAttributes().getNamedItem("timeRed").getNodeValue();
            String timeGreen=light.getAttributes().getNamedItem("timeGreen").getNodeValue();
            String autoRed=light.getAttributes().getNamedItem("autoRed").getNodeValue();
            String autoGreen=light.getAttributes().getNamedItem("autoGreen").getNodeValue();
            String position=light.getAttributes().getNamedItem("position").getNodeValue();
            semafor.setStatus(Integer.parseInt(status), true);
            semafor.setTimeGreen(Integer.parseInt(timeGreen));
            semafor.setTimeRed(Integer.parseInt(timeRed));
            semafor.enableChangeGreen(Boolean.parseBoolean(autoGreen));
            semafor.enableChangeRed(Boolean.parseBoolean(autoRed));
            String[] pozzz=position.split(",");
            Point p=new Point(Integer.parseInt(pozzz[0]),Integer.parseInt(pozzz[1]));
            semafor.moveIMG(p);
            loadedLights.add(semafor);
        }
            
        for (int i = 0; i < lights.getLength(); i++) {
            Node sema=lights.item(i);  
            int idLight=Integer.parseInt(sema.getAttributes().getNamedItem("idLight").getNodeValue());
            NodeList spGreen=((Element)sema).getElementsByTagName("spGreen");
            NodeList spRed=((Element)sema).getElementsByTagName("spRed");
            TrafficLight actLight=getLightById(idLight);
            for (int j = 0; j < spGreen.getLength(); j++) {
                int idLightSp=Integer.parseInt(spGreen.item(j).getAttributes().getNamedItem("idLightSp").getNodeValue());
                int statLightSp=Integer.parseInt(spGreen.item(j).getAttributes().getNamedItem("statLightSp").getNodeValue());
                TrafficLight nextLight=getLightById(idLightSp);
                actLight.addControlGreen(nextLight, statLightSp);

            }
            for (int j = 0; j < spRed.getLength(); j++) {
                int idLightSp=Integer.parseInt(spRed.item(j).getAttributes().getNamedItem("idLightSp").getNodeValue());
                int statLightSp=Integer.parseInt(spRed.item(j).getAttributes().getNamedItem("statLightSp").getNodeValue());
                TrafficLight nextLight=getLightById(idLightSp);
                actLight.addControlRed(nextLight, statLightSp);

            }
        }           
    }
    public static List<TrafficLight> getLights()
    {
        return loadedLights;
    }
    private static TrafficLight getLightById(int id)
    {
        for (TrafficLight loadedLight : loadedLights) {
            if(loadedLight.getID()==id)
                return loadedLight;
        }
        return null;
    }
    public static void saveLights(List<TrafficLight> lights)
    {
        for (TrafficLight s : lights) {
            Element light=doc.createElement("light");

            Attr idLight=doc.createAttribute("idLight");
            idLight.setValue(String.valueOf(s.getID()));
            light.setAttributeNode(idLight);
            
            Attr status=doc.createAttribute("status");
            status.setValue(String.valueOf(s.getStatus()));
            light.setAttributeNode(status);
            
            
            Attr position=doc.createAttribute("position");
            position.setValue(String.valueOf((int)s.getPoz().getX()+","+(int)s.getPoz().getY()));
            light.setAttributeNode(position);

            Attr timeRed=doc.createAttribute("timeRed");
            timeRed.setValue(String.valueOf(s.getTimeRed()));
            light.setAttributeNode(timeRed);
            
            Attr timeOrange=doc.createAttribute("timeOrange");
            timeOrange.setValue(String.valueOf(s.getTimeOrange()));
            light.setAttributeNode(timeOrange);
                
            Attr timeGreen=doc.createAttribute("timeGreen");
            timeGreen.setValue(String.valueOf(s.getTimeGreen()));
            light.setAttributeNode(timeGreen);

            Attr autoRed=doc.createAttribute("autoRed");
            autoRed.setValue(String.valueOf(s.getEnableRed()));
            light.setAttributeNode(autoRed);

            Attr autoGreen=doc.createAttribute("autoGreen");
            autoGreen.setValue(String.valueOf(s.getEnableGreen()));
            light.setAttributeNode(autoGreen);

            for (LightsConnect sp : s.getControlGreen()) {
                Element spGreen=doc.createElement("spGreen");

                
                Attr idLightSp=doc.createAttribute("idLightSp");
                idLightSp.setValue(String.valueOf(sp.getSem().getID()));
                
                Attr statLightSp=doc.createAttribute("statLightSp");
                statLightSp.setValue(String.valueOf(sp.getStatus()));
                
                spGreen.setAttributeNode(statLightSp);
                spGreen.setAttributeNode(idLightSp);
                light.appendChild(spGreen);
            }
            for (LightsConnect sp : s.getControlRed()) {
                Element spRed=doc.createElement("spRed");

                Attr idLightSp=doc.createAttribute("idLightSp");
                idLightSp.setValue(String.valueOf(sp.getSem().getID()));
                
                Attr statLightSp=doc.createAttribute("statLightSp");
                statLightSp.setValue(String.valueOf(sp.getStatus()));
                
                spRed.setAttributeNode(statLightSp);
                spRed.setAttributeNode(idLightSp);
                light.appendChild(spRed);
            }
            root.appendChild(light);
        }
    }
}

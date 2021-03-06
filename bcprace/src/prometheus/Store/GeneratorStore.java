/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import java.awt.Point;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.CarControll;
import prometheus.DrawControll;
import prometheus.LightsControll;
import prometheus.Police.PoliceCombin;
import prometheus.PoliceControll;
import prometheus.Street.Connect;

/**
 *
 * @author Honza
 */
public class GeneratorStore {

    private Document doc;
    private Element root;
    public GeneratorStore(Document doc, Element root) {
        this.doc=doc;
        this.root=root;
    }
    public void saveGenerConfig(int carDeley, int tramDeley, boolean generRunCar, boolean generRunTram, boolean runPol, boolean runLigh)
    {
        Element generator=doc.createElement("generator");
        Attr carDel=doc.createAttribute("carDel");
        carDel.setValue(String.valueOf(carDeley));
        generator.setAttributeNode(carDel);
        
        Attr tramDel=doc.createAttribute("tramDeley");
        tramDel.setValue(String.valueOf(tramDeley));
        generator.setAttributeNode(tramDel);
        
        Attr generCar=doc.createAttribute("generCar");
        generCar.setValue(String.valueOf(generRunCar));
        generator.setAttributeNode(generCar);
        
        Attr generTram=doc.createAttribute("generTram"); 
        generTram.setValue(String.valueOf(generRunTram));
        generator.setAttributeNode(generTram);
        
        Attr runPolice=doc.createAttribute("runPolice"); 
        runPolice.setValue(String.valueOf(runPol));
        generator.setAttributeNode(runPolice);
        
        Attr runLights=doc.createAttribute("runLights"); 
        runLights.setValue(String.valueOf(runLigh));
        generator.setAttributeNode(runLights);
        root.appendChild(generator);

    }
    public void loadGenerConfig()
    {
        NodeList gen=doc.getElementsByTagName("generator");

        Node generator=gen.item(0);     
        
        int carDel=Integer.parseInt(generator.getAttributes().getNamedItem("carDel").getNodeValue());
        int tramDel=Integer.parseInt(generator.getAttributes().getNamedItem("tramDeley").getNodeValue());

        boolean generTram=Boolean.parseBoolean(generator.getAttributes().getNamedItem("generTram").getNodeValue());
        boolean generCar=Boolean.parseBoolean(generator.getAttributes().getNamedItem("generCar").getNodeValue());
        boolean runPolice=Boolean.parseBoolean(generator.getAttributes().getNamedItem("runPolice").getNodeValue());
        boolean runLights=Boolean.parseBoolean(generator.getAttributes().getNamedItem("runLights").getNodeValue());
        CarControll.setGenerDeleyCar(carDel);
        CarControll.setGenerDeleyTram(tramDel);
        CarControll.setCarGeneratorRun(generCar);
        CarControll.setTramGeneratorRun(generTram);
        PoliceControll.setPoliceRun(runPolice);
        LightsControll.setLightsRun(runLights);
    }
}

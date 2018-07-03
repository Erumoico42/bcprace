/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import prometheus.Police.PoliceCombin;
import prometheus.Street.MyCurve;
import prometheus.Street.StreetSegment;
import prometheus.TrafficLights.TrafficLight;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.CarControll;
import prometheus.DrawControll;

/**
 *
 * @author Honza
 */
public class SegmentStore {
    private Element root;
    private Document doc;
    private List<StreetSegment> loadedSegments=new ArrayList<>();
    private List<StreetSegment> startSegmentsTram=new ArrayList<>();
    private List<StreetSegment> startSegmentsCar=new ArrayList<>();
    public SegmentStore(Element root, Document doc)
    {
        this.root=root;
        this.doc=doc;
        
    }
    public List<StreetSegment> getLoadedSegments()
    {
        return loadedSegments;
    }
   
    public void saveSegments(List<MyCurve> curves, List<StreetSegment> startTram, List<StreetSegment> startCar)
    {
        for (MyCurve curve : curves) {
            for (StreetSegment usek : curve.getSegments()) {
                saveSegment(usek);
            }
        }
        for (StreetSegment streetSegment : startCar) {
            saveStart(streetSegment, false);
        }
        for (StreetSegment streetSegment : startTram) {
            saveStart(streetSegment, true);
        }
    }
    public void saveStart(StreetSegment s, boolean tram)
    {
        Element startSegment=doc.createElement("startSegment");   
        Attr idStartSegment=doc.createAttribute("idStartSegment");
        idStartSegment.setValue(String.valueOf(s.getId()));
        startSegment.setAttributeNode(idStartSegment);
        
        Attr isTram=doc.createAttribute("isTram");
        isTram.setValue(String.valueOf(tram));
        startSegment.setAttributeNode(isTram);
        
        root.appendChild(startSegment);
    }
    public void saveSegment(StreetSegment u)
    {   
        Element segment=doc.createElement("segment");   
        Attr idSegment=doc.createAttribute("idSegment");
        idSegment.setValue(String.valueOf(u.getId()));
        segment.setAttributeNode(idSegment);
        
        Attr p0=doc.createAttribute("p0");
        p0.setValue(String.valueOf((int)u.getP0().getX()+","+(int)u.getP0().getY()));
        segment.setAttributeNode(p0);

        Attr p1=doc.createAttribute("p1");
        p1.setValue(String.valueOf((int)u.getP1().getX()+","+(int)u.getP1().getY()));
        segment.setAttributeNode(p1);

        Attr p2=doc.createAttribute("p2");
        p2.setValue(String.valueOf((int)u.getP2().getX()+","+(int)u.getP2().getY()));
        segment.setAttributeNode(p2);

        Attr p3=doc.createAttribute("p3");
        p3.setValue(String.valueOf((int)u.getP3().getX()+","+(int)u.getP3().getY()));
        segment.setAttributeNode(p3);

        Attr winkAngle=doc.createAttribute("winkAngle");
        winkAngle.setValue(String.valueOf(u.getWinkAngle()));
        segment.setAttributeNode(winkAngle);
        
        Attr stopWink=doc.createAttribute("stopWink");
        stopWink.setValue(String.valueOf(u.isStopWinker()));
        segment.setAttributeNode(stopWink);
        
        for (StreetSegment uNext : u.getDalsiUseky()) {
            Element nextSegment=doc.createElement("nextSegment");

            Attr idNextSegment=doc.createAttribute("idNextSegment");
            idNextSegment.setValue(String.valueOf(uNext.getId()));
            nextSegment.setAttributeNode(idNextSegment);
            segment.appendChild(nextSegment);
        }
        for (StreetSegment uu : u.getPredchoziUseky()) {
            Element lastSegment=doc.createElement("lastSegment");

            Attr idLastSegment=doc.createAttribute("idLastSegment");
            idLastSegment.setValue(String.valueOf(uu.getId()));
            lastSegment.setAttributeNode(idLastSegment);
            segment.appendChild(lastSegment);
        }
        for (StreetSegment cp : u.getCheckPoints()) {
            Element checkPoint=doc.createElement("checkPoint");

            Attr idCheckPoint=doc.createAttribute("idCheckPoint");
            idCheckPoint.setValue(String.valueOf(cp.getId()));
            checkPoint.setAttributeNode(idCheckPoint);
            segment.appendChild(checkPoint);
        }
        
        for (TrafficLight semafor : u.getSemafory()) {
            Element light=doc.createElement("subLight");

            Attr idLight=doc.createAttribute("idLight");
            idLight.setValue(String.valueOf(semafor.getID()));
            light.setAttributeNode(idLight);
            segment.appendChild(light);
        }
        for (PoliceCombin pk : u.getPK()) {
            Element policeCombin=doc.createElement("policeCombin");

            Attr idPoliceCombin=doc.createAttribute("idPoliceCombin");
            idPoliceCombin.setValue(String.valueOf(pk.getId()));
            policeCombin.setAttributeNode(idPoliceCombin);
            segment.appendChild(policeCombin);
        }
        root.appendChild(segment);
    }   
    private void loadStars()
    {
        NodeList starts=doc.getElementsByTagName("startSegment");
        for (int i = 0; i < starts.getLength(); i++) {
            Node start=starts.item(i);   
            int idStartSegment=Integer.valueOf(start.getAttributes().getNamedItem("idStartSegment").getNodeValue());
            StreetSegment act=getUsekById(idStartSegment);
            boolean isTram=Boolean.valueOf(start.getAttributes().getNamedItem("isTram").getNodeValue());
            if(isTram)
                DrawControll.addStartSegmentTram(act);
            else
                DrawControll.addStartSegmentCar(act);
        }
    }
    public void loadSegments(List<TrafficLight> lights, List<PoliceCombin> polKombs)
    {
        NodeList us=doc.getElementsByTagName("segment");
        for (int i = 0; i < us.getLength(); i++) {
            
            
            Node usek=us.item(i);           
            String p=usek.getAttributes().getNamedItem("p0").getNodeValue();
            String[] s0=p.split(",");
            Point p0=new Point(Integer.parseInt(s0[0]),Integer.parseInt(s0[1]));
            
            p=usek.getAttributes().getNamedItem("p1").getNodeValue();
            String[] s1=p.split(",");
            Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));
            
            p=usek.getAttributes().getNamedItem("p2").getNodeValue();
            String[] s2=p.split(",");
            Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
            
            p=usek.getAttributes().getNamedItem("p3").getNodeValue();
            String[] s3=p.split(",");
            Point p3=new Point(Integer.parseInt(s3[0]),Integer.parseInt(s3[1]));
             
            StreetSegment u=new StreetSegment(p0, p3);
            u.setP1(p1);
            u.setP2(p2);
            
            
            //boolean stopWink=Boolean.valueOf(usek.getAttributes().getNamedItem("stopWink").getNodeValue());
            //u.setStopWinker(stopWink);
                    
                    
            double winkAngle=Double.valueOf(usek.getAttributes().getNamedItem("winkAngle").getNodeValue());
            u.setWinkAngle(winkAngle);
            
            int idSegment=Integer.valueOf(usek.getAttributes().getNamedItem("idSegment").getNodeValue());
            u.setId(idSegment);
            
            NodeList sems=((Element)usek).getElementsByTagName("subLight");
            for (int j = 0; j < sems.getLength(); j++) {
                int idLight=Integer.parseInt(sems.item(j).getAttributes().getNamedItem("idLight").getNodeValue());
                for (TrafficLight light : lights) {
                    if(light.getID()==idLight)
                    {
                        u.addSemafor(light);
                    }
                }
                
            }
            NodeList policeCombin=((Element)usek).getElementsByTagName("policeCombin");
            for (int j = 0; j < policeCombin.getLength(); j++) {
                int idPoliceCombin=Integer.parseInt(policeCombin.item(j).getAttributes().getNamedItem("idPoliceCombin").getNodeValue());
                for (PoliceCombin pk : polKombs) {
                    if(pk.getId()==idPoliceCombin)
                    {
                        u.addPK(pk);
                    }
                }
                
            }
            
            
            loadedSegments.add(u);
        }
        for (int i = 0; i < us.getLength(); i++) {
            Node usek=us.item(i); 
            
            int idAct=Integer.valueOf(usek.getAttributes().getNamedItem("idSegment").getNodeValue());
            StreetSegment actUs=getUsekById(idAct);
            
            NodeList predch=((Element)usek).getElementsByTagName("lastSegment");
            
            for (int j = 0; j < predch.getLength(); j++) {                
                int idLast=Integer.valueOf(predch.item(j).getAttributes().getNamedItem("idLastSegment").getNodeValue());
                StreetSegment lastUs=getUsekById(idLast);
                actUs.setPredchoziUseky(lastUs);
            }
            NodeList dalsi=((Element)usek).getElementsByTagName("nextSegment");
            for (int j = 0; j < dalsi.getLength(); j++) {
                int idNext=Integer.valueOf(dalsi.item(j).getAttributes().getNamedItem("idNextSegment").getNodeValue());
                StreetSegment nextUs=getUsekById(idNext);
                actUs.setDalsiUseky(nextUs);
            }
            
            NodeList checkPoints=((Element)usek).getElementsByTagName("checkPoint");
            for (int j=0; j<checkPoints.getLength(); j++)
            {
                int idCp=Integer.valueOf(checkPoints.item(j).getAttributes().getNamedItem("idCheckPoint").getNodeValue());
                StreetSegment cpUs=getUsekById(idCp);
                actUs.addCheckPoint(cpUs);
                
            }
        }   
        loadStars();
    }
    private StreetSegment getUsekById(int id)
    {
        for (StreetSegment loadedSegment : loadedSegments) {
            if(loadedSegment.getId()==id){
                return loadedSegment;
            }
        }
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import prometheus.Street.Connect;
import prometheus.Street.MyCurve;
import prometheus.Street.StreetSegment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.DrawControll;

/**
 *
 * @author Honza
 */
public class CurveStore {
    private static Document doc;
    private static Element root;
    private static List<Connect> connects=new ArrayList<>();
    private static List<StreetSegment> segments;
    private static List<MyCurve> mycurves=new ArrayList<>();
    private static DrawControll draw;
    public CurveStore(Document doc, Element root, DrawControll draw)
    {
        this.draw=draw;
        this.doc=doc;
        this.root=root;
    }
    public static void saveCurve(List<MyCurve> curves)
    {
        for (MyCurve mc : curves) {
            Element curve=doc.createElement("curve");   

            Attr idCurve=doc.createAttribute("idCurve");
            idCurve.setValue(String.valueOf(mc.getId()));
            curve.setAttributeNode(idCurve);

            Attr idConn0=doc.createAttribute("idConn0");
            idConn0.setValue(String.valueOf(mc.getC0().getId()));
            curve.setAttributeNode(idConn0);

            Attr idConn3=doc.createAttribute("idConn3");
            idConn3.setValue(String.valueOf(mc.getC3().getId()));
            curve.setAttributeNode(idConn3);

            Attr p0=doc.createAttribute("p0");
            p0.setValue(String.valueOf((int)mc.getCurve().getStartX()+","+(int)mc.getCurve().getStartY()));
            curve.setAttributeNode(p0);

            Attr p1=doc.createAttribute("p1");
            p1.setValue(String.valueOf((int)mc.getCurve().getControlX1()+","+(int)mc.getCurve().getControlY1()));
            curve.setAttributeNode(p1);

            Attr p2=doc.createAttribute("p2");
            p2.setValue(String.valueOf((int)mc.getCurve().getControlX2()+","+(int)mc.getCurve().getControlY2()));
            curve.setAttributeNode(p2);

            Attr p3=doc.createAttribute("p3");
            p3.setValue(String.valueOf((int)mc.getCurve().getEndX()+","+(int)mc.getCurve().getEndY()));
            curve.setAttributeNode(p3);
            root.appendChild(curve);


            Attr idFirstSegment=doc.createAttribute("idFirstSegment");
            idFirstSegment.setValue(String.valueOf(mc.getFirst().getId()));
            curve.setAttributeNode(idFirstSegment);

            Attr idLastSegment=doc.createAttribute("idLastSegment");
            idLastSegment.setValue(String.valueOf(mc.getLast().getId()));
            curve.setAttributeNode(idLastSegment);

            for (StreetSegment segment : mc.getSegments()) {
                System.out.println(segment.getId());
                Element subSegment=doc.createElement("subSegment");
                Attr idSubSegment=doc.createAttribute("idSubSegment");
                idSubSegment.setValue(String.valueOf(segment.getId()));
                subSegment.setAttributeNode(idSubSegment);
                curve.appendChild(subSegment);
            }
            root.appendChild(curve);
        }
    }
    private static Connect getConnectById(int id)
    {
        for (Connect connect : connects) {
            if(connect.getId()==id)
                return connect;
        }
        return null;
    }      
    public static void loadCurves(List<StreetSegment> segmentss, List<Connect> connectss)
    {
        connects=connectss;
        segments=segmentss;
        NodeList curves=doc.getElementsByTagName("curve");
            MyCurve mc;
            
            //Prometheus.setLasCurveId(curves.getLength()-1);
            for (int i = 0; i < curves.getLength(); i++) {
                Node curve=curves.item(i); 
                
                int conn0Id=Integer.parseInt(curve.getAttributes().getNamedItem("idConn0").getNodeValue());
                int conn3Id=Integer.parseInt(curve.getAttributes().getNamedItem("idConn3").getNodeValue());
                
                String p;
                
                p=curve.getAttributes().getNamedItem("p1").getNodeValue();
                String[] s1=p.split(",");
                Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));
                
                p=curve.getAttributes().getNamedItem("p2").getNodeValue();
                String[] s2=p.split(",");
                Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
                
                Connect conn0=getConnectById(conn0Id);
                
                Connect conn3=getConnectById(conn3Id);
                
                int idCurve=Integer.parseInt(curve.getAttributes().getNamedItem("idCurve").getNodeValue());
                mc=new MyCurve(conn0,conn3, draw);
                conn0.addStartCurve(mc);
                conn3.addEndCurve(mc);
                initMC(mc, p1, p2, idCurve);
                
                
                int idFirstSegment=Integer.parseInt(curve.getAttributes().getNamedItem("idFirstSegment").getNodeValue());
                mc.setFirst(getUsekById(idFirstSegment));
                int idLastSegment=Integer.parseInt(curve.getAttributes().getNamedItem("idLastSegment").getNodeValue());
                mc.setLast(getUsekById(idLastSegment));
                
                NodeList subSegment=((Element)curve).getElementsByTagName("subSegment");
                for (int j = 0; j < subSegment.getLength(); j++) {
                    int idSubSegment=Integer.parseInt(subSegment.item(j).getAttributes().getNamedItem("idSubSegment").getNodeValue());
                    mc.addSegment(getUsekById(idSubSegment));
                    
                }
            }
    }
    private static StreetSegment getUsekById(int id)
    {
        for (StreetSegment usek : segments) {
            if(usek.getId()==id){
                return usek;
            }
        }
        return null;
    }
    private static void initMC(MyCurve mc, Point p2, Point p3, int id)
    {
        mc.setId(id);
        mc.getC1().move(p2.getX(), p2.getY());
        //mc.getControl1().moveControls(p2);
        mc.getC2().move(p3.getX(), p3.getY());
        //mc.getControl2().moveControls(p3);
        mc.setChangedControlls();
        mycurves.add(mc);
    }
}

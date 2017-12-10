/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 *
 * @author Honza
 */
public class XMLStore {
    private static List<Usek> useky=new ArrayList<Usek>();
    private static List<Usek> startUseky=new ArrayList<Usek>();
    private static List<MyCurve> mycurves=new ArrayList<MyCurve>();
    public XMLStore()
    {
        
    }
    public static void reader()
    {
        
        try {
            
            File input=new File("D:\\xml2.xml");
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc;
            doc = db.parse(input);
            doc.getDocumentElement().normalize();
            curves(doc);
            //useky(doc);
            
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        
    }
    private static void useky(Document doc)
    {
        NodeList us=doc.getElementsByTagName("usek");
        for (int i = 0; i < us.getLength(); i++) {
            Usek u=new Usek();
            Node usek=us.item(i); 
            /*NodeList predch=((Element)us).getElementsByTagName("predchUsek");
            if(predch.getLength()==0)
                startUseky.add(u);
            */
            String p=usek.getAttributes().getNamedItem("p1").getNodeValue();
            String[] s1=p.split(",");
            Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));
            u.setP1(p1);
            p=usek.getAttributes().getNamedItem("p2").getNodeValue();
            String[] s2=p.split(",");
            Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
            u.setP12(p2);
            p=usek.getAttributes().getNamedItem("p3").getNodeValue();
            String[] s3=p.split(",");
            Point p3=new Point(Integer.parseInt(s3[0]),Integer.parseInt(s3[1]));
            u.setP21(p3);
            p=usek.getAttributes().getNamedItem("p4").getNodeValue();
            String[] s4=p.split(",");
            Point p4=new Point(Integer.parseInt(s4[0]),Integer.parseInt(s4[1]));
            u.setP2(p4); 
            useky.add(u);
        }
        for (int i = 0; i < us.getLength(); i++) {
            Node usek=us.item(i); 
            NodeList predch=((Element)usek).getElementsByTagName("predchUsek");
            for (int j = 0; j < predch.getLength(); j++) {
                String p=predch.item(j).getAttributes().getNamedItem("idPredchUsek").getNodeValue();
                useky.get(i).setPredchoziUseky(useky.get(Integer.parseInt(p)));
            }
            NodeList dalsi=((Element)usek).getElementsByTagName("dalsiUsek");
            for (int j = 0; j < dalsi.getLength(); j++) {
                String p=dalsi.item(j).getAttributes().getNamedItem("idDalsiiUsek").getNodeValue();
                useky.get(i).setDalsiUseky(useky.get(Integer.parseInt(p)));
            }
        }    
    }
    private static void curves(Document doc)
    {
        NodeList curves=doc.getElementsByTagName("curve");
            MyCurve mc;
            Prometheus.setLasCurveId(curves.getLength()-1);
            for (int i = 0; i < curves.getLength(); i++) {
                Node curve=curves.item(i); 
                NodeList ends=((Element)curve).getElementsByTagName("endCurve");
                
                if(ends.getLength()==0)
                {
                    Prometheus.setConnect(null);
                }
                else
                {
                    String e=ends.item(0).getAttributes().getNamedItem("idEndCurve").getNodeValue();
                    int endId=Integer.parseInt(e);
                    for (MyCurve mcc : mycurves) {
                        
                        if(mcc.getId()==endId){
                            Prometheus.setConnect(mcc.getConnect3());
                            break;
                        }
                    }
                }
                
                String p=curve.getAttributes().getNamedItem("p1").getNodeValue();
                String[] s1=p.split(",");
                Point p1=new Point(Integer.parseInt(s1[0]),Integer.parseInt(s1[1]));
                
                p=curve.getAttributes().getNamedItem("p2").getNodeValue();
                String[] s2=p.split(",");
                Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
                
                p=curve.getAttributes().getNamedItem("p3").getNodeValue();
                String[] s3=p.split(",");
                Point p3=new Point(Integer.parseInt(s3[0]),Integer.parseInt(s3[1]));
                
                p=curve.getAttributes().getNamedItem("p4").getNodeValue();
                String[] s4=p.split(",");
                Point p4=new Point(Integer.parseInt(s4[0]),Integer.parseInt(s4[1]));
                
                String joined=String.valueOf(curve.getAttributes().getNamedItem("joined").getNodeValue());
                int idCurve=Integer.parseInt(curve.getAttributes().getNamedItem("idCurve").getNodeValue());
                if(joined.equals("true"))
                {
                    Connect actConn=Prometheus.getActConn();
                    NodeList starts=((Element)curve).getElementsByTagName("startCurve");
                    String s=starts.item(0).getAttributes().getNamedItem("idStartCurve").getNodeValue();
                    int endId=Integer.parseInt(s);
                    for (MyCurve mcc : mycurves) {
                        
                        if(mcc.getId()==endId){
                            mc=new MyCurve(actConn,mcc.getConnect0());
                            initMC(mc, p2, p3, p4, idCurve, i);
                            mc.setJoined();
                            break;
                        }
                        
                    }
                    
                }
                else
                {
                    mc=Prometheus.newCurve(p1);
                    initMC(mc, p2, p3, p4, idCurve, i);
                }
                
                
            }
            /*for (int i = 0; i < curves.getLength(); i++) {
                Node curve=curves.item(i); 
                NodeList ends=((Element)curve).getElementsByTagName("endCurve");
                for (int j = 0; j < ends.getLength(); j++) {
                    String p=ends.item(j).getAttributes().getNamedItem("idEndCurve").getNodeValue();
                    mycurves.get(i).getConnect0().addEndCurve(mycurves.get(Integer.parseInt(p)));
                }
                NodeList starts=((Element)curve).getElementsByTagName("startCurve");
                for (int j = 0; j < starts.getLength(); j++) {
                    String p=ends.item(j).getAttributes().getNamedItem("idStartCurve").getNodeValue();
                    mycurves.get(i).getConnect3().addStartCurve(mycurves.get(j));
                }
            }*/
    }
    private static void initMC(MyCurve mc, Point p2, Point p3, Point p4, int id, int i)
    {
        mc.setId(id);
        mc.getControl1().moveControl(p2.getX(), p2.getY());
        mc.getControl1().moveControls(p2);
        mc.getControl2().moveControl(p3.getX(), p3.getY());
        mc.getControl2().moveControls(p3);
        mc.moveP3(p4.getX(), p4.getY());
        mc.getConnect3().getCircle().setCenterX(i);
        mc.getConnect3().move(p4);
        mycurves.add(mc);
    }
    public void writer(List<MyCurve> curves, List<Usek> useky)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc=db.newDocument();

            Element root=doc.createElement("root");
            doc.appendChild(root);

            for (MyCurve c : curves) {
                Element curve=doc.createElement("curve");
            
                Attr idCurve=doc.createAttribute("idCurve");
                idCurve.setValue(String.valueOf(c.getId()));
                curve.setAttributeNode(idCurve);
                
                Attr joined=doc.createAttribute("joined");
                joined.setValue(String.valueOf(c.getJoined()));
                curve.setAttributeNode(joined);
                
                Attr p1=doc.createAttribute("p1");
                p1.setValue(String.valueOf((int)c.getCurve().getStartX()+","+(int)c.getCurve().getStartY()));
                curve.setAttributeNode(p1);
                
                Attr p2=doc.createAttribute("p2");
                p2.setValue(String.valueOf((int)c.getCurve().getControlX1()+","+(int)c.getCurve().getControlY1()));
                curve.setAttributeNode(p2);
                
                Attr p3=doc.createAttribute("p3");
                p3.setValue(String.valueOf((int)c.getCurve().getControlX2()+","+(int)c.getCurve().getControlY2()));
                curve.setAttributeNode(p3);
                
                Attr p4=doc.createAttribute("p4");
                p4.setValue(String.valueOf((int)c.getCurve().getEndX()+","+(int)c.getCurve().getEndY()));
                curve.setAttributeNode(p4);
                
                for (MyCurve ec : c.getConnect0().getEndCurves()) {
                    Element endCurve=doc.createElement("endCurve");
                    
                    Attr idendCurve=doc.createAttribute("idEndCurve");
                    idendCurve.setValue(String.valueOf(ec.getId()));
                    endCurve.setAttributeNode(idendCurve);
                    curve.appendChild(endCurve);
                }
                
                for (MyCurve sc : c.getConnect3().getStartCurves()) {
                    Element startCurve=doc.createElement("startCurve");
                    
                    Attr idStartCurve=doc.createAttribute("idStartCurve");
                    idStartCurve.setValue(String.valueOf(sc.getId()));
                    startCurve.setAttributeNode(idStartCurve);
                    curve.appendChild(startCurve);
                }
                root.appendChild(curve);
                
            }
         /*for (Usek u : useky) {
                Element usek=doc.createElement("usek");
            
                Attr idUsek=doc.createAttribute("idUsek");
                idUsek.setValue(String.valueOf(u.getId()));
                usek.setAttributeNode(idUsek);
                
                if(u.getP1()!=null){
                    Attr p1=doc.createAttribute("p1");
                    p1.setValue(String.valueOf(u.getP1().getX()+","+u.getP1().getY()));
                    usek.setAttributeNode(p1);

                    Attr p2=doc.createAttribute("p2");
                    p2.setValue(String.valueOf(u.getP12().getX()+","+u.getP12().getY()));
                    usek.setAttributeNode(p2);

                    Attr p3=doc.createAttribute("p3");
                    p3.setValue(String.valueOf(u.getP21().getX()+","+u.getP21().getY()));
                    usek.setAttributeNode(p3);

                    Attr p4=doc.createAttribute("p4");
                    p4.setValue(String.valueOf(u.getP2().getX()+","+u.getP2().getY()));
                    usek.setAttributeNode(p4);

                    for (Usek uu : u.getDalsiUseky()) {
                        Element dalsiUsek=doc.createElement("dalsiUsek");

                        Attr idDalsiUsek=doc.createAttribute("idDalsiUsek");
                        idDalsiUsek.setValue(String.valueOf(uu.getId()));
                        dalsiUsek.setAttributeNode(idDalsiUsek);
                        usek.appendChild(dalsiUsek);
                    }
                }
                for (Usek uu : u.getPredchoziUseky()) {
                    Element predchUsek=doc.createElement("predchUsek");
                    
                    Attr idPredchUsek=doc.createAttribute("idPredchUsek");
                    idPredchUsek.setValue(String.valueOf(uu.getId()));
                    predchUsek.setAttributeNode(idPredchUsek);
                    usek.appendChild(predchUsek);
                }
                root.appendChild(usek);
            }     */
        
        TransformerFactory tfc=TransformerFactory.newInstance();
        Transformer tf=tfc.newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source=new DOMSource(doc);
        StreamResult result=new StreamResult(new File("D:\\xml2.xml"));
        tf.transform(source, result);
        } catch (DOMException dome)
        {
            throw new Error(dome);
            
        }catch(ParserConfigurationException pce)
        {
            throw new Error(pce);
            
        }catch(TransformerException te)
        {
            throw new Error(te);
        }
        catch(TransformerFactoryConfigurationError tfce)
        {
            throw new Error(tfce);
        }
    }
    
}

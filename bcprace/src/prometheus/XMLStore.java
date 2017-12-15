/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
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
import org.xml.sax.SAXException;

/**
 *
 * @author Honza
 */
public class XMLStore {
    private static List<Usek> useky=new ArrayList<Usek>();
    private static List<Usek> startUseky=new ArrayList<Usek>();
    private static List<MyCurve> mycurves=new ArrayList<MyCurve>();
    private static List<Connect> connects=new ArrayList<Connect>();
    public XMLStore()
    {
        
    }
    public static void openFile()
    {
        FileChooser fch=new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showOpenDialog(null);
        if (file != null) { 
            reader(file);
        }
        
    }
    public static void saveFile(List<MyCurve> curves, List<Usek> useky, List<Connect> connects, String bgSource, ImageView iv)
    {
        FileChooser fch=new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showSaveDialog(null);
        if (file != null) { 
            writer(curves, useky, connects, file, bgSource, iv);
        }
    }
    private static void reader(File input)
    {
        
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc;
            doc = db.parse(input);
            doc.getDocumentElement().normalize();
            
            readConnects(doc);
            readCurves(doc);
            
            Prometheus.removeCircles();
            Prometheus.cleanUseky();
            readUseky(doc);
            Prometheus.setStartUseky(startUseky);
            
            
            NodeList bg=doc.getElementsByTagName("background");
            Node background=bg.item(0);
            boolean isnull= Boolean.parseBoolean(background.getAttributes().getNamedItem("isNull").getNodeValue());
            
            if(!isnull){
                String source= background.getAttributes().getNamedItem("bgSource").getNodeValue();
                String width=background.getAttributes().getNamedItem("width").getNodeValue();
                String height=background.getAttributes().getNamedItem("height").getNodeValue();
                
                String p= background.getAttributes().getNamedItem("position").getNodeValue();
                String[] pos=p.split(",");
                Point position=new Point(Integer.parseInt(pos[0]),Integer.parseInt(pos[1]));
                ImageView iv=new ImageView(new Image(source));
                iv.setLayoutX(position.getX());
                iv.setLayoutY(position.getY());
                iv.setFitHeight(Integer.parseInt(height));
                iv.setFitWidth(Integer.parseInt(width));
                Prometheus.setBg(iv);
                Prometheus.loadBg(iv);
                Prometheus.setBgSource(source);
            }
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    private static void readConnects(Document doc)
    {
        NodeList conn=doc.getElementsByTagName("connect");
        for (int i = 0; i < conn.getLength(); i++) {
            
            Node con=conn.item(i);           
            int idConn=Integer.parseInt(con.getAttributes().getNamedItem("idConnect").getNodeValue());
            boolean start=Boolean.parseBoolean(con.getAttributes().getNamedItem("start").getNodeValue());
            String p=con.getAttributes().getNamedItem("pointConn").getNodeValue();
            String[] s=p.split(",");
            Point pp=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
            
            Connect connect=new Connect(pp);
            connect.setID(idConn);
            connect.setStart(start);
            
            connects.add(connect);
        }
        Prometheus.setLastConnId(conn.getLength()-1);
    }
    private static void writeConnects(Element root, Document doc, List<Connect> connects)
    {
        for (Connect c : connects) {
            Element connect=doc.createElement("connect");

            Attr idConnect=doc.createAttribute("idConnect");
            idConnect.setValue(String.valueOf(c.getID()));
            connect.setAttributeNode(idConnect);
            
            Attr start=doc.createAttribute("start");
            start.setValue(String.valueOf(c.getStart()));
            connect.setAttributeNode(start);
            
            Attr point=doc.createAttribute("pointConn");
            point.setValue(String.valueOf((int)c.getPoint().getX()+","+(int)c.getPoint().getY()));
            connect.setAttributeNode(point);
            root.appendChild(connect);
        }
    }
    private static void readUseky(Document doc)
    {
        NodeList us=doc.getElementsByTagName("usek");
        for (int i = 0; i < us.getLength(); i++) {
            Usek u=new Usek();
            
            Node usek=us.item(i);           
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
            u.setCir();
            useky.add(u);
        }
        SemtamforControl sc=Prometheus.getSC();
        for (int i = 0; i < us.getLength(); i++) {
            Node usek=us.item(i); 
            NodeList predch=((Element)usek).getElementsByTagName("predchUsek");
            if(predch.getLength()==0)
                startUseky.add(useky.get(i));
            for (int j = 0; j < predch.getLength(); j++) {
                String id=predch.item(j).getAttributes().getNamedItem("idPredchUsek").getNodeValue();
                useky.get(i).setPredchoziUseky(useky.get(Integer.parseInt(id)));
            }
            NodeList dalsi=((Element)usek).getElementsByTagName("dalsiUsek");
            for (int j = 0; j < dalsi.getLength(); j++) {
                String id=dalsi.item(j).getAttributes().getNamedItem("idDalsiUsek").getNodeValue();
                useky.get(i).setDalsiUseky(useky.get(Integer.parseInt(id)));
            }
            NodeList checkPoints=((Element)usek).getElementsByTagName("checkPoint");
            for (int j=0; j<checkPoints.getLength(); j++)
            {
                String cp=checkPoints.item(j).getAttributes().getNamedItem("idCheckPoint").getNodeValue();
                useky.get(i).addCheckPoint(useky.get(Integer.parseInt(cp)));
            }
            NodeList semafory=((Element)usek).getElementsByTagName("semafor");
            for (int j=0; j<semafory.getLength(); j++)
            {
                Node semafor=semafory.item(j); 
                String color=semafor.getAttributes().getNamedItem("color").getNodeValue();
                String p=semafor.getAttributes().getNamedItem("poz").getNodeValue();
                int time=Integer.parseInt(semafor.getAttributes().getNamedItem("time").getNodeValue());
                String[] s2=p.split(",");
                Semafor s=new Semafor(color, sc);
                s.setPoz(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]));
                s.setTime(time);
                useky.get(i).addSemafor(s);
            }
        }   
    }
    private static void readCurves(Document doc)
    {
        NodeList curves=doc.getElementsByTagName("curve");
            MyCurve mc;
            Prometheus.setLasCurveId(curves.getLength()-1);
            for (int i = 0; i < curves.getLength(); i++) {
                Node curve=curves.item(i); 
                
                int conn0Id=Integer.parseInt(curve.getAttributes().getNamedItem("conn0").getNodeValue());
                int conn3Id=Integer.parseInt(curve.getAttributes().getNamedItem("conn3").getNodeValue());
                
                String p;
                
                p=curve.getAttributes().getNamedItem("p2").getNodeValue();
                String[] s2=p.split(",");
                Point p2=new Point(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]));
                
                p=curve.getAttributes().getNamedItem("p3").getNodeValue();
                String[] s3=p.split(",");
                Point p3=new Point(Integer.parseInt(s3[0]),Integer.parseInt(s3[1]));
                
                Connect conn0=null;
                Connect conn3=null;
                for (Connect conn : connects) {
                    if(conn.getID()==conn0Id)
                        conn0=conn;
                    if(conn.getID()==conn3Id)
                        conn3=conn;
                }
                int idCurve=Integer.parseInt(curve.getAttributes().getNamedItem("idCurve").getNodeValue());
                if(conn0!=null && conn3!= null)
                {
                    mc=new MyCurve(conn0,conn3);
                    initMC(mc, p2, p3, idCurve);
                }
            }
    }
    
    private static void initMC(MyCurve mc, Point p2, Point p3, int id)
    {
        mc.setId(id);
        mc.getControl1().moveControl(p2.getX(), p2.getY());
        mc.getControl1().moveControls(p2);
        mc.getControl2().moveControl(p3.getX(), p3.getY());
        mc.getControl2().moveControls(p3);
        mycurves.add(mc);
    }
    
    private static void writeCurves(Element root, Document doc, List<MyCurve> curves)
    {
        for (MyCurve c : curves) {
            Element curve=doc.createElement("curve");

            Attr idCurve=doc.createAttribute("idCurve");
            idCurve.setValue(String.valueOf(c.getId()));
            curve.setAttributeNode(idCurve);

            Attr conn0=doc.createAttribute("conn0");
            conn0.setValue(String.valueOf(c.getConnect0().getID()));
            curve.setAttributeNode(conn0);
            
            Attr conn3=doc.createAttribute("conn3");
            conn3.setValue(String.valueOf(c.getConnect3().getID()));
            curve.setAttributeNode(conn3);

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
            root.appendChild(curve);
        }
    }
    private static void writeUseky(Element root, Document doc, List<Usek> useky)
    {
        for (Usek u : useky) {
            Element usek=doc.createElement("usek");

            Attr idUsek=doc.createAttribute("idUsek");
            idUsek.setValue(String.valueOf(u.getId()));
            usek.setAttributeNode(idUsek);

            if(u.getP1()!=null){
                Attr p1=doc.createAttribute("p1");
                p1.setValue(String.valueOf((int)u.getP1().getX()+","+(int)u.getP1().getY()));
                usek.setAttributeNode(p1);

                Attr p2=doc.createAttribute("p2");
                p2.setValue(String.valueOf((int)u.getP12().getX()+","+(int)u.getP12().getY()));
                usek.setAttributeNode(p2);

                Attr p3=doc.createAttribute("p3");
                p3.setValue(String.valueOf((int)u.getP21().getX()+","+(int)u.getP21().getY()));
                usek.setAttributeNode(p3);

                Attr p4=doc.createAttribute("p4");
                p4.setValue(String.valueOf((int)u.getP2().getX()+","+(int)u.getP2().getY()));
                usek.setAttributeNode(p4);


            }
            for (Usek uu : u.getDalsiUseky()) {
                Element dalsiUsek=doc.createElement("dalsiUsek");

                Attr idDalsiUsek=doc.createAttribute("idDalsiUsek");
                idDalsiUsek.setValue(String.valueOf(uu.getId()));
                dalsiUsek.setAttributeNode(idDalsiUsek);
                usek.appendChild(dalsiUsek);
            }
            for (Usek uu : u.getPredchoziUseky()) {
                Element predchUsek=doc.createElement("predchUsek");

                Attr idPredchUsek=doc.createAttribute("idPredchUsek");
                idPredchUsek.setValue(String.valueOf(uu.getId()));
                predchUsek.setAttributeNode(idPredchUsek);
                usek.appendChild(predchUsek);
            }
            for (Usek cp : u.getCheckPoints()) {
                Element checkPoint=doc.createElement("checkPoint");

                Attr idcp=doc.createAttribute("idCheckPoint");
                idcp.setValue(String.valueOf(cp.getId()));
                checkPoint.setAttributeNode(idcp);
                usek.appendChild(checkPoint);
            }
            for (Semafor s : u.getSemafory()) {
                Element semafor=doc.createElement("semafor");

                Attr color=doc.createAttribute("color");
                color.setValue(s.getColor());
                semafor.setAttributeNode(color);
                Attr poz=doc.createAttribute("poz");
                poz.setValue(String.valueOf((int)s.getPoz().getX()+","+(int)s.getPoz().getY()));
                semafor.setAttributeNode(poz);
                Attr time=doc.createAttribute("time");
                time.setValue(String.valueOf(s.getTime()));
                semafor.setAttributeNode(time);
                usek.appendChild(semafor);
            }
            root.appendChild(usek);
        }
    }
    public static void writer(List<MyCurve> curves, List<Usek> useky, List<Connect> connects, File file, String bgSource, ImageView iv)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc=db.newDocument();

            Element root=doc.createElement("root");
            doc.appendChild(root);
            writeConnects(root, doc, connects);
            writeCurves(root, doc, curves);
            writeUseky(root, doc, useky);
            
            Element background=doc.createElement("background");
            Attr isnull=doc.createAttribute("isNull");
            if(bgSource!=null)
            {
                isnull.setValue("false");
                Attr bgs=doc.createAttribute("bgSource");
                Attr width=doc.createAttribute("width");
                Attr height=doc.createAttribute("height");
                Attr pos=doc.createAttribute("position");       
                bgs.setValue(bgSource);
                if(iv!=null)
                {
                    
                    width.setValue(String.valueOf((int)iv.getFitWidth()));
                    height.setValue(String.valueOf((int)iv.getFitHeight()));
                    pos.setValue(String.valueOf((int)iv.getLayoutX()+","+(int)iv.getLayoutY()));
                    
                }
                else
                {
                    width.setValue("null");
                    height.setValue("null");
                    pos.setValue(String.valueOf("0,0"));
                }
                background.setAttributeNode(bgs);
                background.setAttributeNode(width);
                background.setAttributeNode(height);
                background.setAttributeNode(pos);
            }
            else
            {
                isnull.setValue("true");
            }
            background.setAttributeNode(isnull); 
            root.appendChild(background);
            
            TransformerFactory tfc=TransformerFactory.newInstance();
            Transformer tf=tfc.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(file);
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

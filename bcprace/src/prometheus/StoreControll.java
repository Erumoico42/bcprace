/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus;

import prometheus.Police.Police;
import prometheus.Store.BackgroundStore;
import prometheus.Store.ConnectStore;
import prometheus.Store.CurveStore;
import prometheus.Store.LightsStore;
import prometheus.Store.PoliceStore;
import prometheus.Store.SegmentStore;
import prometheus.Street.Connect;
import prometheus.Street.MyCurve;
import prometheus.Street.StreetSegment;
import prometheus.TrafficLights.TrafficLight;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Honza
 */
public class StoreControll {
    public static void loader(File input)
    {
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc;
            doc = db.parse(input);
            doc.getDocumentElement().normalize();
            
            DrawControll.setEnableSplit(false);
            LightsStore ls=new LightsStore(doc, null);
            ls.loadLights();
            PoliceStore ps=new PoliceStore(doc, null);
            ps.loadPolice();
            SegmentStore ss=new SegmentStore(null, doc);
            ss.loadSegments(ls.getLights(), ps.getPolCombs());
            ConnectStore cs=new ConnectStore(doc, null);
            cs.loadConnects();
            CurveStore cus=new CurveStore(doc, null, prometheus.Prometheus.getDrawControll());
            cus.loadCurves(ss.getLoadedSegments(), cs.getLoadedConnects());
            
            new BackgroundStore(doc, null).loadBackground();
            //load commands (doc);
            DrawControll.setEnableSplit(true);
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        } catch (SAXException ex) {
            throw new Error(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        } 
    }
    public static void saver(List<MyCurve> curves, List<Connect> connects, List<TrafficLight> semafory,List<Police> polices, File file, String bgSource, HBox bg)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc=db.newDocument();
            Element root=doc.createElement("root");
            doc.appendChild(root);
            
            new CurveStore(doc, root, prometheus.Prometheus.getDrawControll()).saveCurve(curves);
            new ConnectStore(doc, root).saveConnects(connects);
            new SegmentStore(root, doc).saveSegments(curves);
            new LightsStore(doc, root).saveLights(semafory);
            new PoliceStore(doc, root).savePolice(polices);
            new BackgroundStore(doc, root).saveBackground(bgSource, bg);
            //save commands (doc, root)
            TransformerFactory tfc=TransformerFactory.newInstance();
            Transformer tf=tfc.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(file);
            tf.transform(source, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(StoreControll.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(StoreControll.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(StoreControll.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveFile(List<MyCurve> curves, List<Connect> connects, List<TrafficLight> semafory,List<Police> poldas, String bgSource, HBox background)
    {
        FileChooser fch=new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showSaveDialog(null);
        if (file != null) { 
            saver(curves, connects, semafory,poldas, file, bgSource, background);
        }
    }
    public static void openFile()
    {
        FileChooser fch=new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML soubory (*.xml)", "*.xml");
        fch.getExtensionFilters().add(filter);
        File file = fch.showOpenDialog(null);
        if (file != null) { 
            DrawControll.clean();
            loader(file);
            
        } 
    }
}

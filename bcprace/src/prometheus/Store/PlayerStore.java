/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import prometheus.Player;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class PlayerStore {
    private static Document doc;
    private static Element root;
    private List<Player> players=new ArrayList<>();
    private File input;
    public PlayerStore() {
    }
    
    public void loadTopList()
    {
        try {
            String path=Prometheus.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path=path.substring(0, path.lastIndexOf("/"))+"/topList.xml";
            path=path.replaceAll("/", "\\\\");
            input=new File(path);
            
            if(input.exists()){
                
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                DocumentBuilder db=dbf.newDocumentBuilder();
                doc = db.parse(input);
                doc.getDocumentElement().normalize();
                loadPlayers();
            }
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public List<Player> getPlayers()
    {
        return players;
    }
    public void saveTopList(Player player)
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            
            if(!input.exists()){
                input.createNewFile();
                System.out.println(input.getPath());
                doc=db.newDocument();
                root=doc.createElement("root");
                doc.appendChild(root);
            }
            else
            {
                doc = db.parse(input);
                root = doc.getDocumentElement();
                
            }
            savePlayer(player);
            TransformerFactory tfc=TransformerFactory.newInstance();
            Transformer tf=tfc.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(input);
            tf.transform(source, result);
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PlayerStore.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private void loadPlayers()
    {
        NodeList players=doc.getElementsByTagName("player");
        for (int i = 0; i < players.getLength(); i++) {
            Node player=players.item(i);    
            
            String name=player.getAttributes().getNamedItem("name").getNodeValue();
            double avgTime=Double.valueOf(player.getAttributes().getNamedItem("avgTime").getNodeValue());
            int totalRuns=Integer.valueOf(player.getAttributes().getNamedItem("totalRuns").getNodeValue());
            int crashes=Integer.valueOf(player.getAttributes().getNamedItem("crashes").getNodeValue());
            int lightsRun=Integer.valueOf(player.getAttributes().getNamedItem("lightsRun").getNodeValue());
            int policeRun=Integer.valueOf(player.getAttributes().getNamedItem("policeRun").getNodeValue());
            int rightsRun=Integer.valueOf(player.getAttributes().getNamedItem("rightsRun").getNodeValue());
            String minTime=player.getAttributes().getNamedItem("minTime").getNodeValue();
            String maxTime=player.getAttributes().getNamedItem("maxTime").getNodeValue();
            String mark=player.getAttributes().getNamedItem("mark").getNodeValue();
            String date=player.getAttributes().getNamedItem("date").getNodeValue();
            Player p=new Player(name, crashes, totalRuns, lightsRun, policeRun, rightsRun, avgTime, minTime, maxTime, mark, date);
            this.players.add(p);
        }         
    }

    private void savePlayer(Player pla)
    {
        Element player=doc.createElement("player");

        Attr name=doc.createAttribute("name");
        name.setValue(pla.getName());
        player.setAttributeNode(name);
        
        Attr avgTime=doc.createAttribute("avgTime");
        avgTime.setValue(String.valueOf(pla.getAvgTime()));
        player.setAttributeNode(avgTime);
        
        Attr totalRuns=doc.createAttribute("totalRuns");
        totalRuns.setValue(String.valueOf(pla.getTotalRuns()));
        player.setAttributeNode(totalRuns);
        
        Attr crashes=doc.createAttribute("crashes");
        crashes.setValue(String.valueOf(pla.getTotalCrashes()));
        player.setAttributeNode(crashes);
        
        Attr lightsRun=doc.createAttribute("lightsRun");
        lightsRun.setValue(String.valueOf(pla.getLightsRuns()));
        player.setAttributeNode(lightsRun);
        
        Attr policeRun=doc.createAttribute("policeRun");
        policeRun.setValue(String.valueOf(pla.getPolicesRuns()));
        player.setAttributeNode(policeRun);
        
        Attr rightsRun=doc.createAttribute("rightsRun");
        rightsRun.setValue(String.valueOf(pla.getLightsRuns()));
        player.setAttributeNode(rightsRun);
        
        Attr minTime=doc.createAttribute("minTime");
        minTime.setValue(String.valueOf(pla.getMinTimeStr()));
        player.setAttributeNode(minTime);
        
        Attr maxTime=doc.createAttribute("maxTime");
        maxTime.setValue(String.valueOf(pla.getMaxTimeStr()));
        player.setAttributeNode(maxTime);
        
        Attr mark=doc.createAttribute("mark");
        mark.setValue(String.valueOf(pla.getMark()));
        player.setAttributeNode(mark);
        
        Attr date=doc.createAttribute("date");
        date.setValue(String.valueOf(pla.getDate()));
        player.setAttributeNode(date);
        
        root.appendChild(player);
    }
}

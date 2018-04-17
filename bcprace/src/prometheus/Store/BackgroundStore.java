/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import java.awt.Point;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.DrawControll;
import prometheus.Prometheus;

/**
 *
 * @author Honza
 */
public class BackgroundStore {
    private static Document doc;
    private static Element root;

    public BackgroundStore(Document doc, Element root) {
        this.doc=doc;
        this.root=root;
    }
    
    public static void saveBackground(String bgSource, HBox bg)
    {
        
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
                if(bg!=null)
                {
                    
                    width.setValue(String.valueOf((int)bg.getWidth()));
                    height.setValue(String.valueOf((int)bg.getHeight()));
                    pos.setValue(String.valueOf((int)bg.getLayoutX()+","+(int)bg.getLayoutY()));
                    
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
    }
    public static void loadBackground()
    {
        
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
                Prometheus.getDrawControll().loadBackground(new Image(source));
                Prometheus.getDrawControll().moveDefBg(position.getX(), position.getY(), Double.valueOf(width), Double.valueOf(height));
                Prometheus.getDrawControll().setBgSource(source);            
            }
            DrawControll.lockBackground(true);
    }
}

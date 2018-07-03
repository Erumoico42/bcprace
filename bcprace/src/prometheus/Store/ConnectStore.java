/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import prometheus.Street.Connect;
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
public class ConnectStore {
    private Document doc;
    private Element root;
    private List<Connect> loadedConnects=new ArrayList<>();

    public ConnectStore(Document doc, Element root) {
        this.doc = doc;
        this.root = root;
    }
    public void saveConnects(List<Connect> connects)
    {
        for (Connect connect : connects) {
            Element conn=doc.createElement("connect"); 
            
            Attr idConnect=doc.createAttribute("idConnect");
            idConnect.setValue(String.valueOf(connect.getId()));
            conn.setAttributeNode(idConnect);

            /*
            Attr start=doc.createAttribute("start");
            start.setValue(String.valueOf(connect.getStart()));
            conn.setAttributeNode(start);
            */
            Attr tram=doc.createAttribute("tram");
            tram.setValue(String.valueOf(connect.isTram()));
            conn.setAttributeNode(tram);
            
            Attr position=doc.createAttribute("position");
            position.setValue(String.valueOf((int)connect.getPoint().getX()+","+(int)connect.getPoint().getY()));
            conn.setAttributeNode(position);
            root.appendChild(conn);
        }
    }
    public void loadConnects()
    {
        NodeList conn=doc.getElementsByTagName("connect");
        for (int i = 0; i < conn.getLength(); i++) {
            
            Node con=conn.item(i);           
            int idConn=Integer.parseInt(con.getAttributes().getNamedItem("idConnect").getNodeValue());
            
            boolean tram=Boolean.parseBoolean(con.getAttributes().getNamedItem("tram").getNodeValue());
            String p=con.getAttributes().getNamedItem("position").getNodeValue();
            String[] s=p.split(",");
            Point pp=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
            
            Connect connect=new Connect(pp, tram);
            connect.setId(idConn);
            
            loadedConnects.add(connect);
            DrawControll.addConnect(connect);
        }
    }
    public List<Connect> getLoadedConnects()
    {
        return loadedConnects;
    }
}

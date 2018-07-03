/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.Store;

import prometheus.Police.Police;
import prometheus.Police.PoliceCombin;
import prometheus.Police.PoliceSide;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prometheus.PoliceControll;

/**
 *
 * @author Honza
 */
public class PoliceStore {
    private Document doc;
    private Element root;
    private List<PoliceSide> policeSides;
    private List<PoliceCombin> policeCombs;

    public PoliceStore(Document doc, Element root) {
        this.doc=doc;
        this.root=root;
    }
    public List<PoliceCombin> getPolCombs()
    {
        return policeCombs;
    }
    public void loadPolice()
    {
        policeCombs=new ArrayList<>();
        
        NodeList pol=doc.getElementsByTagName("police");
        for (int i = 0; i < pol.getLength(); i++) {
            Node police=pol.item(i);
            Police polda=new Police();
            //PoliceControll.addPolice(polda);
            //Prometheus.addPolicie(polda);
            String p=police.getAttributes().getNamedItem("position").getNodeValue();
            polda.setDeley(Integer.parseInt(police.getAttributes().getNamedItem("waitTime").getNodeValue()));
            String[] s=p.split(",");
            polda.move(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
            
            NodeList policeSide=((Element)police).getElementsByTagName("policeSide");
            policeSides=new ArrayList<>();
            for (int j = 0; j < policeSide.getLength(); j++) {
                
                p=policeSide.item(j).getAttributes().getNamedItem("policeSidePosition").getNodeValue();
                s=p.split(",");
                Point point=new Point(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
                int idPoliceSide=Integer.valueOf(policeSide.item(j).getAttributes().getNamedItem("idPoliceSide").getNodeValue());
                PoliceSide ps=new PoliceSide(point, polda);
                ps.setId(idPoliceSide);
                polda.addSide(ps);
                policeSides.add(ps);
                PoliceControll.addPolice(polda);
            }
            NodeList policeKombin=((Element)police).getElementsByTagName("policeKombin");
            for (int j = 0; j < policeKombin.getLength(); j++) {
                int idPoliceKombin=Integer.parseInt(policeKombin.item(j).getAttributes().getNamedItem("idPoliceKombin").getNodeValue());
                int policeKombinSide1=Integer.parseInt(policeKombin.item(j).getAttributes().getNamedItem("policeKombinSide1").getNodeValue());
                int policeKombinSide2=Integer.parseInt(policeKombin.item(j).getAttributes().getNamedItem("policeKombinSide2").getNodeValue());
                PoliceSide ps1=getPSbyId(policeKombinSide1);
                PoliceSide ps2=getPSbyId(policeKombinSide2);
                PoliceCombin pk=new PoliceCombin(polda, ps1, ps2);
                pk.setId(idPoliceKombin);
                pk.setTime(Integer.parseInt(policeKombin.item(j).getAttributes().getNamedItem("policeKombinTime").getNodeValue()));
                policeCombs.add(pk);
                polda.addKomb(pk);
            }
        }
    }
    private PoliceSide getPSbyId(int id)
    {
        for (PoliceSide policeSide : policeSides) {
            if(policeSide.getId()==id)
                return policeSide;
        }
        return null;
    }
    public void savePolice(List<Police> polices)
    {
        for (Police p : polices) {
            Element police=doc.createElement("police");
            
            Attr position=doc.createAttribute("position");
            position.setValue(String.valueOf((int)p.getPoz().getX()+","+(int)p.getPoz().getY()));
            police.setAttributeNode(position);
            Attr waitTime=doc.createAttribute("waitTime");
            waitTime.setValue(String.valueOf(p.getDeley()));
            police.setAttributeNode(waitTime);
            
            for (PoliceSide ps : p.getStrany()) {
                Element policeSide=doc.createElement("policeSide");
                Attr idPoliceSide=doc.createAttribute("idPoliceSide");
                idPoliceSide.setValue(String.valueOf(ps.getId()));
                policeSide.setAttributeNode(idPoliceSide);
                Attr policeSidePosition=doc.createAttribute("policeSidePosition");
                policeSidePosition.setValue(String.valueOf(String.valueOf((int)ps.getPoint().getX()+","+(int)ps.getPoint().getY())));
                policeSide.setAttributeNode(policeSidePosition);
                police.appendChild(policeSide);
            }
            for (PoliceCombin pk : p.getPk()) {
                Element policeKombin=doc.createElement("policeKombin");
                Attr idPoliceKombin=doc.createAttribute("idPoliceKombin");
                idPoliceKombin.setValue(String.valueOf(pk.getId()));
                policeKombin.setAttributeNode(idPoliceKombin);
                
                Attr policeKombinSide1=doc.createAttribute("policeKombinSide1");
                policeKombinSide1.setValue(String.valueOf(pk.getPs1().getId()));
                policeKombin.setAttributeNode(policeKombinSide1);
                
                Attr policeKombinSide2=doc.createAttribute("policeKombinSide2");
                policeKombinSide2.setValue(String.valueOf(pk.getPs2().getId()));
                policeKombin.setAttributeNode(policeKombinSide2);
                
                Attr policeKombinTime=doc.createAttribute("policeKombinTime");
                policeKombinTime.setValue(String.valueOf(pk.getTime()));
                policeKombin.setAttributeNode(policeKombinTime);
                
                police.appendChild(policeKombin);
            }

            root.appendChild(police);
        }
    }
    
}

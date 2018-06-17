/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Honza
 */
public class MenuGroup {
    private final BorderPane mainBorder;
    private final Group group;
    private final Label lbl;
    private final SubScene groupScene;
    public MenuGroup(String label) {
        group=new Group();
        lbl=new Label(label);
        lbl.setLayoutY(53);
        groupScene=new SubScene(group, 100, 70);
        mainBorder = new BorderPane();
        mainBorder.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(1),
            new BorderWidths(1))));
        mainBorder.setLayoutY(-1);
        mainBorder.setCenter(groupScene);
        group.getChildren().addAll(lbl);
    }
    public void setWidth(double width)
    {
        groupScene.setWidth(width);
    }
    public void setLblLayout(double lay)
    {
        lbl.setLayoutX(lay);
    }
    public void setLayout(double lay)
    {
        mainBorder.setLayoutX(lay);
    }
    public void addItems(Node ... nodes)
    {
        group.getChildren().addAll(nodes);
    }

    public Group getGroup() {
        return group;
    }
    
    public BorderPane getMenuGroup()
    {
        return mainBorder;
    }
    
}

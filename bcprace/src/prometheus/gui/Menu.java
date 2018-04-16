/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import prometheus.GuiControll;

/**
 *
 * @author Honza
 */
public class Menu {
    private final Group maingGroup, flapGroup, groupGroup;
    private final SubScene menu, flaps, groups;
    private Border flapBorder;
    private final double deselectHeight=22, deselectLayout=6;
    private final double selectHeight=28, selectLayout=3;
    public Menu() {
        maingGroup=new Group();
        menu = new SubScene(maingGroup, 100, 0);
        
        BorderPane mainBorder = new BorderPane();
        mainBorder.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(2),
            new BorderWidths(1))));
        mainBorder.setCenter(maingGroup);
        
        
        flapGroup=new Group();
        flaps=new SubScene(flapGroup, 600, 30);
        
        groupGroup=new Group();
        groups=new SubScene(groupGroup, 600, 71);
        
        BorderPane groupBorder = new BorderPane();
        groupBorder.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(2),
            new BorderWidths(1.5))));
        groupBorder.setCenter(groups);
        groupBorder.setLayoutY(29);
        initBorderSelected();
        maingGroup.getChildren().addAll(groupBorder, flaps);
    }
    public void addFlaps(MenuFlap ... flaps)
    {
        for (MenuFlap flap : flaps) {
            Button btn=flap.getFlap();
            deselect(btn);
            flap.setAction(getActionOnClick(flap));
            flapGroup.getChildren().add(btn);
        }
        
    }
    private void initBorderSelected()
    {
        flapBorder=new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID, new CornerRadii(3),
            new BorderWidths(2)));
    }
    public EventHandler getActionOnClick(MenuFlap mf)
    {
        return new EventHandler() {
            @Override
            public void handle(Event event) {
                changeContent(mf);
            }
        };
    }
    public void changeContent(MenuFlap mf)
    {
        if(GuiControll.getActualMenuFlap()!=null)
            deselect(GuiControll.getActualMenuFlap().getFlap());
        GuiControll.setActualMenuFlap(mf);
        select(mf.getFlap());
        groupGroup.getChildren().clear();
        groupGroup.getChildren().add(mf.getContent());
    }
    public Group getMenu()
    {
        return maingGroup;
    }
    private void select(Button btn)
    {
        btn.setBorder(flapBorder);
        btn.setMinHeight(selectHeight);
        btn.setMaxHeight(selectHeight);
        btn.setLayoutY(selectLayout);
    }
    private void deselect(Button btn)
    {
        btn.setBorder(Border.EMPTY);
        btn.setMaxHeight(deselectHeight);
        btn.setMinHeight(deselectHeight);
        btn.setLayoutY(deselectLayout);
    }
    public void changeWidth(double wid)
    {
        menu.setWidth(wid-18);
        flaps.setWidth(wid-18);
        groups.setWidth(wid-19);
    }
}

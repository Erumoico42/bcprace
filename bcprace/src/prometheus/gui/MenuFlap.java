/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prometheus.gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Honza
 */
public class MenuFlap {
    private final Button flap;
    private final Group content;
    public MenuFlap(String name) {
        flap=new Button(name);
        content=new Group();
    }
    public void setAction(EventHandler e)
    {
        flap.setOnAction(e);
    }
    public void addGroups(MenuGroup ... groups)
    {
        for (MenuGroup group : groups) {
            content.getChildren().add(group.getMenuGroup());
        }
        
    }
    public Group getContent()
    {
        return content;
    }
    public Button getFlap()
    {
        return flap;
    }
    
}

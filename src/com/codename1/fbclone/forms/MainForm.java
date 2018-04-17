package com.codename1.fbclone.forms;

import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.layouts.BorderLayout;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.GridLayout;

public class MainForm extends Form {
    private Tabs mainUI = new Tabs();
    public MainForm() {
        super("", new BorderLayout());
        mainUI.hideTabs();
        mainUI.addTab("", new CameraContainer());
        mainUI.addTab("", new NewsfeedContainer());
        mainUI.addTab("", new FriendsContainer());
        mainUI.addTab("", new NotificationsContainer());
        mainUI.addTab("", new MoreContainer());
        mainUI.setSelectedIndex(1, false);
        add(CENTER, mainUI);
        
        getToolbar().addMaterialCommandToLeftBar("", 
                FontImage.MATERIAL_CAMERA_ALT, e -> {});
        getToolbar().addMaterialCommandToRightBar("", 
                FontImage.MATERIAL_CHAT, e -> {});
        Button searchButton = new Button("Search", "TitleSearch");
        FontImage.setMaterialIcon(searchButton, 
                FontImage.MATERIAL_SEARCH);
        getToolbar().setTitleComponent(searchButton);
        
        ButtonGroup gp = new ButtonGroup();
        RadioButton newsfeed = 
                createToggle(FontImage.MATERIAL_WEB, gp, 1);
        RadioButton friends = 
                createToggle(FontImage.MATERIAL_PEOPLE_OUTLINE, gp, 2);
        RadioButton notifications = createToggle(
                FontImage.MATERIAL_NOTIFICATIONS_NONE, gp, 3);
        RadioButton more = 
                createToggle(FontImage.MATERIAL_MENU, gp, 4);
        
        Container buttons = 
                GridLayout.encloseIn(4, newsfeed, friends, notifications, more);
        if(mainUI.getTabPlacement() == TOP) {
            add(NORTH, buttons);
        } else {
            add(SOUTH, buttons);
        }
    }
    
    private RadioButton createToggle(char icon, ButtonGroup gp, 
            int offset) {
        RadioButton rb = RadioButton.createToggle("", gp);
        rb.setUIID("MainTab");
        FontImage.setMaterialIcon(rb, icon);
        rb.setSelected(offset == 1);
        rb.addActionListener(e -> mainUI.setSelectedIndex(offset));
        return rb;
    }
}

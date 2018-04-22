package com.codename1.fbclone.forms;

import com.codename1.ui.Button;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Tabs;
import com.codename1.ui.layouts.BorderLayout;

public class MainForm extends Form {
    private Tabs mainUI = new Tabs();
    public MainForm() {
        super("", new BorderLayout());
        mainUI.addTab("", FontImage.MATERIAL_WEB, 5f, 
                new NewsfeedContainer());
        mainUI.addTab("", FontImage.MATERIAL_PEOPLE_OUTLINE, 5f, 
                new FriendsContainer());
        mainUI.addTab("", FontImage.MATERIAL_NOTIFICATIONS_NONE, 
                5f, new NotificationsContainer());
        mainUI.addTab("", FontImage.MATERIAL_MENU, 5f,
                new MoreContainer());
        add(CENTER, mainUI);
        
        getToolbar().addMaterialCommandToLeftBar("", 
                FontImage.MATERIAL_CAMERA_ALT, 4, e -> {});
        getToolbar().addMaterialCommandToRightBar("", 
                FontImage.MATERIAL_CHAT, 4, e -> {});
        Button searchButton = new Button("Search", "TitleSearch");
        FontImage.setMaterialIcon(searchButton, 
                FontImage.MATERIAL_SEARCH);
        getToolbar().setTitleComponent(searchButton);
    }
}

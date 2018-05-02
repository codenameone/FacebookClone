package com.codename1.fbclone.forms;

import com.codename1.components.FloatingActionButton;
import com.codename1.contacts.Contact;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.startThread;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
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
        
        
        FloatingActionButton fab = 
                FloatingActionButton.createFAB(FontImage.MATERIAL_SYNC);
        Container friends = fab.bindFabToContainer(new FriendsContainer());
        fab.addActionListener(e -> uploadContacts());
        
        mainUI.addTab("", FontImage.MATERIAL_PEOPLE_OUTLINE, 5f, 
                friends);
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
    
    private void uploadContacts() {
        startThread(() -> {
            Contact[] cnt = Display.getInstance().
                    getAllContacts(true, true, false, true, true, false);
            ServerAPI.uploadContacts(cnt);
        }, "ContactUploader").start();
    }
}

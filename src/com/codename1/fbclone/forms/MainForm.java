package com.codename1.fbclone.forms;

import com.codename1.components.FloatingActionButton;
import com.codename1.contacts.Contact;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.startThread;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.Form;
import com.codename1.ui.Tabs;
import com.codename1.ui.layouts.BorderLayout;

public class MainForm extends Form {
    private Tabs mainUI = new Tabs();
    public MainForm() {
        super("", new BorderLayout());
        mainUI.addTab("", MATERIAL_WEB, 5f, new NewsfeedContainer());
        
        FloatingActionButton fab = 
               FloatingActionButton.createFAB(MATERIAL_IMPORT_CONTACTS);
        Container friends = fab.bindFabToContainer(new FriendsContainer());
        fab.addActionListener(e -> uploadContacts());
        
        mainUI.addTab("", MATERIAL_PEOPLE_OUTLINE, 5f, 
                friends);
        mainUI.addTab("", MATERIAL_NOTIFICATIONS_NONE, 
                5f, new NotificationsContainer());
        mainUI.addTab("", MATERIAL_MENU, 5f,
                new MoreContainer());
        add(CENTER, mainUI);
        
        getToolbar().addMaterialCommandToLeftBar("", 
                MATERIAL_CAMERA_ALT, 4, e -> {});
        getToolbar().addMaterialCommandToRightBar("", 
                MATERIAL_CHAT, 4, e -> {});
        Button searchButton = new Button("Search", "TitleSearch");
        setMaterialIcon(searchButton, MATERIAL_SEARCH);
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

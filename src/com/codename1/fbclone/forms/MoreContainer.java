package com.codename1.fbclone.forms;

import com.codename1.components.MultiButton;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.Style;

public class MoreContainer extends Container {
    public MoreContainer() {
        super(BoxLayout.y());
        setUIID("HalfPaddedContainer");
        setScrollableY(true);
        MultiButton me = new MultiButton(ServerAPI.me().fullName());
        me.setTextLine2("View your profile");
        me.setIcon(ServerAPI.me().getAvatar(9f));
        me.setIconUIID("PaddedContainer");
        me.setUIID("Container");
        me.getAllStyles().setBorder(Border.createUnderlineBorder(2, 0xcccccc));
        me.setUIIDLine1("MultiLine1WithMargin");
        me.setLinesTogetherMode(true);
        add(me);

        add(new Label("Favorites", "FriendSubtitle"));
        
        addAll(
                createButton("Find Friends", 
                        FontImage.MATERIAL_PEOPLE, 0x4080FF),
                createButton("Discover People", 
                        FontImage.MATERIAL_PHOTO_ALBUM, 0x54C7EC),
                createButton("Events", FontImage.MATERIAL_EVENT, 
                        0xF35369),
                createButton("Instagram", 
                        FontImage.MATERIAL_PEOPLE, 0xF35369),
                createButton("Recommendations", 
                        FontImage.MATERIAL_FAVORITE, 0xF35369),
                createButton("Install Messenger", 
                        FontImage.MATERIAL_CHAT, 0x4267B2),
                createButton("Crisis Response", 
                        FontImage.MATERIAL_WARNING, 0x4DBBA6));        
    }
    
    private MultiButton createButton(String title, char icon, int color) {
        MultiButton b = new MultiButton(title);
        b.setUIID("Container");
        b.setIcon(FontImage.createMaterial(icon, "LargeCircleIcon", 5f));
        b.setIconUIID("LargeCircleIcon");
        b.getIconComponent().getAllStyles().setBorder(RoundBorder.create().
                color(color));
        
        return b;
    }
}

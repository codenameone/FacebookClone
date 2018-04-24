package com.codename1.fbclone.forms;

import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Form;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;

public class NewPostForm extends Form {
    private static final String[] POST_STYLES = { 
        "Label", "PostStyleHearts", "PostStyleHands", "PostStyleBlack", 
        "PostStyleRed", "PostStylePurple" };
    public NewPostForm() {
        super("Create Post", new BorderLayout());
        Form current = getCurrentForm();
        getToolbar().setBackCommand("Cancel", 
                Toolbar.BackCommandPolicy.
                        WHEN_USES_TITLE_OTHERWISE_ARROW, 
                e -> current.showBack());
        getToolbar().addMaterialCommandToRightBar("", 
                FontImage.MATERIAL_DONE, e -> {});
        User me = ServerAPI.me();
        Container userSettings = BorderLayout.west(
                new Label(me.getAvatar(6.5f), "HalfPaddedContainer"));
        Button friends = new Button("Friends", "FriendCombo");
        FontImage.setMaterialIcon(friends, FontImage.MATERIAL_PEOPLE);
        userSettings.add(CENTER,
                BoxLayout.encloseY(
                        new Label(me.fullName(), "MultiLine1"),
                        FlowLayout.encloseIn(friends)));
        add(NORTH, userSettings);
        TextArea post = new TextArea(3, 80);
        post.setUIID("Label");
        post.setGrowByContent(false);
        Container postStyles = createPostStyles(post);
        add(CENTER, LayeredLayout.encloseIn(
                BorderLayout.north(post), BorderLayout.south(postStyles)));
        setEditOnShow(post);
    }
    
    
    private Container createPostStyles(TextArea post) {
        Container postStyles = new Container(BoxLayout.x());
        postStyles.setScrollableX(true);
        int size = convertToPixels(8);
        ButtonGroup bg = new ButtonGroup();
        for(String s : POST_STYLES) {
            Button postStyleButton = RadioButton.createToggle("", bg);
            postStyleButton.setShowEvenIfBlank(true);
            postStyleButton.setUIID(s);
            Style stl = postStyleButton.getAllStyles();
            stl.setBorder(RoundRectBorder.create());
            stl.setPadding(3, 3, 3, 3);
            stl.setMarginUnit(Style.UNIT_TYPE_DIPS);
            stl.setMargin(1, 1, 1, 1);
            int strokeColor = 0xffffff;
            if(s.equals("Label")) {
                stl.setBgTransparency(255);
                stl.setBgColor(0xffffff);
                strokeColor = 0;
            } 
            postStyleButton.getPressedStyle().setBorder(RoundRectBorder.
                    create().strokeColor(strokeColor).
                    strokeOpacity(255).
                    stroke(0.5f, true));
            postStyleButton.addActionListener(e -> changeStyle(post, s));
            postStyleButton.setPreferredSize(new Dimension(size, size));
            postStyles.add(postStyleButton);
        }
        return postStyles;        
    }

    private void changeStyle(TextArea post, String s) {
        if(s.equals("Label")) {
            post.setUIID(s);
            post.getParent().setUIID("Container");
        } else {
            post.setUIID("PostStyleText");
            post.getParent().setUIID(s);
        }
        revalidate();
    }
}

package com.codename1.fbclone.forms;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ScaleImageLabel;
import com.codename1.components.SliderBridge;
import com.codename1.components.ToastBar;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.io.MultipartRequest;
import com.codename1.media.Media;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Form;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Slider;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.plaf.Style;

public class NewPostForm extends Form {
    private static final String[] POST_STYLES = { 
        "Label", "PostStyleHearts", "PostStyleHands", "PostStyleBlack", 
        "PostStyleRed", "PostStylePurple" };
    private TextArea post = new TextArea(3, 80);
    private String postStyleValue;
    private String attachment;
    private String mime;
    private Button postButton;
    private NewPostForm() {
        super("Create Post", new BorderLayout());
    }
    
    private void initUI(Container postStyles) {
        Form current = getCurrentForm();
        getToolbar().setBackCommand("Cancel", 
                Toolbar.BackCommandPolicy.
                        WHEN_USES_TITLE_OTHERWISE_ARROW, 
                e -> current.showBack());
        Command c = getToolbar().addMaterialCommandToRightBar("", 
                FontImage.MATERIAL_DONE, e -> post(current));
        postButton = getToolbar().findCommandComponent(c);
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
        post.setUIID("Label");
        post.setGrowByContent(false);
        Component l;
        if(postStyles != null) {
            l = LayeredLayout.encloseIn(
                    BorderLayout.north(post), postStyles);
        } else {
            l = post;
        }
        add(CENTER, l);
        setEditOnShow(post);        
    }
    
    public static NewPostForm createPost() {
        NewPostForm n = new NewPostForm();
        Container postStyles = n.createPostStyles(n.post);
        n.initUI(BorderLayout.south(postStyles));
        return n;
    }
    
    public static NewPostForm createImagePost(EncodedImage img, 
            ImagePicker p) {
        NewPostForm n = new NewPostForm();
        n.initUI(null);
        n.mime = "image/jpeg";
        ScaleImageLabel i = new ScaleImageLabel(img) {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(getDisplayWidth(), 
                    getDisplayHeight() / 2);
            }
        };
        Slider s = n.upload(p);
        n.add(SOUTH, LayeredLayout.encloseIn(
            i, BorderLayout.south(s)));
        return n;
    }

    public static NewPostForm createVideoPost(Media m, ImagePicker p) {
        NewPostForm n = new NewPostForm();
        n.initUI(null);
        Slider s = n.upload(p);
        n.mime = "video/mp4";
        Container videoContainer = new Container(new LayeredLayout()) {
            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(getDisplayWidth(), 
                    getDisplayHeight() / 2);
            }            
        };
        videoContainer.add(m.getVideoComponent());
        videoContainer.add(BorderLayout.south(s));
        n.add(SOUTH, videoContainer);
        n.addShowListener(e -> {
            m.play();
            m.setVolume(0);
            m.setTime(Math.min(m.getDuration() / 2, 1000));
            m.pause();
        });
        return n;
    }

    private Slider upload(ImagePicker p) {
        postButton.setEnabled(false);
        Slider s = new Slider();
        MultipartRequest m = p.upload(e -> {
            attachment = e;
            postButton.setEnabled(true);
        });
        SliderBridge.bindProgress(m, s);
        return s;
    }
        
    private void post(Form previousForm) {
        Dialog dlg = new InfiniteProgress().showInifiniteBlocking();
        Post p = new Post().
                content.set(post.getText()).
                visibility.set("public").
                styling.set(postStyleValue);
        if(attachment != null) {
            p.attachments.put(attachment, mime);
        }
        if(!ServerAPI.post(p)) {
            dlg.dispose();
            ToastBar.showErrorMessage("Error posting to server");
            return;
        }
        previousForm.showBack();
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
            postStyleValue = null;
        } else {
            post.setUIID("PostStyleText");
            post.getParent().setUIID(s);
            postStyleValue = s;
        }
        revalidate();
    }
}

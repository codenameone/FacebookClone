package com.codename1.fbclone.forms;

import com.codename1.components.SpanLabel;
import com.codename1.fbclone.components.RichTextView;
import com.codename1.fbclone.data.Comment;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsfeedContainer extends InfiniteContainer {
    private long lastTime;

    @Override
    public Component[] fetchComponents(int index, int amount) {        
        ArrayList<Component> components = new ArrayList<>();
        if(lastTime == 0 || index == 0) {
            lastTime = System.currentTimeMillis();
            components.add(createPostBar());
            components.add(UIUtils.createSpace());
        }
        List<Post> response = ServerAPI.fetchTimelinePosts(lastTime, amount);
        if(response == null) {
            return null;
        }
            
        for(Post p : response) {
            components.add(createNewsItem(p.user.get(), p));
            components.add(UIUtils.createHalfSpace());
            lastTime = p.date.getLong();
        }
        Component[] cmps = new Component[components.size()];
        components.toArray(cmps);
        return cmps;
    }
    
    private Container createNewsItem(User u, Post p) {
        Button avatar = new Button("", u.getAvatar(6.5f), "CleanButton");
        Button name = new Button(u.fullName(), "PostTitle");
        Button postTime = new Button(formatRelativeTime(p.date.get()), 
                "PostSubTitle");
        Button menu = new Button("", "Label");
        FontImage.setMaterialIcon(menu, 
                FontImage.MATERIAL_MORE_HORIZ);
        Container titleArea = BorderLayout.centerEastWest(
                FlowLayout.encloseMiddle(BoxLayout.encloseY(name, postTime)), 
                FlowLayout.encloseIn(menu), avatar);
        titleArea.setUIID("HalfPaddedContainer");
        Component body;
        if(p.content.get().indexOf('<') > -1) {
            body = new RichTextView(p.content.get());
        } else {
            body = new SpanLabel(p.content.get());
        }
        body.setUIID("HalfPaddedContainer");

        
        Button like = new Button("Like", "CleanButton");
        Button comment = new Button("Comment", "CleanButton");
        Button share = new Button("Share", "CleanButton");
        FontImage.setMaterialIcon(like, FontImage.MATERIAL_THUMB_UP);
        FontImage.setMaterialIcon(comment, FontImage.MATERIAL_COMMENT);
        FontImage.setMaterialIcon(share, FontImage.MATERIAL_SHARE);
        
        Container stats = new Container(new BorderLayout(), "PaddedContainer");
        if(p.likes.size() > 0) {
            Label thumbUp = new Label("", "SmallBlueCircle");
            FontImage.setMaterialIcon(thumbUp, 
                    FontImage.MATERIAL_THUMB_UP);
            Label count = new Label("" + p.likes.size(), "SmallLabel");
            stats.add(WEST, BoxLayout.encloseX(thumbUp, count));
        }
        
        if(p.comments.size() > 0) {
            stats.add(EAST, new Label(p.comments.size() + " comments", 
                    "SmallLabel"));
        }
        
        Container buttonBar = GridLayout.encloseIn(3, like, comment, share);
        buttonBar.setUIID("HalfPaddedContainer");
        
        return BoxLayout.encloseY(titleArea, body, stats, buttonBar);
    }
    
    private String formatRelativeTime(long time) {
        long relativeTime = System.currentTimeMillis() - time;
        if(relativeTime < 60000) {
            return "Just now";
        }
        relativeTime /= 60000;
        if(relativeTime < 60) {
            return relativeTime + " minutes ago";
        }
        return L10NManager.getInstance().
                        formatDateShortStyle(new Date(time));
    }
    
    private Container createPostBar() {
        Button avatar = new Button(ServerAPI.me().getAvatar(6.5f), "Label");
        Button writePost = new Button("What's on your mind?", 
                "NewPostButton");
        Button gallery = new Button("Photo", "GalleryButton");
        FontImage.setMaterialIcon(gallery, 
                FontImage.MATERIAL_PHOTO_LIBRARY, 2.9f);
        gallery.setTextPosition(BOTTOM);
        Container c = BorderLayout.centerEastWest(writePost, gallery, avatar);
        c.setUIID("HalfPaddedContainer");
        writePost.addActionListener(e -> new NewPostForm().show());
        return c;
    }
}
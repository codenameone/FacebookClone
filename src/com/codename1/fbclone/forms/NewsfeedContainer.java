package com.codename1.fbclone.forms;

import com.codename1.components.MultiButton;
import com.codename1.components.SpanLabel;
import com.codename1.fbclone.components.RichTextView;
import com.codename1.fbclone.data.Comment;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
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
import com.codename1.ui.layouts.GridLayout;
import java.util.Date;

public class NewsfeedContainer extends InfiniteContainer {

    @Override
    public Component[] fetchComponents(int index, int amount) {        
        if(index == 0) {
            Comment firstPost = new Comment().
                    date.set(System.currentTimeMillis()).
                    text.set("First post!!!").
                    id.set("Comment1").
                    userId.set(User.me().id.get());
            
            Post dummyPost = new Post().
                    userId.set(User.me().id.get()).
                    content.set("This is a <b>POST</b> that includes HTML").
                    date.set(System.currentTimeMillis() - 60000).
                    id.set("Post1").
                    type.set("html").
                    likes.add(User.me()).
                    comments.add(firstPost);
            return new Component[] {
                createPostBar(),
                createSpace(),
                createNewsItem(User.me(), dummyPost)
            };
        }
        return null;
    }
    
    private Container createNewsItem(User u, Post p) {
        Button avatar = new Button("", u.getAvatar(), "Label");
        Button name = new Button(u.firstName.get() + " " + u.familyName.get(),
                "PostTitle");
        Button postTime = new Button(formatRelativeTime(p.date.get()), 
                "PostSubTitle");
        Button menu = new Button("", "Label");
        FontImage.setMaterialIcon(menu, 
                FontImage.MATERIAL_MORE_HORIZ);
        Container titleArea = BorderLayout.centerEastWest(
                BoxLayout.encloseY(name, postTime), menu, avatar);
        Component body;
        if(p.content.get().indexOf('<') > -1) {
            body = new RichTextView(p.content.get());
        } else {
            body = new SpanLabel(p.content.get());
        }
        
        Button like = new Button("Like", "Label");
        Button comment = new Button("Comment", "Label");
        Button share = new Button("Share", "Label");
        
        Container stats = new Container(new BorderLayout());
        if(p.likes.size() > 0) {
            Label thumbUp = new Label("", "SmallBlueCircle");
            FontImage.setMaterialIcon(thumbUp, 
                    FontImage.MATERIAL_THUMB_UP);
            Label count = new Label("" + p.likes.size());
            stats.add(WEST, BoxLayout.encloseX(thumbUp, count));
        }
        
        if(p.comments.size() > 0) {
            stats.add(EAST, new Label(p.comments.size() + " comments"));
        }
        
        Container buttonBar = GridLayout.encloseIn(3, like, comment, share);
        
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
    
    private Label createSpace() {
        Label l = new Label("", "PaddedSeparator");
        l.setShowEvenIfBlank(true);
        return l;
    }
    
    private Container createPostBar() {
        Button avatar = new Button(User.me().getAvatar());
        Button writePost = new Button("What's on your mind?", 
                "NewPostButton");
        Button gallery = new Button("Photo", "GalleryButton");
        FontImage.setMaterialIcon(gallery, 
                FontImage.MATERIAL_PHOTO_LIBRARY, 3);
        gallery.setTextPosition(BOTTOM);
        return BorderLayout.centerEastWest(writePost, gallery, avatar);
    }
}

package com.codename1.fbclone.forms;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ToastBar;
import com.codename1.fbclone.data.Comment;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.URLImage;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;

public class CommentsForm extends Form {
    private TextField commentField = new TextField(); 
    private Container comments = new Container(BoxLayout.y());
    private String replyCommentId;
    public CommentsForm(Post p, Comment replyingTo) {
        super(p.title.get(), new BorderLayout());
        
        commentField.getAllStyles().setBorder(Border.createEmpty());
        Button send = new Button("", "Label");
        Container post = BorderLayout.centerEastWest(commentField, send, 
            null);
        setMaterialIcon(send, MATERIAL_SEND);
        replyCommentId = replyingTo == null ? null : replyingTo.id.get();
        send.addActionListener(e -> postComment(p));
        commentField.setDoneListener(e -> postComment(p));
        
        Form previous = getCurrentForm();
        getToolbar().addMaterialCommandToLeftBar("", 
            MATERIAL_ARROW_BACK, e -> previous.showBack());
        for(Comment cmt : p.comments) {
            addComment(cmt);
        }
        add(SOUTH, post);
        add(CENTER, comments);
    }
    
    private void postComment(Post p) {
        Dialog ip = new InfiniteProgress().showInifiniteBlocking();
        Comment cm = new Comment().
            postId.set(p.id.get()).
            userId.set(ServerAPI.me().id.get()).
            parentComment.set(replyCommentId).
            text.set(commentField.getText());
        if(!ServerAPI.comment(cm)) {
            ip.dispose();
            ToastBar.showErrorMessage("Failed to post comment!");
            return;
        }
        ip.dispose();
        addComment(cm);
        commentField.setText("");
        animateLayout(150);
    }
    
    private void addComment(Comment cm) {
        Component c = createComment(cm);
        if(cm.parentComment.get() != null) {
            Component parent = findParentComment(cm.parentComment.get());
            if(parent != null) {
                Container chld = (Container)parent.
                    getClientProperty("child");
                if(chld == null) {
                    chld = BoxLayout.encloseY(c);
                    chld.getAllStyles().setPaddingLeft(convertToPixels(3));
                    parent.putClientProperty("child", chld);
                    int pos = comments.getComponentIndex(parent);
                    comments.addComponent(pos + 1, chld);
                } else {
                    chld.add(c);
                }
            } else {
                comments.add(c);
            }
        } else {
            comments.add(c);
        }
    }
    
    private Component createComment(Comment cm) {
        TextArea c = new TextArea(cm.text.get());
        c.setEditable(false);
        c.setFocusable(false);
        c.setUIID("Comment");
        Label avatar = new Label(getAvatarImage(cm.userId.get()));
        Container content = BorderLayout.centerEastWest(c, null, avatar);
        if(cm.id.get() != null && cm.parentComment.get() == null) {
            Button reply = new Button("reply", "SmallBlueLabel");
            content.add(SOUTH, FlowLayout.encloseRight(reply));
            reply.addActionListener(e -> replyCommentId = cm.id.get());
        }
        content.putClientProperty("comment", cm);
        return content;
    }
    
    private Component findParentComment(String id) {
        for(Component cmp : comments) {
            Comment c = (Comment)cmp.getClientProperty("comment");
            if(c != null && id.equals(c.id.get())) {
                return cmp;
            } 
        }
        return null;
    }
    
    private Image getAvatarImage(String userId) {
        int size = convertToPixels(5);
        return URLImage.createCachedImage(userId + "avatar", 
            ServerAPI.BASE_URL + "user/avatar/" + userId, 
            Image.createImage(size, size), 
            URLImage.FLAG_RESIZE_SCALE_TO_FILL);
    } 
}

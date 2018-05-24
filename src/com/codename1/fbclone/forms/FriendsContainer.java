package com.codename1.fbclone.forms;

import com.codename1.components.ToastBar;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.FontImage;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;

public class FriendsContainer extends Container {
    public FriendsContainer() {
        super(BoxLayout.y());
        setScrollableY(true);
        init();
        addPullToRefresh(() -> {
            ServerAPI.refreshMe();
            removeAll();
            init();
            revalidate();
        });
    }

    private void init() {
        int friendCount = ServerAPI.me().friendRequests.size();
        
        EncodedImage placeholder = FontImage.createMaterial(
                MATERIAL_PERSON, "Label", 18).toEncodedImage();
        
        add(createTitle("FRIEND REQUESTS", friendCount));
        if(friendCount == 0) {
            Container padded = new Container(new BorderLayout(), 
                    "PaddedContainer");
            padded.add(CENTER, 
                    new Label("No new Friend Requests", "CenterLabel"));
        } else {
            for(User u : ServerAPI.me().friendRequests) {
                Image i;
                if(u.avatar.get() != null) {
                    i = URLImage.createCachedImage(u.id.get()+"-avatar.jpg", 
                        u.avatarUrl(), placeholder, 
                        URLImage.FLAG_RESIZE_SCALE_TO_FILL);
                } else {
                    i = placeholder;
                }
                add(friendRequestEntry(u, i, true));
                add(UIUtils.createHalfSpace());
            }
        }
                
        add(UIUtils.createHalfSpace());
        add(createTitle("PEOPLE YOU MAY KNOW", 0));
        for(User u : ServerAPI.me().peopleYouMayKnow) {
            Image i;
            if(u.avatar.get() != null) {
                i = URLImage.createCachedImage(u.id.get() + "-avatar.jpg", 
                    u.avatarUrl(), placeholder, 
                    URLImage.FLAG_RESIZE_SCALE_TO_FILL);
            } else {
                i = placeholder;
            }
            add(friendRequestEntry(u, i, false));
            add(UIUtils.createHalfSpace());
        }
    }
        
    private Container friendRequestEntry(User u, Image avatar, 
            boolean request) {
        Label name = new Label(u.fullName(), "FriendName");
        Button confirm;
        Button delete;
        if(request) {
            confirm = new Button("Confirm", "FriendConfirm");
            delete = new Button("Delete", "FriendDelete");
            bindConfirmDeleteEvent(u, confirm, delete);
        } else {
            confirm = new Button("Add Friend", "FriendConfirm");
            delete = new Button("Remove", "FriendDelete");
            bindAddRemoveFriendEvent(u, confirm, delete);
        }
        Container cnt = 
                BoxLayout.encloseY(name,
                        GridLayout.encloseIn(2, confirm, delete));
        cnt.setUIID("PaddedContainer");
        return BorderLayout.centerEastWest(cnt, null, 
                new Label(avatar, "Container"));
    }    
    
    private Container findParent(Container button) {
        if(button.getParent() != this) {
            return findParent(button.getParent());
        }
        return button;
    }
    
    private void bindAddRemoveFriendEvent(
            User u, Button add, Button remove) {
        add.addActionListener(e -> {
            ServerAPI.sendFriendRequest(u.id.get());
            findParent(remove.getParent()).remove();
            animateLayout(150);
            ToastBar.showMessage("Sent friend request", 
                    FontImage.MATERIAL_INFO);
            ServerAPI.refreshMe();
        });
        remove.addActionListener(e -> {
            findParent(remove.getParent()).remove();
            animateLayout(150);
            ServerAPI.me().peopleYouMayKnow.remove(u);
            ServerAPI.update(ServerAPI.me());
        });
    }
    
    private void bindConfirmDeleteEvent(
            User u, Button add, Button remove) {
        add.addActionListener(e -> {
            findParent(remove.getParent()).remove();
            animateLayout(150);
            if(ServerAPI.acceptFriendRequest(u.id.get())) {
                ServerAPI.refreshMe();
                ToastBar.showMessage("You are now friends with " + u.fullName(), 
                        FontImage.MATERIAL_INFO);
            }
        });
        remove.addActionListener(e -> {
            findParent(remove.getParent()).remove();
            animateLayout(150);
            ServerAPI.me().friendRequests.remove(u);
            ServerAPI.update(ServerAPI.me());
        });
    }

    private Component createTitle(String title, int count) {
        Label titleLabel = new Label(title, "FriendSubtitle");
        if(count > 0) {
            Label countLabel = new Label("" + count, "SmallRedCircle");
            return FlowLayout.encloseMiddle(titleLabel, countLabel);
        }
        return titleLabel;
    }    
}

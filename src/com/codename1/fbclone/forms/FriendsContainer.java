package com.codename1.fbclone.forms;

import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Border;

public class FriendsContainer extends Container {
    public FriendsContainer() {
        super(BoxLayout.y());
        setScrollableY(true);
        init();
        addPullToRefresh(() -> {
            removeAll();
            init();
            revalidate();
        });
    }

    private void init() {
        int friendCount = ServerAPI.me().friendRequests.size();
        
        int imageSize = convertToPixels(18);
        EncodedImage placeholder = EncodedImage.createFromImage(
                Image.createImage(imageSize, imageSize), false);
        
        add(createTitle("FRIEND REQUESTS", friendCount));
        if(friendCount == 0) {
            Container padded = new Container(new BorderLayout(), 
                    "PaddedContainer");
            padded.add(CENTER, 
                    new Label("No new Friend Requests", "CenterLabel"));
        } else {
            for(User u : ServerAPI.me().friendRequests) {
                Image i = URLImage.createCachedImage(u.id.get() + "-avatar.jpg", 
                        u.avatar.get(), placeholder, 
                        URLImage.FLAG_RESIZE_SCALE_TO_FILL);
                add(friendRequestEntry(u, i, true));
                add(UIUtils.createHalfSpace());
            }
        }
                
        add(UIUtils.createHalfSpace());
        add(createTitle("PEOPLE YOU MAY KNOW", 0));
        for(User u : ServerAPI.me().peopleYouMayKnow) {
            Image i = URLImage.createCachedImage(u.id.get() + "-avatar.jpg", 
                    u.avatar.get(), placeholder, 
                    URLImage.FLAG_RESIZE_SCALE_TO_FILL);
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
        } else {
            confirm = new Button("Add Friend", "FriendConfirm");
            delete = new Button("Remove", "FriendDelete");
        }
        Container cnt = 
                BoxLayout.encloseY(name,
                        GridLayout.encloseIn(2, confirm, delete));
        cnt.setUIID("PaddedContainer");
        return BorderLayout.centerEastWest(cnt, null, 
                new Label(avatar, "Container"));
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

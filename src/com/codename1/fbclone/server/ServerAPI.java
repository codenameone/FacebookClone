package com.codename1.fbclone.server;

import com.codename1.fbclone.data.Comment;
import com.codename1.fbclone.data.Notification;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.forms.UIUtils;
import static com.codename1.ui.CN.*;
import com.codename1.ui.FontImage;
import java.util.ArrayList;
import java.util.List;

public class ServerAPI {
    private static User me;

    private static final String avatarUrl = "https://www.codenameone.com/images/diverseui-avatars/";
    private static final long initTime = System.currentTimeMillis();
    private final static User[] dummyUsers; 
    
    static {
        dummyUsers = new User[] {
            new User().id.set("TODO-2").
                            firstName.set("David").
                            familyName.set("Something").
                            avatar.set(avatarUrl + "image-1.png"),
            new User().id.set("TODO-3").
                            firstName.set("Dana").
                            familyName.set("Something Else").
                            avatar.set(avatarUrl + "image-3.png"),
            new User().id.set("TODO-4").
                            firstName.set("Carl").
                            familyName.set("Not Something").
                            avatar.set(avatarUrl + "image-2.png"),
            new User().id.set("TODO-5").
                            firstName.set("Donna").
                            familyName.set("Enough with Something").
                            avatar.set(avatarUrl + "image-4.png")
        };
    }
    
    public static User me() {
        if(me == null) {
            me = new User().
                    id.set("TODO-1").
                    firstName.set("Shai").
                    familyName.set("Almog");
            
            me.friendRequests.add(dummyUsers[0]);
            me.friendRequests.add(dummyUsers[1]);

            me.peopleYouMayKnow.add(dummyUsers[2]);
            me.peopleYouMayKnow.add(dummyUsers[3]);
        }
        return me;
    }
    
    public static List<Post> fetchTimelinePosts(long since, int amount) {
        if(since >= initTime) {
            Comment firstPost = new Comment().
                    date.set(System.currentTimeMillis()).
                    text.set("First post!!!").
                    id.set("Comment1").
                    userId.set(ServerAPI.me().id.get());
            
            Post dummyPost = new Post().
                    user.set(ServerAPI.me()).
                    content.set("This is a <b>POST</b> that includes HTML").
                    date.set(System.currentTimeMillis() - 60000).
                    id.set("Post1").
                    likes.add(ServerAPI.me()).
                    comments.add(firstPost);
            
            List<Post> response = new ArrayList<>();
            response.add(dummyPost);
            return response;
        }             
        return null;
    }

    
    public static List<Notification> fetchNotifications(long since, int amount) {
        if(since < initTime - UIUtils.DAY) {
            return null;
        } 
        List<Notification> response = new ArrayList<>();
        response.add(new Notification().id.set("Notify-1").
                user.set(dummyUsers[0]).
                text.set("liked Your Post").
                date.set(initTime - 60000).
                reaction.set("" + FontImage.MATERIAL_FAVORITE).
                reactionColor.set(0xff0000));
        response.add(new Notification().id.set("Notify-2").
                user.set(dummyUsers[1]).
                text.set("commented on your post").
                date.set(initTime - 600000000).
                reaction.set("" + FontImage.MATERIAL_CHAT).
                reactionColor.set(0xff00));
        return response;
    }
}

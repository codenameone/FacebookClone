package com.codename1.fbclone.server;

import com.codename1.contacts.Contact;
import com.codename1.fbclone.data.Comment;
import com.codename1.fbclone.data.Notification;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.io.JSONParser;
import com.codename1.io.MultipartRequest;
import com.codename1.io.Preferences;
import com.codename1.io.Util;
import com.codename1.io.rest.RequestBuilder;
import com.codename1.io.rest.Response;
import com.codename1.io.rest.Rest;
import static com.codename1.ui.CN.*;
import com.codename1.util.Callback;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerAPI {
    private static User me;
    private static final String BASE_URL = "http://localhost:8080/";
    private static String token;

    private static RequestBuilder get(String path) {
        if(token != null) {
            return Rest.get(BASE_URL + path).
                header("auth", token).jsonContent();
        }
        return Rest.get(BASE_URL + path).jsonContent();
    }

    private static RequestBuilder post(String path) {
        if(token != null) {
            return Rest.post(BASE_URL + path).
                header("auth", token).jsonContent();
        }
        return Rest.post(BASE_URL + path).jsonContent();
    }

    public static boolean isLoggedIn() {
        token = Preferences.get("authtoken", null);
        return token != null;
    }

    public static void login(User u, Callback<User> callback) {
        signupOrLogin("user/login", u, callback);
    }

    private static void signupOrLogin(String url, User u,
        final Callback<User> callback) {
        post(url).
            body(u.getPropertyIndex().toJSON()).
            getAsJsonMap(
                new Callback<Response<Map>>() {
                    @Override
                    public void onSucess(Response<Map> value) {
                        if(value.getResponseCode() != 200) {
                            callback.onError(u, null, value.
                                getResponseCode(),
                                "Login Error");
                            return;
                        }
                        me = new User();
                        me.getPropertyIndex().
                        populateFromMap(value.getResponseData());
                        Preferences.set("authtoken", me.authtoken.
                            get());
                        token = me.authtoken.get();
                        me.getPropertyIndex().storeJSON("me.json");
                        callback.onSucess(me);
                    }

                    @Override
                    public void onError(Object sender, Throwable err,
                        int errorCode, String errorMessage) {
                        callback.onError(sender, err, errorCode,
                            errorMessage);
                    }
                });
    }

    public static void refreshMe() {
        Response<Map> map = get("user/refresh").getAsJsonMap();
        if(map.getResponseCode() == 200) {
            me = new User();
            me.getPropertyIndex().
                populateFromMap(map.getResponseData());
            me.getPropertyIndex().storeJSON("me.json");
        }
    }

    public static void signup(User u, Callback<User> callback) {
        signupOrLogin("user/signup", u, callback);
    }

    public static boolean verifyUser(String code, boolean email) {
        Response<String> s = get("user/verify").
            queryParam("code", code).
            queryParam("email", "" + email).getAsString();
        return "OK".equals(s.getResponseData());
    }

    public static boolean update(User u) {
        Response<String> s = post("user/update").
            body(u.getPropertyIndex().toJSON()).getAsString();
        return "OK".equals(s.getResponseData());
    }

    public static boolean setAvatar(String media) {
        Response<String> s = get("user/set-avatar").
            queryParam("media", media).getAsString();
        return "OK".equals(s.getResponseData());
    }

    public static boolean sendFriendRequest(String userId) {
        Response<String> s = get("user/send-friend-request").
            queryParam("userId", userId).getAsString();
        return "OK".equals(s.getResponseData());
    }

    public static boolean acceptFriendRequest(String userId) {
        Response<String> s = get("user/accept-friend-request").
            queryParam("userId", userId).getAsString();
        return "OK".equals(s.getResponseData());
    }

    private static String contactsToJSON(Contact[] contacts) {
        StringBuilder content = new StringBuilder("[");
        boolean first = true;
        for(Contact c : contacts) {
            String dname = c.getDisplayName();
            if(dname != null) {
                if(!first) {
                    content.append(",");
                }
                first = false;
                Map<String, String> data = new HashMap();
                data.put("fullName", dname);
                String phone = c.getPrimaryPhoneNumber();
                if(phone != null) {
                    data.put("phone", phone);
                    Map phones = c.getPhoneNumbers();
                    if(phones != null && phones.size() > 1) {
                        for(Object p : phones.values()) {
                            if(!p.equals(phone)) {
                                data.put("secondaryPhone", phone);
                                break;
                            }
                        }
                    }
                }
                String email = c.getPrimaryEmail();
                if(email != null) {
                    data.put("email", email);
                }
                content.append(JSONParser.mapToJson(data));
            }
        }
        content.append("]");
        return content.toString();
    }

    public static boolean uploadContacts(Contact[] contacts) {
        Response<String> s = post("user/contacts").
            body(contactsToJSON(contacts)).getAsString();
        return "OK".equals(s.getResponseData());
    }

    public static void uploadMedia(String mime, String role,
        String visibility,
        String fileName, byte[] data,
        SuccessCallback<String> callback) {
        MultipartRequest mp = new MultipartRequest() {
            private String mediaId;

            @Override
            protected void readResponse(InputStream input) 
                throws IOException {
                mediaId = Util.readToString(input);
            }

            @Override
            protected void postResponse() {
                callback.onSucess(mediaId);
            }
        };
        mp.setUrl(BASE_URL + "media/upload");
        mp.addRequestHeader("auth", token);
        mp.addRequestHeader("Accept", "application/json");
        mp.addArgument("role", role);
        mp.addArgument("visibility", visibility);
        mp.addData("file", data, mime);
        mp.setFilename("file", fileName);
        addToQueue(mp);
    }

    public static User me() {
        if(me == null && isLoggedIn()) {
            me = new User();
            me.getPropertyIndex().loadJSON("me.json");
        }
        return me;
    }

    public static List<Notification> listNotifications(int page, int size) {
        Response<Map> response = get("user/notifications").
            queryParam("page", "" + page).
            queryParam("size", "" + size).getAsJsonMap();
        if(response.getResponseCode() == 200) {
            List<Map> l = (List<Map>) response.getResponseData().get("root");
            List<Notification> responseList = new ArrayList<>();
            for(Map m : l) {
                Notification n = new Notification();
                n.getPropertyIndex().populateFromMap(m);
                responseList.add(n);
            }
            return responseList;
        }
        return null;
    }

    private static List<Post> processPostResponse(Response<Map> response) {
        if(response.getResponseCode() == 200) {
            List<Map> l = (List<Map>) response.getResponseData().get("root");
            List<Post> responseList = new ArrayList<>();
            for(Map m : l) {
                Post p = new Post();
                p.getPropertyIndex().populateFromMap(m);
                responseList.add(p);
            }
            return responseList;
        }
        return null;
    }

    public static List<Post> postsOf(String user, int page, int size) {
        return processPostResponse(
            get("post/list").
            queryParam("user", user).
            queryParam("page", "" + page).
            queryParam("size", "" + size).getAsJsonMap());
    }

    public static List<Post> newsfeed(int page, int size) {
        return processPostResponse(
            get("post/feed").
            queryParam("page", "" + page).
            queryParam("size", "" + size).getAsJsonMap());
    }

    public static boolean post(Post pd) {
        String key = post("post/new").body(pd.getPropertyIndex().toJSON()).
            getAsString().getResponseData();
        pd.id.set(key);
        return key != null;
    }

    public static boolean comment(Comment c) {
        String key = post("post/new").body(c.getPropertyIndex().toJSON()).
            getAsString().getResponseData();
        c.id.set(key);
        return key != null;
    }

    public static boolean like(Post p) {
        String ok = get("post/like").queryParam("postId", p.id.get()).
            getAsString().getResponseData();
        return ok != null && ok.equals("OK");
    }

}

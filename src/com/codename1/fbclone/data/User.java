package com.codename1.fbclone.data;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.properties.ListProperty;
import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;
import static com.codename1.ui.CN.*;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.FontImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.plaf.Style;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.io.InputStream;

public class User implements PropertyBusinessObject {
    public final Property<String, User> id = new Property<>("id");
    public final Property<String, User> firstName = new Property<>("firstName");
    public final Property<String, User> familyName = new Property<>("familyName");
    public final Property<String, User> email = new Property<>("email");
    public final Property<String, User> phone = new Property<>("phone");
    public final Property<String, User> gender = new Property<>("gender");
    public final LongProperty<User> birthday = 
            new LongProperty<>("birthday");
    public final Property<String, User> avatar = new Property<>("avatar");
    public final ListProperty<User, User> friends = 
            new ListProperty<>("friends");
    public final ListProperty<User, User> friendRequests = 
            new ListProperty<>("friendRequests");
    public final ListProperty<User, User> peopleYouMayKnow = 
            new ListProperty<>("peopleYouMayKnow");
    
    
    private final PropertyIndex idx = new PropertyIndex(this, "User", 
            id, firstName, familyName, email, phone, gender, avatar, 
            birthday, friends, friendRequests, peopleYouMayKnow);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }

    public String fullName() {
        return firstName.get() + " " + familyName.get();
    }
    
    public Image getAvatar(float imageSize) {
        String filename = "round-avatar-" + imageSize + "-" + id.get();
        if(existsInStorage(filename)) {
            try(InputStream is = 
                    createStorageInputStream(filename);) {
                return Image.createImage(is);
            } catch(IOException err) {
                Log.e(err);
                deleteStorageFile(filename);
            }
        }
        int size = convertToPixels(imageSize);
        Image temp = Image.createImage(size, size, 0xff000000);
        Graphics g = temp.getGraphics();
        g.setAntiAliased(true);
        g.setColor(0xffffff);
        g.fillArc(0, 0, size, size, 0, 360);
        Object mask = temp.createMask();
        Style s = new Style();
        s.setFgColor(0xc2c2c2);
        s.setBgTransparency(255);
        s.setBgColor(0xe9e9e9);        
        FontImage x = FontImage.createMaterial(
                FontImage.MATERIAL_PERSON, s, size);
        Image avatarImg = x.fill(size, size);
        if(avatarImg instanceof FontImage) {
            avatarImg = ((FontImage)avatarImg).toImage();
        }
        avatarImg = avatarImg.applyMask(mask);
        if(avatar.get() != null) {
            return URLImage.createToStorage(
                    EncodedImage.createFromImage(avatarImg, false), 
                    filename,
                    avatar.get(),
                    URLImage.createMaskAdapter(temp));
        }
        return avatarImg;
    }

}

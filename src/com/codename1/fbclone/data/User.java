package com.codename1.fbclone.data;

import com.codename1.properties.ListProperty;
import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;
import static com.codename1.ui.CN.convertToPixels;
import com.codename1.ui.FontImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.plaf.Style;

public class User implements PropertyBusinessObject {
    private static User me;
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
    
    
    private final PropertyIndex idx = new PropertyIndex(this, "User", 
            id, firstName, familyName, email, phone, gender, avatar, 
            birthday, friends);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }

    public Image getAvatar() {
        int size = convertToPixels(5);
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
        FontImage x = FontImage.createMaterial(FontImage.MATERIAL_PERSON, s, size);
        Image avatar = x.fill(size, size);
        if(avatar instanceof FontImage) {
            avatar = ((FontImage)avatar).toImage();
        }
        avatar = avatar.applyMask(mask);
        return avatar;
    }

    public static User me() {
        if(me == null) {
            me = new User().
                    id.set("TODO").
                    firstName.set("Shai").
                    familyName.set("Almog");
        }
        return me;
    }
}

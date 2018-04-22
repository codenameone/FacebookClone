package com.codename1.fbclone.data;

import com.codename1.properties.BooleanProperty;
import com.codename1.properties.IntProperty;
import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;

public class Notification  implements PropertyBusinessObject {
    public final Property<String, Notification> id = new Property<>("id");
    public final Property<User, Notification> user = 
            new Property<>("user", User.class);
    public final Property<String, Notification> text = new Property<>("text");
    public final Property<String, Notification> reaction =
            new Property<>("reaction");
    public final IntProperty<Notification> reactionColor = 
            new IntProperty<>("reactionColor");
    public final LongProperty<Notification> date = 
            new LongProperty<>("date");
    public final BooleanProperty<Notification> wasRead = 
            new BooleanProperty<>("wasRead");
    
    private final PropertyIndex idx = new PropertyIndex(this, "Notification", 
            id, user, text, reaction, reactionColor, date, wasRead);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }
}

package com.codename1.fbclone.data;

import com.codename1.properties.ListProperty;
import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;
import com.codename1.ui.EncodedImage;

public class Post implements PropertyBusinessObject {
    public final Property<String, Post> id = new Property<>("id");
    public final Property<String, Post> userId = new Property<>("userId");
    public final LongProperty<Post> date =  new LongProperty<>("date");
    public final Property<String, Post> title = new Property<>("title");
    public final Property<String, Post> content = new Property<>("content");
    public final Property<String, Post> type = new Property<>("type");
    public final Property<String, Post> visibility = new Property<>("visibility");
    
    public final ListProperty<Comment, Post> comments = 
            new ListProperty<>("comment", Comment.class);
    public final ListProperty<User, Post> likes = 
            new ListProperty<>("likes", User.class);
    
    
    private final PropertyIndex idx = new PropertyIndex(this, "Post", 
            id, userId, date, title, content, type, visibility, comments, likes);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }
    
}

package com.codename1.fbclone.data;

import com.codename1.properties.ListProperty;
import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;

public class Post implements PropertyBusinessObject {
    public final Property<String, Post> id = new Property<>("id");
    public final Property<User, Post> user = new Property<>("user", 
            User.class);
    public final LongProperty<Post> date =  new LongProperty<>("date");
    public final Property<String, Post> title = new Property<>("title");
    public final Property<String, Post> content = new Property<>("content");
    public final Property<String, Post> visibility = new Property<>("visibility");
    public final Property<String, Post> styling = new Property<>("styling");
    
    public final ListProperty<Comment, Post> comments = 
            new ListProperty<>("comments", Comment.class);
    public final ListProperty<User, Post> likes = 
            new ListProperty<>("likes", User.class);
    
    
    private final PropertyIndex idx = new PropertyIndex(this, "Post", 
            id, user, date, title, content, visibility, styling, comments, likes);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }
    
}

package com.codename1.fbclone.data;

import com.codename1.properties.LongProperty;
import com.codename1.properties.Property;
import com.codename1.properties.PropertyBusinessObject;
import com.codename1.properties.PropertyIndex;

public class Comment implements PropertyBusinessObject {
    public final Property<String, Comment> id = new Property<>("id");
    public final Property<String, Comment> postId = new Property<>("postId");
    public final Property<String, Comment> userId = new Property<>("userId");
    public final Property<String, Comment> parentComment = 
            new Property<>("parentComment", Comment.class);
    public final LongProperty<Comment> date =  new LongProperty<>("date");
    public final Property<String, Comment> text = new Property<>("text");
    
    private final PropertyIndex idx = new PropertyIndex(this, "Comment", 
            id, postId, userId, date, parentComment, text);
    
    @Override
    public PropertyIndex getPropertyIndex() {
        return idx;
    }

}

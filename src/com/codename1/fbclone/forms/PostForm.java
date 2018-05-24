package com.codename1.fbclone.forms;

import com.codename1.fbclone.data.Post;
import static com.codename1.ui.CN.getCurrentForm;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BoxLayout;

public class PostForm extends Form {
    
    public PostForm(Post p) {
        super(p.title.get(), BoxLayout.y());
        add(NewsfeedContainer.createNewsItem(p.user.get(), p));
        Form previous = getCurrentForm();
        getToolbar().addMaterialCommandToLeftBar("", 
            MATERIAL_ARROW_BACK, e -> previous.showBack());
    }
}

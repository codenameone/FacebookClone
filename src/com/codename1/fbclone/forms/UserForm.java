package com.codename1.fbclone.forms;

import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Component;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.layouts.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class UserForm extends Form {
    private User user;
    private InfiniteContainer ic = new InfiniteContainer() {
        @Override
        public Component[] fetchComponents(int index, int amount) {
            ArrayList<Component> components = new ArrayList<>();
            int page = index / amount;
            if(index % amount > 0) {
                page++;
            }
            List<Post> response = ServerAPI.postsOf(user.id.get(), 
                page, amount);
            if(response == null) {
                if(index == 0) {
                    return UIUtils.toArray(components);
                }
                return null;
            }

            for(Post p : response) {
                components.add(NewsfeedContainer.
                    createNewsItem(p.user.get(), p));
                components.add(UIUtils.createHalfSpace());
            }
            return UIUtils.toArray(components);
            
        }
    };
    
    public UserForm(User user) {
        super(user.fullName(), new BorderLayout());
        this.user = user;
        add(CENTER, ic);
        Form previous = getCurrentForm();
        getToolbar().addMaterialCommandToLeftBar("", MATERIAL_CLOSE, e -> 
            previous.showBack());
    }
}

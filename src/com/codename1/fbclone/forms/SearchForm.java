package com.codename1.fbclone.forms;

import com.codename1.components.MultiButton;
import com.codename1.fbclone.data.Post;
import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Component;
import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import static com.codename1.ui.FontImage.*;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import static com.codename1.ui.CN.*;
import com.codename1.ui.util.UITimer;
import java.util.ArrayList;
import java.util.List;

public class SearchForm extends Form {
    private boolean searchPeople;
    private String lastSearchValue;
    private UITimer pendingTimer;
    private long lastSearchTime;
    private TextField searchField = new TextField();
    private InfiniteContainer ic = new InfiniteContainer() {
        @Override
        public Component[] fetchComponents(int index, int amount) {
            if(searchField.getText().length() < 2) {
                return null;
            }
            int page = index / amount;
            if(index % amount > 0) {
                page++;
            }
            List<Component> response = new ArrayList<>();
            if(searchPeople) {
                List<User> results = ServerAPI.searchPeople(
                    searchField.getText(), page, amount);
                if(results == null) {
                    return null;
                }
                for(User u : results) {
                    response.add(createEntry(u));
                }
            } else {
                List<Post> results = ServerAPI.searchPosts(
                    searchField.getText(), page, amount);
                if(results == null) {
                    return null;
                }
                for(Post u : results) {
                    response.add(createEntry(u));
                }
            }
            if(response.isEmpty()) {
                return null;
            }
            return UIUtils.toArray(response);
        }
    };
    
    public SearchForm() {
        super(new BorderLayout());
        searchField.setUIID("Title");
        searchField.getAllStyles().setAlignment(LEFT);
        searchField.addDataChangedListener((i, ii) -> updateSearch());
        Toolbar tb = getToolbar();
        tb.setTitleComponent(searchField);
        Form previous = getCurrentForm();
        tb.addMaterialCommandToLeftBar("", MATERIAL_CLOSE, e -> 
            previous.showBack());
        tb.addMaterialCommandToRightBar("", MATERIAL_PERSON, e -> {
            searchPeople = true;
            ic.refresh();
        });
        tb.addMaterialCommandToRightBar("", MATERIAL_PAGES, e -> {
            searchPeople = false;
            ic.refresh();
        });
        add(CENTER, ic);
        setEditOnShow(searchField);
    }
    
    private void updateSearch() {
        String text = searchField.getText();
        if(text.length() > 2) {
            if(lastSearchValue != null) {
                if(lastSearchValue.equalsIgnoreCase(text)) {
                    return;
                }
                if(pendingTimer != null) {
                    pendingTimer.cancel();
                }
                long t = System.currentTimeMillis();
                if(t - lastSearchTime < 800) {
                    pendingTimer = UITimer.timer((int)(t - lastSearchTime), 
                        false, this, () -> {
                            lastSearchTime = System.currentTimeMillis();
                            ic.refresh();
                        });
                    return;
                }
            }
            lastSearchTime = System.currentTimeMillis();
            ic.refresh();
        }
    }
    
    private Component createEntry(User u) {
        MultiButton mb = new MultiButton(u.fullName());
        mb.setIcon(u.getAvatar(8));
        mb.addActionListener(e -> new UserForm(u).show());
        return mb;
    }

    private Component createEntry(Post p) {
        MultiButton mb = new MultiButton(p.title.get());
        mb.setTextLine2(p.content.get());
        mb.setIcon(p.user.get().getAvatar(8));
        mb.addActionListener(e -> new PostForm(p).show());
        return mb;
    }
}

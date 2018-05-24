package com.codename1.fbclone.forms;

import com.codename1.fbclone.data.User;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.properties.InstantUI;
import com.codename1.properties.UiBinding;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import static com.codename1.ui.FontImage.*;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;

public class SettingsForm extends Form {
    private Label cover = new Label(" ", "LoginTitle");
    private Label avatar = new Label("", "LabelFrame");
    private Button changeCover = 
        new Button(MATERIAL_CAMERA_ALT, "CameraLabel");
    private Button changeAvatar = 
        new Button(MATERIAL_CAMERA_ALT, "CameraLabel");
    
    public SettingsForm() {
        super("", BoxLayout.y());
        Form previous = getCurrentForm();
        getToolbar().setBackCommand("Back", e -> previous.showBack());
        User me = ServerAPI.me();
        avatar.setIcon(me.getAvatar(12));
        if(me.cover.get() != null) {
            me.fetchCoverImage(i -> {
                cover.getAllStyles().setBgImage(i);
                repaint();
            });
        }
        changeAvatar.addActionListener(e -> pickAvatar(avatar));
        changeCover.addActionListener(e -> pickCover(cover));
        Container coverContainer = LayeredLayout.encloseIn(
            cover, 
            FlowLayout.encloseRightBottom(changeCover)
        );
        coverContainer.setUIID("SettingsMargin");
        Container avatarContainer = LayeredLayout.encloseIn(
            avatar, 
            FlowLayout.encloseRightBottom(changeAvatar)
        );
        add(LayeredLayout.encloseIn(
            coverContainer,
            FlowLayout.encloseCenterBottom(avatarContainer)
        ));
        add(new Label(me.fullName(), "CenterLargeThinLabel"));
        add(createButtonBar());
    }

    private Container createButtonBar() {
        Button activity = new Button(MATERIAL_HISTORY, "CleanButton");
        Button settings = new Button(MATERIAL_SETTINGS, "CleanButton");
        Button viewAs = new Button(MATERIAL_ACCOUNT_CIRCLE, "CleanButton");
        Button more = new Button(MATERIAL_MORE_HORIZ, "CleanButton");
        settings.addActionListener(e -> 
            showUserEditForm(ServerAPI.me()));
        return GridLayout.encloseIn(4, activity, settings, viewAs, more);
    }
    
    private void pickAvatar(Label l) {
        ImagePicker p = new ImagePicker();
        p.pickImage(img -> {
            p.upload(mediaId -> {
                ServerAPI.me().avatar.set(mediaId);
                ServerAPI.setAvatar(mediaId);
                Image temp = img.fill(l.getIcon().getWidth(), 
                    l.getIcon().getHeight());
                temp = temp.applyMask(ServerAPI.me().
                    getAvatarMask(temp.getWidth()).createMask());
                l.setIcon(temp);
            });
        });
    }
    
    private void pickCover(Label l) {
        ImagePicker p = new ImagePicker();
        p.pickImage(img -> {
            p.upload(mediaId -> {
                ServerAPI.me().cover.set(mediaId);
                ServerAPI.setCover(mediaId);
                l.getAllStyles().setBgImage(img);
            });
        });
    }
    
    @Override
    protected void initGlobalToolbar() {
        Toolbar tb = new Toolbar(true);
        tb.setUIID("Container");
        setToolbar(tb);
    }

    private void showUserEditForm(User me) {
        InstantUI iu = new InstantUI();
        iu.excludeProperties(me.authtoken, me.avatar, me.cover,
            me.friendRequests, me.friends, me.peopleYouMayKnow, me.id,
            me.password, me.birthday);
        iu.setMultiChoiceLabels(me.gender, "Male", "Female", "Other"); 
        iu.setMultiChoiceValues(me.gender, "Male", "Female", "Other");
        Container cnt = iu.createEditUI(me, true);
        cnt.setUIID("PaddedContainer");
        cnt.setScrollableY(true);
        Form edit = new Form("Edit", new BorderLayout());
        edit.add(BorderLayout.CENTER, cnt);
        edit.getToolbar().setBackCommand("Back", e -> {
            showBack();
            UiBinding.unbind(me);
            callSerially(() -> ServerAPI.update(me));
        });
        edit.show();
    }
}

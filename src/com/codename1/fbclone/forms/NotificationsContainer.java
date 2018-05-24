package com.codename1.fbclone.forms;

import com.codename1.fbclone.components.RichTextView;
import com.codename1.fbclone.data.Notification;
import com.codename1.fbclone.server.ServerAPI;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.RoundBorder;
import java.util.List;

public class NotificationsContainer extends InfiniteContainer {
    @Override
    public Component[] fetchComponents(int index, int amount) {
        int page = index / amount;
        if(index % amount > 0) {
            page++;
        }
        List<Notification> response = ServerAPI.listNotifications(page,
            amount);
        if(response == null) {
            return null;
        }
        Component[] notifications = new Component[response.size()];
        int iter = 0;
        for(Notification n : response) {
            notifications[iter] = createNotificationEntry(n);
            iter++;
        }
        return notifications;
    }

    private Container createNotificationEntry(Notification n) {
        Image avatar = n.user.get().getAvatar(13);
        Label icon = new Label("", "SmallBlueCircle");
        icon.getAllStyles().setBorder(RoundBorder.create().
            color(n.reactionColor.get()));
        FontImage.setMaterialIcon(icon, n.reaction.get().charAt(0), 2);
        Container avatarContainer = LayeredLayout.encloseIn(
            new Label(avatar, "HalfPaddedContainer"),
            FlowLayout.encloseRightBottom(icon));
        RichTextView rt = new RichTextView("<b>" + n.user.get().fullName() +
            "</b> " + n.text.get());
        Label time = new Label(UIUtils.formatTimeAgo(n.date.get()),
            "SmallBlueLabel");
        Container yContainer = BoxLayout.encloseY(rt, time);
        yContainer.setUIID("HalfPaddedContainer");
        return BorderLayout.
            centerEastWest(yContainer, null, avatarContainer);
    }
}

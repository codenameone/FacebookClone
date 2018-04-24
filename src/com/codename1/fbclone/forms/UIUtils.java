package com.codename1.fbclone.forms;

import com.codename1.l10n.L10NManager;
import com.codename1.ui.Label;
import java.util.Date;

public class UIUtils {
    public static final long HOUR = 60 * 60000;
    public static final long DAY = 24 * HOUR;
    
    public static Label createSpace() {
        Label l = new Label("", "PaddedSeparator");
        l.setShowEvenIfBlank(true);
        return l;
    }
    
    public static Label createHalfSpace() {
        Label l = new Label("", "HalfPaddedSeparator");
        l.setShowEvenIfBlank(true);
        return l;
    }
    
    public static String formatTimeAgo(long time) {
        long current = System.currentTimeMillis() - time;
        if(current < HOUR) {
            long minutes = current / 60000;
            if(minutes < 2) {
                return "Just now";
            }
            return  minutes + " minutes ago";
        }
        if(current < HOUR * 10) {
            return  (current / HOUR) + " hours ago";
        }
        return L10NManager.getInstance().
                formatDateTimeShort(new Date(time));
    }
}

package com.codename1.fbclone;

import com.codename1.fbclone.forms.LoginForm;
import com.codename1.fbclone.forms.MainForm;
import com.codename1.fbclone.forms.SignupForm;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Label;
import com.codename1.ui.animations.MorphTransition;
import com.codename1.ui.animations.Motion;
import com.codename1.ui.util.UITimer;

public class UIController {
    public static void showSplashScreen() {
        Form splash = new Form(new BorderLayout(
                    BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
        splash.setUIID("SplashForm");
        Label logo = new Label("\uf308", "IconFont");
        logo.setName("Logo");
        splash.add(CENTER, logo);
        splash.setTransitionOutAnimator(
                MorphTransition.
                        create(1200).
                        morph("Logo"));
        final Motion anim = Motion.createLinearMotion(0, 127, 1000);
        anim.start();
        UITimer.timer(20, true, splash, () -> {
            if(anim.isFinished()) {
                showLoginForm();
            } else {
                logo.getUnselectedStyle().setOpacity(anim.getValue() + 127);
                logo.repaint();
            }
        });
        splash.show();
    }
    
    public static void showLoginForm() {
        new LoginForm().show();
    }
    
    public static void showSignup() {
        SignupForm.createTerms().show();
    }
    
    public static void showMainUI() {
    }
}

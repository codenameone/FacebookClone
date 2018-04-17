package com.codename1.fbclone.forms;

import com.codename1.fbclone.UIController;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import static com.codename1.ui.CN.*;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;

public class LoginForm extends Form {
    private Label logo = new Label("\uf308", "IconFont");
    private TextField user = new TextField("", "Email or Phone", 30, 
            TextField.EMAILADDR);
    private TextField password = new TextField("", "Password", 30, 
            TextField.PASSWORD);

    private Button login = new Button("Log In");
    private Button signUp = new Button("Sign Up for Facebook");
    private Button needHelp = new Button("Need Help?");
    
    public LoginForm() {
        super(new BorderLayout());
        getToolbar().setUIID("Container");
        logo.setName("Logo");        
        Container logoContainer = BorderLayout.centerAbsolute(logo);
        logoContainer.setUIID("LoginTitle");
        
        signUp.addActionListener(e -> UIController.showSignup());
        
        add(NORTH, logoContainer);
        if(!isTablet()) {
            BorderLayout bl = ((BorderLayout)getLayout());
            bl.defineLandscapeSwap(NORTH, EAST);
        }
                
        if(getUIManager().isThemeConstant("ComponentGroupBool", false)) {
            Container content = 
                    BorderLayout.centerAbsolute(
                        BoxLayout.encloseY(
                            ComponentGroup.enclose(
                                    user,
                                    password),
                            login));
            content.setUIID("PaddedContainer");
            add(CENTER, content);
            
            login.setUIID("BlueButtonOnBlueBackground");
            setUIID("SplashForm");
            signUp.setUIID("WhiteLinkButton");
            needHelp.setUIID("WhiteLinkButton");
            content.add(SOUTH, 
                    FlowLayout.encloseCenter(
                            signUp, needHelp));
        } else {
            login.setUIID("BlueButton");
            Button forgotPassword = new Button("Forgot Password", "BlueText");
            signUp.setUIID("GreenButton");
            signUp.setText("Create new Facebook Account");
            Container cnt = BoxLayout.encloseY(
                    user,
                    password,
                    login,
                    forgotPassword,
                    signUp);
            cnt.setUIID("PaddedContainer");
            cnt.setScrollableY(true);
            add(CENTER, cnt);
        }
    }

    protected boolean shouldPaintStatusBar() {
        return false;
    }    
}

package com.codename1.fbclone.forms;

import com.codename1.components.SpanLabel;
import com.codename1.fbclone.UIController;
import com.codename1.fbclone.components.RichTextView;
import com.codename1.messaging.Message;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextComponent;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.TextModeLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import java.util.Date;

public class SignupForm extends Form {
    Container content = new Container(BoxLayout.y(), "PaddedContainer");
    Container south = new Container(BoxLayout.y());
    protected SignupForm(String title, String backLabel, Form previous) {
        super(title, new BorderLayout());
        setUIID("SignupForm");
        content.setScrollableY(true);
        add(CENTER, content);
        getToolbar().setBackCommand(backLabel, 
                Toolbar.BackCommandPolicy.WHEN_USES_TITLE_OTHERWISE_ARROW,
                e -> previous.showBack());
        getToolbar().getTitleComponent().setUIID("Title", "TitleLandscape");
        Button problem = new Button("Report a Problem", "BlueLink");
        south.add(problem);
        problem.addActionListener(e -> 
                sendMessage("Problem with Facebook Clone", 
                        new Message("Details..."), 
                        "mark@facebook.com"));
        add(SOUTH, south);
    }
    
    Button createNextButton(ActionListener al) {
        Button next = new Button("Next", "NextButton");
        next.addActionListener(al);
        return next;
    }
    
    public static SignupForm createTerms() {
        SignupForm s = new SignupForm("Create Account", "Sign In",
                    getCurrentForm());
        Label title = new Label("Terms & Conditions", "SignupSubHeader");
        RichTextView rt = new RichTextView("By signing up you agree to our "
                + "<a href=\"terms\">Facebook Terms</a> and that you have "
                + "read our <a href=\"data-policy\">Data Policy</a>, including "
                + "our <a href=\"cookie-use\">Cookie Use</a>.");
        rt.setAlignment(CENTER);
        rt.setUIID("PaddedContainer");
        Button next = s.createNextButton(e -> createName().show());
        next.setText("I Agree");
        
        rt.addLinkListener(e -> {
            String link = (String)e.getSource();
            execute("https://www.codenameone.com/");
        });                
        s.content.addAll(title, rt, next);
        return s;
    }
    
    public static SignupForm createName() {
        SignupForm s = new SignupForm("Name", "Terms",
                getCurrentForm());
        Label title = new Label("What's Your Name?", "SignupSubHeader");
        TextComponent first = new TextComponent().
                label("First Name").columns(12);
        TextComponent last = new TextComponent().
                label("Last Name").columns(12);
        TextModeLayout layout = new TextModeLayout(1, 2);
        Container textContainer = new Container(layout, "PaddedContainer");
        textContainer.add(layout.createConstraint().
                widthPercentage(50), first);
        textContainer.add(layout.createConstraint().
                widthPercentage(50), last);
        
        last.getField().setDoneListener(e -> createBirthday().show());
        
        s.content.addAll(title, textContainer,
            s.createNextButton(e -> createBirthday().show()));
        return s;
    }

    public static SignupForm createBirthday() {
        SignupForm s = new SignupForm("Birthday", 
                "Name", 
                getCurrentForm());
        Label title = new Label("What's Your Birthday?", "SignupSubHeader");
        Picker datePicker = new Picker();
        datePicker.setType(PICKER_TYPE_DATE);
        int twentyYears = 60000 * 60 * 24 * 365 * 20;
        datePicker.setDate(new Date(System.currentTimeMillis() - twentyYears));        
        s.content.addAll(title, datePicker,
            s.createNextButton(e -> createGender().show()));
        return s;
    }

    private static RadioButton createGenderButton(ButtonGroup bg, 
            String label, String icon) {
        Style unselectedIconStyle = UIManager.getInstance().
                    getComponentStyle("GenderIcons");
        Style selectedIconStyle = UIManager.getInstance().
                    getComponentSelectedStyle("GenderIcons");
        FontImage unIcon = 
                    FontImage.create(icon, unselectedIconStyle);
        FontImage selIcon = 
                    FontImage.create(icon, selectedIconStyle);
        RadioButton rb  = RadioButton.createToggle(label, unIcon, bg);
        rb.setTextPosition(BOTTOM);
        rb.setRolloverIcon(selIcon);
        rb.setPressedIcon(selIcon);
        rb.setUIID("GenderToggle");
        return rb;
    }
    
    public static SignupForm createGender() {
        SignupForm s = new SignupForm("Gender", 
                "Birthday",
                getCurrentForm());
        Label title = new Label("What's Your Gender?", "SignupSubHeader");

        ButtonGroup bg = new ButtonGroup();
        RadioButton female = createGenderButton(bg, "Female", "\ue800");
        RadioButton male = createGenderButton(bg, "Male", "\ue801");
        
        Container buttons = GridLayout.encloseIn(2, female, male);
        buttons.setUIID("PaddedContainer");
        
        s.content.addAll(title, buttons,
            s.createNextButton(e -> createNumber().show()));
        return s;
    }

    public static SignupForm createNumber() {
        return createMobileOrEmail("Mobile Number", 
                "What's Your Mobile Number?",
                "Sign Up With Email Address",
                TextArea.PHONENUMBER,
                e -> createEmail().show());
    }

    private static SignupForm createMobileOrEmail(String formTitle, 
            String subtitle, String signUpWith, int constraint,
            ActionListener goToOther) {
        SignupForm s = new SignupForm(formTitle, getCurrentForm().getTitle(),
                getCurrentForm());
        Label title = new Label(subtitle, "SignupSubHeader");

        TextComponent textEntry = new TextComponent().
                label(formTitle).
                columns(20).
                constraint(constraint);

        Container textContainer = new Container(new TextModeLayout(1, 1), 
                "PaddedContainer");
        textContainer.add(textEntry);

        Button mobile = new Button(signUpWith, "BoldBlueLink");
        mobile.addActionListener(goToOther);
        s.south.addComponent(0, mobile);
        
        s.content.addAll(title, textContainer,
            s.createNextButton(e -> 
                    createPassword(TextArea.PHONENUMBER == constraint, 
                            textEntry.getText()).show()));
        return s;
    }
    
    public static SignupForm createEmail() {
        return createMobileOrEmail("Email Address", 
                "What's Your Email Address?",
                "Sign Up With Mobile Number",
                TextArea.EMAILADDR,
                e -> createNumber().show());
    }

    public static SignupForm createPassword(boolean phone, String value) {
        SignupForm s = new SignupForm("Password", 
                getCurrentForm().getTitle(),
                getCurrentForm());
        Label title = new Label("Choose a Password", "SignupSubHeader");

        TextComponent password = new TextComponent().
                label("Password").
                columns(20);

        Container textContainer = new Container(new TextModeLayout(1, 1), 
                "PaddedContainer");
        textContainer.add(password);
        
        s.content.addAll(title, textContainer,
            s.createNextButton(e -> createConfirmation(phone, value).show()));
        return s;
    }

    public static SignupForm createConfirmation(
                boolean phone, String value) {
        SignupForm s = new SignupForm("Account Confirmation", "Password",
                getCurrentForm());
        SpanLabel title;
        if(phone) {
            title = new SpanLabel("Enter the code from your mobile phone", 
                        "SignupSubHeader");
        } else {
            title = new SpanLabel("Enter the code from your email", 
                        "SignupSubHeader");
        }
        
        SpanLabel line;
        if(phone) {
            line = new SpanLabel("Let us know this phone belongs to you. "
                    + "Enter the code in the SMS sent to " + value, "CenterLabel");
        } else {
            line = new SpanLabel("Let us know this email belongs to you. "
                    + "Enter the code in the message sent to " + value, 
                    "CenterLabel");
        }
        
        TextComponent confirm = new TextComponent().
                label("Confirmation Code").
                columns(20).
                constraint(TextArea.NUMERIC);

        Container textContainer = new Container(new TextModeLayout(1, 1), 
                "PaddedContainer");
        textContainer.add(confirm);
        
        Button done = s.createNextButton(e -> UIController.showMainUI());
        done.setText("Confirm");
        s.content.addAll(title, line, textContainer, done);
        return s;
    }
}

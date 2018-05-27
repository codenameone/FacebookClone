package com.codename1.fbclone.components;

import com.codename1.io.CharArrayReader;
import com.codename1.ui.Button;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.StringUtil;
import com.codename1.xml.XMLParser;
import java.io.IOException;

public class RichTextView extends Container {
    private String text;
    private float fontSize = 2.6f;
    private EventDispatcher listeners = new EventDispatcher();
    
    private Font currentFont;
    private int currentColor = 0;
    private String currentLink;
    private Style lastCmp;
    private Font defaultFont;
    private Font boldFont;
    private Font italicFont;
    private int sizeOfSpace;
    
    public RichTextView() { 
        init(null);
    }

    public RichTextView(String text, String uiid) {
        init(uiid);
        setText(text);
    }

    public RichTextView(String text) {
        init(null);
        setText(text);
    }

    private void init(String uiid) {
        boldFont = Font.createTrueTypeFont(NATIVE_MAIN_BOLD, fontSize);
        italicFont = Font.createTrueTypeFont(NATIVE_ITALIC_LIGHT, fontSize);
        if(uiid == null) {
            defaultFont = Font.createTrueTypeFont(NATIVE_MAIN_LIGHT, 
                fontSize); 
        } else {
            Style s = UIManager.getInstance().getComponentStyle(uiid);
            defaultFont = s.getFont();
            boldFont = boldFont.derive(defaultFont.getHeight(), 
                Font.STYLE_BOLD);
            italicFont = italicFont.derive(defaultFont.getHeight(), 
                Font.STYLE_ITALIC);
        }
        sizeOfSpace = defaultFont.charWidth(' '); 
        currentFont = defaultFont;
    }
    
    public void setAlignment(int align) {
        ((FlowLayout)getLayout()).setAlign(align);
    }

    private void createComponent(String t) {
        if(t.indexOf(' ') > -1) {
            for(String s : StringUtil.tokenize(t, ' ')) {
                createComponent(s);
            }
            return;
        } 
        Label l;
        if(currentLink != null) {
            Button b = new Button(t, "Label");
            final String currentLinkValue = currentLink;
            b.addActionListener(e -> listeners.fireActionEvent(
                    new ActionEvent(currentLinkValue)));
            l = b;
        } else {
            l = new Label(t);
        }
        Style s = l.getAllStyles();
        s.setFont(currentFont); 
        s.setFgColor(currentColor); 
        s.setPaddingUnit(Style.UNIT_TYPE_PIXELS);
        s.setPadding(0, 0, 0, sizeOfSpace);
        s.setMargin(0, 0, 0, 0);
        lastCmp = s;
        add(l);
    }
    
    public final void setText(String text) {
        this.text = text;
        removeAll();
        try {
            char[] chrs = ("<body>" + text + "</body>").toCharArray();
            new Parser().eventParser(new CharArrayReader(chrs));
        } catch(IOException err) {
            log(err);
        }
    }

    public String getText() {
        return text;
    }
    
    public void addLinkListener(ActionListener al) {
        listeners.addListener(al);
    }

    public void removeLinkListener(ActionListener al) {
        listeners.removeListener(al);
    }

    class Parser extends XMLParser {
        @Override
        protected void textElement(String text) {
            if(text.length() > 0) {
                if(lastCmp != null && text.startsWith(" ")) {
                    lastCmp.setPadding(0, 0, 0, sizeOfSpace);                
                }
                createComponent(text);
                if(!text.endsWith(" ")) {
                    lastCmp.setPadding(0, 0, 0, 0);
                }
            }
        }

        @Override
        protected boolean startTag(String tag) {
            switch(tag.toLowerCase()) {
                case "a":
                    currentColor = 0x4267B2;
                    break;
                case "b":
                    currentFont = boldFont;
                    break;
                case "i":
                    currentFont = italicFont;
                    break;
            }
            return true;
        }

        @Override
        protected void endTag(String tag) {
            currentColor = 0;
            currentLink = null;
            currentFont = defaultFont;
        }

        @Override
        protected void attribute(
                String tag, String attributeName, String value) {
            if(tag.toLowerCase().equals("a") && 
                    attributeName.toLowerCase().equals("href")) {
                currentLink = value;
            }
        }

        @Override
        protected void notifyError(int errorId, String tag, 
                String attribute, String value, String description) {
            log("Error during parsing: " + tag);
        }
    }
}
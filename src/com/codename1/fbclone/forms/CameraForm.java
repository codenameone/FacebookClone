package com.codename1.fbclone.forms;

import com.codename1.camerakit.CameraEvent;
import com.codename1.camerakit.CameraKit;
import com.codename1.camerakit.CameraListener;
import com.codename1.components.FloatingActionButton;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import static com.codename1.ui.CN.*;
import com.codename1.ui.layouts.LayeredLayout;

public class CameraForm extends Form {
    public CameraForm(CameraKit ck) {
        super(new LayeredLayout());
        setScrollableY(false);
        
        ck.addCameraListener(new CameraListener() {
            @Override
            public void onError(CameraEvent ev) {
            }

            @Override
            public void onImage(CameraEvent ev) {
                ImagePicker p = ImagePicker.create(ev.getJpeg());
                NewPostForm.createImagePost(p.getImage(), p).show();
            }

            @Override
            public void onVideo(CameraEvent ev) {
            }
        });
        
        add(ck.getView());
        FloatingActionButton fab = FloatingActionButton.createFAB(
            FontImage.MATERIAL_CAMERA);
        fab.bindFabToContainer(this, CENTER, BOTTOM);
        fab.addActionListener(e -> ck.captureImage());
        getToolbar().setUIID("Container");
        Form previous = getCurrentForm();
        getToolbar().addMaterialCommandToLeftBar("", 
            FontImage.MATERIAL_CLOSE, e -> previous.showBack());
    }
}

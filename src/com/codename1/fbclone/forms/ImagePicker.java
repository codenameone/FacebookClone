package com.codename1.fbclone.forms;

import com.codename1.fbclone.server.ServerAPI;
import com.codename1.io.Log;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.io.InputStream;

public class ImagePicker {
    private String fileName;
    private EncodedImage img;
    public void pickImage(SuccessCallback<EncodedImage> onPick) {
        Display.getInstance().openGallery(e -> {
            if(e == null) {
                return;
            }
            String s = (String)e.getSource();
            if(s != null) {
                try(InputStream i = openFileInputStream(s)) {
                    img = EncodedImage.create(i, (int)getFileLength(s));
                    fileName = s;
                    onPick.onSucess(img);
                } catch(IOException err) {
                    Log.e(err);
                }
            }
        }, GALLERY_IMAGE);
    }

    public void upload(SuccessCallback<String> mediaId) {
        ServerAPI.uploadMedia("image/jpeg", "photo", "public", fileName, 
            img.getImageData(), mediaId);
    }
}

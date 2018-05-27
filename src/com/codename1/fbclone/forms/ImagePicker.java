package com.codename1.fbclone.forms;

import com.codename1.fbclone.server.ServerAPI;
import com.codename1.io.Log;
import com.codename1.io.MultipartRequest;
import com.codename1.io.Util;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class ImagePicker {
    private String fileName;
    private EncodedImage img;
    private int mode = GALLERY_IMAGE;
    public ImagePicker() {
    }

    public ImagePicker(int mode) {
        this.mode = mode;
    }
    
    public static ImagePicker create(String fileName) {
        ImagePicker p = new ImagePicker();
        try(InputStream i = openFileInputStream(fileName)) {
            p.img = EncodedImage.create(i, (int)getFileLength(fileName));
            p.fileName = fileName;
            return p;
        } catch(IOException err) {
            Log.e(err);
            return null;
        }
    }

    public static ImagePicker create(byte[] data) {
        ImagePicker p = new ImagePicker();
        p.img = EncodedImage.create(data);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HHmm");
        p.fileName = "camera-photo " + sd.format(new Date()) + ".jpeg";
        return p;
    }

    public void pickImage(SuccessCallback<EncodedImage> onPick) {
        pick(onPick, null);
    }

    public void pick(SuccessCallback<EncodedImage> onImage, 
            SuccessCallback<Media> onVideo) {
        Display.getInstance().openGallery(e -> {
            if(e == null) {
                return;
            }
            String s = (String)e.getSource();
            if(s == null) {
                return;
            }
            try {
                String sl = s.toLowerCase();
                fileName = s;
                if(sl.endsWith("jpeg") || sl.endsWith("jpg") || 
                    sl.endsWith("png")) {
                    try(InputStream i = openFileInputStream(s);) {
                        img = EncodedImage.create(i, (int)getFileLength(s));
                        onImage.onSucess(img);
                    }
                } else {
                    Media m = MediaManager.createMedia(s, true);
                    onVideo.onSucess(m);
                }
            } catch(IOException err) {
                Log.e(err);
            } 
        }, mode);
    }
    
    public MultipartRequest upload(SuccessCallback<String> mediaId) {
        if(img != null) {
            return ServerAPI.uploadMedia("image/jpeg", "photo", "public", 
                fileName, img.getImageData(), mediaId);
        } else {
            return ServerAPI.uploadMedia("video/mp4", "video", "public", 
                fileName, null, mediaId);
        }
    }
    
    public EncodedImage getImage() {
        return img;
    }
}

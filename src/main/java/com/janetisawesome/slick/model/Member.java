package com.janetisawesome.slick.model;
import android.content.Context;
import android.util.Log;

import com.janetisawesome.slick.R;
import com.janetisawesome.slick.utils.FileHelper;

import java.io.File;

/**
 * Member
 * Encapsulates a Member of Slack. Contains logic for image conversion
 * JSON/Gson serializable
 */
public class Member {

    private static final String TAG = "Member";

    // Private Data
    private String name;
    private String realName;
    private String color;
    private Profile profile;

    private transient File thumbnailCacheFile;
    private transient File imageCacheFile;
    // Public Methods
    public Member() {}

    // Member Data accessors
    public String getUserName() { return name; }
    public String getRealName() { return realName; }
    public int getColorInt() {
        try {
            return Integer.parseInt(color, 16);
        } catch (NumberFormatException nex) {
            Log.e(TAG, "getColorInt number format exception color=" + color);
        }
        return -1;
    }

    // Profile data accessors
    public String getTitle() {
        return profile.title;
    }

    public String getPhoneNumber() {
        return profile.phone;
    }

    public String getSkype() {
        return profile.skype;
    }

    public String getEmail() {
        return profile.email;
    }

    /**
     * Get the thumbnail network URI path
     * Adjusts for current screen resolution
     * @return
     * URI string to the thumbnail image
     */
    public String getThumbnailUri(Context context) {
        //TODO: check screen resolution & choose image(but we don't have the right images)
        return profile.image_72;
    }

    /**
     * Get the thumbnail path for File Cache
     * @param context
     * @return
     */
    public String getThumbnailCacheUri(Context context) {

        return "file:///" + getThumbnailCacheFile(context).getAbsolutePath();
    }


    public File getThumbnailCacheFile(Context context) {
        if (thumbnailCacheFile == null) {
            thumbnailCacheFile = new File(FileHelper.getImageCacheDir(context), getUserName() + "_th.png");
        }
        return thumbnailCacheFile;
    }

    /**
     * Get the image path for the current screen resolution
     * Used for Member detail views
     * @return
     * URI string to the large image
     */
    public String getImageUri(Context context) {
        //TODO: check screen resolution & choose image (but we don't have the right images)
        return this.profile.image_192;
    }

    /**
     * Get the thumbnail path for File Cache
     * @param context
     * @return
     */
    public String getImageCacheUri(Context context) {
        return "file:///" + getImageCacheFile(context).getAbsolutePath();
    }

    public File getImageCacheFile(Context context) {
        if (imageCacheFile == null) {
            imageCacheFile = new File(FileHelper.getImageCacheDir(context), getUserName() + ".png");
        }
        return imageCacheFile;
    }

}

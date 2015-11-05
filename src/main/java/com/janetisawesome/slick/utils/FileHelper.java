package com.janetisawesome.slick.utils;

import android.content.Context;

import java.io.File;

/**
 * FileHelper utility class
 *
 * Aggregate all the application file storage calls here,
 * so we know what is being stored and retrieved.
 */
public class FileHelper {

    private static String TAG = "FileHelper";

    // Do not instantiate
    private FileHelper() {}

    //////////////////////////////////////////////////////
    // Constants
    public static final String IMAGE_CACHE_PATH = "image";

    //////////////////////////////////////////////////////
    // Methods

    /**
     * getImageCacheDir
     * @param context
     * @return
     * the root for Image cache
     */
    public static File getImageCacheDir(Context context) {
        //TODO: use a subdir
        return context.getFilesDir();
    }

}

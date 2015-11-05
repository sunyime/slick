package com.janetisawesome.slick.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.janetisawesome.slick.model.Member;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MemberImageLoader {

    private static final String TAG = "MemberImageLoader";

    private Context mContext;
    private MemberImageLoader(Context context) {
        mContext = context;
    }

    /////////////////////////////////////////////////////////
    // Global Instance
    // (NOT thread-safe. for UI use only)
    private static MemberImageLoader sInstance = null;
    public static MemberImageLoader getInstance(Context context) {
        if (sInstance == null || sInstance.mContext != context) {
            sInstance = new MemberImageLoader(context);
        }
        return sInstance;
    }

    /**
     * Picasso Target
     */
    public static class LoaderTarget implements Target {

        private Context mContext;
        private Member  mMember;
        private ImageView mImageView;

        /**
         * Constructor
         * For loading image from the cached file
         * @param imageView
         */
        public LoaderTarget(ImageView imageView) {
            mImageView = imageView;
        }

        /**
         * Constructor
         * For loading image from the network (and then saving it to the file cache)
         * @param context
         * @param member
         * @param imageView
         */
        public LoaderTarget(Context context, Member member, ImageView imageView) {
            mContext = context;
            mMember = member;
            mImageView = imageView;
        }

        /**
         * cancel
         * Cancels the image load. Release references ASAP
         * ImageView will not be changed upon onBitmapLoaded
         */
        public void cancel() {
            mContext = null;
            mMember = null;
            mImageView = null;
        }

        /**
         * isCancelled
         * @return
         */
        public boolean isCancelled() {
            return mImageView == null;
        }

        @Override
        public void onPrepareLoad(Drawable arg0) {
            return;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom source) {

            // Cache network images to disk
            if (source == Picasso.LoadedFrom.NETWORK) {
                File file = mMember.getImageCacheFile(mContext);
                FileOutputStream os = null;

                try {

                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.close();
                }
                catch (FileNotFoundException fnf) {
                    Log.e(TAG, "onBitmapLoaded file not found", fnf);
                }
                catch (IOException ioe) {
                    Log.e(TAG, "onBitmapLoaded file write error", ioe);
                }
            }

            if (!isCancelled()) {
                mImageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
            return;
        }
    };

}

package com.debug.kiemtienuongcafe.process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;


/**
 * Created by Tam on 10/18/2017.
 */

public class CaptureManager {
    private static final String TAG = "nhatnhat";
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static CaptureManager INSTANCE;
    private Intent mIntent;
    private onSavedImageListener onSavedImageListener;
    private onGrantedPermissionListener onGrantedPermissionListener;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private int width,height, density;
    private boolean canTakeImage=true;
    onBitmapListener onBitmapListener;
    Image image;


    public static CaptureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CaptureManager();
        }
        return INSTANCE;
    }


    public void setOnGrantedPermissionListener(CaptureManager.onGrantedPermissionListener onGrantedPermissionListener) {
        this.onGrantedPermissionListener = onGrantedPermissionListener;
    }

    public void requestScreenshotPermission(@NonNull Activity activity, int requestId) {
        if (mIntent == null) {
            Log.d(TAG, "requestScreenshotPermission");
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                if (mediaProjectionManager != null) {
                    activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestId);
                }
            } catch (Exception e) {
            }

        } else {
            Log.d(TAG, "requestScreenshotPermission: aaaaa");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    Log.d(TAG, "requestScreenshotPermission: false");
                    if (onGrantedPermissionListener != null)
                        onGrantedPermissionListener.onResult(false);
                } else {
                    if (onGrantedPermissionListener != null)
                        onGrantedPermissionListener.onResult(true);
                }
                Log.d(TAG, "requestScreenshotPermission: true");
            } else {
                if (onGrantedPermissionListener != null)
                    onGrantedPermissionListener.onResult(true);
            }
        }
    }


    public void onActivityResult(int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (resultCode == Activity.RESULT_OK && data != null) {
            mIntent = data;
            if (onGrantedPermissionListener != null) onGrantedPermissionListener.onResult(true);
        } else {
            if (onGrantedPermissionListener != null) onGrantedPermissionListener.onResult(false);
            mIntent = null;
        }
    }
    public void init(@NonNull final Context context){
        onBitmapListener = (CaptureManager.onBitmapListener) context;
        final MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        try {
            if (mediaProjectionManager != null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mIntent);
            }
        } catch (IllegalStateException e) {
            Log.d(TAG, "takeScreenshot: mediaprojection already started");
        }

        density = context.getResources().getDisplayMetrics().densityDpi;
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final Point size = new Point();
        display.getRealSize(size);
//            if(width<height) {
        width = size.y;
        height = size.x;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onImageAvailable(final ImageReader reader) {

                Log.d("nhatnhat", "screen state change -> take screenshot: ");
                new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(final Void... params) {
                        Bitmap bitmap = null;
                        if (image != null) {
                            Image.Plane[] planes = image.getPlanes();
                            ByteBuffer buffer = planes[0].getBuffer();
                            int pixelStride = planes[0].getPixelStride(), rowStride = planes[0].getRowStride(), rowPadding = rowStride - pixelStride * width;


                            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(buffer);
                            Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(bmpGrayscale);
                            Paint paint = new Paint();
                            ColorMatrix cm = new ColorMatrix();
                            cm.setSaturation(0);
                            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                            paint.setColorFilter(f);
                            c.drawBitmap(bitmap, 0, 0, paint);
                            bitmap.recycle();
//                                bitmap.recycle();
//                                virtualDisplay.release();
                            image.close();
                            return bmpGrayscale;
                        }

//                        if (image != null)
//                            image.close();
//                            if (reader != null) {
//                                reader.close();
//                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final Bitmap bitmap) {
                        super.onPostExecute(bitmap);
//                            if (onSavedImageListener != null)
//                                onSavedImageListener.onSavedFailed();
                        onBitmapListener.onBitmapReady(bitmap);

                    }
                }.execute();

            }
        }, null);

        try {
            virtualDisplay = mediaProjection.createVirtualDisplay(SCREENCAP_NAME, width, height, density, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
        } catch (SecurityException e) {
            if (onSavedImageListener != null) onSavedImageListener.onNoPermission();
        }
//        mediaProjection.registerCallback(callback, null);
    }

    public void takeScreenshot() {
        image = imageReader.acquireNextImage();

    }

    private MediaProjection.Callback callback = new MediaProjection.Callback() {
        @Override
        public void onStop() {
            super.onStop();
            if (imageReader != null)
                imageReader.setOnImageAvailableListener(null, null);
            if (mediaProjection != null)
                mediaProjection.unregisterCallback(this);
        }
    };

    public void stopMediaProjection() {
        Log.d(TAG, "stopMediaProjection");
        if (mediaProjection != null) {
            if (callback != null)
                mediaProjection.unregisterCallback(callback);
            mediaProjection.stop();
            mIntent = null;
        }
    }

    public interface onSavedImageListener {
        void onBitmapComplete(Bitmap bitmap);

        void onSavedSuccess();

        void onSavedFailed();

        void onNoPermission();
    }

    public interface onBitmapListener{
        void onBitmapReady(Bitmap bitmap);
    }

    public interface onGrantedPermissionListener {
        void onResult(boolean isGranted);
    }
}


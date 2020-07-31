package com.debug.kiemtienuongcafe.remote;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.kiemtienuongcafe.R;
import com.debug.kiemtienuongcafe.process.CaptureManager;
import com.debug.kiemtienuongcafe.remote.model.CustomPath;
import com.debug.kiemtienuongcafe.remote.model.RemoteProfile;
import com.debug.kiemtienuongcafe.server.ClientManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RemoteService extends AccessibilityService{
    private Handler mHandler;
    private Intent broadcast =new Intent();
    public static boolean isConnected=false;
    private LayoutInflater layoutInflater;
    private  WindowManager windowManager;

    private CaptureManager captureManager;
    /**
     *
     */
    RemoteProfile profile;
    private boolean isCanClick=true;
    private boolean isCanClickMoRong=true;
    private GestureResultCallback gestureResultCallback;


    /**
     * Click
     */
    Map<String,AccessibilityNodeInfo> accessibilityNodeInfos;
    List<AccessibilityNodeInfo> accessibilityNodeInfoList;
    @Override
    public void onCreate() {
        super.onCreate();
        accessibilityNodeInfos = new HashMap<>();
        accessibilityNodeInfoList = new ArrayList<>();
//        ClientManager.getInstance().init(this);
//        clickViews = new ArrayList<>();
        HandlerThread handlerThread = new HandlerThread("auto-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
//
//        layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View view= layoutInflater.inflate(R.layout.overlay,null);
//        int LAYOUT_FLAG;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;}
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//               ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x =100;
//        params.y=100;
//       windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
//        view.findViewById(R.id.btn_addClick).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                addNewClickPosition();
//            }
//        });
//        view.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mRunnable == null) {
//                    mRunnable = new IntervalRunnable();
//                }
//                mHandler.postDelayed(mRunnable, 1000);
//            }
//        });
//        view.setOnTouchListener(new View.OnTouchListener() {
//            float dX, dY;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//
//                        dX = v.getX() - event.getRawX();
//                        dY = v.getY() - event.getRawY();
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        params.x= (int) (event.getRawX() + dX);
//                        params.y= (int) (event.getRawY() + dY);
//                        windowManager.updateViewLayout(view,params);
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });

//        windowManager.addView(view, params);
        gestureResultCallback = new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                isCanClick=true;

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                isCanClick=false;
            }
        };
    }
//    private void addNewClickPosition(){
//        int LAYOUT_FLAG;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;}
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//               100,
//               100,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//
//        ClickView clickview =new ClickView(this,windowManager,params);
//        clickview.setText("ádasdasdasd");
//        windowManager.addView(clickview,params);
//        clickViews.add(clickview);
//    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        isConnected=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            profile = (RemoteProfile) intent.getSerializableExtra(Cons.REMOTEPROFILE);
//            if(profile!=null){
//                switch (profile.getRemoteType()){
//                    case CLICK:{
//                        if (mRunnable == null) {
//                            mRunnable = new IntervalRunnable();
//                        }
//                        pathListWhenClick = profile.getPathList();
//                        if(pathListWhenClick.size()==0){
//                            Toast.makeText(this, "No paths to click", Toast.LENGTH_SHORT).show();
//                        }else {
//                            if (profile.getLoopTime()!=0&&profile.getLoopTime() < pathListWhenClick.size()) {
//                                pathListWhenClick.subList(pathListWhenClick.size() - profile.getLoopTime(), pathListWhenClick.size()).clear();
//                            }
//                            mHandler.postDelayed(mRunnable, profile.getDelayOnStart());
//                        }
//                        break;
//                    }
//                }
//            }else {
//                Toast.makeText(this, "Remote Profile must not be null", Toast.LENGTH_SHORT).show();
//            }
////            if (Cons.RemoteClick.equals(action)) {
////                clickPosition = intent.getIntArrayExtra(Cons.RemoteClick);
////                    if(clickPosition==null){
////                        Toast.makeText(this, "Action_Click need position int[x,y]", Toast.LENGTH_SHORT).show();
////                    }else {
////                        if (mRunnable == null) {
////                            mRunnable = new IntervalRunnable();
////                        }
////                        mHandler.postDelayed(mRunnable, click_delay);
////                    }
////            } else if (Cons.RemoteSwipe.equals(action)) {
////            }
//        }
        return super.onStartCommand(intent, flags, startId);
    }


    boolean inPrivateMode=false;
    boolean isOpenMoreTab=false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

        AccessibilityNodeInfo source = event.getSource();
        if (source != null ) {
            if(source.getPackageName().equals("com.android.chrome")){
                if(source.getChildCount()>0){
                    for (int i = 0; i < source.getChildCount(); i++) {
                        Rect position =new Rect();
                        source.getChild(i).getBoundsInScreen(position);
                        Log.d("nhatnhat", "child1" + source.getChild(i).toString()+"/"+position.centerX()+":"+position.centerY());
                        if(source.getChild(i).getContentDescription()!=null&&source.getChild(i).getContentDescription().toString().contains("Tùy chọn khác")){
                            if(!isOpenMoreTab&&isCanClick) {
                                click(source.getChild(i),200, new GestureResultCallback() {
                                    @Override
                                    public void onCompleted(GestureDescription gestureDescription) {
                                        super.onCompleted(gestureDescription);
                                        isOpenMoreTab = true;
                                        isCanClick=true;
                                    }

                                    @Override
                                    public void onCancelled(GestureDescription gestureDescription) {
                                        super.onCancelled(gestureDescription);
                                        isOpenMoreTab = false;
                                        isCanClick=true;
                                    }
                                });
                            }
                        }
                        if(source.getChild(i).getChildCount()>0){
                            for (int j = 0; j < source.getChild(i).getChildCount(); j++) {

                                Log.d("nhatnhat", "child2" + source.getChild(i).getChild(j).toString());
                                if(source.getChild(i).getChild(j).getText()!=null&&source.getChild(i).getChild(j).getText().toString().contains("Tìm kiếm hoặc nhập địa chỉ web")){
//                                    if(inAnDanhMode) {
//                                        Bundle arguments = new Bundle();
//                                        arguments.putCharSequence(
//                                                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "Con chó vẹn ");
//                                        source.getChild(i).getChild(j).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                                    }
                                }else if(source.getChild(i).getChild(j).getText()!=null&&source.getChild(i).getChild(j).getText().toString().contains("Tab ẩn danh mới")){

                                }
                            }
                        }
                    }
                }

                if(source.getText()!=null) {
                    if (source.getText().equals("Cho phép") || source.getText().equals("Allow") || source.getText().equals("Skip Ad") || source.getText().equals("0 giây")) {
                        Rect position = new Rect();
                        source.getBoundsInScreen(position);
                        Path path = new Path();
                        path.moveTo(position.centerX(), position.centerY());
                        GestureDescription.Builder builder = new GestureDescription.Builder();
                        builder.addStroke(new GestureDescription.StrokeDescription(path, 1000, 1000));
                        final GestureDescription gestureDescription = builder.build();
                        dispatchGesture(gestureDescription, new GestureResultCallback() {
                            @Override
                            public void onCompleted(GestureDescription gestureDescription) {
                                super.onCompleted(gestureDescription);
                                Toast.makeText(RemoteService.this, "Clicked Cho Phép", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(GestureDescription gestureDescription) {
                                super.onCancelled(gestureDescription);
                                Toast.makeText(RemoteService.this, "Fail" + gestureDescription.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }, null);
                    }
                    for (int i = 0; i < source.getChildCount(); i++) {
                        if (source.getChild(i) != null && source.getChild(i).getText() != null) {
                            if (source.getChild(i).getText().equals("Cho phép") || source.getChild(i).getText().equals("Allow") || source.getChild(i).getText().equals("Skip Ad") || source.getChild(i).getText().equals("0 giây")) {
                                Rect position = new Rect();
                                source.getChild(i).getBoundsInScreen(position);
                                Path path = new Path();
                                path.moveTo(position.centerX(), position.centerY());
                                GestureDescription.Builder builder = new GestureDescription.Builder();
                                builder.addStroke(new GestureDescription.StrokeDescription(path, 1000, 1000));
                                final GestureDescription gestureDescription = builder.build();
                                dispatchGesture(gestureDescription, new GestureResultCallback() {
                                    @Override
                                    public void onCompleted(GestureDescription gestureDescription) {
                                        super.onCompleted(gestureDescription);
                                        Toast.makeText(RemoteService.this, "Clicked Cho Phép", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(GestureDescription gestureDescription) {
                                        super.onCancelled(gestureDescription);
                                        Toast.makeText(RemoteService.this, "Fail" + gestureDescription.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }, null);
                            }
                            Log.d("nhatnhat", "onAccessibilityEvent: " + source.getChild(i).getText());
                        }
                    }
                }

            }else {
                Rect position2 = new Rect();
                source.getBoundsInScreen(position2);
                Log.d("nonchrome", "onAccessibilityEvent: "+source.getText()+"/"+position2.centerX()+":"+position2.centerY());
                for (int i = 0; i < source.getChildCount(); i++) {
                    if (source.getChild(i) != null && source.getChild(i).getText() != null) {
                        if (source.getChild(i).getText().equals("Cho phép") || source.getChild(i).getText().equals("Allow") || source.getChild(i).getText().equals("Skip Ad") || source.getChild(i).getText().equals("0 giây")) {

                        }
                        Log.d("nhatnhat", "onAccessibilityEvent: " + source.getChild(i).getText());
                    }
                }

            }
        }

    }

    private void click() {
        isCanClick=false;
        if(accessibilityNodeInfoList.size()==0){
            Toast.makeText(this, "Click completed", Toast.LENGTH_SHORT).show();
            isCanClick=true;
            return;
        }
        final AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoList.get(0);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path =new Path();
        Rect position = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(position);
        path.moveTo(position.centerX(),position.centerY());
        builder.addStroke(new GestureDescription.StrokeDescription( path, 0,1000));
        final GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Toast.makeText(RemoteService.this,   gestureDescription.getStrokeCount()+"", Toast.LENGTH_SHORT).show();
                accessibilityNodeInfoList.remove(0);
                mHandler.postDelayed(mRunnable,1000 );
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Toast.makeText(RemoteService.this, "Fail"+gestureDescription.toString(), Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    private void click(AccessibilityNodeInfo child, final long duration, final GestureResultCallback gestureResultCallback) {
        isCanClick=false;
        Rect position = new Rect();
        child.getBoundsInScreen(position);
        final Path path = new Path();
        path.moveTo(position.centerX(), position.centerY());
        if(path.isEmpty())
            return;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GestureDescription.Builder builder = new GestureDescription.Builder();
                builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
                final GestureDescription gestureDescription = builder.build();
                dispatchGesture(gestureDescription, gestureResultCallback, null);
            }
        }, 2000);

    }

    @Override
    public void onInterrupt() {

    }


//    private void swipe(){
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//
//        int middleYValue = displayMetrics.heightPixels / 2;
//        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
//        final int rightSizeOfScreen = leftSideOfScreen * 3;
//        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//        Path path = new Path();
//
//        if (clicker_now%2==0) {
//            //Swipe left
//            path.moveTo(rightSizeOfScreen, middleYValue);
//            path.lineTo(leftSideOfScreen, middleYValue);
//        } else {
//            //Swipe right
//            path.moveTo(leftSideOfScreen, middleYValue);
//            path.lineTo(rightSizeOfScreen, middleYValue);
//        }
//
//        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 100, 50));
//        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                super.onCompleted(gestureDescription);
//                clicker_now += 1;
////                if(clicker_now==number_clicker){
////                    clicker_now = 0;
////                }
//                mHandler.postDelayed(mRunnable, (long) 1000);
//            }
//        }, null);
//    }
    private IntervalRunnable mRunnable;



    private class IntervalRunnable implements Runnable {
        @Override
        public void run() {
            click();
//            playTap();
//            swipe();
        }
    }


}

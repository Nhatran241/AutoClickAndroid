package com.debug.kiemtienuongcafe.remote;

import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;


public class ClickView extends androidx.appcompat.widget.AppCompatTextView {
    private Path path = new android.graphics.Path();
    private int looptime=0;
    private long duration=100;
    private long delay_start=500;
    private long delay_loop=500;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private boolean readyForClick =false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(readyForClick)
            return false;
        float dX = 0, dY = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        dX = getX() - event.getRawX();
                        dY = getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        params.x= (int) (event.getRawX() + dX);
                        params.y= (int) (event.getRawY() + dY);
                        path.moveTo( params.x+100,params.y);
                        windowManager.updateViewLayout(this,params);
                        break;
                    default:
                        return false;
                }
                return true;
    }

    public boolean isReadyForClick() {
        return readyForClick;
    }

    public void setReadyForClick(boolean readyForClick) {
        this.readyForClick = readyForClick;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getLooptime() {
        return looptime;
    }

    public void setLooptime(int looptime) {
        this.looptime = looptime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDelay_start() {
        return delay_start;
    }

    public void setDelay_start(long delay_start) {
        this.delay_start = delay_start;
    }

    public long getDelay_loop() {
        return delay_loop;
    }

    public void setDelay_loop(long delay_loop) {
        this.delay_loop = delay_loop;
    }

    public ClickView(Context context,WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context);
        this.windowManager =windowManager;
        this.params =params;
        path.moveTo( params.x,params.y);
    }

    public ClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

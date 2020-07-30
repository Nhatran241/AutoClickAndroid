package com.debug.kiemtienuongcafe.remote.model;

import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class RemoteProfile implements Serializable {
    public enum RemoteType{
        CLICK,
        SWIPE
    }
    private RemoteType remoteType;
    private long delayOnStart =0;
    private long delayOnLoop =500;
    private long duration;
    private int loopTime =0;

    private Path path;

    public RemoteProfile(@NonNull RemoteType remoteType, @NonNull Path path,int loopTime,long delayOnStart, long delayOnLoop, long duration) {
        this.remoteType = remoteType;
        this.path = path;
        this.loopTime = loopTime;
        this.delayOnStart = delayOnStart;
        this.delayOnLoop = delayOnLoop;
        this.duration = duration;
    }

    public RemoteType getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(@NonNull RemoteType remoteType) {
        this.remoteType = remoteType;
    }

    public long getDelayOnStart() {
        return delayOnStart;
    }

    public void setDelayOnStart(long delayOnStart) {
        this.delayOnStart = delayOnStart;
    }

    public long getDelayOnLoop() {
        return delayOnLoop;
    }

    public void setDelayOnLoop(long delayOnLoop) {
        this.delayOnLoop = delayOnLoop;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLoopTime() {
        return loopTime;
    }

    public void setLoopTime(int loopTime) {
        this.loopTime = loopTime;
    }

    public Path getPathList() {
        return path;
    }

    public void setPathList(@NonNull Path path) {
        this.path = path;
    }
}

package com.debug.kiemtienuongcafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.kiemtienuongcafe.process.CaptureManager;
import com.debug.kiemtienuongcafe.remote.RemoteService;
import com.debug.kiemtienuongcafe.server.ClientManager;
import com.nhatran241.simplepermission.PermissionManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements PermissionManager.IGrantPermissionListener{

    private static final int REQUESTACCESSIBILITY = 2222;
    private static final int REQUESTCAPTURE = 2333;
    private CaptureManager captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(RemoteService.isConnected){
            PermissionManager.getInstance().GrantPermission(this, PermissionManager.PermissionType.OVERLAY,this);
        }else {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent,REQUESTACCESSIBILITY);
        }
//            captureManager = new CaptureManager();
//            captureManager.requestScreenshotPermission(this, REQUESTCAPTURE);
//            captureManager.setOnGrantedPermissionListener(new CaptureManager.onGrantedPermissionListener() {
//                @Override
//                public void onResult(boolean isGranted) {
//                    if (isGranted) {
//                        Toast.makeText(MainActivity.this, "camera", Toast.LENGTH_SHORT).show();
//                            PermissionManager.getInstance().GrantPermission(MainActivity.this, PermissionManager.PermissionType.OVERLAY,MainActivity.this);
//
//                    } else {
//                        captureManager.requestScreenshotPermission(MainActivity.this, REQUESTCAPTURE);
//                    }
//                }
//            });



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.getInstance().onActivityForResult(this,requestCode,resultCode,data);
        if(requestCode==REQUESTCAPTURE){
            captureManager.onActivityResult(resultCode, data);
        }

    }

    @Override
    public void OnGrantPermissionSuccess(PermissionManager.PermissionType permissionType) {
        Toast.makeText(this, ""+RemoteService.isConnected, Toast.LENGTH_SHORT).show();
        if(RemoteService.isConnected){
            PermissionManager.getInstance().GrantPermission(this, PermissionManager.PermissionType.OVERLAY,this);
        }else {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent,REQUESTACCESSIBILITY);
        }

    }

    @Override
    public void OnGrantPermissionFail(PermissionManager.PermissionType permissionType, String errror) {

    }

    private class GetIP extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect("http://www.checkip.org").get();
                return doc.getElementById("yourip").select("h1").first().select("span").text();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String res) {
            super.onPostExecute(res);
            if(TextUtils.isEmpty(res)){
                Toast.makeText(MainActivity.this, "No ip", Toast.LENGTH_SHORT).show();
            }else {
                ClientManager.getInstance().checkIp(res, new ClientManager.ClientManagerListener() {
                    @Override
                    public void onIPIsNew(boolean b) {
                        if(b) {
                            ClientManager.getInstance().addIp(res, this);
                        }else {
                            Toast.makeText(MainActivity.this, "exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAddIpSuccess() {

                    }

                    @Override
                    public void onAddIpFail(String e) {
                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCheckIp(String e) {
                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

}

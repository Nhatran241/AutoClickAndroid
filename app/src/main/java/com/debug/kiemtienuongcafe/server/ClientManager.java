package com.debug.kiemtienuongcafe.server;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientManager {
    private static ClientManager instance;
    private FirebaseFirestore db;

    public static ClientManager getInstance() {
        if(instance==null)
            instance = new ClientManager();
        return instance;
    }
    public void init(Context context){
        db = FirebaseFirestore.getInstance();
    }
    public void checkIp(final String IP, final ClientManagerListener clientManagerListener){
        if(db==null)
            return;
        DocumentReference docRef = db.collection("SERVER").document("IPLIST");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = new ArrayList<>();

                        Map<String, Object> map = document.getData();
                        if (map != null) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                list.add(entry.getKey());
                            }
                        }
                        clientManagerListener.onIPIsNew(!list.contains(IP));

                    } else {
                        clientManagerListener.onIPIsNew(false);
                    }
                } else {
                    clientManagerListener.onCheckIp(task.getException().getMessage());
                }
            }
        });
    }
    public void addIp(String IP, final ClientManagerListener clientManagerListener){
        Map<String, Boolean> ip = new HashMap<>();
        ip.put(IP, true);

        db.collection("SERVER").document("IPLIST").set(ip)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        clientManagerListener.onAddIpSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        clientManagerListener.onAddIpFail(e.getMessage());
                    }
                });
    }
    public interface ClientManagerListener{
        void onIPIsNew(boolean b);
        void onAddIpSuccess();
        void onAddIpFail(String e);
        void onCheckIp(String e);
    }
}

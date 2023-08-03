package com.miniproject.cyberfraudsocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityChat extends AppCompatActivity {

    AdapterMessage adapter;
    List<ModelMessage> list;
    RecyclerView recyclerView;

    EditText messageEdt;
    ImageButton send;

    MaterialToolbar toolbar;

    String userID,userName;
    String docId;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //bind all the views
        initiateWithID();

        setUpToolbar();

        //initiate firebase classes
        initiateFirebaseClasses();

        //setup tab recycler view
        setupRecyclerView();

        //get Data from firebase
        getDataFromFirebase();

        messageEdt.performClick();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageEdt.getText().toString().trim().length()>0) {
                    sendMessage();
                    messageEdt.setText("");
                }
            }
        });

    }

    private void sendMessage() {

        String s =  messageEdt.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa, MMM dd");
        String currentDateandTime = sdf.format(new Date());

        Map<String,Object> map = new HashMap<>();

        map.put(Keys.CHAT_NAME,userName);
        map.put(Keys.CHAT_MESSAGE,s);
        map.put(Keys.CHAT_TIME,currentDateandTime);

        fStore.collection(Keys.CHAT_COLLECTION)
                .add(map)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            String id = task.getResult().getId();
                            fStore.collection(Keys.CHATROOM_COLLECTION)
                                    .document(docId)
                                    .update(Keys.CHATROOM_MESSAGES, FieldValue.arrayUnion(id));
                        }
                    }
                });
    }

    private void getDataFromFirebase() {

        fStore.collection(Keys.CHATROOM_COLLECTION)
                .document(docId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        try{
                            Map<String, Object> map = value.getData();
                            List<String> ids = (List<String>) map.get(Keys.CHATROOM_MESSAGES);

                            if(ids!=null) {
                                list.clear();
                                adapter.notifyDataSetChanged();

                                for (String id : ids) {
                                    fStore.collection(Keys.CHAT_COLLECTION)
                                            .document(id)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot ds) {
                                                    String name = ds.getString(Keys.CHAT_NAME);
                                                    String date = ds.getString(Keys.CHAT_TIME);
                                                    String message = ds.getString(Keys.CHAT_MESSAGE);

                                                    ModelMessage m = new ModelMessage(name, date, message);
                                                    list.add(m);
                                                }
                                            });
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){

                        }

                    }
                });

    }

    private void initiateWithID() {

        messageEdt = findViewById(R.id.chatroom_message);
        send = findViewById(R.id.chatroom_send);

        toolbar = findViewById(R.id.toolbar_chat);

        recyclerView = findViewById(R.id.chatroom_rv);

        docId = "try";

        Bundle b = getIntent().getExtras();
        docId = b.get(Keys.CHATROOM_NAME).toString();

        messageEdt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageEdt, InputMethodManager.SHOW_IMPLICIT);

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        userName = dc.getString(Keys.USER_NAME);
                    }
                });

        fStore.collection(Keys.CHATROOM_COLLECTION)
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        toolbar.setTitle(documentSnapshot.getString(Keys.CHATROOM_NAME));
                    }
                });
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setupRecyclerView() {
        list = new ArrayList<>();

        adapter = new AdapterMessage(getApplicationContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
}
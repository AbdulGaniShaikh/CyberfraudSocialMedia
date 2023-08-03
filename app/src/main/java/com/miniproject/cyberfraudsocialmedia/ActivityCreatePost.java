package com.miniproject.cyberfraudsocialmedia;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityCreatePost extends AppCompatActivity {

    ImageButton close;
    ImageView img,dp;
    Button send,attach;
    EditText text;
    TextView name;
    Uri imgUri;

    boolean isAtttached;

    ProgressDialog progressDialog;

    String userID,userName;
    private final int REQUEST_CODE = 100;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage fStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //add Constraints to input fields;
        addTextChangeListner();

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAtttached){
                    isAtttached = false;
                    img.setVisibility(View.GONE);
                    attach.setText("Attach Image");
                    img.setImageURI(null);
                    imgUri = null;
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,REQUEST_CODE);
                    attach.setEnabled(false);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = text.getText().toString().trim();
                Map<String,Object> map = Keys.getTags(s);
                createPost(s,map);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void createPost(String s, Map<String, Object> map) {

        progressDialog = new ProgressDialog(ActivityCreatePost.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setTitle("Creating post");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String imgName = UUID.randomUUID().toString();

        if (imgUri!=null){
            storageReference.child(Keys.POST_COLLECTION+"/"+imgName)
                    .putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Map<String, Object> data = new HashMap<>();
                            data.put(Keys.POST_BY, userID);
                            data.put(Keys.POST_BYNAME,userName);
                            data.put(Keys.POST_TAGS,map);
                            data.put(Keys.POST_IMG,imgName);
                            data.put(Keys.POST_TEXT,s);
                            data.put(Keys.POST_TIMESTAMP,new Timestamp(new Date()));

                            fStore.collection(Keys.POST_COLLECTION)
                                    .add(data)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                saveToUserDB(task.getResult().getId());
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Posting unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Map<String, Object> data = new HashMap<>();
            data.put(Keys.POST_BY, userID);
            data.put(Keys.POST_BYNAME,userName);
            data.put(Keys.POST_TAGS,map);
            data.put(Keys.POST_IMG,"");
            data.put(Keys.POST_TEXT,s);
            data.put(Keys.POST_TIMESTAMP,new Timestamp(new Date()));

            fStore.collection(Keys.POST_COLLECTION)
                    .add(data)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                saveToUserDB(task.getResult().getId());
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }

    }

    private void saveToUserDB(String id) {

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.USER_POSTS+"."+id,true);
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .update(map);

        String s = text.getText().toString().trim();
        Map<String,Object> keys = Keys.getTags(s);

        map.clear();
        map.put(id,true);

        for (Map.Entry<String,Object> entry : keys.entrySet()) {

            fStore.collection(Keys.HASHTAG_COLLECTION)
                    .document(entry.getKey())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                fStore.collection(Keys.HASHTAG_COLLECTION)
                                        .document(entry.getKey())
                                        .update(Keys.HASHTAG_POSTID, FieldValue.arrayUnion(id));

                            }else{
                                Map<String,Object> set = new HashMap<>();
                                set.put(Keys.HASHTAG_POSTID, Arrays.asList(id));

                                fStore.collection(Keys.HASHTAG_COLLECTION)
                                        .document(entry.getKey())
                                        .set(set);
                            }
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==REQUEST_CODE){

            img.setVisibility(View.VISIBLE);
            imgUri = data.getData();
            img.setImageURI(imgUri);
            attach.setText("Remove Image");
            isAtttached = true;

            Toast.makeText(getApplicationContext(), imgUri.getPath(), Toast.LENGTH_SHORT).show();

        }
        attach.setEnabled(true);

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();

        userID = fAuth.getCurrentUser().getUid();
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        userName = dc.getString(Keys.USER_NAME);
                        name.setText(userName);
                    }
                });
    }

    private void initiateWithID() {

        close = findViewById(R.id.create_post_close);
        dp = findViewById(R.id.create_post_dp);
        img = findViewById(R.id.create_post_img);

        text = findViewById(R.id.create_post_edt);
        name = findViewById(R.id.create_post_username);
        send = findViewById(R.id.create_post_send);
        attach = findViewById(R.id.create_post_attach);

        isAtttached = false;

    }

    private void addTextChangeListner() {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                send.setEnabled(text.getText().toString().trim().length()>0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
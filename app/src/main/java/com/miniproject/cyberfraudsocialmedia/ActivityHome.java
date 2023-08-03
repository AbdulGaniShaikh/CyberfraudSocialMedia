package com.miniproject.cyberfraudsocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityHome extends AppCompatActivity {

    FloatingActionButton fab;

    AdapterPost adapter;
    List<ModelPost> list;
    RecyclerView recyclerView;

    String userID,userName;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*
        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //setup tab recycler view
        setupRecyclerView();

        //get the tweet data from firebase
        getDataFromFirebase();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityHome.this,ActivityCreatePost.class));
            }
        });
        */
    }

    /*private void getDataFromFirebase() {

        fStore.collection(Keys.POST_COLLECTION)
                .orderBy(Keys.POST_TIMESTAMP, Query.Direction.ASCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                        for (DocumentSnapshot dc : value.getDocuments()){

                            SimpleDateFormat sfd = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa");
                            String date = sfd.format(dc.getTimestamp(Keys.POST_TIMESTAMP).toDate());

                            String imgname = dc.getString(Keys.POST_IMG);
                            if(imgname.length()>0) {
                                try {
                                    final File file = File.createTempFile(imgname, "");
                                    storageReference.child("posts/" + imgname)
                                            .getFile(file)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    ModelPost m = new ModelPost(
                                                            dc.getString(Keys.POST_BYNAME),
                                                            dc.getString(Keys.POST_BY),
                                                            date,
                                                            dc.getString(Keys.POST_TEXT),
                                                            BitmapFactory.decodeFile(file.getAbsolutePath()),
                                                            0
                                                    );
                                                    list.add(m);
                                                    adapter.notifyItemInserted(list.size() - 1);
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                ModelPost m = new ModelPost(
                                        dc.getString(Keys.POST_BYNAME),
                                        dc.getString(Keys.POST_BY),
                                        date,
                                        dc.getString(Keys.POST_TEXT),
                                        null,
                                        0
                                );
                                list.add(m);
                                adapter.notifyItemInserted(list.size() - 1);
                            }
                        }
                    }
                });

    }

    private void initiateWithID() {

        fab = findViewById(R.id.home_fab);
        recyclerView = findViewById(R.id.home_rv);

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
                    }
                });
    }

    private void setupRecyclerView() {
        list = new ArrayList<>();

        adapter = new AdapterPost(getApplicationContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }*/
}
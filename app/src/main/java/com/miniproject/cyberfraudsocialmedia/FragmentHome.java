package com.miniproject.cyberfraudsocialmedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentHome extends Fragment {

    FloatingActionButton fab;

    AdapterPost adapter;
    List<ModelPost> list;
    RecyclerView recyclerView;

    MaterialToolbar toolbar;
    EditText searchBar;

    String userID,userName;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_home, container, false);
        fab = root.findViewById(R.id.home_fab);
        recyclerView = root.findViewById(R.id.home_rv);
        searchBar = root.findViewById(R.id.search_home);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),ActivityCreatePost.class));
            }
        });


        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //setup tab recycler view
        setupRecyclerView();

        //get the tweet data from firebase
        getDataFromFirebase();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }


    private void getDataFromFirebase() {

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
                                                            dc.getId(),
                                                            dc.getString(Keys.POST_IMG),
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
                                        dc.getId(),
                                        null,
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

        adapter = new AdapterPost(getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterPost.OnItemClickListener() {
            @Override
            public void onDeleteClick(ModelPost m) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Confirm deletion");
                builder.setMessage("Are you sure you want to delete this post?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fStore.collection(Keys.POST_COLLECTION)
                                        .document(m.getDocId())
                                        .delete();

                                Map<String, Object> map = new HashMap<>();
                                map.put(Keys.USER_POSTS+"."+m.getDocId(), FieldValue.delete());

                                fStore.collection(Keys.USER_COLLECTION)
                                        .document(userID)
                                        .update(map);

                                if (m.getImageId()!=null)
                                    storageReference.child(Keys.POST_COLLECTION+"/"+m.getImageId()).delete();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
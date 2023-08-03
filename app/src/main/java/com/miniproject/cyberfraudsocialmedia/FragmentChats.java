package com.miniproject.cyberfraudsocialmedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentChats extends Fragment {

    View root;
    FloatingActionButton fab;

    AdapterChatList adapter;
    List<ModelChatList> list;
    RecyclerView recyclerView;

    AlertDialog.Builder ansBuilder;
    AlertDialog ansDialog;

    TextInputEditText mAns;
    TextInputLayout mAnsTxt;
    TextView submitAnsBtn,qAns;

    String userID,userName;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chats, container, false);

        fab = root.findViewById(R.id.chats_fab);
        recyclerView = root.findViewById(R.id.chats_rv);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerFormDisplay();
            }
        });

        //initiate firebase classes
        initiateFirebaseClasses();

        //setup tab recycler view
        setupRecyclerView();

        //get Data from firebase;
        getDataFromFirebase();

        return root;
    }

    private void answerFormDisplay() {
        ansBuilder = new AlertDialog.Builder(recyclerView.getContext());
        View popForm = getLayoutInflater().inflate(R.layout.item_createroom, null);

        mAns = popForm.findViewById(R.id.ans_FA);
        mAnsTxt = popForm.findViewById(R.id.ans_txt_FA);
        submitAnsBtn = popForm.findViewById(R.id.submit_FA);

        ansBuilder.setView(popForm);
        ansDialog = ansBuilder.create();

        ansDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ansDialog.show();

        submitAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer = mAns.getText().toString();

                if (answer.equals("")){
                    mAnsTxt.setError("Enter the Answer First");
                    return;
                }
                mAnsTxt.setError(null);

                Map<String, Object> map = new HashMap<>();
                map.put(Keys.CHATROOM_NAME,answer);
                List<String> el = new ArrayList<>();
                map.put(Keys.CHATROOM_MESSAGES,el);

                fStore.collection(Keys.CHATROOM_COLLECTION)
                        .add(map)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Chatroom created", Toast.LENGTH_SHORT).show();
                                ansDialog.dismiss();
                            }
                        });
            }
        });
        ansDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fab.setClickable(true);
            }
        });
    }

    private void getDataFromFirebase() {

        fStore.collection(Keys.CHATROOM_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> l = value.getDocuments();
                        if (!l.isEmpty()){
                            list.clear();
                            adapter.notifyDataSetChanged();
                            for (DocumentSnapshot ds : l){
                                String name = ds.getString(Keys.CHATROOM_NAME);
                                List<String> ls = (List<String>) ds.get(Keys.CHATROOM_MESSAGES);
                                String message;
                                if (ls.isEmpty()){
                                    message = "No Messages Yet";
                                }else{
                                    message = ls.get(ls.size()-1);
                                }
                                list.add(new ModelChatList(name,message,ds.getId()));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

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
    }

    private void setupRecyclerView() {
        list = new ArrayList<>();

        adapter = new AdapterChatList(getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterChatList.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(),ActivityChat.class);
                intent.putExtra(Keys.CHATROOM_NAME,list.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onChatDeleteClick(int i) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Confirm deletion");
                builder.setMessage("Are you sure you want to delete this chatroom?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ModelChatList m = list.get(i);

                                fStore.collection(Keys.CHATROOM_COLLECTION)
                                        .document(m.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                list.remove(i);
                                                adapter.notifyItemRemoved(i);
                                            }
                                        });
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
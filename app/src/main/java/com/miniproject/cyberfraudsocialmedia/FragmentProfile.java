package com.miniproject.cyberfraudsocialmedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragmentProfile extends Fragment {

    TextView nametxt, emailtxt, resettxt, logouttxt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageReference;

    String userID, userName, userEmail;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    TextInputEditText mResetPass;
    TextInputLayout textInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        nametxt = root.findViewById(R.id.profile_name);
        emailtxt = root.findViewById(R.id.profile_email);
        resettxt = root.findViewById(R.id.resettxt);
        logouttxt = root.findViewById(R.id.logouttxt);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = fAuth.getCurrentUser();


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
                        userEmail = dc.getString(Keys.USER_EMAIL);
                        nametxt.setText(userName);
                        emailtxt.setText(userEmail);
                    }
                });

        resettxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                View popForm = getLayoutInflater().inflate(R.layout.reset_password,null);
                TextView textView = popForm.findViewById(R.id.resettxtmsg);
                textView.setText("Enter your new Password greater than 6 characters");
                textInputLayout = popForm.findViewById(R.id.resetpasslayout);
                textInputLayout.setHint("Enter New Password");

                mResetPass = popForm.findViewById(R.id.resetpass);
                builder.setView(popForm);

                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String password = mResetPass.getText().toString();
                        firebaseUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Password Reset Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();
            }
        });

        logouttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Log Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), ActivityAuthentication.class));
                getActivity().finish();

            }
        });

        return root;
    }
}
package com.example.camguard.UI.User;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ModuleUser module;
    BottomNavigationView bottomNavigationView;
    TextView tvUsername, tvEmail, tvReports;
    Button btnLogout, btnEdit;
    boolean passwordVisible = false;
    FirebaseFirestore FireStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        module = new ModuleUser(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        tvUsername = findViewById(R.id.nameTextView);
        tvEmail = findViewById(R.id.emailTextView);
        tvReports = findViewById(R.id.reportCountTextView);
        btnLogout = findViewById(R.id.LogOutButton);
        btnLogout.setOnClickListener(this);
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);


            tvUsername.setText(CurrentUser.getName());
            tvEmail.setText(CurrentUser.getEmail());
            tvReports.setText("Reports: " + module.getReports(CurrentUser.getName()));
            if (!module.DoesRemember()) {
                module.DoNotRemember();
            }

        if(CurrentUser.getEmail().equals("s16131@nhs.co.il"))
        {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        }
        bottomNavigationView.setSelectedItemId(R.id.menu_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(UserActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_camera)
            {
                Intent intent = new Intent(UserActivity.this, CameraActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_admin)
            {
                Intent intent = new Intent(UserActivity.this, AdminActivity.class);
                startActivity(intent);
            }
            return true;
        });



    }

    @Override
    public void onClick(View view) {
        if(view == btnLogout)
        {
            module.DoNotRemember();
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if(view == btnEdit)
        {
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.update_user);
            EditText upname,upmail,uppass;
            Button btnClose,btnUpdate;
            btnUpdate = dialog.findViewById(R.id.btnUpdate);
            btnClose= dialog.findViewById(R.id.btnCancel);
            upname = dialog.findViewById(R.id.editTextName);
            upmail = dialog.findViewById(R.id.editTextEmail);
            uppass = dialog.findViewById(R.id.editTextPassword);
            uppass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    final int DRAWABLE_RIGHT = 2;
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        if(motionEvent.getRawX() >= (uppass.getRight() - uppass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                        {
                            if(passwordVisible)
                            {
                                uppass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                                uppass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                passwordVisible = false;
                            } else
                            {
                                uppass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                                uppass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                passwordVisible = true;
                            }
                        }
                    }
                    return false;
                }
            });
            upname.setText(CurrentUser.getName());
            upmail.setText(CurrentUser.getEmail());
            uppass.setText(module.getUserByName(CurrentUser.getName()).getString(2));
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mailU,nameU,passU;
                    mailU = upmail.getText().toString();
                    nameU = upname.getText().toString();
                    passU = uppass.getText().toString();

                    if(!module.CheckUps(upname, upmail,uppass))
                    {
                        return;
                    }

                    CheckForFirebase(upname, upmail, new FirebaseCallback() {
                        @Override
                        public void onResult() {
                            if(upname.getError() == null && upmail.getError() == null) {
                                module.UpdateSharedPreference(nameU, mailU);
                                module.UpdateUser(CurrentUser.getId(), nameU, passU, mailU);
                                module.UpdateFireStoreUser(tvUsername.getText().toString(), nameU, mailU, passU);
                                tvUsername.setText(nameU);
                                tvEmail.setText(mailU);
                                dialog.dismiss();
                            }
                        }
                    });

                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public interface FirebaseCallback {
        void onResult();
    }
    public void CheckForFirebase(EditText etUser, EditText etEmail, FirebaseCallback firebaseCallback)
    {
        checkUserAndEmailExistence(etUser, etEmail, new FireStoreCallback() {
            @Override
            public void onCallback(boolean usernameExists, boolean emailExists) {
                if (usernameExists) {
                    etUser.setError("Username already exists");
                }
                if (emailExists) {
                    etEmail.setError("Email already exists");
                }
                firebaseCallback.onResult();
            }
        });

    }


    public interface FireStoreCallback {
        void onCallback(boolean usernameExists, boolean emailExists);
    }

    public void checkUserAndEmailExistence(EditText user, EditText email ,final FireStoreCallback callback) {
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean usernameExists = false;
                boolean emailExists = false;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getData().get("name").toString().equals(user.getText().toString()) && !document.getData().get("name").toString().equals(CurrentUser.getName())) {
                        user.setError("Username already exists");
                        usernameExists = true;
                    }
                    if (document.getData().get("email").toString().equals(email.getText().toString()) && !document.getData().get("email").toString().equals(CurrentUser.getEmail())) {
                        email.setError("Email already exists");
                        emailExists = true;
                    }
                }
                callback.onCallback(usernameExists, emailExists);
            }
        });
    }
}
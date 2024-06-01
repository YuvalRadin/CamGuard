package com.example.camguard.UI.User;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ModuleUser module;
    BottomNavigationView bottomNavigationView;
    TextView tvUsername, tvEmail, tvReports;
    Button btnLogout, btnEdit, btnMyMarkers;

    boolean passwordVisible = false;

    /**
     * Initializes the user interface elements and sets up event listeners.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize ModuleUser
        module = new ModuleUser(this);

        // Initialize UI elements
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvUsername = findViewById(R.id.nameTextView);
        tvEmail = findViewById(R.id.emailTextView);
        tvReports = findViewById(R.id.reportCountTextView);
        btnLogout = findViewById(R.id.LogOutButton);
        btnEdit = findViewById(R.id.btnEdit);
        btnMyMarkers = findViewById(R.id.btnMyMarkers);

        // Set click listeners for buttons
        btnLogout.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnMyMarkers.setOnClickListener(this);

        // Set user information
        tvUsername.setText(CurrentUser.getName());
        tvEmail.setText(CurrentUser.getEmail());
        tvReports.setText("Reports: " + module.getReports(CurrentUser.getName()));

        // Check if remember me is enabled, if not, forget user credentials
        if (!module.DoesRemember()) {
            module.DoNotRemember();
        }

        // Dynamically change menu for admin user
        if (CurrentUser.getEmail().equals("s16131@nhs.co.il")) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        }

        // Set up bottom navigation item selection listener
        bottomNavigationView.setSelectedItemId(R.id.menu_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_map) {
                Intent mapIntent = new Intent(UserActivity.this, FragmentMap.class);
                startActivity(mapIntent);
            } else if (item.getItemId() == R.id.menu_camera) {
                Intent cameraIntent = new Intent(UserActivity.this, CameraActivity.class);
                startActivity(cameraIntent);
            } else if (item.getItemId() == R.id.menu_admin) {
                Intent adminIntent = new Intent(UserActivity.this, AdminActivity.class);
                startActivity(adminIntent);
            }
            return true;
        });
    }

    /**
     * Handles onClick events for various buttons in the UserActivity.
     *
     * @param view The clicked view.
     */
    @Override
    public void onClick(View view) {
        if (view == btnLogout) {
            // Logout user and navigate to MainActivity
            module.DoNotRemember();
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (view == btnEdit) {
            // Open a dialog for user profile editing
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.update_user);
            // Initialize UI elements in the dialog
            EditText upname = dialog.findViewById(R.id.editTextName);
            EditText upmail = dialog.findViewById(R.id.editTextEmail);
            EditText uppass = dialog.findViewById(R.id.editTextPassword);
            Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
            Button btnClose = dialog.findViewById(R.id.btnCancel);
            // Set password visibility toggle
            uppass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    final int DRAWABLE_RIGHT = 2;
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if(motionEvent.getRawX() >= (uppass.getRight() - uppass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if(passwordVisible) {
                                uppass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                                uppass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                passwordVisible = false;
                            } else {
                                uppass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                                uppass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                passwordVisible = true;
                            }
                        }
                    }
                    return false;
                }
            });
            // Set current user's information in the edit fields
            upname.setText(CurrentUser.getName());
            upmail.setText(CurrentUser.getEmail());
            uppass.setText(module.getUserByName(CurrentUser.getName()).getString(2));
            // Set click listener for the update button
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle update button click event
                    String mailU,nameU,passU;
                    mailU = upmail.getText().toString();
                    nameU = upname.getText().toString();
                    passU = uppass.getText().toString();

                    if(!module.CheckUps(upname, upmail,uppass))
                    {
                        return;
                    }
                    module.checkUserAndEmailExistence(nameU, mailU, new FirebaseHelper.CredentialsCheck() {
                        @Override
                        public void onCredentialsCheckComplete(boolean doesUserExist, boolean doesEmailExist) {
                            if(!doesUserExist && !doesEmailExist)
                            {
                                module.updateSharedPreference(nameU, mailU);
                                module.updateUser(CurrentUser.getId(), nameU, passU, mailU);
                                module.updateFireStoreUser(tvUsername.getText().toString(), nameU, mailU, passU);
                                CurrentUser.setName(nameU);
                                CurrentUser.setEmail(mailU);
                                tvUsername.setText(nameU);
                                tvEmail.setText(mailU);
                                dialog.dismiss();
                            }
                            if(doesUserExist)
                            {
                                upname.setError("username already exists");
                            }
                            if(doesEmailExist)
                            {
                                upmail.setError("email already exists");
                            }
                        }
                    });
                }
            });
            // Set click listener for the close button
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            // Set dialog layout parameters and show the dialog
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.show();
        }


        if (view == btnMyMarkers) {
            // Show progress dialog while fetching markers
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Fetching all of your markers!");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Create dialog to display markers
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.my_markers);
            TableLayout myMarkers = dialog.findViewById(R.id.markersList);
            RelativeLayout myMarkersLayout = dialog.findViewById(R.id.myMarkersLayout);

            // Set click listener for the close button
            Button btnClose = dialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            // Fetch user's markers from the database
            module.getMyMarkers(new FirebaseHelper.markersGotten() {
                @Override
                public void onMarkersGotten(Task<QuerySnapshot> task, LinkedList<Uri> photos) {
                    boolean flag = false;

                    for (int i = 0; i < photos.size(); i++) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(i);
                        if (document.getData().get("Reporter").toString().equals(CurrentUser.getName())) {
                            flag = true;
                            // Create TableRow to display marker details
                            TableRow Row = new TableRow(getBaseContext());
                            Row.setLayoutParams(new TableRow.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    0.25f));

                            // Create ImageView to display marker image
                            ImageView imageView = new ImageView(getBaseContext());
                            imageView.setImageURI(photos.get(i)); // Replace with your image resource
                            Glide.with(getBaseContext()).load(photos.get(i)).preload();
                            Glide.with(getBaseContext()).load(photos.get(i)).into(imageView);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setAdjustViewBounds(true);
                            imageView.setLayoutParams(new TableRow.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    0.25f));

                            // Create TextView for marker description
                            TextView descriptionTextView = new TextView(getBaseContext());
                            descriptionTextView.setText(document.getData().get("Description").toString());
                            descriptionTextView.setTextSize(16);
                            descriptionTextView.setTextColor(getResources().getColor(android.R.color.black, null));
                            descriptionTextView.setLayoutParams(new TableRow.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    1f));

                            // Create TextView for marker ID
                            TextView idTextView = new TextView(getBaseContext());
                            idTextView.setText(document.getData().get("PictureKey").toString().substring(8));
                            idTextView.setTextSize(16);
                            idTextView.setTextColor(getResources().getColor(android.R.color.black, null));
                            idTextView.setLayoutParams(new TableRow.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    1f));

                            // Create Button to delete marker
                            Button actionButton = new Button(getBaseContext());
                            actionButton.setText("Delete");
                            actionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    module.deleteMarkerByID(document.getData().get("PictureKey").toString().substring(8));
                                    Row.removeAllViews();
                                }
                            });
                            actionButton.setLayoutParams(new TableRow.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    1f));

                            // Add views to TableRow
                            Row.addView(imageView);
                            Row.addView(descriptionTextView);
                            Row.addView(idTextView);
                            Row.addView(actionButton);

                            // Add TableRow to TableLayout
                            myMarkers.addView(Row);
                        }
                    }

                    // Display message if no markers found
                    if (!flag) {
                        myMarkersLayout.removeAllViews();
                        TextView tv = new TextView(getBaseContext());
                        tv.setText("It Seems like you don't have any reports made yet!");
                        tv.setTextSize(30);
                        tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        myMarkersLayout.addView(tv);
                        myMarkersLayout.addView(btnClose);
                    }

                    // Set dialog layout parameters and show the dialog
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    dialog.show();
                    progressDialog.dismiss();
                }
            });
        }
    }


}
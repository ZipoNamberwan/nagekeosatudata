package com.bps.nagekeosatudata.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;

public class ViewChatAdminActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 0;

    private CardView listCardView;
    private CardView infoCardView;
    private CardView emailCardView;
    private CardView whatsappCardView;
    private RecyclerView chatRecyclerView;
    private RecyclerView kontakRecyclerView;
    private RecyclerView whatsappRecyclerView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private UserModel userModel;

    boolean isFirstCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        chatRecyclerView = findViewById(R.id.recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setHasFixedSize(true);
        kontakRecyclerView = findViewById(R.id.email_recycler_view);
        kontakRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        kontakRecyclerView.setHasFixedSize(true);
        whatsappRecyclerView = findViewById(R.id.whatsapp_recycler_view);
        whatsappRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        whatsappRecyclerView.setHasFixedSize(true);

        progressBar = findViewById(R.id.progress_bar);
        listCardView = findViewById(R.id.card_view_chat);
        infoCardView = findViewById(R.id.information);
        emailCardView = findViewById(R.id.card_view_email);
        whatsappCardView = findViewById(R.id.card_view_whatsapp);

        firebaseAuth = FirebaseAuth.getInstance();

        isFirstCreate = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            if (isFirstCreate){
                Toast.makeText(this,
                        "Welcome " + FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName(),
                        Toast.LENGTH_LONG)
                        .show();

                isFirstCreate = false;
            }

            setupDialogList();
        }
    }

    private void setupDialogList() {

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        createUserModel();

        updateUserInformation(userModel.getId(), userModel.getUsername(),
                userModel.getUrlPhoto(), userModel.getLastSeen(), true, false);

        displayListAdmin();
        displayWhatsappAdmin();
        displayEmailAdmin();
    }

    private void createUserModel() {
        String idUser = firebaseAuth.getCurrentUser().getUid();
        String username = "";
        if (firebaseAuth.getCurrentUser().getDisplayName() != null){
            username = firebaseAuth.getCurrentUser().getDisplayName();
        }
        String urlPhoto = "";
        if (firebaseAuth.getCurrentUser().getPhotoUrl() != null){
            urlPhoto = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
        }
        long lastSeen = System.currentTimeMillis();

        userModel = new UserModel(idUser, username, urlPhoto, lastSeen);
    }

    private void updateUserInformation(String idUser, String username, String urlPhoto, long lastSeen, boolean isOnline, boolean isTyping) {

        String key = reference.child(idUser).getKey();

        Map<String, Object> childUpdates = ChatUtils.updateUserInformation(key, username, urlPhoto, lastSeen, isOnline, isTyping);

        reference.updateChildren(childUpdates);
    }

    private void displayListAdmin() {

        listCardView.setVisibility(View.GONE);
        infoCardView.setVisibility(View.GONE);
        emailCardView.setVisibility(View.GONE);
        whatsappCardView.setVisibility(View.GONE);

        DatabaseReference ref = reference.child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<UserModel> adminUsers = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (UserModel userModel : ChatUtils.getAdminList()){
                        UserModel temp = snapshot.child("information").getValue(UserModel.class);
                        if (snapshot.getKey().equals(userModel.getId())){
                            userModel.setOnline(temp.getIsOnline());
                            userModel.setTyping(temp.getIsTyping());
                            userModel.setLastSeen(temp.getLastSeen());
                            //userModel.setUsername("Admin Konsultasi Statistik Online");
                            userModel.setUsername(temp.getUsername());
                            userModel.setUrlPhoto(temp.getUrlPhoto());
                            adminUsers.add(userModel);
                        }
                    }
                }

                AdminUserAdapter adapter = new AdminUserAdapter(adminUsers, ViewChatAdminActivity.this, new RecyclerViewClickListener() {
                    @Override
                    public void onItemClick(Object object) {
                        Intent i = new Intent(ViewChatAdminActivity.this, ChatActivity.class);
                        i.putExtra(ChatActivity.ID_ADMIN_RECEIVER, ((UserModel) object).getId());
                        i.putExtra(ChatActivity.ID_USER_SENDER, userModel.getId());
                        i.putExtra(ChatActivity.USERNAME_RECEIVER, ((UserModel) object).getUsername());
                        i.putExtra(ChatActivity.URL_PHOTO_RECEIVER, ((UserModel) object).getUrlPhoto());
                        startActivity(i);
                    }
                });

                chatRecyclerView.setAdapter(adapter);

                listCardView.setVisibility(View.VISIBLE);
                infoCardView.setVisibility(View.VISIBLE);
                emailCardView.setVisibility(View.VISIBLE);
                whatsappCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayEmailAdmin(){
        ArrayList<Kontak> kontaks = new ArrayList<>();

        kontaks.add(new Kontak(R.drawable.ic_email_black_24dp, "Email (bps3513@bps.go.id)", "Kirim email ke kami tentang pertanyaan seputar data-data statistik", "Email"));
        kontaks.add(new Kontak(R.drawable.ic_call_black_24dp, "Telepon", "Hubungi kami melalui telepon di jam kerja", "Telepon"));

        KontakAdapter adapter = new KontakAdapter(kontaks, ViewChatAdminActivity.this, new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                if (((Kontak) object).getJenis().equals("Email")){
                    Intent i = new Intent(ViewChatAdminActivity.this, SendEmailActivity.class);
                    startActivity(i);
                }else if (((Kontak) object).getJenis().equals("Telepon")){
                    callAdmin();
                }
            }
        });

        kontakRecyclerView.setAdapter(adapter);
    }


    private void displayWhatsappAdmin() {
        ArrayList<Kontak> kontaks = new ArrayList<>();

        kontaks.add(new Kontak(R.drawable.ic_whatsapp, "Rony Hadiyanto", "Petugas Pelayanan Statistik Terpadu", "Whatsapp1"));
        kontaks.add(new Kontak(R.drawable.ic_whatsapp, "Dicky Dita Firmansyah", "Petugas Pelayanan Statistik Terpadu", "Whatsapp2"));

        KontakAdapter adapter = new KontakAdapter(kontaks, ViewChatAdminActivity.this, new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                String phoneNumberWithCountryCode = "";
                if (((Kontak)object).getJenis().equals("Whatsapp1")){
                    phoneNumberWithCountryCode = "+6281331773627";
                } else if (((Kontak)object).getJenis().equals("Whatsapp2")){
                    phoneNumberWithCountryCode = "+6285346200543";
                }
                String message = "";

                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(
                                        String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                )
                        )
                );
            }
        });

        whatsappRecyclerView.setAdapter(adapter);
    }

    private void callAdmin() {
        String no = "(0335) 422117";
        String uri = "tel:" + no ;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser() != null) {
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    true, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth.getCurrentUser() != null) {
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    false, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                setupDialogList();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                onPause();
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            if(firebaseAuth.getCurrentUser() != null) {
                updateUserInformation(userModel.getId(), userModel.getUsername(),
                        userModel.getUrlPhoto(), System.currentTimeMillis(),
                        false, false);
            }
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ViewChatAdminActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        } /*else if (item.getItemId() == R.id.menu_edit_profile){
            Intent intent = new Intent(this, SettingsProfileActivity.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(item);
    }
}

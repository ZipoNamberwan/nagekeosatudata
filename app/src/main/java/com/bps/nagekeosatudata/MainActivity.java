package com.bps.nagekeosatudata;


import android.Manifest;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bps.nagekeosatudata.about.AboutActivity;
import com.bps.nagekeosatudata.infografis.InfografisFragment;
import com.bps.nagekeosatudata.podes.PodesFragment;
import com.bps.nagekeosatudata.sectoral.DataSektoralFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.bps.nagekeosatudata.berita.BeritaFragment;
import com.bps.nagekeosatudata.brs.BrsFragment;
import com.bps.nagekeosatudata.chat.ChatActivity;
import com.bps.nagekeosatudata.chat.ViewChatAdminActivity;
import com.bps.nagekeosatudata.indikator.IndikatorFragment;
import com.bps.nagekeosatudata.publikasi.PublikasiFragment;
import com.bps.nagekeosatudata.tabelstatis.TabelFragment;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    public static final String SEARCH_KEYWORD = "search keyword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.white));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_bps_launcher);
//            getSupportActionBar().setSubtitle(R.string.subtitle);
            getSupportActionBar().setTitle(R.string.toolbar_title);
        }

        IndikatorFragment indikatorFragment = new IndikatorFragment();
//        BrsFragment brsFragment = new BrsFragment();
        PublikasiFragment publikasiFragment = new PublikasiFragment();
        PodesFragment podesFragment = new PodesFragment();
        DataSektoralFragment dataSektoralFragment = new DataSektoralFragment();
//        TabelFragment tabelFragment = new TabelFragment();
        BeritaFragment beritaFragment = new BeritaFragment();
        InfografisFragment infografisFragment = new InfografisFragment();

        com.bps.nagekeosatudata.ViewPagerAdapter viewPagerAdapter = new com.bps.nagekeosatudata.ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(indikatorFragment, "Beranda");
        viewPagerAdapter.addFragment(publikasiFragment, "Publikasi");
        viewPagerAdapter.addFragment(podesFragment, "Potensi Desa");
        viewPagerAdapter.addFragment(dataSektoralFragment, "Data Sektoral");
//        viewPagerAdapter.addFragment(tabelFragment, "Tabel Statis");
//        viewPagerAdapter.addFragment(brsFragment, "BRS");
        viewPagerAdapter.addFragment(infografisFragment, "Infografis");
        viewPagerAdapter.addFragment(beritaFragment, "Berita");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        checkPermission();

        com.bps.nagekeosatudata.AppUtils.createNotificationChannel(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), ViewChatAdminActivity.class);
                startActivity(i);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("sented") != null) {
                String sender = bundle.getString("sented");
                String receiver = bundle.getString("user");
                String username = bundle.getString("username");
                String photo = bundle.getString("photo");

                Intent i = new Intent(this, ChatActivity.class);
                i.putExtra(ChatActivity.ID_ADMIN_RECEIVER, receiver);
                i.putExtra(ChatActivity.ID_USER_SENDER, sender);
                i.putExtra(ChatActivity.USERNAME_RECEIVER, username);
                i.putExtra(ChatActivity.URL_PHOTO_RECEIVER, photo);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(SEARCH_SERVICE);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchManager != null) {
                SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
                searchView.setSearchableInfo(searchableInfo);
                searchView.setOnSearchClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSearchRequested();
                    }
                });
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }
//        else if (id == R.id.action_about){
//            Intent i = new Intent(this, AboutActivity.class);
//            startActivity(i);
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested(@Nullable SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }
}
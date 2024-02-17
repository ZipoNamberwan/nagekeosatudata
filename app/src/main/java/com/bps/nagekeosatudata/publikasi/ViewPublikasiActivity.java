package com.bps.nagekeosatudata.publikasi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bps.nagekeosatudata.AppUtils;
import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.VolleySingleton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewPublikasiActivity extends AppCompatActivity {

    public static final String ID_PUBLIKASI = "id publikasi";

    private TextView judulTv;
    private TextView tanggalTv;
    private TextView issnTv;
    private ImageView coverImage;
    private TextView abstrakTv;
    private TextView nomerKatalog;
    private TextView nomerPublikasi;
    private View issnView;
    private View noKatalogView;
    private View noPubView;
    private FloatingActionButton fab;

    private ShimmerFrameLayout shimmerFrameLayout;
    private View failureView;

    private String idPublikasi;
    private String tanggal;
    private String judul;
    private String urlPdf;
    private boolean isTokenExpired;

    private View main1;
    private View main2;

    private JSONObject jsonObject;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_publikasi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        idPublikasi = getIntent().getStringExtra(ID_PUBLIKASI);

        setupInitView();

        getData(idPublikasi);
    }

    private void getData(String idPublikasi) {

        setViewVisibility(false, true, false);

        String s = getResources()
                .getString(R.string.web_service_path_detail_publication) + getResources().getString(R.string.api_key)
                + "&id=" + idPublikasi;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, s , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonObject = response.getJSONObject("data");
                    judul = jsonObject.getString("title");
                    tanggal = jsonObject.getString("rl_date");
                    judulTv.setText(jsonObject.getString("title"));
                    tanggalTv.setText(AppUtils.getDate(tanggal, false));

                    Picasso.get()
                            .load(jsonObject.getString("cover"))
                            .error(new IconicsDrawable(getBaseContext()).color(ContextCompat.getColor(getBaseContext(), R.color.md_grey_300)).icon(GoogleMaterial.Icon.gmd_broken_image))
                            .placeholder(new IconicsDrawable(getBaseContext()).color(ContextCompat.getColor(getBaseContext(), R.color.md_grey_300)).icon(GoogleMaterial.Icon.gmd_image))
                            .fit()
                            .into(coverImage);

                    abstrakTv.setText(Html.fromHtml(jsonObject.getString("abstract")));

                    if (!jsonObject.getString("issn").equals("")){
                        issnTv.setText(jsonObject.getString("issn"));
                    }else {
                        issnView.setVisibility(View.GONE);
                    }
                    if (!jsonObject.getString("kat_no").equals("")){
                        nomerKatalog.setText(jsonObject.getString("kat_no"));
                    }else {
                        noKatalogView.setVisibility(View.GONE);
                    }
                    if (!jsonObject.getString("pub_no").equals("")){
                        nomerPublikasi.setText(jsonObject.getString("pub_no"));
                    }else {
                        noPubView.setVisibility(View.GONE);
                    }

                    urlPdf = jsonObject.getString("pdf");

                    setViewVisibility(true, false, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setViewVisibility(false, false, true);
            }
        });
        queue.add(request);
    }

    private void setupInitView() {
        judulTv = findViewById(R.id.judul_publikasi);
        tanggalTv = findViewById(R.id.tanggal_publikasi);
        issnTv = findViewById(R.id.issn_publikasi);
        abstrakTv = findViewById(R.id.abstrak);
        coverImage = findViewById(R.id.cover_publikasi);
        nomerKatalog = findViewById(R.id.nomer_katalog);
        nomerPublikasi = findViewById(R.id.nomer_publikasi);
        issnView = findViewById(R.id.issn_view);
        noKatalogView = findViewById(R.id.nomer_katalog_view);
        noPubView = findViewById(R.id.nomer_publikasi_view);

        main1 = findViewById(R.id.main1);
        main2 = findViewById(R.id.main2);

        fab = findViewById(R.id.fab);

        final ViewPublikasiActivity activity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*final Snackbar snackbar = Snackbar.make(view, R.string.checking_user_auth_string, Snackbar.LENGTH_INDEFINITE);

                snackbar.show();*/

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                /*Intent i = new Intent(activity, AuthActivity.class);
                                String token = AppUtils.getToken(activity);
                                if (token==null){
                                    startActivity(i);
                                }else {
                                    String s = urlPdf + token;
                                    String namaFile = judul.replaceAll("\\W+", "");
                                    AppUtils.downloadFile(activity, s, judul, namaFile + ".pdf");
                                }*/
                                String s = urlPdf.replace("&tokenuser=", "");
                                String namaFile = judul.replaceAll("\\W+", "");
                                AppUtils.downloadFile(activity, s, judul, namaFile + ".pdf");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                        .setTitle("Download")
                        .setMessage("Download Publikasi?")
                        .setPositiveButton("Ya", onClickListener)
                        .setNegativeButton("Tidak", onClickListener)
                        .show();
            }
        });

        shimmerFrameLayout = findViewById(R.id.shimmer);
        failureView = findViewById(R.id.view_failure);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(idPublikasi);
            }
        });

    }

    private void setViewVisibility(boolean isMainVisible, boolean isShimmerVisible, boolean isFailureVisible) {

        if (isMainVisible){
            main1.setVisibility(View.VISIBLE);
            main2.setVisibility(View.VISIBLE);
            fab.show();
        }else {
            main1.setVisibility(View.GONE);
            main2.setVisibility(View.GONE);
            fab.hide();
        }

        if (isShimmerVisible){
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }

        if (isFailureVisible){
            failureView.setVisibility(View.VISIBLE);
        }else {
            failureView.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            if (judul != null){
                String urlShare = AppUtils.getUrlShare(getString(R.string.web_share_publication), tanggal, idPublikasi, judul);
                AppUtils.share(this, judul, urlShare);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

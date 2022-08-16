package com.bps.nagekeosatudata.brs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import com.bps.nagekeosatudata.AppUtils;
import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.VolleySingleton;

public class ViewBrsActivity extends AppCompatActivity {

    public static final String ID_BRS = "id brs";

    private String idBrs;
    private String urlBrs;
    private String judul;
    private String tanggal;

    private TextView judulTv;
    private TextView releaseDateTv;
    private TextView sizeTv;
    private WebView abstrakWebView;

    private View mainView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View failureView;

    private FloatingActionButton fab;

    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_brs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idBrs = getIntent().getStringExtra(ID_BRS);

        setUpInitView();

        getData(idBrs);

    }

    private void setUpDetailView() throws JSONException {
        judul = jsonObject.getString("title");
        tanggal = jsonObject.getString("rl_date");
        String tanggalFormatted = AppUtils.getDate(tanggal, false);
        String size = "Size: " + jsonObject.getString("size");
        String abstrak = Html.fromHtml(jsonObject.getString("abstract")).toString();
        urlBrs = jsonObject.getString("pdf");

        judulTv.setText(judul);
        releaseDateTv.setText(tanggalFormatted);
        sizeTv.setText(size);
        abstrakWebView.loadData(abstrak, "text/html; charset=UTF-8", null);
    }

    private void getData(String idBrs) {
        setViewVisibility(false, true, false);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String s = getResources()
                .getString(R.string.web_service_path_detail_brs) + getResources().getString(R.string.api_key)
                + "&id=" + idBrs;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, s , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonObject = response.getJSONObject("data");
                    setUpDetailView();
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

    private void setUpInitView(){
        mainView = findViewById(R.id.main);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        failureView = findViewById(R.id.view_failure);

        judulTv = findViewById(R.id.judul);
        releaseDateTv = findViewById(R.id.last_periode);
        sizeTv = findViewById(R.id.size);
        abstrakWebView = findViewById(R.id.abstrak);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                /*Intent i = new Intent(ViewBrsActivity.this, AuthActivity.class);
                                String token = AppUtils.getToken(ViewBrsActivity.this);

                                if (token==null){
                                    startActivity(i);
                                }else {
                                    String s = urlBrs + token;
                                    String namaFile = judul.replaceAll("\\W+", "");
                                    AppUtils.downloadFile(ViewBrsActivity.this, s, judul, namaFile + ".pdf");
                                }*/
                                String s = urlBrs.replace("&tokenuser=", "");
                                String namaFile = judul.replaceAll("\\W+", "");
                                AppUtils.downloadFile(ViewBrsActivity.this, s, judul, namaFile + ".pdf");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewBrsActivity.this);
                builder
                        .setTitle("Download")
                        .setMessage("Download BRS?")
                        .setPositiveButton("Ya", onClickListener)
                        .setNegativeButton("Tidak", onClickListener)
                        .show();
            }
        });

        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(idBrs);
            }
        });
    }

    private void setViewVisibility(boolean isMainVisible, boolean isShimmerVisible, boolean isFailureVisible) {

        if (isMainVisible){
            mainView.setVisibility(View.VISIBLE);
            fab.show();
        }else {
            mainView.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            String urlShare = AppUtils.getUrlShare(getString(R.string.web_share_brs), tanggal, idBrs, judul);
            AppUtils.share(this, judul, urlShare);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

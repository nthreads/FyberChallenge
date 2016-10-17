package com.fyber.challenge.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fyber.challenge.FyberChallenge;
import com.fyber.challenge.R;
import com.fyber.challenge.adapter.OffersAdapter;
import com.fyber.challenge.entity.Offer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nauman Zubair on 15/10/2016.
 */

public class OffersActivity extends AppCompatActivity {

    RecyclerView recyclerView;


    OffersAdapter adapter;
    ArrayList<Offer> offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        offers = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String offersString = extras.getString(FyberChallenge.EXTRAS_OFFERS);
            try {

                JSONArray jArrayOffers = new JSONArray(offersString);

                for (int i = 0; i < jArrayOffers.length(); i++) {
                    offers.add(new Offer(jArrayOffers.getJSONObject(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        adapter = new OffersAdapter(offers);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

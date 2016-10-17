package com.fyber.challenge.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nauman Zubair on 15/10/2016.
 */

public class Offer {
    private String title;
    private String teaser;
    private String thumbnailUrl;
    private int payout;

    public Offer() {
        title = "";
        teaser = "";
        thumbnailUrl = "";
        payout = 0;
    }

    public Offer(JSONObject jsonObject) {
        this();

        try {
            if(jsonObject.has("title")) setTitle(jsonObject.getString("title"));
            if(jsonObject.has("teaser")) setTeaser(jsonObject.getString("teaser"));
            if(jsonObject.has("payout")) setPayout(jsonObject.getInt("payout"));

            if(jsonObject.has("thumbnail")) {
                JSONObject jObjThumbnail = jsonObject.getJSONObject("thumbnail");
                if(jObjThumbnail.has("hires"))
                    setThumbnailUrl(jObjThumbnail.getString("hires"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getPayout() {
        return payout;
    }

    public void setPayout(int payout) {
        this.payout = payout;
    }
}

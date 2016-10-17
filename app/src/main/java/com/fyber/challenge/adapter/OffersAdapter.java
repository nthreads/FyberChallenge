package com.fyber.challenge.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fyber.challenge.R;
import com.fyber.challenge.business.CustomImageLoadingListener;
import com.fyber.challenge.business.CustomImageLoadingProgressListener;
import com.fyber.challenge.entity.Offer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
/**
 * Created by Nauman Zubair on 15/10/2016.
 */
public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {

    DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.stub_image)
            .showImageForEmptyUri(R.drawable.stub_image)
            .showImageOnFail(R.drawable.stub_image)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private List<Offer> offersList;

    public OffersAdapter(List<Offer> offersList) {
        this.offersList = offersList;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_offer, viewGroup, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int i) {
        Offer offer = offersList.get(i);

        holder.tvTitle.setText(offer.getTitle());
        holder.tvTeaser.setText(offer.getTeaser());
        holder.tvPayout.setText("" + offer.getPayout());

        ImageLoader.getInstance().displayImage(offer.getThumbnailUrl(), holder.ivThumbnail, displayImageOptions,
                new CustomImageLoadingListener(holder.progress),
                new CustomImageLoadingProgressListener(holder.progress));
    }

    @Override
    public int getItemCount() {
        return this.offersList.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvTeaser;
        TextView tvPayout;

        ImageView ivThumbnail;
        ProgressBar progress;

        OfferViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTeaser = (TextView) itemView.findViewById(R.id.tvTeaser);
            tvPayout = (TextView) itemView.findViewById(R.id.tvPayout);

            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}

/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.model.News;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GlideApp;
import me.trashout.utils.ViewUtils;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private static final String TAG = NewsListAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final OnNewsItemClickListener onNewsItemClickListener;
    private final Context context;
    private List<News> newsList;

    private LayoutInflater inflater;

    private View headerView;

    public NewsListAdapter(Context context, List<News> newsList, OnNewsItemClickListener onNewsItemClickListener) {
        this.context = context;
        this.newsList = newsList;
        this.onNewsItemClickListener = onNewsItemClickListener;
    }

    /**
     * Get news list
     *
     * @return
     */
    public List<News> getNewsList() {
        return newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_news, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        final News news = newsList.get(position);
        holder.bindView(news);
    }

    abstract class NewsViewHolder extends RecyclerView.ViewHolder {

        NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public abstract void bindView(News news);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    /**
     * View holder representative trash list item
     */
    public class ListViewHolder extends NewsViewHolder {

        @BindView(R.id.main_layout)
        RelativeLayout mainLayout;
        @BindView(R.id.news_title)
        TextView newsTitle;
        @BindView(R.id.news_perex)
        TextView newsPerex;
        @BindView(R.id.news_image)
        ImageView newsImage;
        @BindView(R.id.news_date)
        TextView newsDate;

        ListViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(News news) {
            newsTitle.setText(news.getTitle().replaceAll("<[^>]*>", ""));
            if(!TextUtils.isEmpty(news.getBody())) {
                newsPerex.setText(news.getBody().replaceAll("<[^>]*>", ""));
            }
            newsDate.setText(DateTimeUtils.DATE_FORMAT.format(news.getCreated()));
            if (news.getImages() != null && !news.getImages().isEmpty() && ViewUtils.checkImageStorage(news.getImages().get(0))) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(news.getImages().get(0).getSmallestImage());
                GlideApp.with(mainLayout.getContext())
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(newsImage);
            } else {
                newsImage.setImageResource(R.drawable.ic_image_placeholder_rectangle);
            }

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNewsItemClickListener.onNewsClick(newsList.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface OnNewsItemClickListener {
        void onNewsClick(News news);
    }
}
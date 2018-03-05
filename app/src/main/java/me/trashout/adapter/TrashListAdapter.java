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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import me.trashout.R;
import me.trashout.model.Constants;
import me.trashout.model.Trash;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.PositionUtils;

/**
 * TrashOutNGO
 *
 * @package me.trashout.adapter
 * @since 21.10.2016
 */
public class TrashListAdapter extends RecyclerView.Adapter<TrashListAdapter.TrashViewHolder> {
    private static final String TAG = TrashListAdapter.class.getSimpleName();

    private final OnDumpItemClickListener onDumpItemClickListener;
    private final Context context;
    private List<Trash> trashList;
    private LatLng lastPosition;

    private LayoutInflater inflater;


    public TrashListAdapter(Context context, List<Trash> trashList, OnDumpItemClickListener onDumpItemClickListener, LatLng lastPosition) {
        this.context = context;
        this.trashList = trashList;
        this.onDumpItemClickListener = onDumpItemClickListener;
        this.lastPosition = lastPosition;
    }

    @Override
    public int getItemCount() {
        return trashList.size();
    }

    /**
     * Get trash list
     *
     * @return
     */
    public List<Trash> getTrashList() {
        return trashList;
    }

    @Override
    public void onBindViewHolder(TrashViewHolder trashViewHolder, int position) {
        final Trash trash = trashList.get(position);

        trashViewHolder.place.setText(String.format(context.getString(R.string.specific_distance_away_formatter), lastPosition != null ? PositionUtils.getRoundedFormattedDistance(context, (int) PositionUtils.computeDistance(lastPosition, trash.getPosition())) : "?", context.getString(R.string.global_distanceAttribute_away)));

        if (trash.getLastChangeDate() != null)
            trashViewHolder.information.setText(DateTimeUtils.getRoundedTimeAgo(context, trash.getLastChangeDate()));

        trashViewHolder.types.setText(TextUtils.join(", ", Constants.TrashType.getTrashTypeNameList(context, trash.getTypes())));


        // TODO update needed state?
        if (trash.isUpdateNeeded()) {
            trashViewHolder.statusIcon.setImageResource(R.drawable.ic_trash_status_unknown);
        } else if (Constants.TrashStatus.CLEANED.equals(trash.getStatus())) {
            trashViewHolder.statusIcon.setImageResource(R.drawable.ic_trash_activity_cleaned);
        } else if (Constants.TrashStatus.STILL_HERE.equals(trash.getStatus()) && (trash.getUpdateHistory() == null || trash.getUpdateHistory().isEmpty())) {
            trashViewHolder.statusIcon.setImageResource(R.drawable.ic_trash_activity_reported);
        } else {
            trashViewHolder.statusIcon.setImageResource(R.drawable.ic_trash_activity_updated);
        }

        if (trash.getImages() != null && !trash.getImages().isEmpty() && !TextUtils.isEmpty(trash.getImages().get(0).getSmallestImage()) && trash.getImages().get(0).getSmallestImage().contains("trashoutngo")) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(trash.getImages().get(0).getSmallestImage());
            Glide.with(context).using(new FirebaseImageLoader()).load(mImageRef).centerCrop().thumbnail(0.2f).placeholder(R.drawable.ic_image_placeholder_square).into(trashViewHolder.image);
        } else {
            trashViewHolder.image.setImageResource(R.drawable.ic_image_placeholder_square);
        }

    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_list_trash, viewGroup, false);

        return new TrashViewHolder(itemView);
    }

    /**
     * setting last position
     *
     * @param lastPosition
     */
    public void setLastPosition(LatLng lastPosition) {
        this.lastPosition = lastPosition;
        notifyDataSetChanged();
    }

    /**
     * View holder representative trash list item
     */
    public class TrashViewHolder extends RecyclerView.ViewHolder {

        ImageView statusIcon;
        TextView place;
        TextView types;
        TextView information;
        ImageView image;

        public TrashViewHolder(View v) {
            super(v);
            statusIcon = (ImageView) v.findViewById(R.id.trash_state_icon);
            place = (TextView) v.findViewById(R.id.trash_place);
            types = (TextView) v.findViewById(R.id.trash_types);
            information = (TextView) v.findViewById(R.id.trash_date);
            image = (ImageView) v.findViewById(R.id.trash_image);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDumpItemClickListener.onDumpClick(trashList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnDumpItemClickListener {
        public void onDumpClick(Trash Trash);
    }
}
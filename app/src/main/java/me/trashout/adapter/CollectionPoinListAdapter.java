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
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import me.trashout.R;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Constants;
import me.trashout.utils.PositionUtils;

public class CollectionPoinListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CollectionPoinListAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final OnCollectionPointItemClickListener onCollectionPointItemClickListener;
    private final Context context;
    private List<CollectionPoint> collectionPointList;
    private LatLng lastPosition;

    private LayoutInflater inflater;

    private View headerView;

    public CollectionPoinListAdapter(Context context, List<CollectionPoint> collectionPointList, OnCollectionPointItemClickListener onCollectionPointItemClickListener, LatLng lastPosition) {
        this.context = context;
        this.collectionPointList = collectionPointList;
        this.onCollectionPointItemClickListener = onCollectionPointItemClickListener;
        this.lastPosition = lastPosition;
    }

    /**
     * Get trash list
     *
     * @return
     */
    public List<CollectionPoint> getCollectionPointList() {
        return collectionPointList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CollectionPoint collectionPoint = collectionPointList.get(position);

        if (holder instanceof CollectionPointHeaderViewHolder) {
            CollectionPointHeaderViewHolder collectionPointHeaderViewHolder = (CollectionPointHeaderViewHolder) holder;
            collectionPointHeaderViewHolder.name.setText(collectionPoint.getSize().equals(Constants.CollectionPointSize.DUSTBIN) ? context.getString(Constants.CollectionPointSize.DUSTBIN.getStringResId()) : collectionPoint.getName());
            collectionPointHeaderViewHolder.types.setText(Html.fromHtml(context.getString(R.string.recyclable_gray, context.getString(R.string.collectionPoint_detail_mobile_recycable)) + TextUtils.join(", ", Constants.CollectionPointType.getCollectionPointTypeNameList(context, collectionPoint.getTypes()))));
            collectionPointHeaderViewHolder.distance.setText(String.format(context.getString(R.string.distance_away_formatter), (lastPosition != null && collectionPoint.getPosition() != null) ? PositionUtils.getRoundedFormattedDistance(context, (int) PositionUtils.computeDistance(lastPosition, collectionPoint.getPosition())) : "?", context.getString(R.string.global_distanceAttribute_away)));
            collectionPointHeaderViewHolder.position.setText(collectionPoint.getPosition() != null ? PositionUtils.getFormattedLocation(context, collectionPoint.getPosition().latitude, collectionPoint.getPosition().longitude) : "?");
            if (getItemCount() > 1)
                collectionPointHeaderViewHolder.otherJunkyardsHeader.setVisibility(View.VISIBLE);
            else
                collectionPointHeaderViewHolder.otherJunkyardsHeader.setVisibility(View.GONE);
        } else {
            CollectionPointViewHolder collectionPointViewHolder = (CollectionPointViewHolder) holder;
            collectionPointViewHolder.name.setText(collectionPoint.getSize().equals(Constants.CollectionPointSize.DUSTBIN) ? context.getString(Constants.CollectionPointSize.DUSTBIN.getStringResId()) : collectionPoint.getName());
            collectionPointViewHolder.distance.setText(String.format(context.getString(R.string.distance_away_formatter), (lastPosition != null && collectionPoint.getPosition() != null) ? PositionUtils.getRoundedFormattedDistance(context, (int) PositionUtils.computeDistance(lastPosition, collectionPoint.getPosition())) : "?", context.getString(R.string.global_distanceAttribute_away)));
            collectionPointViewHolder.types.setText(TextUtils.join(", ", Constants.CollectionPointType.getCollectionPointTypeNameList(context, collectionPoint.getTypes())));
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_HEADER) {
            inflater = LayoutInflater.from(viewGroup.getContext());
            View v = inflater.inflate(R.layout.layout_collection_point_list_header, viewGroup, false);
            return new CollectionPointHeaderViewHolder(v);
        } else {
            inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.item_list_collection_points, viewGroup, false);
            return new CollectionPointViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return collectionPointList.size();
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
    public class CollectionPointViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView types;
        TextView distance;

        public CollectionPointViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.collection_point_name);
            types = v.findViewById(R.id.collection_point_types);
            distance = v.findViewById(R.id.collection_point_distance);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCollectionPointItemClickListener.onCollectionPointClick(collectionPointList.get(getAdapterPosition()));
                }
            });
        }
    }

    public class CollectionPointHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView distance;
        private final TextView position;
        private final TextView types;
        private final Button readMore;
        private final View otherJunkyardsHeader;

        public CollectionPointHeaderViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.collection_point_list_header_name);
            distance = v.findViewById(R.id.collection_point_list_header_distance);
            position = v.findViewById(R.id.collection_point_list_header_position);
            types = v.findViewById(R.id.collection_point_list_header_type);
            readMore = v.findViewById(R.id.collection_point_list_header_more_btn);
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCollectionPointItemClickListener.onCollectionPointClick(collectionPointList.get(0));
                }
            });
            otherJunkyardsHeader = v.findViewById(R.id.collection_point_list_header_other_junkyards);
        }
    }

    public interface OnCollectionPointItemClickListener {
        void onCollectionPointClick (CollectionPoint collectionPoint);
    }
}
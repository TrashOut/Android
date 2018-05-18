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
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

import me.trashout.R;
import me.trashout.model.Constants;
import me.trashout.model.UserActivity;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PositionUtils;

public class UserActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = UserActivityListAdapter.class.getSimpleName();

    private final Context context;
    private final IClickOnActivityItem iClickOnActivityItem;
    private List<UserActivity> userActivityList;
    private LatLng lastPosition;

    private LayoutInflater inflater;

    public interface IClickOnActivityItem{
        void onClick(int itemId);
    }

    public UserActivityListAdapter(Context context, List<UserActivity> userActivityList, LatLng lastPosition, IClickOnActivityItem iClickOnActivityItem) {
        this.context = context;
        this.userActivityList = userActivityList;
        this.lastPosition = lastPosition;
        this.iClickOnActivityItem = iClickOnActivityItem;
    }

    /**
     * Get user activity list
     *
     * @return
     */
    public List<UserActivity> getUserActivityList() {
        return userActivityList;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final UserActivity userActivity = userActivityList.get(position);
        final UserActivityViewHolder userActivityViewHolder = (UserActivityViewHolder) holder;

        userActivityViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iClickOnActivityItem.onClick(Integer.parseInt(userActivity.getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        userActivityViewHolder.userActivityName.setText(userActivity.getUserInfo().getFullName(context));
        userActivityViewHolder.userActivityDate.setText(DateTimeUtils.DATE_FORMAT.format(userActivity.getCreated()));
        userActivityViewHolder.userActivityPosition.setText(userActivity.getPosition() != null ? PositionUtils.getFormattedLocation(context, userActivity.getPosition().latitude, userActivity.getPosition().longitude) : "?");

        if (userActivity.getActivity().getImages() != null && !userActivity.getActivity().getImages().isEmpty() && !TextUtils.isEmpty(userActivity.getActivity().getImages().get(0).getSmallestImage()) && userActivity.getActivity().getImages().get(0).getSmallestImage().contains("trashoutngo")) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(userActivity.getActivity().getImages().get(0).getSmallestImage());
            GlideApp.with(context).load(mImageRef).centerCrop().thumbnail(0.2f).placeholder(R.drawable.ic_image_placeholder_square).into(userActivityViewHolder.userActivityImage);
        } else {
            userActivityViewHolder.userActivityImage.setImageResource(R.drawable.ic_image_placeholder_square);
        }

        if (Constants.ActivityAction.CREATE.getName().equals(userActivity.getAction())) {
            userActivityViewHolder.userActivityType.setImageResource(Constants.ActivityAction.CREATE.getIconUpdateActionResId());
        } else {
            userActivityViewHolder.userActivityType.setImageResource(userActivity.getActivity().getStatus().getIconUpdatehistoryResId());
        }


        if (Constants.ActivityAction.CREATE.getName().equals(userActivity.getAction())) {
            userActivityViewHolder.userActivityName.setText(context.getString(R.string.reported_activity_placeholder,
                    context.getString(R.string.home_recentActivity_you),
                    context.getString(Constants.ActivityAction.CREATE.getStringUpdateActionResId()).toLowerCase(),
                    context.getString(R.string.user_activity_title_thisDump),
                    "",
                    "")
            );
        } else {
            userActivityViewHolder.userActivityName.setText(context.getString(R.string.reported_activity_placeholder,
                    context.getString(R.string.home_recentActivity_you),
                    context.getString(userActivity.getActivity().getStatus().getStringUpdateHistoryResId()).toLowerCase(),
                    context.getString(R.string.user_activity_title_thisDump),
                    context.getString(R.string.home_recentActivity_as),
                    context.getString(userActivity.getActivity().getStatus().getStringResId()).toLowerCase())
            );
        }

        userActivityViewHolder.userActivityDate.setText(DateTimeUtils.DATE_FORMAT.format(userActivity.getCreated()));

        if (lastPosition != null) {
            userActivityViewHolder.userActivityDistance.setText(String.format(context.getString(R.string.distance_away_formatter), (lastPosition != null && userActivity.getPosition() != null) ? PositionUtils.getRoundedFormattedDistance(context, (int) PositionUtils.computeDistance(lastPosition, userActivity.getPosition())) : "?", context.getString(R.string.global_distanceAttribute_away)));
        } else {
            userActivityViewHolder.userActivityDistance.setVisibility(View.GONE);
        }

        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        new GeocoderTask(geocoder, userActivity.getGps().getLat(), userActivity.getGps().getLng(), new GeocoderTask.Callback() {
            @Override
            public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                if (!TextUtils.isEmpty(geocoderResult.getFormattedShortAddress())) {
                    userActivityViewHolder.userActivityPosition.setText(geocoderResult.getFormattedShortAddress());
                    userActivityViewHolder.userActivityPosition.setVisibility(View.VISIBLE);
                } else {
                    userActivityViewHolder.userActivityPosition.setVisibility(View.GONE);
                }
            }
        }).execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_list_user_activity, viewGroup, false);
        return new UserActivityViewHolder(itemView);

    }

    @Override
    public int getItemCount() {
        return userActivityList.size();
    }

    /**
     * View holder representative trash list item
     */
    public class UserActivityViewHolder extends RecyclerView.ViewHolder {

        ImageView userActivityImage;
        ImageView userActivityType;
        TextView userActivityName;
        TextView userActivityDate;
        TextView userActivityDistance;
        TextView userActivityPosition;

        public UserActivityViewHolder(View v) {
            super(v);
            userActivityImage = v.findViewById(R.id.user_activity_image);
            userActivityType = v.findViewById(R.id.user_activity_type);
            userActivityName = v.findViewById(R.id.user_activity_name);
            userActivityDate = v.findViewById(R.id.user_activity_date);
            userActivityDistance = v.findViewById(R.id.user_activity_distance);
            userActivityPosition = v.findViewById(R.id.user_activity_position);
        }
    }
}
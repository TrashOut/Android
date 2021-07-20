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

package me.trashout.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetTrashDetailResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Constants;
import me.trashout.model.Event;
import me.trashout.model.Image;
import me.trashout.model.Trash;
import me.trashout.model.UpdateHistory;
import me.trashout.model.User;
import me.trashout.model.presentation.FullScreenImage;
import me.trashout.service.CreateTrashNewSpamService;
import me.trashout.service.GetTrashDetailService;
import me.trashout.service.JoinUserToEventService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.SelectableImageButton;
import me.trashout.ui.SquareImageView;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import me.trashout.utils.ViewUtils;
import pub.devrel.easypermissions.EasyPermissions;

public class TrashDetailFragment extends BaseFragment implements BaseService.UpdateServiceListener, ITrashFragment, EventCreateFragment.OnCreateEventListener {

    private static final String BUNDLE_TRASH_ID = "BUNDLE_TRASH_ID";

    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int GET_TRASH_DETAIL_REQUEST_ID = 301;
    private static final int TRASH_CREATE_SPAM_REQUEST_ID = 302;
    private static final int TRASH_JOIN_TO_EVENT_REQUEST_ID = 303;
    private static final String EVENT_ID_TAG = "Event_id:";

    @BindView(R.id.trash_detail_container)
    View trashDetailViewContainer;
    @BindView(R.id.trash_detail_cleaned_btn)
    Button trashDetailCleanedBtn;
    @BindView(R.id.trash_detail_image)
    ImageView trashDetailImage;
    @BindView(R.id.trash_detail_state_icon)
    ImageView trashDetailStateIcon;
    @BindView(R.id.trash_detail_state_name)
    TextView trashDetailStateName;
    @BindView(R.id.trash_detail_state_time)
    TextView trashDetailStateTime;
    @BindView(R.id.trash_detail_photo_count)
    TextView trashDetailPhotoCount;
    @BindView(R.id.trash_detail_still_here_btn)
    AppCompatButton trashDetailStillHereBtn;
    @BindView(R.id.trash_detail_history)
    TextView trashDetailHistory;
    @BindView(R.id.trash_detail_history_container)
    LinearLayout trashDetailHistoryContainer;
    @BindView(R.id.trash_detail_history_card_view)
    CardView trashDetailHistoryCardView;
    @BindView(R.id.trash_detail_information)
    TextView trashDetailInformation;
    @BindView(R.id.trash_detail_size_icon)
    ImageView trashDetailSizeIcon;
    @BindView(R.id.trash_detail_size_text)
    TextView trashDetailSizeText;
    @BindView(R.id.trash_detail_accessibility)
    TextView trashDetailAccessibility;
    @BindView(R.id.trash_detail_accessibility_text)
    TextView trashDetailAccessibilityText;
    @BindView(R.id.trash_detail_information_card_view)
    CardView trashDetailInformationCardView;
    @BindView(R.id.trash_detail_location)
    TextView trashDetailLocation;
    @BindView(R.id.trash_detail_map)
    ImageView trashDetailMap;
    @BindView(R.id.trash_detail_position)
    TextView trashDetailPosition;
    @BindView(R.id.trash_detail_accuracy_location)
    TextView trashDetailAccuracyLocation;
    @BindView(R.id.trash_detail_place)
    TextView trashDetailPlace;
    @BindView(R.id.trash_detail_direction_btn)
    AppCompatButton trashDetailDirectionBtn;
    @BindView(R.id.trash_detail_location_card_view)
    CardView trashDetailLocationCardView;
    @BindView(R.id.trash_detail_additional_information)
    TextView trashDetailAdditionalInformation;
    @BindView(R.id.trash_detail_additional_information_text)
    TextView trashDetailAdditionalInformationText;
    @BindView(R.id.trash_detail_create_event_btn)
    AppCompatButton trashDetailCreateEventBtn;
    @BindView(R.id.trash_detail_send_notification_btn)
    AppCompatButton trashDetailSendNotificationBtn;
    @BindView(R.id.trash_detail_report_as_spam_btn)
    AppCompatButton trashDetailReportAsSpamBtn;
    @BindView(R.id.trash_detail_edit_fab)
    FloatingActionButton trashDetailEditFab;
    @BindView(R.id.trash_detail_location_approximately)
    TextView trashDetailLocationApproximately;
    @BindView(R.id.trash_detail_accuracy_location_text)
    TextView trashDetailAccuracyLocationText;
    @BindView(R.id.divider1)
    View divider1;
    @BindView(R.id.divider2)
    View divider2;
    @BindView(R.id.trash_detail_toolbar)
    Toolbar trashDetailToolbar;
    @BindView(R.id.trash_detail_toolbar_back)
    ImageView trashDetailToolbarBack;
    @BindView(R.id.trash_detail_no_event)
    TextView trashDetailNoEvent;
    @BindView(R.id.trash_detail_event_container)
    LinearLayout trashDetailEventContainer;
    @BindView(R.id.trash_detail_event_card_view)
    CardView trashDetailEventCardView;
    @BindView(R.id.trash_detail_type_container)
    FlexboxLayout trashDetailTypeContainerFlexbox;

    private Long mTrashId;
    private Trash mTrash;
    private Event mJoinedEvent;
    private ArrayList<FullScreenImage> mImages;

    private LayoutInflater inflater;

    private LatLng lastPosition;
    private User user;
    private boolean needRefresh = false;

    public static TrashDetailFragment newInstance(long trashId) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_TRASH_ID, trashId);
        TrashDetailFragment ret = new TrashDetailFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    protected boolean useCustomFragmentToolbar() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_detail, container, false);
        ButterKnife.bind(this, view);

        this.inflater = inflater;

        user = PreferencesHandler.getUserData(getContext());
        mJoinedEvent = null;

        trashDetailToolbar.inflateMenu(R.menu.menu_trash_detail);
        trashDetailToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_share) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mTrash.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.global_share)));
                    return true;
                }
                return false;
            }
        });

        if (mTrash == null || needRefresh) {
            loadData();
        } else {
            setupTrashData(mTrash);
        }

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "setUpMapIfNeeded: permission check");
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        } else {
            setLastPosition();
        }

        return view;
    }

    private void loadData ()
    {
        if (!isNetworkAvailable()) {
            showToast(R.string.global_internet_offline);
            return;
        }

        needRefresh = false;
        if (EasyPermissions.hasPermissions(getContext(), perms))
            showProgressDialog();
        trashDetailViewContainer.setVisibility(View.GONE);
        GetTrashDetailService.startForRequest(getActivity(), GET_TRASH_DETAIL_REQUEST_ID, getTrashId());
    }

    /**
     * Get trash id
     *
     * @return
     */
    private Long getTrashId() {
        if (mTrashId == null)
            mTrashId = getArguments().getLong(BUNDLE_TRASH_ID);
        return mTrashId;
    }

    /**
     * Setup trash data
     *
     * @param trash
     */
    private void setupTrashData(Trash trash) {
        mImages = getFullScreenImagesFromTrash(trash);

        trashDetailStateName.setText(trash.getStatus().getStringResId());
        if (trash.isUpdateNeeded()) {
            trashDetailStateIcon.setImageResource(R.drawable.ic_trash_status_unknown_red);
            trashDetailStateName.setText(R.string.trash_updateNeeded);
        } else if (Constants.TrashStatus.CLEANED.equals(trash.getStatus())) {
            trashDetailStateIcon.setImageResource(R.drawable.ic_trash_activity_cleaned);
        } else if (Constants.TrashStatus.STILL_HERE.equals(trash.getStatus()) && (trash.getUpdateHistory() == null || trash.getUpdateHistory().isEmpty())) {
            trashDetailStateIcon.setImageResource(R.drawable.ic_trash_activity_reported);
        } else {
            trashDetailStateIcon.setImageResource(R.drawable.ic_trash_activity_updated);
        }

        trashDetailStateTime.setText(DateTimeUtils.getRoundedTimeAgo(getContext(), trash.getLastChangeDate()));

        trashDetailSizeIcon.setImageResource(trash.getSize().getIconResId());
        trashDetailSizeText.setText(trash.getSize().getStringResId());

        trashDetailTypeContainerFlexbox.removeAllViews();
        for (Constants.TrashType trashType : trash.getTypes()) {
            if (trashType != null) {
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.flexBasisPercent = 0.5f;
                trashDetailTypeContainerFlexbox.addView(getTrashTypeView(trashType), layoutParams);
            }
        }

        trashDetailHistoryContainer.removeAllViews();
        trashDetailHistoryContainer.addView(getTrashUpdateHistoryView(UpdateHistory.createLastUpdateHistoryFromTrash(trash), (trash.getUpdateHistory() == null || trash.getUpdateHistory().isEmpty())));
        int updateHistoryOrder = 0;

        if (trash.getUpdateHistory() != null && !trash.getUpdateHistory().isEmpty()) {
            ArrayList<UpdateHistory> preparedUpdateHistory = prepareUpdateHistory(trash.getUpdateHistory());

            for (UpdateHistory updateHistory : preparedUpdateHistory) {


                if (trashDetailHistoryContainer.getChildCount() > 0)
                    trashDetailHistoryContainer.addView(ViewUtils.getDividerView(getContext()));

                trashDetailHistoryContainer.addView(getTrashUpdateHistoryView(updateHistory, updateHistoryOrder == preparedUpdateHistory.size() - 1));
                updateHistoryOrder++;
            }
        }

        trashDetailPhotoCount.setText(String.valueOf(mImages != null ? mImages.size() : 0));

        String accessibilityText;
        if (trash.getAccessibility() != null && !TextUtils.isEmpty(accessibilityText = trash.getAccessibility().getAccessibilityString(getContext()))) {
            trashDetailAccessibilityText.setText(accessibilityText);
        } else {
            trashDetailAccessibility.setVisibility(View.GONE);
            trashDetailAccessibilityText.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(trash.getNote())) {
            trashDetailAdditionalInformation.setVisibility(View.VISIBLE);
            trashDetailAdditionalInformationText.setVisibility(View.VISIBLE);

            trashDetailAdditionalInformationText.setText(trash.getNote());
        } else {
            trashDetailAdditionalInformation.setVisibility(View.GONE);
            trashDetailAdditionalInformationText.setVisibility(View.GONE);
        }

        if (trash.getGps() != null && trash.getGps().getArea() != null && !TextUtils.isEmpty(trash.getGps().getArea().getFormatedLocation())) {
            trashDetailPlace.setText(trash.getGps().getArea().getFormatedLocation());
        } else {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            new GeocoderTask(geocoder, trash.getGps().getLat(), trash.getGps().getLng(), new GeocoderTask.Callback() {
                @Override
                public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                    if (!TextUtils.isEmpty(geocoderResult.getFormattedAddress())) {
                        trashDetailPlace.setText(geocoderResult.getFormattedAddress());
                    } else {
                        trashDetailPlace.setVisibility(View.GONE);
                    }
                }
            }).execute();
        }

        trashDetailPosition.setText(PositionUtils.getFormattedLocation(getContext(), trash.getGps().getLat(), trash.getGps().getLng()));

        trashDetailAccuracyLocationText.setText(String.format(getString(R.string.accuracy_formatter), trash.getGps().getAccuracy()));
        if (lastPosition != null)
            trashDetailLocationApproximately.setText(String.format(getString(R.string.specific_distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, trash.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));

        String mapUrl = PositionUtils.getStaticMapUrl(getActivity(), trash.getGps().getLat(), trash.getGps().getLng());
        try {
            URI mapUri = new URI(mapUrl.replace("|", "%7c"));
            Log.d(TAG, "setupTrashData: mapUrl = " + String.valueOf(mapUri.toURL()));
            GlideApp.with(this).load(String.valueOf(mapUri.toURL())).centerCrop().into(trashDetailMap);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (trash.getImages() != null && !trash.getImages().isEmpty() && ViewUtils.checkImageStorage(trash.getImages().get(0))) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(trash.getImages().get(0).getFullStorageLocation());
            GlideApp.with(this)
                    .load(mImageRef)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder_rectangle)
                    .into(trashDetailImage);
        }

        trashDetailEventContainer.removeAllViews();
        if (trash.getEvents() != null && !trash.getEvents().isEmpty()) {
            trashDetailEventCardView.setVisibility(View.VISIBLE);
            trashDetailNoEvent.setVisibility(View.GONE);

            for (Event event : trash.getEvents()) {
                if (trashDetailEventContainer.getChildCount() > 0)
                    trashDetailEventContainer.addView(ViewUtils.getDividerView(getContext()));

                trashDetailEventContainer.addView(getTrashEventView(event));
            }
        } else {
            trashDetailEventCardView.setVisibility(View.GONE);
            trashDetailNoEvent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update event view
     *
     * @param event
     */
    private void updateEventView(Event event) {
        View eventView = trashDetailEventContainer.findViewWithTag(EVENT_ID_TAG + event.getId());

        if (eventView == null)
            return;

        AppCompatButton trashEventJoinBtn = eventView.findViewById(R.id.trash_event_join_btn);

        if (user == null || event.getUserId() == user.getId()) {
            trashEventJoinBtn.setVisibility(View.GONE);
        } else {
            int visibility = View.VISIBLE;
            for (User usr : event.getUsers()) {
                if (usr.getId() == user.getId()) {
                    visibility = View.GONE;
                    break;
                }
            }

            trashEventJoinBtn.setVisibility(visibility);
        }
    }

    /**
     * Hide event view join button
     *
     * @param event
     */
    private void hideEventJoinButton(Event event) {
        View eventView = trashDetailEventContainer.findViewWithTag(EVENT_ID_TAG + event.getId());

        if (eventView == null)
            return;

        AppCompatButton trashEventJoinBtn = eventView.findViewById(R.id.trash_event_join_btn);
        trashEventJoinBtn.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 4) {
            setLastPosition();
        }
    }

    private void setLastPosition() throws SecurityException {
        GoogleApiClient mGoogleApiClient = ((MainActivity) getActivity()).getGoogleApiClient();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lastPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (trashDetailLocationApproximately != null && mTrash != null)
                trashDetailLocationApproximately.setText(String.format(getString(R.string.specific_distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, mTrash.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));
        }
    }

    /**
     * prepare update history states and order
     *
     * @param updateHistories
     */
    private ArrayList<UpdateHistory> prepareUpdateHistory(List<UpdateHistory> updateHistories) {
        ArrayList<UpdateHistory> preparedUpdateHistory = new ArrayList<>(updateHistories);
        //Collections.sort(preparedUpdateHistory, UpdateHistory.Comparators.SORT_BY_LAST_UPDATE_DESC);

        /*Constants.TrashStatus lastTrashStatus = Constants.TrashStatus.STILL_HERE;
        preparedUpdateHistory.get(0).getChanged().setStatus(lastTrashStatus);
        for (UpdateHistory updateHistory : preparedUpdateHistory) {
            lastTrashStatus = updateHistory.getChanged().getStatus() != null ? updateHistory.getChanged().getStatus() : lastTrashStatus;
            updateHistory.getChanged().setStatus(lastTrashStatus);
        }*/

        Collections.sort(preparedUpdateHistory, UpdateHistory.Comparators.SORT_BY_LAST_UPDATE_ASC);
        return preparedUpdateHistory;
    }

    /**
     * Create and return Trash type view
     *
     * @param trashType
     * @return
     */
    private View getTrashTypeView(Constants.TrashType trashType) {
        View trashTypeView = inflater.inflate(R.layout.layout_trash_type, null);
        if (trashType != null) {
            TextView trashTypeName = trashTypeView.findViewById(R.id.trash_type_name);
            SelectableImageButton trashTypeIcon = trashTypeView.findViewById(R.id.trash_type_icon);

            trashTypeName.setText(trashType.getStringResId());
            trashTypeIcon.setImageResource(trashType.getIconResId());
            trashTypeIcon.setBackgroundSelectedColor(ContextCompat.getColor(getContext(), trashType.getBgColorResId()));
            trashTypeIcon.setSelected(true);
        }
        return trashTypeView;
    }


    /**
     * Create and return history view
     *
     * @param updateHistory
     * @return simple history view
     */
    private View getTrashUpdateHistoryView(final UpdateHistory updateHistory, boolean isFirst) {
        View trashUpdateHistoryView = inflater.inflate(R.layout.layout_trash_update_history, null);

        ImageView trashUpdateIcon = trashUpdateHistoryView.findViewById(R.id.trash_update_icon);
        TextView trashUpdateName = trashUpdateHistoryView.findViewById(R.id.trash_update_name);
        TextView trashUpdate = trashUpdateHistoryView.findViewById(R.id.trash_update_date);
        TextView trashUpdateChangeState = trashUpdateHistoryView.findViewById(R.id.trash_update_change_state);
        LinearLayout trashUpdateImageContainer = trashUpdateHistoryView.findViewById(R.id.trash_update_image_container);

        trashUpdateChangeState.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_red));
        if (!isFirst) {
            trashUpdateIcon.setImageResource(R.drawable.ic_trash_activity_updated);
            if (Constants.TrashStatus.STILL_HERE.equals(updateHistory.getChanged().getStatus())) {
                trashUpdateChangeState.setText(R.string.trash_status_stillHere);
                trashUpdateIcon.setImageResource(R.drawable.ic_trash_activity_updated);
            } else if (Constants.TrashStatus.MORE.equals(updateHistory.getChanged().getStatus())) {
                trashUpdateChangeState.setText(R.string.trash_status_more);
            } else if (Constants.TrashStatus.LESS.equals(updateHistory.getChanged().getStatus())) {
                trashUpdateChangeState.setText(R.string.trash_status_less);
            } else if (Constants.TrashStatus.CLEANED.equals(updateHistory.getChanged().getStatus())) {
                trashUpdateChangeState.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_green));
                trashUpdateChangeState.setText(R.string.trash_status_cleaned);
                trashUpdateIcon.setImageResource(R.drawable.ic_trash_activity_cleaned);
            }
        } else if (updateHistory.getChanged().getStatus() != null) {
            trashUpdateIcon.setImageResource(R.drawable.ic_trash_activity_reported);
            trashUpdateChangeState.setText(R.string.trash_created);
        }

        if (updateHistory.isAnonymous()) {
            trashUpdateName.setText(getString(R.string.trash_anonymous));
        } else {
            trashUpdateName.setText(updateHistory.getOrganization() != null ? updateHistory.getOrganization().getName() : updateHistory.getUserInfo().getFullName(getContext()));
        }
        trashUpdate.setText(DateTimeUtils.getRoundedTimeAgo(getContext(), updateHistory.getUpdateTime()));

        if (updateHistory.isContainImages()) {
            int imagePosition = 0;

            final ArrayList<FullScreenImage> fullScreenImages = new ArrayList<>();
            // create container of fullscreen photos for each update history row
            for (Image image: updateHistory.getChanged().getImages())
            {
                if(updateHistory.isAnonymous()){
                    fullScreenImages.add(new FullScreenImage(image, getResources().getString(R.string.trash_anonymous), updateHistory.getUpdateTime()));
                }else{
                    fullScreenImages.add(new FullScreenImage(image, updateHistory.getOrganization() != null ? updateHistory.getOrganization().getName() : updateHistory.getUserInfo().getFullName(getContext()), updateHistory.getUpdateTime()));
                }
            }

            for (Image image : updateHistory.getChanged().getImages()) {
                SquareImageView squareImageView = new SquareImageView(getContext());
                squareImageView.setAdjustViewBounds(true);
                int imageSize = getResources().getDimensionPixelSize(R.dimen.update_image_size);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                layoutParams.setMargins(0, 0, 20, 0);
                String fullStorageLocation = image.getFullStorageLocation() != null ? image.getFullStorageLocation() : image.getThumbStorageLocation();
                if (fullStorageLocation != null && !fullStorageLocation.isEmpty()) {
                    StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fullStorageLocation);
                    GlideApp.with(this)
                            .load(mImageRef)
                            .centerCrop()
                            .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_image_placeholder_square)
                            .into(squareImageView);

                    final int position = imagePosition;
                    squareImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhotoFullscreenFragment photoFullscreenFragment = PhotoFullscreenFragment.newInstance(fullScreenImages,
                                    position);
                            getBaseActivity().replaceFragment(photoFullscreenFragment);
                        }
                    });
                } else {
                    squareImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder_square));
                }

                trashUpdateImageContainer.addView(squareImageView, layoutParams);
                imagePosition++;
            }
        }

        return trashUpdateHistoryView;
    }

    /**
     * Create and return Event view
     *
     * @param event
     * @return
     */
    private View getTrashEventView(final Event event) {
        View trashEventView = inflater.inflate(R.layout.layout_trash_event, null);

        TextView trashEventName = trashEventView.findViewById(R.id.trash_event_name);
        TextView trashEventTime = trashEventView.findViewById(R.id.trash_event_time);
        TextView trashEventDescription = trashEventView.findViewById(R.id.trash_event_description);
        Button trashEventJoinBtn = trashEventView.findViewById(R.id.trash_event_join_btn);
        Button trashEventDetailBtn = trashEventView.findViewById(R.id.trash_event_detail_btn);

        trashEventName.setText(event.getName());
        trashEventTime.setText(DateTimeUtils.DATE_FORMAT.format(event.getStart()));
        trashEventDescription.setText(event.getDescription());

//        trashEventJoinBtn.setVisibility(user == null || event.getUserId() == user.getId() ? View.GONE : View.VISIBLE);

        if (user == null || event.getUserId() == user.getId()) {
            trashEventJoinBtn.setVisibility(View.GONE);
        } else {
            int visibility = View.VISIBLE;
            for (User usr : event.getUsers()) {
                if (usr.getId() == user.getId()) {
                    visibility = View.GONE;
                    break;
                }
            }

            trashEventJoinBtn.setVisibility(visibility);
        }

        trashEventJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    showToast(R.string.event_signToJoin);
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.global_validation_warning)
                            .content(R.string.event_joinEventConfirmationMessage)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    if (mJoinedEvent != null) {
//                                        Toast.makeText(getContext(), "You're joining an event", Toast.LENGTH_SHORT).show();
//                                    }

                                    mJoinedEvent = event;
                                    showProgressDialog();
                                    JoinUserToEventService.startForRequest(getContext(), TRASH_JOIN_TO_EVENT_REQUEST_ID, event.getId(), Collections.singletonList(user.getId()));
                                }
                            })
                            .build();

                    dialog.show();
                }
            }
        });

        trashEventDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailFragment eventDetailFragment = EventDetailFragment.newInstance(event.getId());
                getBaseActivity().replaceFragment(eventDetailFragment);
            }
        });

        trashEventView.setTag(EVENT_ID_TAG + event.getId());
        return trashEventView;
    }

    private void sendNotificationEmail() {
        Log.d(TAG, "Send email");
           Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.trash_illegalDumpIn_X), mTrash.getGps().getArea().getFormatedLocation()));
        emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.trash_reportedDetailsonWeb_X), mTrash.getUrl()));

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.global_sendEmail)));
            Log.d(TAG, "Finished sending email...");
        } catch (ActivityNotFoundException ex) {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.global_validation_warning)
                    .content(R.string.global_validation_noEmailClientInstalled)
                    .positiveText(android.R.string.ok)
                    .autoDismiss(true)
                    .build();

            dialog.show();
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return TrashDetailFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetTrashDetailService.class);
        serviceClass.add(CreateTrashNewSpamService.class);
        serviceClass.add(JoinUserToEventService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (getContext() == null)
            return;

        if (apiResult.getRequestId() == GET_TRASH_DETAIL_REQUEST_ID) {
            if (apiResult.isValidResponse() && apiResult.getResult() != null) {
                ApiGetTrashDetailResult apiGetTrashDetailResult = (ApiGetTrashDetailResult) apiResult.getResult();
                mTrash = apiGetTrashDetailResult.getTrash();
                setupTrashData(mTrash);
                dismissProgressDialog();
                trashDetailViewContainer.setVisibility(View.VISIBLE);
            } else {
                dismissProgressDialog();
                showToast(R.string.global_fetchError);
            }
        } else if (apiResult.getRequestId() == TRASH_CREATE_SPAM_REQUEST_ID) {
            dismissProgressDialog();
            showToast(apiResult.isValidResponse() ? R.string.trash_messageWasReceived : R.string.trash_markedAsSpam_alreadyMarked);
        } else if (apiResult.getRequestId() == TRASH_JOIN_TO_EVENT_REQUEST_ID) {
            dismissProgressDialog();
            showToast(apiResult.isValidResponse() ? R.string.event_joinEventSuccessful : R.string.event_joinEventFailed);

            if (apiResult.isValidResponse()) {
                hideEventJoinButton(mJoinedEvent);
                openAddEventToCalendarDialog(mJoinedEvent);
            }

            mJoinedEvent = null;
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick({R.id.trash_detail_cleaned_btn, R.id.trash_detail_still_here_btn, R.id.trash_detail_more_btn, R.id.trash_detail_less_btn, R.id.trash_detail_direction_btn, R.id.trash_detail_create_event_btn, R.id.trash_detail_send_notification_btn, R.id.trash_detail_report_as_spam_btn, R.id.trash_detail_edit_fab, R.id.trash_detail_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.trash_detail_cleaned_btn:
                TrashReportOrEditFragment trashReportOrEditFragmentCleaned = TrashReportOrEditFragment.newInstance(mTrash, true, false, false, false);
                getBaseActivity().replaceFragment(trashReportOrEditFragmentCleaned);
                break;
            case R.id.trash_detail_still_here_btn:
                TrashReportOrEditFragment trashReportOrEditFragmentStillHere = TrashReportOrEditFragment.newInstance(mTrash, false, true, false, false);
                getBaseActivity().replaceFragment(trashReportOrEditFragmentStillHere);
                break;
            case R.id.trash_detail_more_btn:
                TrashReportOrEditFragment trashReportOrEditFragmentMore = TrashReportOrEditFragment.newInstance(mTrash, false, false, true, false);
                getBaseActivity().replaceFragment(trashReportOrEditFragmentMore);
                break;
            case R.id.trash_detail_less_btn:
                TrashReportOrEditFragment trashReportOrEditFragmentLess = TrashReportOrEditFragment.newInstance(mTrash, false, false, false, true);
                getBaseActivity().replaceFragment(trashReportOrEditFragmentLess);
                break;
            case R.id.trash_detail_direction_btn:
                if (mTrash != null) {
                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + mTrash.getGps().getLat() + "," + mTrash.getGps().getLng());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                break;
            case R.id.trash_detail_create_event_btn:
                EventCreateFragment eventCreateFragment = EventCreateFragment.newInstance(getTrashId(), mTrash != null ? mTrash.getPosition() : null);
                eventCreateFragment.setTargetFragment(this, 0);
                getBaseActivity().replaceFragment(eventCreateFragment);
                break;
            case R.id.trash_detail_send_notification_btn:
                sendNotificationEmail();
                break;
            case R.id.trash_detail_report_as_spam_btn:
                if (mTrash != null) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.global_validation_warning)
                            .content(R.string.trash_spam_comfirmation)
                            .positiveText(android.R.string.yes)
                            .negativeText(android.R.string.cancel)
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    showProgressDialog();
                                    CreateTrashNewSpamService.startForRequest(getContext(), TRASH_CREATE_SPAM_REQUEST_ID, mTrash.getActivityId(), user.getId());
                                }
                            })
                            .build();

                    dialog.show();
                }
                break;
            case R.id.trash_detail_edit_fab:
                TrashReportOrEditFragment trashReportOrEditFragment = TrashReportOrEditFragment.newInstance(mTrash, false, false, false, false);
                getBaseActivity().replaceFragment(trashReportOrEditFragment);
                break;
            case R.id.trash_detail_image:
                if (mTrash != null && mTrash.getImages() != null && !mTrash.getImages().isEmpty()) {
                    PhotoFullscreenFragment photoFullscreenFragment = PhotoFullscreenFragment.newInstance(mImages, 0);
                    getBaseActivity().replaceFragment(photoFullscreenFragment);
                }
                break;
        }
    }

    @OnClick(R.id.trash_detail_toolbar_back)
    public void onBackClick() {
        finish();
    }

    @Override
    public void onEventCreated() {
        this.needRefresh = true;
    }

    public void onTrashChanged() {
        this.needRefresh = true;
    }

    public void onEventJoined() {
        this.needRefresh = true;
    }

    private void openAddEventToCalendarDialog(final Event event) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.event_addToCalendar)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utils.addEventToCalender(getContext(), event);
                    }
                })
                .build();

        dialog.show();
    }

    public ArrayList<FullScreenImage> getFullScreenImagesFromTrash(Trash trash) {
        ArrayList<FullScreenImage> fullScreenImages = new ArrayList<>();
        UpdateHistory latestUpdateHistory = UpdateHistory.createLastUpdateHistoryFromTrash(trash);
        for (Image image: trash.getImages())
        {
            if(latestUpdateHistory.isAnonymous()){
                fullScreenImages.add(new FullScreenImage(image, getResources().getString(R.string.trash_anonymous), latestUpdateHistory.getUpdateTime()));
            }else{
                fullScreenImages.add(new FullScreenImage(image, latestUpdateHistory.getOrganization() != null ? latestUpdateHistory.getOrganization().getName() : latestUpdateHistory.getUserInfo().getFullName(getContext()), latestUpdateHistory.getUpdateTime()));
            }
        }

        if (trash.getUpdateHistory() != null && !trash.getUpdateHistory().isEmpty()) {
            ArrayList<UpdateHistory> preparedUpdateHistory = prepareUpdateHistory(trash.getUpdateHistory());
            for (UpdateHistory updateHistory : preparedUpdateHistory) {
                for (Image image: updateHistory.getChanged().getImages())
                {
                    if(updateHistory.isAnonymous()){
                        fullScreenImages.add(new FullScreenImage(image, getResources().getString(R.string.trash_anonymous), updateHistory.getUpdateTime()));
                    }else{
                        fullScreenImages.add(new FullScreenImage(image, updateHistory.getOrganization() != null ? updateHistory.getOrganization().getName() : updateHistory.getUserInfo().getFullName(getContext()), updateHistory.getUpdateTime()));
                    }
                }
            }
        }

        return fullScreenImages;
    }
}

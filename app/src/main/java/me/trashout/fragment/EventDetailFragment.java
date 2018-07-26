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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetEventDetailResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Constants;
import me.trashout.model.Event;
import me.trashout.model.TrashPoint;
import me.trashout.model.User;
import me.trashout.service.GetEventDetailService;
import me.trashout.service.JoinUserToEventService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import me.trashout.utils.ViewUtils;

public class EventDetailFragment extends BaseFragment implements ITrashFragment, BaseService.UpdateServiceListener {

    private static final int GET_EVENT_DETAIL_REQUEST_ID = 750;
    private static final int JOIN_TO_EVENT_REQUEST_ID = 751;

    private static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    @BindView(R.id.event_detail_name)
    TextView eventDetailName;
    @BindView(R.id.event_detail_time)
    TextView eventDetailTime;
    @BindView(R.id.event_detail_description)
    TextView eventDetailDescription;
    @BindView(R.id.event_detail_join_btn)
    AppCompatButton eventDetailJoinBtn;
    @BindView(R.id.event_detail_info_card_view)
    CardView eventDetailInfoCardView;
    @BindView(R.id.event_detail_phone_icon)
    ImageView eventDetailPhoneIcon;
    @BindView(R.id.event_detail_phone)
    TextView eventDetailPhone;
    @BindView(R.id.event_detail_phone_info)
    TextView eventDetailPhoneInfo;
    @BindView(R.id.event_detail_phone_layout)
    RelativeLayout eventDetailPhoneLayout;
    @BindView(R.id.event_detail_email_icon)
    ImageView eventDetailEmailIcon;
    @BindView(R.id.event_detail_email)
    TextView eventDetailEmail;
    @BindView(R.id.event_detail_email_info)
    TextView eventDetailEmailInfo;
    @BindView(R.id.event_detail_email_layout)
    RelativeLayout eventDetailEmailLayout;
    @BindView(R.id.event_detail_map)
    ImageView eventDetailMap;
    @BindView(R.id.event_detail_position)
    TextView eventDetailPosition;
    @BindView(R.id.event_detail_place)
    TextView eventDetailPlace;
    @BindView(R.id.event_detail_direction_btn)
    AppCompatButton eventDetailDirectionBtn;
    @BindView(R.id.event_detail_location_card_view)
    CardView eventDetailLocationCardView;
    @BindView(R.id.event_detail_we_have_title)
    TextView eventDetailWeHaveTitle;
    @BindView(R.id.event_detail_we_have)
    TextView eventDetailWeHave;
    @BindView(R.id.event_detail_bring_title)
    TextView eventDetailBringTitle;
    @BindView(R.id.event_detail_bring)
    TextView eventDetailBring;
    @BindView(R.id.event_detail_equipment_card_view)
    CardView eventDetailEquipmentCardView;
    @BindView(R.id.event_detail_list_of_trash_title)
    TextView eventDetailListOfTrashTitle;
    @BindView(R.id.event_detail_trash_list_container)
    LinearLayout eventDetailTrashListContainer;
    @BindView(R.id.event_detail_trash_list_card_view)
    CardView eventDetailTrashlispCardView;

    private LayoutInflater inflater;

    private Event mEvent;
    private Long mEventId;

    private LatLng lastPosition;
    private User user;

    private OnEventJoinedListener onEventJoinedListener;

    public interface OnEventJoinedListener {
        void onEventJoined();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onEventJoinedListener = (OnEventJoinedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEventJoinedListener");
        }
    }

    public static EventDetailFragment newInstance(long eventId) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_EVENT_ID, eventId);
        EventDetailFragment ret = new EventDetailFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);
        this.inflater = inflater;

        user = PreferencesHandler.getUserData(getContext());

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "setUpMapIfNeeded: permission check");
        } else {
            setLastPosition();
        }

        if (mEvent == null) {
            showProgressDialog();
            GetEventDetailService.startForRequest(getActivity(), GET_EVENT_DETAIL_REQUEST_ID, getEventId());
        } else {
            setupEventData(mEvent);
        }

        return view;
    }

    /**
     * Get event id
     *
     * @return
     */
    private Long getEventId() {
        if (mEventId == null)
            mEventId = getArguments().getLong(BUNDLE_EVENT_ID);
        return mEventId;
    }

    private void setupEventData(Event event) {
        if (user == null || mEvent.getUserId() == user.getId()) {
            eventDetailJoinBtn.setVisibility(View.GONE);
        } else {
            int visibility = View.VISIBLE;
            for (User usr : event.getUsers()) {
                if (usr.getId() == user.getId()) {
                    visibility = View.GONE;
                    break;
                }
            }

            eventDetailJoinBtn.setVisibility(visibility);
        }

        eventDetailName.setText(event.getName());
        if (event.getStart() != null)
            eventDetailTime.setText(String.format("%s, %s", DateTimeUtils.DATE_TIME_FORMAT.format(event.getStart()), DateTimeUtils.getDurationTimeString(getContext(), event.getDuration() * 60 * 1000)));
        else
            eventDetailTime.setText("?");
        eventDetailDescription.setText(event.getDescription());


        eventDetailPhone.setText(event.getContact().getPhone());
        eventDetailEmail.setText(event.getContact().getEmail());


        eventDetailWeHave.setText(event.getHave());
        eventDetailBring.setText(event.getBring());


        if (event.getGps() != null && event.getGps().getArea() != null && !TextUtils.isEmpty(event.getGps().getArea().getFormatedLocation())) {
            eventDetailPlace.setText(event.getGps().getArea().getFormatedLocation());
        } else {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            new GeocoderTask(geocoder, event.getGps().getLat(), event.getGps().getLng(), new GeocoderTask.Callback() {
                @Override
                public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                    if (!TextUtils.isEmpty(geocoderResult.getFormattedAddress())) {
                        eventDetailPlace.setText(geocoderResult.getFormattedAddress());
                    } else {
                        eventDetailPlace.setVisibility(View.GONE);
                    }
                }
            }).execute();
        }

        eventDetailPosition.setText(PositionUtils.getFormattedLocation(getContext(), event.getGps().getLat(), event.getGps().getLng()));

        String mapUrl = PositionUtils.getStaticMapUrl(getActivity(), event.getGps().getLat(), event.getGps().getLng());
        try {
            URI mapUri = new URI(mapUrl.replace("|", "%7c"));
            Log.d(TAG, "setupDumpData: mapUrl = " + String.valueOf(mapUri.toURL()));
            GlideApp.with(this).load(String.valueOf(mapUri.toURL())).centerCrop().dontTransform().into(eventDetailMap);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        if (event.getTrashPoints() != null && !event.getTrashPoints().isEmpty()) {
            eventDetailListOfTrashTitle.setVisibility(View.VISIBLE);
            eventDetailTrashlispCardView.setVisibility(View.VISIBLE);

            eventDetailTrashListContainer.removeAllViews();
            for (TrashPoint trashPoint : event.getTrashPoints()) {
                if (eventDetailTrashListContainer.getChildCount() > 0)
                    eventDetailTrashListContainer.addView(ViewUtils.getDividerView(getContext()));

                eventDetailTrashListContainer.addView(getTrashView(trashPoint));
            }
        } else {
            eventDetailListOfTrashTitle.setVisibility(View.GONE);
            eventDetailTrashlispCardView.setVisibility(View.GONE);
        }
    }

    /**
     * Create and return Trash view
     *
     * @param trashPoint
     * @return
     */
    private View getTrashView(TrashPoint trashPoint) {
        View eventTrashView = inflater.inflate(R.layout.layout_event_trash, null);

        TextView eventTrashDate = eventTrashView.findViewById(R.id.event_trash_date);
        TextView eventTrashTypes = eventTrashView.findViewById(R.id.event_trash_types);
        TextView eventTrashLocation = eventTrashView.findViewById(R.id.event_trash_location);
        ImageView eventTrashImage = eventTrashView.findViewById(R.id.event_trash_image);
        ImageView eventTrashStatusIcon = eventTrashView.findViewById(R.id.event_trash_status_icon);

        eventTrashLocation.setText(String.format(getContext().getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, trashPoint.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));

        if (trashPoint.getLastChangeDate() != null)
            eventTrashDate.setText(DateTimeUtils.getRoundedTimeAgo(getContext(), trashPoint.getLastChangeDate()));

        eventTrashTypes.setText(TextUtils.join(", ", Constants.TrashType.getTrashTypeNameList(getContext(), trashPoint.getTypes())));

        eventTrashStatusIcon.setImageResource(trashPoint.getStatus().getIconResId());

        if (trashPoint.getImages() != null && !trashPoint.getImages().isEmpty() && !TextUtils.isEmpty(trashPoint.getImages().get(0).getSmallestImage())) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(trashPoint.getImages().get(0).getSmallestImage());
            GlideApp.with(getContext()).load(mImageRef).centerCrop().placeholder(R.drawable.ic_image_placeholder_square).into(eventTrashImage);
        } else {
            eventTrashImage.setImageResource(R.drawable.ic_image_placeholder_square);
        }


        return eventTrashView;
    }

    private void setLastPosition() throws SecurityException {
        LatLng lastLatLng = ((MainActivity) getActivity()).getLastPosition();
        if (lastLatLng != null) {
            lastPosition = lastLatLng;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.event_detail_header));
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return EventDetailFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetEventDetailService.class);
        serviceClass.add(JoinUserToEventService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (getContext() == null)
            return;

        if (apiResult.getRequestId() == GET_EVENT_DETAIL_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                ApiGetEventDetailResult apiGetTrashDetailResult = (ApiGetEventDetailResult) apiResult.getResult();
                mEvent = apiGetTrashDetailResult.getEvent();
                setupEventData(mEvent);
            } else {
                getBaseActivity().showToast(R.string.global_error_api_text);
            }
        } else if (apiResult.getRequestId() == JOIN_TO_EVENT_REQUEST_ID) {
            dismissProgressDialog();
            getBaseActivity().showToast(apiResult.isValidResponse() ? R.string.event_joinEventSuccessful : R.string.event_joinEventFailed);

            if (apiResult.isValidResponse()) {
                eventDetailJoinBtn.setVisibility(View.GONE);
                if (onEventJoinedListener != null) {
                    onEventJoinedListener.onEventJoined();
                }
                openAddEventToCalendarDialog();
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick({R.id.event_detail_join_btn, R.id.event_detail_phone_layout, R.id.event_detail_email_layout, R.id.event_detail_direction_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.event_detail_join_btn:
                if (mEvent != null) {
                    if (user == null) {
                        getBaseActivity().showToast(R.string.event_signToJoin);
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
                                        showProgressDialog();
                                        JoinUserToEventService.startForRequest(getContext(), JOIN_TO_EVENT_REQUEST_ID, mEvent.getId(), Collections.singletonList(user.getId()));
                                    }
                                })
                                .build();

                        dialog.show();
                    }
                }
                break;
            case R.id.event_detail_phone_layout:
                if (mEvent != null && mEvent.getContact() != null && !TextUtils.isEmpty(mEvent.getContact().getPhone())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mEvent.getContact().getPhone(), null));
                    startActivity(intent);
                }
                break;
            case R.id.event_detail_email_layout:
                if (mEvent != null && mEvent.getContact() != null && !TextUtils.isEmpty(mEvent.getContact().getEmail())) {
                    sendEmail();
                }
                break;
            case R.id.event_detail_direction_btn:
                if (mEvent != null) {
                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + mEvent.getGps().getLat() + "," + mEvent.getGps().getLng());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                break;
        }
    }

    private void sendEmail() {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo(mEvent.getContact().getEmail())
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(R.string.global_sendEmail)
                .startChooser();
    }

    private void openAddEventToCalendarDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.event_addToCalendar)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utils.addEventToCalender(getContext(), mEvent);
                    }
                })
                .build();

        dialog.show();
    }
}

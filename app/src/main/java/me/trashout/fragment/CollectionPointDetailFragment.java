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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.Html;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetCollectionPointDetailResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ICollectionPointFragment;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Constants;
import me.trashout.model.OpeningHour;
import me.trashout.model.User;
import me.trashout.service.CreateCollectionPointNewSpamService;
import me.trashout.service.GetCollectionPointDetailService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 08.12.2016
 */
public class CollectionPointDetailFragment extends BaseFragment implements ICollectionPointFragment, BaseService.UpdateServiceListener {

    private static final String BUNDLE_COLLECTION_POINT_ID = "BUNDLE_COLLECTION_POINT_ID";

    private static final int GET_COLLECTION_POINT_DETAIL_REQUEST_ID = 850;
    private static final int CREATE_COLLECTION_POINT_NEW_SPAM_REQUEST_ID = 851;

    @BindView(R.id.collection_point_detail_name)
    TextView collectionPointDetailName;
    @BindView(R.id.collection_point_detail_place)
    TextView collectionPointDetailPlace;
    @BindView(R.id.collection_point_detail_distance)
    TextView collectionPointDetailDistance;
    @BindView(R.id.collection_point_detail_position)
    TextView collectionPointDetailPosition;
    @BindView(R.id.collection_point_detail_type)
    TextView collectionPointDetailType;
    @BindView(R.id.collection_point_detail_card_view)
    CardView collectionPointDetailCardView;
    @BindView(R.id.collection_point_detail_phone_icon)
    ImageView collectionPointDetailPhoneIcon;
    @BindView(R.id.collection_point_detail_phone)
    TextView collectionPointDetailPhone;
    @BindView(R.id.collection_point_detail_phone_info)
    TextView collectionPointDetailPhoneInfo;
    @BindView(R.id.collection_point_detail_phone_layout)
    RelativeLayout collectionPointDetailPhoneLayout;
    @BindView(R.id.collection_point_detail_email_icon)
    ImageView collectionPointDetailEmailIcon;
    @BindView(R.id.collection_point_detail_email)
    TextView collectionPointDetailEmail;
    @BindView(R.id.collection_point_detail_email_info)
    TextView collectionPointDetailEmailInfo;
    @BindView(R.id.collection_point_detail_email_layout)
    RelativeLayout collectionPointDetailEmailLayout;
    @BindView(R.id.collection_point_detail_opening_hours)
    TextView collectionPointDetailOpeningHours;
    @BindView(R.id.collection_point_detail_opening_hours_container)
    LinearLayout collectionPointDetailOpeningHoursContainer;
    @BindView(R.id.collection_point_detail_note)
    TextView collectionPointDetailNote;
    @BindView(R.id.collection_point_detail_no_exist_btn)
    AppCompatButton collectionPointDetailNoExistBtn;
    @BindView(R.id.collection_point_detail_direction_btn)
    AppCompatButton collectionPointDetailDirectionBtn;

    private Long mCollectionPointId;

    private CollectionPoint mCollectionPoint;
    private LatLng lastPosition;
    private User user;

    public static CollectionPointDetailFragment newInstance(Long collectionPointId) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_COLLECTION_POINT_ID, collectionPointId);
        CollectionPointDetailFragment ret = new CollectionPointDetailFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_point_detail, container, false);
        ButterKnife.bind(this, view);

        user = PreferencesHandler.getUserData(getContext());

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission check");
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        } else {
            setLastPosition();
        }

        if (mCollectionPoint == null) {
            showProgressDialog();
            GetCollectionPointDetailService.startForRequest(getActivity(), GET_COLLECTION_POINT_DETAIL_REQUEST_ID, getCollectionPointId());
        } else {
            setupCollectionPointData(mCollectionPoint);
        }
        return view;
    }

    /**
     * Get collection point id
     *
     * @return
     */
    private Long getCollectionPointId() {
        if (mCollectionPointId == null)
            mCollectionPointId = getArguments().getLong(BUNDLE_COLLECTION_POINT_ID);
        return mCollectionPointId;
    }

    /**
     * setup collection point data
     *
     * @param collectionPoint
     */
    private void setupCollectionPointData(CollectionPoint collectionPoint) {
        if (isAdded()) {
            collectionPointDetailName.setText(collectionPoint.getSize().equals(Constants.CollectionPointSize.DUSTBIN) ? getString(Constants.CollectionPointSize.DUSTBIN.getStringResId()) : collectionPoint.getName());

            collectionPointDetailType.setText(Html.fromHtml(getString(R.string.recyclable_gray) + TextUtils.join(", ", Constants.CollectionPointType.getCollectionPointTypeNameList(getContext(), collectionPoint.getTypes()))));
            collectionPointDetailDistance.setText(String.format(getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, collectionPoint.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));
            collectionPointDetailPosition.setText(collectionPoint.getPosition() != null ? PositionUtils.getFormattedLocation(getContext(), collectionPoint.getPosition().latitude, collectionPoint.getPosition().longitude) : "?");

            if (collectionPoint.getOpeningHours() == null || collectionPoint.getOpeningHours().isEmpty()) {
                collectionPointDetailOpeningHoursContainer.setVisibility(View.GONE);
                collectionPointDetailOpeningHours.setVisibility(View.GONE);
            } else {
                collectionPointDetailOpeningHoursContainer.setVisibility(View.VISIBLE);
                collectionPointDetailOpeningHoursContainer.removeAllViews();
                collectionPointDetailOpeningHours.setVisibility(View.VISIBLE);
                for (Map<String, List<OpeningHour>> openingHoursMap : collectionPoint.getOpeningHours()) {
                    for (Map.Entry<String, List<OpeningHour>> openingHourEntry : openingHoursMap.entrySet()) {
                        LinearLayout openingHoursLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_opening_hours, null);
                        TextView dayNameTextView = openingHoursLayout.findViewById(R.id.txt_day_name);
                        TextView openingHoursTextView = openingHoursLayout.findViewById(R.id.txt_opening_hours);

                        int dayNameResId;
                        switch (openingHourEntry.getKey()) {
                            case "Monday":
                                dayNameResId = R.string.global_days_Monday;
                                break;
                            case "Tuesday":
                                dayNameResId = R.string.global_days_Tuesday;
                                break;
                            case "Wednesday":
                                dayNameResId = R.string.global_days_Wednesday;
                                break;
                            case "Thursday":
                                dayNameResId = R.string.global_days_Thursday;
                                break;
                            case "Friday":
                                dayNameResId = R.string.global_days_Friday;
                                break;
                            case "Saturday":
                                dayNameResId = R.string.global_days_Saturday;
                                break;
                            case "Sunday":
                                dayNameResId = R.string.global_days_Sunday;
                                break;
                            default:
                                dayNameResId = R.string.global_days_Monday;
                        }

                        dayNameTextView.setText(getString(dayNameResId));
                        for (OpeningHour openingHour : openingHourEntry.getValue()) {
                            String startString = String.valueOf(openingHour.getStart());
                            String finishString = String.valueOf(openingHour.getFinish());
                            if (startString.length() == 3) {
                                startString = "0" + startString;
                            }
                            if (finishString.length() == 3) {
                                finishString = "0" + finishString;
                            }
                            openingHoursTextView.append(startString.substring(0, 2) + ":" + startString.substring(2, startString.length())
                                    + " - "
                                    + finishString.substring(0, 2) + ":" + finishString.substring(2, finishString.length())
                                    + ", "
                            );
                        }
                        if (openingHourEntry.getValue().size() > 0) {
                            openingHoursTextView.setText(openingHoursTextView.getText().toString().substring(0, openingHoursTextView.getText().length() - 2));
                        }
                        collectionPointDetailOpeningHoursContainer.addView(openingHoursLayout);
                    }
                }
            }
            collectionPointDetailNote.setText(collectionPoint.getNote());

            if (collectionPoint.getPhone() == null || collectionPoint.getPhone().isEmpty()) {
                collectionPointDetailPhoneLayout.setVisibility(View.GONE);
            } else {
                collectionPointDetailPhoneLayout.setVisibility(View.VISIBLE);
                collectionPointDetailPhone.setText(collectionPoint.getPhone());
            }
            if (collectionPoint.getEmail() == null || collectionPoint.getEmail().isEmpty()) {
                collectionPointDetailEmailLayout.setVisibility(View.GONE);
            } else {
                collectionPointDetailEmailLayout.setVisibility(View.VISIBLE);
                collectionPointDetailEmail.setText(collectionPoint.getEmail());
            }

            if (collectionPoint.getGps() != null && collectionPoint.getGps().getArea() != null && !TextUtils.isEmpty(collectionPoint.getGps().getArea().getFormatedLocation())) {
                collectionPointDetailPlace.setText(collectionPoint.getGps().getArea().getFormatedLocation());
            } else {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                new GeocoderTask(geocoder, collectionPoint.getGps().getLat(), collectionPoint.getGps().getLng(), new GeocoderTask.Callback() {
                    @Override
                    public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                        if (!TextUtils.isEmpty(geocoderResult.getFormattedAddress())) {
                            collectionPointDetailPlace.setText(geocoderResult.getFormattedAddress());
                        } else {
                            collectionPointDetailPlace.setVisibility(View.GONE);
                        }
                    }
                }).execute();
            }
        }
    }

    private static int parseDayOfWeek(String day, Locale locale)
            throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", locale);
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    private void setLastPosition() throws SecurityException {
        LatLng lastLatLng = ((MainActivity) getActivity()).getLastPosition();
        if (lastLatLng != null) {
            lastPosition = lastLatLng;
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return CollectionPointDetailFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetCollectionPointDetailService.class);
        serviceClass.add(CreateCollectionPointNewSpamService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_COLLECTION_POINT_DETAIL_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                ApiGetCollectionPointDetailResult apiGetCollectionPointDetailResult = (ApiGetCollectionPointDetailResult) apiResult.getResult();
                mCollectionPoint = apiGetCollectionPointDetailResult.getCollectionPoint();
                setupCollectionPointData(mCollectionPoint);
            }
        } else if (apiResult.getRequestId() == CREATE_COLLECTION_POINT_NEW_SPAM_REQUEST_ID) {
            dismissProgressDialog();
            Toast.makeText(getContext(), apiResult.isValidResponse() || apiResult.getResponse().code() == 500 || apiResult.getResponse().code() == 400 ? R.string.collectionPoint_markedAsSpam_success_thanksMobile : R.string.trash_create_markAsSpamFailed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick({R.id.collection_point_detail_phone_layout, R.id.collection_point_detail_email_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collection_point_detail_phone_layout:
                if (mCollectionPoint != null && !TextUtils.isEmpty(mCollectionPoint.getPhone())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mCollectionPoint.getPhone(), null));
                    startActivity(intent);
                }
                break;
            case R.id.collection_point_detail_email_layout:
                if (mCollectionPoint != null && !TextUtils.isEmpty(mCollectionPoint.getEmail())) {
                    ShareCompat.IntentBuilder.from(getActivity())
                            .setType("message/rfc822")
                            .addEmailTo(mCollectionPoint.getEmail())
                            .setChooserTitle(R.string.global_sendEmail)
                            .startChooser();
                }
                break;
        }
    }

    @OnClick(R.id.collection_point_detail_no_exist_btn)
    public void onNoExistClick() {
        if (mCollectionPoint != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.global_validation_warning)
                    .content(R.string.collectionPoint_markAsNoLongerExistsFailed_message)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .autoDismiss(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showProgressDialog();
                            CreateCollectionPointNewSpamService.startForRequest(getContext(), CREATE_COLLECTION_POINT_NEW_SPAM_REQUEST_ID, mCollectionPoint.getActivityId());
                        }
                    })
                    .build();

            dialog.show();
        }
    }

    @OnClick(R.id.collection_point_detail_direction_btn)
    public void onDirectionClick() {
        if (mCollectionPoint != null && mCollectionPoint.getGps() != null) {
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + mCollectionPoint.getGps().getLat() + "," + mCollectionPoint.getGps().getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }
}

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

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Contact;
import me.trashout.model.Event;
import me.trashout.model.Gps;
import me.trashout.model.User;
import me.trashout.service.CreateEventService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.ViewUtils;

public class EventCreateFragment extends BaseFragment implements ITrashFragment, BaseService.UpdateServiceListener {

    private static final int CREATE_EVENT_REQUEST_ID = 250;
    private static final int REQUEST_PLACE_PICKER = 20;

    private static final String BUNDLE_DATE_YEAR = "BUNDLE_DATE_YEAR";
    private static final String BUNDLE_DATE_MONTH = "BUNDLE_DATE_MONTH";
    private static final String BUNDLE_DATE_DAY = "BUNDLE_DATE_DAY";

    private static final String BUNDLE_TRASH_ID = "BUNDLE_TRASH_ID";
    private static final String BUNDLE_DEFAULT_MEETING_POINT = "BUNDLE_DEFAULT_MEETING_POINT";

    @BindView(R.id.create_event_about_title)
    TextView createEventAboutTitle;
    @BindView(R.id.create_event_about_name)
    EditText createEventAboutName;
    @BindView(R.id.create_event_about_description)
    EditText createEventAboutDescription;
    @BindView(R.id.create_event_meeting_point_title)
    TextView createEventMeetingPointTitle;
    @BindView(R.id.create_event_meeting_point_btn)
    AppCompatButton createEventMeetingPointBtn;
    @BindView(R.id.create_event_meeting_point_city)
    TextView createEventMeetingPointCity;
    @BindView(R.id.create_event_meeting_point_street)
    TextView createEventMeetingPointStreet;
    @BindView(R.id.create_event_dumps_title)
    TextView createEventDumpsTitle;
    @BindView(R.id.create_event_dumps_selected)
    TextView createEventDumpsSelected;
    @BindView(R.id.create_event_dumps_btn)
    AppCompatButton createEventDumpsBtn;
    @BindView(R.id.create_event_meeting_date_title)
    TextView createEventMeetingDateTitle;
    @BindView(R.id.create_event_meeting_date_date_from)
    TextView createEventMeetingDateDateFrom;
    @BindView(R.id.create_event_meeting_date_date_to)
    TextView createEventMeetingDateDateTo;
    @BindView(R.id.create_event_equipment_title)
    TextView createEventEquipmentTitle;
    @BindView(R.id.create_event_equipment_bring)
    EditText createEventEquipmentBring;
    @BindView(R.id.create_event_equipment_have)
    EditText createEventEquipmentHave;
    @BindView(R.id.create_event_contact_title)
    TextView createEventContactTitle;
    @BindView(R.id.create_event_contact_email)
    EditText createEventContactEmail;
    @BindView(R.id.create_event_contact_phone)
    EditText createEventContactPhone;
    @BindView(R.id.create_event_btn)
    AppCompatButton createEventBtn;

    private LatLng meetingPoint;

    private ArrayList<Long> selectedTrashOnMap;
    private Date dateFrom;
    private Date dateTo;

    private User user;

    public interface OnSelectTrashIdsOnMapListener {
        void onTrashIdsOnMapSelected(ArrayList<Long> selectedTrash);
    }

    public interface OnCreateEventListener {
        void onEventCreated();
    }

    public static EventCreateFragment newInstance(Long trashId, LatLng defaultMeetingPoint) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_TRASH_ID, trashId);
        b.putParcelable(BUNDLE_DEFAULT_MEETING_POINT, defaultMeetingPoint);
        EventCreateFragment ret = new EventCreateFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_create, container, false);
        ButterKnife.bind(this, view);

        user = PreferencesHandler.getUserData(getContext());
        if (user != null && !TextUtils.isEmpty(user.getEmail()))
            createEventContactEmail.setText(user.getEmail());


        if (selectedTrashOnMap == null) {
            selectedTrashOnMap = new ArrayList<>();
            if (getTrashId() != null)
                selectedTrashOnMap.add(getTrashId());
        }

        createEventDumpsSelected.setText(String.format(getString(R.string.event_create_youSelectedDumps_X), (selectedTrashOnMap == null) ? 0 : selectedTrashOnMap.size()));

        if (meetingPoint != null) {
            setMeetingPoint(meetingPoint);
        } else if (getDefaultMeetingPoint() != null) {
            meetingPoint = getDefaultMeetingPoint();
            setMeetingPoint(meetingPoint);
        }

        return view;
    }

    public Long getTrashId() {
        return getArguments().getLong(BUNDLE_TRASH_ID);
    }


    public LatLng getDefaultMeetingPoint() {
        return getArguments().getParcelable(BUNDLE_DEFAULT_MEETING_POINT);
    }

    public void onTrashOnMapSelected(ArrayList<Long> selectedTrashOnMap) {
        this.selectedTrashOnMap = selectedTrashOnMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.event_create_header));
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACE_PICKER) {

            if (resultCode == Activity.RESULT_OK) {

                final Place place = PlacePicker.getPlace(getActivity(), data);

                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getAttributions(data);
                if (attribution == null) {
                    attribution = "";
                }
                // Print data to debug log
                Log.d(TAG, "Place selected: " + placeId + " (" + name.toString() + ")");

                meetingPoint = place.getLatLng();
                setMeetingPoint(meetingPoint);
            }

        }
    }

    private void setMeetingPoint(LatLng meetingPoint) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        new GeocoderTask(geocoder, meetingPoint.latitude, meetingPoint.longitude, new GeocoderTask.Callback() {
            @Override
            public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                Log.d(TAG, "geocoderResult  = " + geocoderResult);
                if (geocoderResult.getAddress() != null) {
                    createEventMeetingPointStreet.setText(geocoderResult.getAddress().getThoroughfare() + ", " + geocoderResult.getAddress().getFeatureName());
                    createEventMeetingPointCity.setText(geocoderResult.getAddress().getSubAdminArea());
                }
            }
        }).execute();
    }

    @OnClick({R.id.create_event_meeting_point_btn, R.id.create_event_dumps_btn, R.id.create_event_meeting_date_date_from, R.id.create_event_meeting_date_date_to, R.id.create_event_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_event_meeting_point_btn:
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                if (meetingPoint != null) {
                    LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder().include(meetingPoint);
                    intentBuilder.setLatLngBounds(latLngBounds.build());
                }
                Intent intent = null;
                try {
                    intent = intentBuilder.build(getActivity());
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                // Start the Intent by requesting a result, identified by a request code.
                getActivity().startActivityForResult(intent, REQUEST_PLACE_PICKER);
                break;
            case R.id.create_event_dumps_btn:
                if (meetingPoint != null) {
                    EventTrashSelectorFragment eventTrashSelectorFragment = EventTrashSelectorFragment.newInstance(meetingPoint, selectedTrashOnMap);
                    getBaseActivity().replaceFragment(eventTrashSelectorFragment);
                } else {
                    showToast(R.string.trash_validation_mustSelectMeetingPoint);
                }
                break;
            case R.id.create_event_meeting_date_date_from:
                if (!TextUtils.isEmpty(createEventMeetingDateDateFrom.getText())) {
                    try {
                        dateFrom = DateTimeUtils.DATE_TIME_FORMAT.parse(createEventMeetingDateDateFrom.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                showDateTimePickerDialog(true, dateFrom);
                break;
            case R.id.create_event_meeting_date_date_to:
                if (!TextUtils.isEmpty(createEventMeetingDateDateTo.getText())) {
                    try {
                        dateTo = DateTimeUtils.DATE_TIME_FORMAT.parse(createEventMeetingDateDateTo.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                showDateTimePickerDialog(false, dateTo);
                break;
            case R.id.create_event_btn:
                boolean validate = true;
                if (meetingPoint == null) {
                    validate = false;
                }
                if (TextUtils.isEmpty(createEventAboutName.getText())) {
                    validate = false;
                    createEventAboutName.setError(getString(R.string.event_validation_checkName));
                }
                if (!ViewUtils.isValidEmail(createEventContactEmail.getText())) {
                    validate = false;
                    createEventContactEmail.setError(getString(R.string.event_validation_checkEmail));
                }
                if (TextUtils.isEmpty(createEventContactPhone.getText())) {
                    validate = false;
                    createEventContactPhone.setError(getString(R.string.event_validation_checkPhone));
                }
                if (dateFrom == null) {
                    validate = false;
                    createEventMeetingDateDateFrom.setError(getString(R.string.event_validation_durationInvalid));
                }
                if (dateTo == null) {
                    validate = false;
                    createEventMeetingDateDateTo.setError(getString(R.string.event_validation_durationInvalid));
                }
                if (selectedTrashOnMap == null || selectedTrashOnMap.isEmpty()) {
                    validate = false;
                    showToast(R.string.event_validation_selectTrash);
                }

                if (validate) {
                    String name = createEventAboutName.getText().toString();
                    String phone = TextUtils.isEmpty(createEventContactPhone.getText()) ? null : createEventContactPhone.getText().toString();
                    String email = TextUtils.isEmpty(createEventContactEmail.getText()) ? null : createEventContactEmail.getText().toString();
                    Contact contact = new Contact(phone, email);
                    int duration = 0;
                    if (dateTo != null)
                        duration = (int) Math.abs(DateTimeUtils.getDifferenceInMinutes(dateFrom, dateTo));
                    Gps gps = Gps.createGPSFromLatLng(meetingPoint);
                    String description = TextUtils.isEmpty(createEventAboutDescription.getText()) ? null : createEventAboutDescription.getText().toString();
                    String have = TextUtils.isEmpty(createEventEquipmentHave.getText()) ? null : createEventEquipmentHave.getText().toString();
                    String bring = TextUtils.isEmpty(createEventEquipmentBring.getText()) ? null : createEventEquipmentBring.getText().toString();

                    showProgressDialog();
                    Event event = Event.createNewEvent(name, gps, description, dateFrom, duration, bring, have, contact, selectedTrashOnMap, user.getId());
                    CreateEventService.startForRequest(getActivity(), CREATE_EVENT_REQUEST_ID, event);
                }
                break;
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return EventCreateFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(CreateEventService.class);
        return serviceClass;
    }

    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == CREATE_EVENT_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                if (getTargetFragment() != null && getTargetFragment() instanceof OnCreateEventListener) {
                    ((OnCreateEventListener) getTargetFragment()).onEventCreated();
                }
                showToast(R.string.event_created);
                finish();
            } else {
                showToast(R.string.event_create_failed);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    private void showDateTimePickerDialog(final boolean isdateFrom, final Date specifiedDate) {

        Calendar calendar = null;
        if (specifiedDate != null) {
            calendar = new GregorianCalendar();
            calendar.setTime(specifiedDate);
        }

        final Calendar finalCalendar = calendar;

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                                .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                        Calendar calendar = Calendar.getInstance();

                                        int year = dialog.getArguments().getInt(BUNDLE_DATE_YEAR);
                                        int monthOfYear = dialog.getArguments().getInt(BUNDLE_DATE_MONTH);
                                        int dayOfMonth = dialog.getArguments().getInt(BUNDLE_DATE_DAY);

                                        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);

                                        if (isdateFrom) {
                                            dateFrom = calendar.getTime();
                                            createEventMeetingDateDateFrom.setText(DateTimeUtils.DATE_TIME_FORMAT.format(calendar.getTime()));
                                            createEventMeetingDateDateFrom.setError(null);
                                        } else {
                                            dateTo = calendar.getTime();
                                            createEventMeetingDateDateTo.setText(DateTimeUtils.DATE_TIME_FORMAT.format(calendar.getTime()));
                                            createEventMeetingDateDateTo.setError(null);
                                        }
                                    }
                                });

                        Bundle b = new Bundle();
                        b.putInt(BUNDLE_DATE_YEAR, year);
                        b.putInt(BUNDLE_DATE_MONTH, monthOfYear);
                        b.putInt(BUNDLE_DATE_DAY, dayOfMonth);

                        if (finalCalendar != null) {
                            int hourOfDay = finalCalendar.get(Calendar.HOUR_OF_DAY);
                            int min = finalCalendar.get(Calendar.MINUTE);

                            rtpd.setStartTime(hourOfDay, min);
                        }

                        rtpd.setArguments(b);
                        rtpd.show(getFragmentManager(), "RadialTimePickerDialogFragment");
                    }
                });

        if (finalCalendar != null) {
            int year = finalCalendar.get(Calendar.YEAR);
            int month = finalCalendar.get(Calendar.MONTH) + 1;
            int day = finalCalendar.get(Calendar.DAY_OF_MONTH);

            cdp.setPreselectedDate(year, month, day);
        }

        cdp.show(getFragmentManager(), "CalendarDatePickerDialogFragment");
    }
}

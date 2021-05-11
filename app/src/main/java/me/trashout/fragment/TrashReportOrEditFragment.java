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
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

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
import me.trashout.PhotoActivity;
import me.trashout.PositionPickerActivity;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Accessibility;
import me.trashout.model.Constants;
import me.trashout.model.Gps;
import me.trashout.model.Trash;
import me.trashout.model.TrashResponse;
import me.trashout.model.User;
import me.trashout.service.CreateTrashService;
import me.trashout.service.UpdateTrashService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.SelectableImageButton;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.GlideApp;
import me.trashout.service.offline.OfflineTrashManager;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class TrashReportOrEditFragment extends BaseFragment implements ITrashFragment, BaseService.UpdateServiceListener {

    private static final String BUNDLE_EDIT_TRASH = "BUNDLE_EDIT_TRASH";
    private static final String BUNLDE_CLEANED = "BUNLDE_CLEANED";
    private static final String BUNDLE_STILL_HERE = "BUNDLE_STILL_HERE";
    private static final String BUNDLE_MORE = "BUNDLE_MORE";
    private static final String BUNDLE_LESS = "BUNDLE_LESS";

    private static final int UPDATE_TRASH_MIN_DISTANCE = 100;

    private static final int CREATE_TRASH_REQUEST_ID = 450;
    private static final int UPDATE_TRASH_REQUEST_ID = 451;
    private static final int LOCATION_REQUEST_CODE = 6;
    private static final int TAKEN_PHOTO = 33;
    private static final int PICK_POSITION = 34;

    @BindView(R.id.trash_report_take_image_fab)
    FloatingActionButton trashReportTakeImageFab;
    @BindView(R.id.trash_report_images_layout)
    LinearLayout trashReportImagesLayout;
    @BindView(R.id.trash_report_size)
    TextView trashReportSize;
    @BindView(R.id.trash_report_size_bag_btn)
    ImageButton trashReportSizeBagBtn;
    @BindView(R.id.trash_report_size_wheelbarrow_btn)
    ImageButton trashReportSizeWheelbarrowBtn;
    @BindView(R.id.trash_report_size_car_btn)
    ImageButton trashReportSizeCarBtn;
    @BindView(R.id.trash_report_type)
    TextView trashReportType;
    @BindView(R.id.trash_report_type_household_btn)
    SelectableImageButton trashReportTypeHouseholdBtn;
    @BindView(R.id.trash_report_type_automotive_btn)
    SelectableImageButton trashReportTypeAutomotiveBtn;
    @BindView(R.id.trash_report_type_construction_btn)
    SelectableImageButton trashReportTypeConstructionBtn;
    @BindView(R.id.trash_report_type_plastic_btn)
    SelectableImageButton trashReportTypePlasticBtn;
    @BindView(R.id.trash_report_type_electronic_btn)
    SelectableImageButton trashReportTypeElectronicBtn;
    @BindView(R.id.trash_report_type_organic_btn)
    SelectableImageButton trashReportTypeOrganicBtn;
    @BindView(R.id.trash_report_type_metal_btn)
    SelectableImageButton trashReportTypeMetalBtn;
    @BindView(R.id.trash_report_type_liquid_btn)
    SelectableImageButton trashReportTypeLiquidBtn;
    @BindView(R.id.trash_report_type_dangerous_btn)
    SelectableImageButton trashReportTypeDangerousBtn;
    @BindView(R.id.trash_report_type_dead_animals_btn)
    SelectableImageButton trashReportTypeDeadAnimalsBtn;
    @BindView(R.id.trash_report_type_glass_btn)
    SelectableImageButton trashReportTypeGlassBtn;
    @BindView(R.id.trash_report_accessibility)
    TextView trashReportAccessibility;
    @BindView(R.id.trash_report_accessibility_car_switch)
    SwitchCompat trashReportAccessibilityCarSwitch;
    @BindView(R.id.trash_report_accessibility_in_cave)
    TextView trashReportAccessibilityInCave;
    @BindView(R.id.trash_report_accessibility_in_cave_switch)
    SwitchCompat trashReportAccessibilityInCaveSwitch;
    @BindView(R.id.trash_report_accessibility_under_water)
    TextView trashReportAccessibilityUnderWater;
    @BindView(R.id.trash_report_accessibility_under_water_switch)
    SwitchCompat trashReportAccessibilityUnderWaterSwitch;
    @BindView(R.id.trash_report_accessibility_not_for_general_cleanup)
    TextView trashReportAccessibilityNotForGeneralCleanup;
    @BindView(R.id.trash_report_accessibility_not_for_general_cleanup_switch)
    SwitchCompat trashReportAccessibilityNotForGeneralCleanupSwitch;
    @BindView(R.id.trash_report_accessibility_car)
    TextView trashReportAccessibilityCar;
    @BindView(R.id.trash_report_status)
    TextView trashReportStatus;
    @BindView(R.id.trash_report_status_still_here_switch)
    SwitchCompat trashReportStatusStillHereSwitch;
    @BindView(R.id.trash_report_status_still_here)
    TextView trashReportStatusStillHere;
    @BindView(R.id.trash_report_status_its_cleaned_switch)
    SwitchCompat trashReportStatusCleanedItSwitch;
    @BindView(R.id.trash_report_status_its_cleaned)
    TextView trashReportStatusCleanedIt;
    @BindView(R.id.trash_report_status_card_view)
    CardView trashReportStatusCardView;
    @BindView(R.id.trash_report_location)
    TextView trashReportLocation;
    @BindView(R.id.trash_report_map)
    ImageView trashReportMap;
    @BindView(R.id.trash_report_position)
    TextView trashReportPosition;
    @BindView(R.id.trash_report_place)
    TextView trashReportPlace;
    @BindView(R.id.trash_report_location_card_view)
    CardView trashReportLocationCardView;
    @BindView(R.id.trash_report_Additional_information)
    TextView trashReportAdditionalInformation;
    @BindView(R.id.trash_report_additional_information_edit)
    EditText trashReportAdditionalInformationEdit;
    @BindView(R.id.trash_report_send_anonymously_switch)
    SwitchCompat trashReportSendAnonymouslySwitch;
    @BindView(R.id.trash_report_send_anonymously)
    TextView trashReportSendAnonymously;
    @BindView(R.id.trash_report_take_another_image)
    LinearLayout trashReportTakeAnotherImage;
    @BindView(R.id.trash_report_size_container)
    LinearLayout trashReportSizeContainer;
    @BindView(R.id.trash_report_type_container)
    TableLayout trashReportTypeContainer;
    @BindView(R.id.trash_report_accessibility_card_view)
    CardView trashReportAccessibilityCardView;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.trash_report_toolbar_back)
    ImageView trashReportToolbarBack;
    @BindView(R.id.trash_report_toolbar)
    Toolbar trashReportToolbar;
    @BindView(R.id.trash_report_location_better_accuracy_distance)
    TextView trashReportLocationBetterAccuracyDistance;
    @BindView(R.id.trash_report_location_better_accuracy_card_view)
    CardView trashReportLocationBetterAccuracyCardView;
    @BindView(R.id.trash_report_status_cleaned_by_me_switch)
    SwitchCompat trashReportStatusCleanedByMeSwitch;
    @BindView(R.id.trash_report_status_cleaned_by_me)
    TextView trashReportStatusCleanedByMe;
    @BindView(R.id.trash_report_take_images_text)
    TextView trashReportTakeImagesText;
    @BindView(R.id.trash_detail_photo_count)
    TextView photoCountTextView;
    @BindView(R.id.edit_location)
    ImageView editLocation;

    private Trash mTrash;
    private Gson gson;

    private ArrayList<Uri> photos = new ArrayList<>();

    private LatLng mLastLocation;

    private User user;

    private TrashListFragment.OnRefreshTrashListListener mCallback;
    private OnTrashChangedListener onTrashChangedListener;
    private OnDashboardChangedListener onDashboardChangedListener;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private float bestAccuracy = 100;

    public interface OnTrashChangedListener {
        void onTrashChanged();
    }

    public interface OnDashboardChangedListener {
        void onDashboardChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (TrashListFragment.OnRefreshTrashListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        try {
            onTrashChangedListener = (OnTrashChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrashChangedListener");
        }

        try {
            onDashboardChangedListener = (OnDashboardChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDashboardChangedListener");
        }
    }

    public static TrashReportOrEditFragment newInstance(Trash trash, boolean cleaned, boolean stillHere, boolean more, boolean less) {
        Bundle b = new Bundle();
        b.putParcelable(BUNDLE_EDIT_TRASH, trash);
        b.putBoolean(BUNLDE_CLEANED, cleaned);
        b.putBoolean(BUNDLE_STILL_HERE, stillHere);
        b.putBoolean(BUNDLE_MORE, more);
        b.putBoolean(BUNDLE_LESS, less);
        TrashReportOrEditFragment ret = new TrashReportOrEditFragment();
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
//        setHasOptionsMenu(true);
        gson = new Gson();
        openCamera();

        mLastLocation = ((MainActivity) getActivity()).getLastPosition();
        if (mLastLocation != null) {
            Log.d(TAG, "GPS: " + mLastLocation.longitude + " " + mLastLocation.latitude + " - INIT (last position)");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_report_or_edit, container, false);
        ButterKnife.bind(this, view);

        // hide edit location icon for editing trash
        if (getTrash() != null) {
            editLocation.setVisibility(GONE);
        }

        user = PreferencesHandler.getUserData(getContext());
        if (user == null)
            ((MainActivity) getActivity()).signInAnonymously();

        getLocation();

        trashReportToolbar.inflateMenu(R.menu.menu_trash_edit);
        trashReportToolbar.getMenu().findItem(R.id.action_send);
        trashReportToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_send) {
                    if (validateNewData()) {
                        showProgressDialog();
                        String note = !TextUtils.isEmpty(trashReportAdditionalInformationEdit.getText()) ? trashReportAdditionalInformationEdit.getText().toString() : "";
                        Trash trash;
                        if (getTrash() == null) {
                            Gps gps = Gps.createGPSFromLatLng(mLastLocation);
                            gps.setAccuracy((int) bestAccuracy);
                            trash = Trash.createNewTrash(gps, note, getSelectedTrashSize(), getSelectedTrashType(), getAccessibility(), trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        } else if (isTrashStillHere()) {
                            trash = Trash.createStillHereUpdateTrash(getTrash().getId(), getTrash().getGps(), Constants.TrashStatus.STILL_HERE, note, getSelectedTrashSize(), getSelectedTrashType(), getAccessibility(), trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        } else if (isTrashMore()) {
                            trash = Trash.createStillHereUpdateTrash(getTrash().getId(), getTrash().getGps(), Constants.TrashStatus.MORE, note, getSelectedTrashSize(), getSelectedTrashType(), getAccessibility(), trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        } else if (isTrashLess()) {
                            trash = Trash.createStillHereUpdateTrash(getTrash().getId(), getTrash().getGps(), Constants.TrashStatus.LESS, note, getSelectedTrashSize(), getSelectedTrashType(), getAccessibility(), trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        } else if (isTrashCleaned()) {
                            trash = Trash.createCleanedUpdateTrash(getTrash().getId(), getTrash().getGps(), getTrash().getSize(), getTrash().getTypes(), getTrash().getAccessibility(), trashReportStatusCleanedByMeSwitch.isChecked(), note, trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        } else {
                            trash = Trash.createUpdateTrash(getTrash().getId(), getTrash().getGps(), note, getSelectedTrashSize(), getSelectedTrashType(), getAccessibility(), trashReportSendAnonymouslySwitch.isChecked(), user.getId());
                        }

                        if (isNetworkAvailable()) {
                            if (getTrash() == null) {
                                CreateTrashService.startForRequest(getContext(), CREATE_TRASH_REQUEST_ID, trash, photos);
                            } else {
                                UpdateTrashService.startForRequest(getContext(), UPDATE_TRASH_REQUEST_ID, getTrash().getId(), trash, photos);
                            }
                        } else {
                            OfflineTrashManager offlineTrashManager = new OfflineTrashManager(getContext());
                            offlineTrashManager.add(trash, photos, getTrash() != null);
                            dismissProgressDialog();
                            getBaseActivity().replaceFragment(new ThankYouFragmentOfflineTrash(), false);
                        }
                    }
                }
                return false;
            }
        });

        setupData(getTrash(), isTrashCleaned(), isTrashStillHere(), isTrashMore(), isTrashLess());

        pager.setAdapter(new PhotoPagerAdapter(getContext()));
        pager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.photo_page_margin));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getTrash() == null) {
            startLocationUpdatesIfNeed();
        }
    }

    private void startLocationUpdatesIfNeed() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "GPS changed: " + location.getLongitude() + " " + location.getLatitude() + "     ...     " + location.getAccuracy());

                if (bestAccuracy > location.getAccuracy()) {
                    bestAccuracy = location.getAccuracy();
                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "Found better GPS: " + mLastLocation.longitude + " " + mLastLocation.latitude + "     ...     " + location.getAccuracy());

                    setPosition(mLastLocation.latitude, mLastLocation.longitude);
                    setPlaceAddress(mLastLocation);

                    if (location.getAccuracy() < 2) {
                        Log.d(TAG, "Location Manager STOP Updates (good accuracy)");
                        locationManager.removeUpdates(locationListener);
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (bestAccuracy >=2 ) {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "Location Manager START Updates");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
            } else {
                Log.d(TAG, "GPS provider is not available");
                showToast(R.string.trash_create_noGps);
            }
        }
    }

    //    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_trash_edit, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.action_send:
//                return true;
//            default:
//                break;
//        }
//
//        return false;
//    }

    /**
     * Get trash
     *
     * @return
     */
    private Trash getTrash() {
        if (mTrash == null)
            mTrash = getArguments().getParcelable(BUNDLE_EDIT_TRASH);
        return mTrash;
    }

    private int getMinPhotoCountToUpdate() {
//        return getTrash() == null ? 2 : 1;
        getTrash();
        return 1;
    }

    /**
     * Get if edit detail for clean
     *
     * @return
     */
    private boolean isTrashCleaned() {
        return getArguments().getBoolean(BUNLDE_CLEANED, false);
    }

    /**
     * Get if edit detail for still here
     *
     * @return
     */
    private boolean isTrashStillHere() {
        return getArguments().getBoolean(BUNDLE_STILL_HERE, false);
    }

    private boolean isTrashMore() {
        return getArguments().getBoolean(BUNDLE_MORE, false);
    }

    private boolean isTrashLess() {
        return getArguments().getBoolean(BUNDLE_LESS, false);
    }

    /**
     * Setup data trash and edit type
     *
     * @param trash
     * @param cleaned
     * @param stillHere
     */
    private void setupData(Trash trash, boolean cleaned, boolean stillHere, boolean more, boolean less) {


//        trashReportTakeImagesText.setText(getTrash() != null ? R.string.take_at_least_1_photos : R.string.take_at_least_2_photos);
        getTrash();
        trashReportTakeImagesText.setText(R.string.trash_create_takeLeastOnePhoto);

        if (trash != null) {

            if (cleaned) {
                trashReportSize.setVisibility(GONE);
                trashReportSizeContainer.setVisibility(GONE);
                trashReportType.setVisibility(GONE);
                trashReportTypeContainer.setVisibility(GONE);
                trashReportAccessibility.setVisibility(GONE);
                trashReportAccessibilityCardView.setVisibility(GONE);

                trashReportStatusStillHereSwitch.setVisibility(GONE);
                trashReportStatusStillHere.setVisibility(GONE);
                trashReportStatusCleanedByMeSwitch.setVisibility(View.VISIBLE);
                trashReportStatusCleanedItSwitch.setChecked(true);
                trashReportStatusCleanedItSwitch.setClickable(false);
                trashReportMap.setVisibility(GONE);

            } else if (stillHere) {
                trashReportStatus.setVisibility(GONE);
                trashReportStatusCardView.setVisibility(GONE);
                trashReportMap.setVisibility(GONE);
            } else if (more) {
                trashReportStatus.setVisibility(GONE);
                trashReportStatusCardView.setVisibility(GONE);
                trashReportMap.setVisibility(GONE);
            } else if (less) {
                trashReportStatus.setVisibility(GONE);
                trashReportStatusCardView.setVisibility(GONE);
                trashReportMap.setVisibility(GONE);
            }


            if (trash.getSize().equals(Constants.TrashSize.BAG)) {
                trashReportSizeBagBtn.setSelected(true);
            } else if (trash.getSize().equals(Constants.TrashSize.WHEELBARROW)) {
                trashReportSizeWheelbarrowBtn.setSelected(true);
            } else if (trash.getSize().equals(Constants.TrashSize.CAR)) {
                trashReportSizeCarBtn.setSelected(true);
            }

            if (trash.getTypes() != null && !trash.getTypes().isEmpty()) {
                trashReportTypeAutomotiveBtn.setSelected(trash.getTypes().contains(Constants.TrashType.AUTOMOTIVE));
                trashReportTypeConstructionBtn.setSelected(trash.getTypes().contains(Constants.TrashType.CONSTRUCTION));
                trashReportTypeDangerousBtn.setSelected(trash.getTypes().contains(Constants.TrashType.DANGEROUS));
                trashReportTypeElectronicBtn.setSelected(trash.getTypes().contains(Constants.TrashType.ELECTRICS));
                trashReportTypeHouseholdBtn.setSelected(trash.getTypes().contains(Constants.TrashType.DOMESTIC));
                trashReportTypeLiquidBtn.setSelected(trash.getTypes().contains(Constants.TrashType.LIQUID));
                trashReportTypeMetalBtn.setSelected(trash.getTypes().contains(Constants.TrashType.METAL));
                trashReportTypeOrganicBtn.setSelected(trash.getTypes().contains(Constants.TrashType.ORGANIC));
                trashReportTypePlasticBtn.setSelected(trash.getTypes().contains(Constants.TrashType.PLASTIC));
                trashReportTypeDeadAnimalsBtn.setSelected(trash.getTypes().contains(Constants.TrashType.DEAD_ANIMALS));
            }

            if (trash.getAccessibility().isByCar()) {
                trashReportAccessibilityCarSwitch.setChecked(true);
            }
            if (trash.getAccessibility().isInCave()) {
                trashReportAccessibilityInCaveSwitch.setChecked(true);
            }
            if (trash.getAccessibility().isUnderWater()) {
                trashReportAccessibilityUnderWaterSwitch.setChecked(true);
            }
            if (trash.getAccessibility().isNotForGeneralCleanup()) {
                trashReportAccessibilityNotForGeneralCleanupSwitch.setChecked(true);
            }

            trashReportPlace.setText(trash.getGps().getArea().getFormatedLocation());
            setPosition(trash.getGps().getLat(), trash.getGps().getLng());

        } else if (mLastLocation != null) {
            trashReportStatus.setVisibility(GONE);
            trashReportStatusCardView.setVisibility(GONE);

            setPosition(mLastLocation.latitude, mLastLocation.longitude);
            setPlaceAddress(mLastLocation);
        } else {
            trashReportStatus.setVisibility(GONE);
            trashReportStatusCardView.setVisibility(GONE);
            trashReportLocationCardView.setVisibility(GONE);
        }
    }

    private void setPosition(double lat, double lng) {
        trashReportPosition.setText(PositionUtils.getFormattedLocation(getContext(), lat, lng));

        if (trashReportMap.getVisibility() == View.VISIBLE) {
            String mapUrl = PositionUtils.getStaticMapUrl(getActivity(), lat, lng);
            try {
                URI mapUri = new URI(mapUrl.replace("|", "%7c"));
                Log.d(TAG, "setupDumpData: mapUrl = " + String.valueOf(mapUri.toURL()));
                GlideApp.with(this)
                        .load(String.valueOf(mapUri.toURL()))
                        .centerCrop()
                        .dontTransform()
                        .into(trashReportMap);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * retrun selected trash size
     *
     * @return
     */
    private Constants.TrashSize getSelectedTrashSize() {
        Constants.TrashSize trashSize = null;
        if (trashReportSizeWheelbarrowBtn.isSelected()) {
            trashSize = Constants.TrashSize.WHEELBARROW;
        } else if (trashReportSizeCarBtn.isSelected()) {
            trashSize = Constants.TrashSize.CAR;
        } else if (trashReportSizeBagBtn.isSelected()) {
            trashSize = Constants.TrashSize.BAG;
        }
        return trashSize;
    }


    private ArrayList<Constants.TrashType> getSelectedTrashType() {
        ArrayList<Constants.TrashType> selectedTrashTypes = new ArrayList<>();

        if (trashReportTypeHouseholdBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.DOMESTIC);
        if (trashReportTypeAutomotiveBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.AUTOMOTIVE);
        if (trashReportTypeConstructionBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.CONSTRUCTION);
        if (trashReportTypePlasticBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.PLASTIC);
        if (trashReportTypeElectronicBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.ELECTRICS);
        if (trashReportTypeOrganicBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.ORGANIC);
        if (trashReportTypeMetalBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.METAL);
        if (trashReportTypeLiquidBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.LIQUID);
        if (trashReportTypeDangerousBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.DANGEROUS);
        if (trashReportTypeDeadAnimalsBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.DEAD_ANIMALS);
        if (trashReportTypeGlassBtn.isSelected())
            selectedTrashTypes.add(Constants.TrashType.GLASS);

        return selectedTrashTypes;
    }

    private Accessibility getAccessibility() {
        Accessibility accessibility = new Accessibility();

        accessibility.setByCar(trashReportAccessibilityCarSwitch.isChecked());
        accessibility.setInCave(trashReportAccessibilityInCaveSwitch.isChecked());
        accessibility.setUnderWater(trashReportAccessibilityUnderWaterSwitch.isChecked());
        accessibility.setNotForGeneralCleanup(trashReportAccessibilityNotForGeneralCleanupSwitch.isChecked());

        return accessibility;
    }

    private Constants.TrashStatus getStatus() {
        Constants.TrashStatus trashStatus = Constants.TrashStatus.STILL_HERE;

        return trashStatus;
    }


    @OnClick({R.id.trash_report_size_bag_btn, R.id.trash_report_size_wheelbarrow_btn, R.id.trash_report_size_car_btn})
    public void onTrashSizeClick(View view) {

        trashReportSizeBagBtn.setSelected(false);
        trashReportSizeCarBtn.setSelected(false);
        trashReportSizeWheelbarrowBtn.setSelected(false);
        switch (view.getId()) {
            case R.id.trash_report_size_bag_btn:
                view.setSelected(true);
                break;
            case R.id.trash_report_size_wheelbarrow_btn:
                view.setSelected(true);
                break;
            case R.id.trash_report_size_car_btn:
                view.setSelected(true);
                break;
        }
    }

    @OnClick({R.id.trash_report_type_household_btn, R.id.trash_report_type_automotive_btn, R.id.trash_report_type_construction_btn, R.id.trash_report_type_plastic_btn, R.id.trash_report_type_electronic_btn, R.id.trash_report_type_organic_btn, R.id.trash_report_type_metal_btn, R.id.trash_report_type_liquid_btn, R.id.trash_report_type_dangerous_btn, R.id.trash_report_type_dead_animals_btn, R.id.trash_report_type_glass_btn})
    public void onClick(View view) {
        view.setSelected(!view.isSelected());
        switch (view.getId()) {
            case R.id.trash_report_type_household_btn:
                break;
            case R.id.trash_report_type_automotive_btn:
                break;
            case R.id.trash_report_type_construction_btn:
                break;
            case R.id.trash_report_type_plastic_btn:
                break;
            case R.id.trash_report_type_electronic_btn:
                break;
            case R.id.trash_report_type_organic_btn:
                break;
            case R.id.trash_report_type_metal_btn:
                break;
            case R.id.trash_report_type_liquid_btn:
                break;
            case R.id.trash_report_type_dangerous_btn:
                break;
            case R.id.trash_report_type_dead_animals_btn:
                break;
            case R.id.trash_report_type_glass_btn:
                break;
        }
    }

    @OnClick({R.id.trash_report_location_card_view})
    public void onLocationClick(View view) {
        if (getTrash() != null) {
            return; // disable for edit
        }

        Intent intent = new Intent(getContext(), PositionPickerActivity.class);
        intent.putExtra("latlng", mLastLocation);
        getActivity().startActivityForResult(intent, PICK_POSITION);
    }

    private void getLocation() {
        if (this.getActivity() != null &&
                ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "validateNewData: permission check");
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_REQUEST_CODE);
        } else {
            if (mLastLocation != null) {
                trashReportLocationBetterAccuracyDistance.setVisibility(GONE);
                setPosition(mLastLocation.latitude, mLastLocation.longitude);
            }
        }
    }

    @OnClick(R.id.trash_report_take_image_fab)
    public void openCamera() {
        Intent intent = new Intent(getContext(), PhotoActivity.class);
        getActivity().startActivityForResult(intent, TAKEN_PHOTO);
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);

        if (requestCode == TAKEN_PHOTO && resultCode == RESULT_OK) {
            onPhotosReturned(Collections.singletonList(data.getData()));
        } else if (requestCode == PICK_POSITION && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                mLastLocation = extras.getParcelable("latlng");
                bestAccuracy = 0;
                Log.d(TAG, "Picked GPS: " + mLastLocation.longitude + ", " + mLastLocation.latitude);

                Log.d(TAG, "Location Manager STOP Updates (picked manually)");
                locationManager.removeUpdates(locationListener);

                setPosition(mLastLocation.latitude, mLastLocation.longitude);
                setPlaceAddress(mLastLocation);
            }
        }
    }

    private void setPlaceAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        new GeocoderTask(geocoder, latLng.latitude, latLng.longitude, new GeocoderTask.Callback() {
            @Override
            public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                Log.d(TAG, "geocoderResult  = " + geocoderResult);
                if (geocoderResult.getAddress() != null) {
                    trashReportPlace.setText(geocoderResult.getFormattedAddress());
                }
            }
        }).execute();
    }

    /**
     * Setting new photos from camera/gallery to view
     *
     * @param newPhotos
     */
    private void onPhotosReturned(List<Uri> newPhotos) {
        Log.d(TAG, "onPhotosReturned: " + newPhotos);
        photos.addAll(newPhotos);
        photoCountTextView.setText(String.valueOf(photos.size()));

        pager.setVisibility(View.VISIBLE);
        pager.getAdapter().notifyDataSetChanged();
        pager.setCurrentItem(pager.getChildCount() - 1);

        if (photos.size() < getMinPhotoCountToUpdate()) {
            trashReportTakeAnotherImage.setVisibility(View.VISIBLE);
        } else {
            trashReportTakeAnotherImage.setVisibility(View.GONE);
        }
        trashReportImagesLayout.setVisibility(GONE);

        if (photos.size() > 0) {
//            trashReportToolbarBack.setImageResource(R.drawable.ic_ab_back);
//            sendMenuItem.setIcon(R.drawable.ic_ab_send);
        }
    }

    /**
     * Validate entered data - e.g. - photos, position, ...
     *
     * @return
     */
    private boolean validateNewData() {
        boolean isValidate = true;

        if (photos.size() < getMinPhotoCountToUpdate()) {
            showToast(String.format(getString(R.string.trash_edit_minPhotosText), getMinPhotoCountToUpdate()));
            return false;
        }

        if (getSelectedTrashSize() == null) {
            showToast(R.string.trash_validation_sizeRequired);
            isValidate = false;
        } else if (getSelectedTrashType().isEmpty()) {
            isValidate = false;
            showToast(R.string.trash_validation_typeRequired);
        } else if (getTrash() != null) {
            if (mLastLocation != null) {
                if (isValidate = checkUpdatedTrashDistance(getTrash().getPosition(), mLastLocation)) {
                    isValidate = true;
                } else {
                    showToast(R.string.trash_edit_greaterDistanceText);
                    isValidate = false;
                }
            } else {
                showToast("No Location. Please allow location provider");
                getLocation();
                isValidate = false;
            }
        } else if (mLastLocation == null) {
            showToast("No Location. Please allow location provider");
            isValidate = false;
            getLocation();
        }

        return isValidate;
    }

    /**
     * Check, if trash position is nearby than 100m
     *
     * @param trashDistance
     * @param mLastLocation
     * @return
     */
    private boolean checkUpdatedTrashDistance(LatLng trashDistance, LatLng mLastLocation) {
        return PositionUtils.computeDistance(trashDistance, new LatLng(mLastLocation.latitude, mLastLocation.longitude)) < UPDATE_TRASH_MIN_DISTANCE;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationManager != null) {
            Log.d(TAG, "Location Manager STOP Updates (if need)");
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.trash_report_toolbar_back)
    public void onBackClick() {
        finish();
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return TrashReportOrEditFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(CreateTrashService.class);
        serviceClass.add(UpdateTrashService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == CREATE_TRASH_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                showToast(R.string.trash_create_success);
                mCallback.onRefreshTrashList();
                if (onDashboardChangedListener != null) {
                    onDashboardChangedListener.onDashboardChanged();
                }
                redirectToSharePage(apiResult);
            } else {
                showToast(R.string.trash_validation_createFailed);
            }
        } else if (apiResult.getRequestId() == UPDATE_TRASH_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                showToast(R.string.trash_update_success);
                mCallback.onRefreshTrashList();
                if (onTrashChangedListener != null) {
                    onTrashChangedListener.onTrashChanged();
                }
                redirectToSharePage(apiResult);
            } else {
                showToast(R.string.trash_validation_updateFailed);
            }
        }
    }

    private void redirectToSharePage(ApiResult apiResult) {
        try {
            TrashResponse trashResponse = gson.fromJson(((ResponseBody) apiResult.getResponse().body()).string(), TrashResponse.class);
            ThankYouFragmentTrash thankYouFragmentTrash = ThankYouFragmentTrash.newInstance(trashResponse);
            getBaseActivity().replaceFragment(thankYouFragmentTrash, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }


    private class PhotoPagerAdapter extends PagerAdapter {

        private final Context context;

        PhotoPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            container.setBackgroundColor(Color.BLACK);
            ImageView photoView = new ImageView(container.getContext());
            photoView.setAdjustViewBounds(true);
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            GlideApp.with(context)
                    .load(photos.get(position))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(photoView);

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }
}

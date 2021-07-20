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
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetTrashListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Trash;
import me.trashout.service.GetTrashListService;
import me.trashout.service.base.BaseService;

public class EventTrashSelectorFragment extends BaseFragment implements BaseService.UpdateServiceListener, ITrashFragment {

    private static final int GET_TRASH_LIST_REQUEST_ID = 202;
    private static final String BUNDLE_MEETING_POINT = "BUNDLE_MEETING_POINT";
    private static final String BUNDLE_SELECTED_TRASH_LIST = "BUNDLE_SELECTED_TRASH_LIST";

    @BindView(R.id.my_location_fab)
    FloatingActionButton myLocationFab;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private ArrayList<Trash> mTrashList = new ArrayList<>();

    private HashMap<Marker, Trash> markersTrashMap = new HashMap<>();
    private ArrayList<Long> selectedTrashIdList = new ArrayList<>();

    private LatLng mMeetingPoint;

    private EventCreateFragment.OnSelectTrashIdsOnMapListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (EventCreateFragment.OnSelectTrashIdsOnMapListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static EventTrashSelectorFragment newInstance(LatLng meetingPoint, ArrayList<Long> selectedTrashIdList) {
        Bundle b = new Bundle();
        b.putParcelable(BUNDLE_MEETING_POINT, meetingPoint);
        if (selectedTrashIdList != null && !selectedTrashIdList.isEmpty()) {
            long[] selectedTrashIdArray = new long[selectedTrashIdList.size()];
            for (int i = 0; i < selectedTrashIdArray.length; i++) {
                selectedTrashIdArray[i] = selectedTrashIdList.get(i);
            }
            b.putLongArray(BUNDLE_SELECTED_TRASH_LIST, selectedTrashIdArray);
        }
        EventTrashSelectorFragment ret = new EventTrashSelectorFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_trash_selector, container, false);
        ButterKnife.bind(this, view);

        selectedTrashIdList = getSelectedTrashIdList();
        if (selectedTrashIdList == null) {
            selectedTrashIdList = new ArrayList<>();
        }

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
        }
        if (!checkMapFragment()) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mapContainer, mMapFragment, "event_trash_map");
            fragmentTransaction.commit();
        }

        preSetupMapIfNeeded();

        GetTrashListService.startForEventTrashMapRequest(getActivity(), GET_TRASH_LIST_REQUEST_ID, null, getMeetingPoint());
        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event_select_trash, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                if (selectedTrashIdList != null && !selectedTrashIdList.isEmpty() && mCallback != null) {
                    mCallback.onTrashIdsOnMapSelected(selectedTrashIdList);
                    finish();
                }
                return true;
            default:
                break;
        }

        return false;
    }

    private LatLng getMeetingPoint() {
        if (mMeetingPoint == null)
            mMeetingPoint = getArguments().getParcelable(BUNDLE_MEETING_POINT);
        return mMeetingPoint;
    }

    private ArrayList<Long> getSelectedTrashIdList() {
        ArrayList<Long> lastSelectedTrashIds = new ArrayList<>();
        long[] selectgadTrasArray = getArguments().getLongArray(BUNDLE_SELECTED_TRASH_LIST);
        if (selectgadTrasArray != null) {
            for (int i = 0; i < selectgadTrasArray.length; i++) {
                lastSelectedTrashIds.add(selectgadTrasArray[i]);
            }
        }
        return lastSelectedTrashIds;
    }

    /**
     * Check if map fragment was created
     *
     * @return
     */
    private boolean checkMapFragment() {
        Fragment mFragment = getChildFragmentManager().findFragmentByTag("map");
        return mFragment != null && mFragment instanceof SupportMapFragment;
    }

    /**
     * Prepare map and setup map data
     */
    private void preSetupMapIfNeeded() {
        if (mMap == null) {
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMapIfNeeded();
                    prepareMapObject(mTrashList);

                    if (getMeetingPoint() != null) {
                        goToMeetingPoint(getMeetingPoint());
                    } else {
                        goToMyLocation();
                    }
                }
            });
        } else {
            prepareMapObject(mTrashList);

            if (getMeetingPoint() != null) {
                goToMeetingPoint(getMeetingPoint());
            } else {
                goToMyLocation();
            }
        }
    }

    private void prepareMapObject(ArrayList<Trash> trashList) {
        if (mMap == null)
            return;

        Marker marker;

        mMap.clear();

        if (trashList != null) {
            for (Trash trash : trashList) {
                if (trash.getPosition() == null) {
                    continue;
                }
                marker = mMap.addMarker(createMarkerOptions(trash));
                markersTrashMap.put(marker, trash);
            }
        }

        mMap.addMarker(new MarkerOptions()
                .position(getMeetingPoint())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_meeting_point)));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Trash markerTrash = markersTrashMap.get(marker);
                if (markerTrash == null) {
                    return false;
                }
                if (selectedTrashIdList.contains(markerTrash.getId())) {
                    selectedTrashIdList.remove(markerTrash.getId());
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_reported));
                } else {
                    selectedTrashIdList.add(markerTrash.getId());
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_reported_selected));
                }
                return false;
            }
        });
    }

    private MarkerOptions createMarkerOptions(Trash trash) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(trash.getPosition())
                .icon(BitmapDescriptorFactory.fromResource(selectedTrashIdList.contains(trash.getId()) ? R.drawable.ic_map_marker_reported_selected : R.drawable.ic_map_marker_reported));
        return markerOptions;
    }


    /**
     * setup map and check location permission
     */
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "setUpMapIfNeeded: permission check");

                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        1);

                return;
            } else {
                mMap.setMyLocationEnabled(true);
            }

            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            setUpMapIfNeeded();
        } else if (requestCode == 2) {
            goToMyLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle("Nearby dumps");
    }

    private void goToMeetingPoint(LatLng meetingPoint) {
        if (mMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(meetingPoint, 15);
            mMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * Go to my actual position
     */
    private void goToMyLocation() {

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "setUpMapIfNeeded: permission check");
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    2);
        } else {
            GoogleApiClient mGoogleApiClient = ((MainActivity) getActivity()).getGoogleApiClient();
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
            }
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return EventTrashSelectorFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetTrashListService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_TRASH_LIST_REQUEST_ID) {

            if (apiResult.isValidResponse()) {
                ApiGetTrashListResult apiGetTrashListResult = (ApiGetTrashListResult) apiResult.getResult();
                mTrashList = new ArrayList<>(apiGetTrashListResult.getTrashList());
                prepareMapObject(mTrashList);
            } else {
                showToast(R.string.global_fetchError);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.my_location_fab)
    public void onClick() {
        goToMyLocation();
    }
}

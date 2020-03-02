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
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.GeocellUtils;
import com.beoui.geocell.model.BoundingBox;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetTrashListResult;
import me.trashout.api.result.ApiGetZoomPointListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Trash;
import me.trashout.model.TrashFilter;
import me.trashout.model.TrashMapItem;
import me.trashout.model.ZoomPoint;
import me.trashout.service.GetTrashListService;
import me.trashout.service.GetZoomPointListService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.map.OnCameraIdleMultiListener;

public class TrashMapFragment extends BaseFragment implements BaseService.UpdateServiceListener, ITrashFragment {


    private static final int GET_ZOOMPOIN_LIST_REQUEST_ID = 201;
    private static final int GET_TRASH_LIST_REQUEST_ID = 202;

    @BindView(R.id.my_location_fab)
    FloatingActionButton myLocationFab;
    @BindView(R.id.add_dump_fab)
    FloatingActionButton addDumpFab;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private ClusterManager<TrashMapItem> mClusterManager;
    private OnCameraIdleMultiListener onCameraIdleMultiListener = new OnCameraIdleMultiListener();

    private float lastZoom = 0;
    private LatLng lastPosition = null;
    private TrashFilter trashFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_map, container, false);
        ButterKnife.bind(this, view);

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
        }
        if (!checkMapFragment()) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
            fragmentTransaction.commit();
        }

        preSetupMapIfNeeded();

        trashFilter = PreferencesHandler.getTrashFilterData(getContext());

        if (!isNetworkAvailable()) {
            showToast(R.string.global_internet_offline);
        }

        onCameraIdleMultiListener.addOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                refreshMapData(false);
            }
        });

        return view;
    }

    private void refreshMapData(boolean forceRefresh) {
        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        boolean bigSwipe = (lastPosition != null && GeocellUtils.distance(lastPosition, curScreen.getCenter()) > 100000);

        Log.d(TAG, "onCameraIdle: OnCreate - mMap zoom = " + mMap.getCameraPosition().zoom + ", curScreen = " + curScreen + ", lastposition = " + lastPosition + ", bigSwipe = " + bigSwipe + ", distance = " + (lastPosition != null ? GeocellUtils.distance(lastPosition, curScreen.getCenter()) : ""));

        Log.d(TAG, "onCameraIdle: curScreen = " + curScreen);
        BoundingBox boundingBox = new BoundingBox(curScreen.northeast.latitude, curScreen.northeast.longitude, curScreen.southwest.latitude, curScreen.southwest.longitude);

        List<String> geocells = GeocellManager.getMapCells(boundingBox, PositionUtils.getResolutionForMapZoomLevel((int) mMap.getCameraPosition().zoom));
        // Use this for best geocell?
//                List<String> geocells = GeocellManager.bestBboxSearchCells(boundingBox, null);

        Log.d(TAG, "onCameraIdle: GeoCell = " + geocells);

        if (lastZoom != mMap.getCameraPosition().zoom || bigSwipe || forceRefresh) {
            lastZoom = mMap.getCameraPosition().zoom;
            lastPosition = curScreen.getCenter();

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }

            if (isNetworkAvailable()) {
                if (mMap.getCameraPosition().zoom > 9) {
                    GetTrashListService.startForMapTrashRequest(getActivity(), GET_TRASH_LIST_REQUEST_ID, trashFilter, geocells);
                } else {
                    GetZoomPointListService.startForRequest(getActivity(), GET_ZOOMPOIN_LIST_REQUEST_ID, trashFilter, geocells, (int) mMap.getCameraPosition().zoom);
                }
            } else {
                showToast(R.string.global_internet_offline);
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trash_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_filter:
                TrashFilterFragment trashFilterFragment = new TrashFilterFragment();
                getBaseActivity().replaceFragment(trashFilterFragment);
                return true;

            default:
                break;
        }

        return false;
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
                    prepareMapObject();
                }
            });
        }
    }

    private void prepareMapObject() {
        goToMyLocation(true);
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

            mMap.setOnCameraIdleListener(onCameraIdleMultiListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                setUpMapIfNeeded();
            } else if (requestCode == 2) {
                setUpMapIfNeeded();
                goToMyLocation(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle("");
    }

    @OnClick({R.id.my_location_fab, R.id.add_dump_fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_location_fab:
                goToMyLocation(false);
                break;
            case R.id.add_dump_fab:
                addDump();
                break;
        }
    }

    public void addDump() {
        if (isNetworkAvailable()) {
            TrashReportOrEditFragment trashReportOrEditFragment = new TrashReportOrEditFragment();
            getBaseActivity().replaceFragment(trashReportOrEditFragment);
        } else {
            showToast(R.string.global_internet_error_offline);
        }
    }

    /**
     * Go to my actual position
     */
    private void goToMyLocation(boolean afterStart) {

        if (getActivity() != null &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
//                TRASH-1113
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, afterStart ? 6 : 14);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                if (mMap != null) {
                    mMap.animateCamera(cameraUpdate);
                }
            }
        }
    }

    public void onRefreshTrashMap() {
        preSetupMapIfNeeded();
        trashFilter = PreferencesHandler.getTrashFilterData(getContext());
        refreshMapData(true);
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return TrashMapFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetTrashListService.class);
        serviceClass.add(GetZoomPointListService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_ZOOMPOIN_LIST_REQUEST_ID) {

            if (apiResult.isValidResponse()) {
                ApiGetZoomPointListResult apiGetZoomPointListResult = (ApiGetZoomPointListResult) apiResult.getResult();
                List<TrashMapItem> trashMapItems = new ArrayList<>();
                trashMapItems.addAll(apiGetZoomPointListResult.getZoomPoints());
                setTrashList(trashMapItems);
            } else {
                showToast(R.string.global_fetchError);
            }

        } else if (apiResult.getRequestId() == GET_TRASH_LIST_REQUEST_ID) {

            if (apiResult.isValidResponse()) {
                ApiGetTrashListResult apiGetTrashListResult = (ApiGetTrashListResult) apiResult.getResult();
                List<TrashMapItem> trashMapItems = new ArrayList<>();
                trashMapItems.addAll(apiGetTrashListResult.getTrashList());
                setTrashList(trashMapItems);
            } else {
                showToast(R.string.global_fetchError);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    /**
     * Setup trash list on map
     *
     * @param trashMapItems
     */
//    TODO: JAKUB BREHUV - check other way
//    min 16 but kitkat - 19
    private void setTrashList(final List<TrashMapItem> trashMapItems) {
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(getActivity(), mMap);
            mClusterManager.setRenderer(new TrashRenderer());
            onCameraIdleMultiListener.addOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<TrashMapItem>() {
                @Override
                public boolean onClusterItemClick(TrashMapItem trashMapItem) {
                    if (trashMapItem instanceof Trash) {
                        Trash trash = (Trash) trashMapItem;
                        TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(trash.getId());
                        getBaseActivity().replaceFragment(trashDetailFragment);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            mClusterManager.clearItems();
        }

        mClusterManager.addItems(trashMapItems);
        mClusterManager.cluster();
    }

    @Override
    public void onDestroy() {
        if (mClusterManager != null) {
            mClusterManager.clearItems();
        }
        super.onDestroy();
    }

    /**
     * Custom cluster renderer
     */
    private class TrashRenderer extends DefaultClusterRenderer<TrashMapItem> {
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());

        private final int[] BUCKETS = {2, 3, 4, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000};

        private final TextView mClusterText;

        private final AppCompatImageView mClusterPoint1;
        private final AppCompatImageView mClusterPoint2;
        private final AppCompatImageView mClusterPoint3;

        private int colorGreen;
        private int colorRed;
        private int colorYellow;


        public TrashRenderer() {
            super(getActivity(), mMap, mClusterManager);

            View mClusterView = getBaseActivity().getLayoutInflater().inflate(R.layout.layout_map_cluster, null);
            mClusterIconGenerator.setContentView(mClusterView);
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_map_cluster));

            mClusterText = mClusterView.findViewById(R.id.cluster_text);
            mClusterPoint1 = mClusterView.findViewById(R.id.cluster_point_1);
            mClusterPoint2 = mClusterView.findViewById(R.id.cluster_point_2);
            mClusterPoint3 = mClusterView.findViewById(R.id.cluster_point_3);

            if (getContext() != null) {
                colorGreen = ContextCompat.getColor(getContext(), R.color.cluster_color_green);
                colorRed = ContextCompat.getColor(getContext(), R.color.cluster_color_red);
                colorYellow = ContextCompat.getColor(getContext(), R.color.cluster_color_red);
            } else {
                colorGreen = Color.GREEN;
                colorRed = Color.RED;
                colorYellow = Color.RED;
            }
        }

        @Override
        protected void onBeforeClusterItemRendered(TrashMapItem trashMapItem, MarkerOptions markerOptions) {
            // Draw a single dump.

            int dumpMarkerIconRes = R.drawable.ic_trash_status_unknown_red;

            if (trashMapItem instanceof Trash) {

                Trash trash = (Trash) trashMapItem;

                if (trash.getStatus() != null) {
                    switch (trash.getStatus()) {
                        case STILL_HERE:
                        case LESS:
                        case MORE:
                            dumpMarkerIconRes = trash.isUpdateNeeded() ? R.drawable.ic_trash_status_unknown_red : R.drawable.ic_trash_status_remain;
                            break;
                        case CLEANED:
                            dumpMarkerIconRes = R.drawable.ic_trash_status_clean;
                            break;
                        default:
                            dumpMarkerIconRes = R.drawable.ic_trash_status_unknown_red;
                            break;
                    }
                } else {
                    dumpMarkerIconRes = R.drawable.ic_trash_status_unknown_red;
                }

                markerOptions.icon(BitmapDescriptorFactory.fromResource(dumpMarkerIconRes));

            } else if (trashMapItem instanceof ZoomPoint) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getClusterIcon(trashMapItem.getTotal(), trashMapItem.getCleaned(), trashMapItem.getRemains(), trashMapItem.getUpdateNeeded())));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(dumpMarkerIconRes));
            }

        }

        //
        @Override
        protected void onBeforeClusterRendered(Cluster<TrashMapItem> cluster, MarkerOptions markerOptions) {
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).

            int totalMapItems = 0;
            int cleanedMapItems = 0;
            int remainMapItems = 0;
            int updateNeededMapItems = 0;

            for (TrashMapItem trashMapItem : cluster.getItems()) {
                totalMapItems += trashMapItem.getTotal();
                cleanedMapItems += trashMapItem.getCleaned();
                remainMapItems += trashMapItem.getRemains();
                updateNeededMapItems += trashMapItem.getUpdateNeeded();
            }

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getClusterIcon(totalMapItems, cleanedMapItems, remainMapItems, updateNeededMapItems)));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<TrashMapItem> cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

        protected String getClusterText(int bucket) {

            if (bucket <= BUCKETS[0]) {
                return String.valueOf(bucket);
            } else {
                for (int i = 0; i < BUCKETS.length - 1; ++i) {
                    if (bucket < BUCKETS[i + 1]) {
                        if (BUCKETS[i] >= 1000)
                            return String.valueOf(BUCKETS[i] / 1000) + "k" + ((BUCKETS[i] == bucket) ? "" : "+");
                        return String.valueOf(BUCKETS[i]) + ((BUCKETS[i] == bucket) ? "" : "+");
                    }
                }
                return String.valueOf(BUCKETS[BUCKETS.length - 1] / 1000) + "k+";
            }
        }

        protected int getBucket(int clusterSize) {
            if (clusterSize <= BUCKETS[0]) {
                return clusterSize;
            } else {
                for (int i = 0; i < BUCKETS.length - 1; ++i) {
                    if (clusterSize < BUCKETS[i + 1]) {
                        return BUCKETS[i];
                    }
                }

                return BUCKETS[BUCKETS.length - 1];
            }
        }

        /**
         * Stav skládek v clusteru
         * Tři zelené tečky (všechny skládky v clusteru mají status=cleaned)
         * Tři červené tečky (všechny skládky v clusteru mají status=stillHere, more nebo less a zároveň updatedNeeded=false)
         * Tři žluté tečky (všechny skládky v clusteru mají updatedNeeded=true)
         * Dvě zelené a jedna červená tečka (v clusteru existuje alespoň jedna skládka v clusteru, která má status=stillHere nebo more nebo less a zároveň updateNeeded=false, ale je více nebo stejně skládek, které mají status=cleaned, neexistuje žádná skládka, která má updateNeeded=true)
         * Jedna zelená a dvě červené tečky (v clusteru existuje alespoň jedna skládka v clusteru, která má status=cleaned, ale je více skládek, které mají status=stillHere, more nebo less, neexistuje žádná skládka, která má updateNeeded=true)
         * Jedna zelená, jedna červená a jedna žlutá tečka (v clusteru existuje alespoň jedna skládka se status=cleaned, existuje alespoň jedna skládka se status=stillHere, more nebo less a zároveň updatedNeeded=false , a existuje alespoň jedna skládka s updatedNeeded=true)
         * Dvě zelené a jedna žlutá tečka (v clusteru existuje alespoň jedna skládka s updatedNeeded=true, ale je více nebo stejně skládek, které mají status=cleaned)
         * Jedna zelená a dvě žluté tečky (v clusteru existuje alespoň jedna skládka, která má status=cleaned, ale je více skládek, které mají updatedNeeded=true)
         * Dvě červené a jedna žlutá tečka (v clusteru existuje alespoň jedna skládka s updatedNeeded=true, ale je více nebo stejně skládek, které mají status=stillHere, more nebo less a zároveň updatedNeeded=false)
         * Jedna červená a dvě žluté tečky (v clusteru existuje alespoň jedna skládka, která má status=stillHere, more nebo less a zároveň updatedNeeded=false, ale je více skládek, které mají updatedNeeded=true)
         */

        private Bitmap getClusterIcon(int total, int cleaned, int remains, int updateNeeded) {

            int imageResourceArray[] = new int[3];

            int computeGreenDot = 0;
            int computeRedDot = 0;
            int computeYellowDot = 0;

            if (cleaned > 0) {
                computeGreenDot++;
            } else if ((remains - updateNeeded) >= updateNeeded) {
                computeRedDot++;
            } else {
                computeYellowDot++;
            }

            if (remains > updateNeeded) {
                computeRedDot++;
            } else if (cleaned > updateNeeded) {
                computeGreenDot++;
            } else {
                computeYellowDot++;
            }

            if (updateNeeded > 0) {
                computeYellowDot++;
            } else if (cleaned >= remains) {
                computeGreenDot++;
            } else {
                computeRedDot++;
            }

            int i = 0;
            for (; i < computeGreenDot; i++) {
                imageResourceArray[i] = colorGreen;
            }

            for (; i < (computeGreenDot + computeRedDot); i++) {
                imageResourceArray[i] = colorRed;
            }

            for (; i < (computeGreenDot + computeRedDot + computeYellowDot); i++) {
                imageResourceArray[i] = colorYellow;
            }

//            Log.d(TAG, "getClusterIcon: ============================================ \n total = " + total + ", cleaned = " + cleaned + ", remains = " + remains + ", updateNeeded = " + updateNeeded + "\n computeGreenDot = " + computeGreenDot + ", computeRedDot = " + computeRedDot + ", computeYellowDot = " + computeYellowDot + "\n =========================================");


            ImageViewCompat.setImageTintList(mClusterPoint1, ColorStateList.valueOf(imageResourceArray[0]));
            ImageViewCompat.setImageTintList(mClusterPoint2, ColorStateList.valueOf(imageResourceArray[1]));
            ImageViewCompat.setImageTintList(mClusterPoint3, ColorStateList.valueOf(imageResourceArray[2]));

            mClusterText.setText(this.getClusterText(total));

            return mClusterIconGenerator.makeIcon(String.valueOf(total));
        }
    }

}

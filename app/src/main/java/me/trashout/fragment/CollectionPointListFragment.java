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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.adapter.CollectionPoinListAdapter;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.request.ApiGetCollectionPointListRequest;
import me.trashout.api.result.ApiGetCollectionPointListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ICollectionPointFragment;
import me.trashout.model.CollectionPoint;
import me.trashout.model.CollectionPointFilter;
import me.trashout.model.Constants;
import me.trashout.service.GetCollectionPointListService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.EmptyRecyclerView;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 08.12.2016
 */
public class CollectionPointListFragment extends BaseFragment implements ICollectionPointFragment, CollectionPoinListAdapter.OnCollectionPointItemClickListener, BaseService.UpdateServiceListener {

    private static final int GET_COLLECTION_POINT_LIST_REQUEST_ID = 801;

    @BindView(R.id.recyclerview)
    EmptyRecyclerView recyclerview;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    @BindView(R.id.add_collection_point_fab)
    FloatingActionButton addCollectionPointFab;

    private LatLng lastPosition;

    private boolean needRefresh = false;

    private ArrayList<CollectionPoint> collectionPointList;
    private CollectionPoinListAdapter mAdapter;
    private LayoutInflater inflater;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    public interface OnRefreshCollectionPointListListener {
        void onRefreshCollectionPointList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_points_list, container, false);
        this.inflater = inflater;
        ButterKnife.bind(this, view);

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission check");
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        } else {
            getLastPosition();
        }

        recyclerview.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        emptyView.setText(R.string.collectionPoint_zeroNearbyAvailable);
        recyclerview.setEmptyView(emptyView, false);

        recyclerview.setProgressView(progressWheel);

        recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = recyclerview.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems + Configuration.LIST_LIMIT_SIZE) && !(totalItemCount > 0 && totalItemCount < Configuration.LIST_LIMIT_SIZE)) {
                        loadData((collectionPointList.size() / Configuration.LIST_LIMIT_SIZE) + 1);
                        loading = true;
                    }
                }
            }
        });

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onCreateView - swiperefresh");
                loadData(1);
            }
        });
        swiperefresh.setColorSchemeResources(R.color.colorPrimary);

        if (collectionPointList == null || collectionPointList.isEmpty() || needRefresh) {
            if (collectionPointList == null || needRefresh) {
                collectionPointList = new ArrayList<>();
                setCollectionPointList(collectionPointList);
            }
            needRefresh = false;
            loadData(1);
        } else {
            setCollectionPointList(collectionPointList);
        }

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_collection_points_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_filter:
                CollectionPointFilterFragment collectionPointFilterFragment = new CollectionPointFilterFragment();
                getBaseActivity().replaceFragment(collectionPointFilterFragment);
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 3) {
            getLastPosition();
        }
    }

    private void getLastPosition() {
        LatLng lastLatLng = ((MainActivity) getActivity()).getLastPosition();
        if (lastLatLng != null) {
            lastPosition = lastLatLng;
            if (mAdapter != null)
                mAdapter.setLastPosition(lastPosition);
        }
    }

    /**
     * Refresh collection point list after change filter
     */
    public void onRefreshCollectionPointList() {
        needRefresh = true;
    }

    /**
     * Setting collection point list
     *
     * @param collectionPointList
     */
    public void setCollectionPointList(ArrayList<CollectionPoint> collectionPointList) {
        mAdapter = new CollectionPoinListAdapter(getContext(), collectionPointList, this, lastPosition);
        recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onCollectionPointClick(CollectionPoint collectionPoint) {
        CollectionPointDetailFragment collectionPointDetailFragment = CollectionPointDetailFragment.newInstance(collectionPoint.getId());
        getBaseActivity().replaceFragment(collectionPointDetailFragment);
    }

    /**
     * Load dumps data from server
     */
    public void loadData(int page) {
        if (!isNetworkAvailable()) {
            showToast(R.string.global_internet_offline);

            if (!isNetworkAvailable()) {
                showToast(R.string.global_internet_offline);

                if (swiperefresh.isRefreshing()) {
                    swiperefresh.setRefreshing(false);
                }
                return;
            }

            return;
        }

        if (page == 1) {
            if (mLayoutManager != null && mLayoutManager.getItemCount() < 1)
                recyclerview.setLoading(true);
            previousTotal = 0;
            loading = true;
        }
        CollectionPointFilter collectionPointFilter = PreferencesHandler.getCollectionPointFilterData(getContext());
        GetCollectionPointListService.startForRequest(getActivity(), GET_COLLECTION_POINT_LIST_REQUEST_ID, collectionPointFilter, lastPosition, page);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.global_menu_collectionPoints));
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return CollectionPointListFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetCollectionPointListService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_COLLECTION_POINT_LIST_REQUEST_ID) {
            dismissProgressDialog();

            if (swiperefresh != null && swiperefresh.isRefreshing())
                swiperefresh.setRefreshing(false);

            if (apiResult.isValidResponse()) {
                ApiGetCollectionPointListResult apiGetCollectionPointListResult = (ApiGetCollectionPointListResult) apiResult.getResult();
                ApiGetCollectionPointListRequest apiGetCollectionPointListRequest = (ApiGetCollectionPointListRequest) apiResult.getRequest();

                if (collectionPointList == null) {
                    collectionPointList = new ArrayList<>();
                    setCollectionPointList(collectionPointList);
                }
                if (apiGetCollectionPointListRequest.getApiGetCollectionPointListParam().getPage() < 1)
                    collectionPointList.clear();
                collectionPointList.addAll(apiGetCollectionPointListResult.getCollectionPoints());
                mAdapter.notifyDataSetChanged();
            } else {
                showToast(R.string.global_fetchError);
            }

            recyclerview.setLoading(false);
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.add_collection_point_fab)
    public void onClick() {
        addCollectionPoint();
    }

    public void addCollectionPoint() {
        if (isNetworkAvailable()) {

            Bundle params = new Bundle();
            params.putString("add_collection_point_button_clicked", "clicked");
            mFirebaseAnalytics.logEvent("add_collection_point_button", params);

            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.home_recycling_point_add_new_tittle)
                    .content(R.string.home_recycling_point_add_new_redirect)
                    .positiveText(R.string.home_recycling_point_add_new_go_to_web)
                    .negativeText(R.string.home_recycling_point_add_new_do_later)
                    .autoDismiss(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Utils.browseUrl(getActivity(), Constants.ADD_RECYCLING_POINT);
                        }
                    })
                    .build();

            dialog.show();
        } else {
            showToast(R.string.global_internet_error_offline);
        }
    }

}

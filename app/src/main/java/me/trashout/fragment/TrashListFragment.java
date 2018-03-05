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

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.maps.model.LatLng;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.adapter.TrashListAdapter;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.request.ApiGetTrashListRequest;
import me.trashout.api.result.ApiGetTrashListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Trash;
import me.trashout.model.TrashFilter;
import me.trashout.service.GetTrashListService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.EmptyRecyclerView;
import me.trashout.utils.PreferencesHandler;

public class TrashListFragment extends BaseFragment implements BaseService.UpdateServiceListener, TrashListAdapter.OnDumpItemClickListener, ITrashFragment {


    private static final int GET_TRASH_LIST_REQUEST_ID = 101;

    private static final String BUNDLE_TRASH_HUNTER = "BUNDLE_TRASH_HUNTER";
    private static final String BUNDLE_TRASH_HUNTER_AREA = "BUNDLE_TRASH_HUNTER_AREA";

    @BindView(R.id.recyclerview)
    EmptyRecyclerView recyclerview;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.add_dump_fab)
    FloatingActionButton addDumpFab;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    private ArrayList<Trash> trashList;
    private TrashListAdapter mAdapter;
    private LatLng lastPosition;

    private boolean needRefresh = false;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int previousTotal = 0;

    private Boolean mTrashHunterMode;
    private Integer mTrashHunterArea;

    public interface OnRefreshTrashListListener {
        void onRefreshTrashList();
    }

    public static TrashListFragment newInstance(boolean trashHunter, int area) {
        TrashListFragment ret = new TrashListFragment();
        ret.setArguments(generateBundle(trashHunter, area));
        return ret;
    }

    public static Bundle generateBundle(boolean trashHunter, int area) {
        Bundle b = new Bundle();
        b.putBoolean(BUNDLE_TRASH_HUNTER, trashHunter);
        b.putInt(BUNDLE_TRASH_HUNTER_AREA, area);
        return b;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_list, container, false);
        ButterKnife.bind(this, view);

        setLastPosition();

        recyclerview.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        emptyView.setText(R.string.trash_message_noNearByTrash);
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
                        loadData(trashList.size() / Configuration.LIST_LIMIT_SIZE);
                        loading = true;
                    }
                }
            }
        });

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onCreateView - swiperefresh");
                setLastPosition();
                refreshData();            }
        });
        swiperefresh.setColorSchemeResources(R.color.colorPrimary);

        if (trashList == null || trashList.isEmpty() || needRefresh) {
            if (trashList == null || needRefresh) {
                trashList = new ArrayList<>();
                setTrashList(trashList);
            }
            needRefresh = false;

            refreshData();

        } else {
            setTrashList(trashList);
        }

        Log.d(TAG, "onCreateView: isTrashHunterMode = " + isTrashHunterMode());

        getBaseActivity().onBackStackChanged();

        return view;
    }

    private Boolean isTrashHunterMode() {
        if (mTrashHunterMode == null)
            mTrashHunterMode = getArguments().getBoolean(BUNDLE_TRASH_HUNTER, false);
        return mTrashHunterMode;
    }

    private Integer getTrashHunterArea() {
        if (mTrashHunterArea == null)
            mTrashHunterArea = getArguments().getInt(BUNDLE_TRASH_HUNTER_AREA, -1);
        return mTrashHunterArea;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trash_list, menu);
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

    private void setLastPosition() {
        lastPosition = ((MainActivity) getActivity()).getLastPosition();
    }

    private void refreshData() {
        if (lastPosition != null) {
            if (mAdapter != null)
                mAdapter.setLastPosition(lastPosition);
            loadData(0);
        }
    }

    /**
     * Refresh trash list after change filter
     */
    public void onRefreshTrashList() {
        needRefresh = true;
        mTrashHunterMode = false;
    }

    /**
     * Setting trash list
     *
     * @param trashList
     */
    public void setTrashList(ArrayList<Trash> trashList) {
        mAdapter = new TrashListAdapter(getContext(), trashList, this, lastPosition);
        recyclerview.setAdapter(mAdapter);
    }


    @Override
    public void onDumpClick(Trash trash) {
        Log.d(TAG, "onDumpClick: " + trash);
        TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(trash.getId());
        getBaseActivity().replaceFragment(trashDetailFragment);
    }

    /**
     * Load trash data from server
     */
    public void loadData(int page) {
        if (page == 0) {
            if (mLayoutManager != null && mLayoutManager.getItemCount() < 1)
                recyclerview.setLoading(true);
            previousTotal = 0;
            loading = true;
        }

        if (isTrashHunterMode()) {
            GetTrashListService.startForTrashHunterRequest(getActivity(), GET_TRASH_LIST_REQUEST_ID, lastPosition, getTrashHunterArea(), page);
        } else {
            TrashFilter trashFilter = PreferencesHandler.getTrashFilterData(getContext());
            GetTrashListService.startForRequest(getActivity(), GET_TRASH_LIST_REQUEST_ID, trashFilter, lastPosition, page);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle("");
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return TrashListFragment.this;
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
            dismissProgressDialog();

            if (swiperefresh != null && swiperefresh.isRefreshing())
                swiperefresh.setRefreshing(false);

            if (apiResult.isValidResponse()) {
                ApiGetTrashListResult apiGetTrashListResult = (ApiGetTrashListResult) apiResult.getResult();
                ApiGetTrashListRequest apiGetTrashListRequest = (ApiGetTrashListRequest) apiResult.getRequest();

                if (trashList == null) {
                    trashList = new ArrayList<>();
                    setTrashList(trashList);
                }
                if (apiGetTrashListRequest.getApiGetTrashListParam().getPage() < 1)
                    trashList.clear();
                trashList.addAll(apiGetTrashListResult.getTrashList());
                mAdapter.notifyDataSetChanged();
            }

            recyclerview.setLoading(false);
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.add_dump_fab)
    public void onClick() {
        TrashReportOrEditFragment trashReportOrEditFragment = new TrashReportOrEditFragment();
        getBaseActivity().replaceFragment(trashReportOrEditFragment);
    }
}

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.adapter.UserActivityListAdapter;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetUserActivityResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.model.User;
import me.trashout.model.UserActivity;
import me.trashout.service.GetUserActivityService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.EmptyRecyclerView;
import me.trashout.utils.PreferencesHandler;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 21.02.2017
 */
public class UserActivityListFragment extends BaseFragment implements IProfileFragment, BaseService.UpdateServiceListener {

    private static final int GET_USER_ACTIVITY_LIST_REQUEST_ID = 660;

    @BindView(R.id.recyclerview)
    EmptyRecyclerView recyclerview;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    private ArrayList<UserActivity> userActivityList;
    private UserActivityListAdapter mAdapter;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_activity_list, container, false);
        ButterKnife.bind(this, view);

        user = PreferencesHandler.getUserData(getContext());

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        emptyView.setText(R.string.home_noUserActivities);
        recyclerview.setEmptyView(emptyView, false);

        recyclerview.setProgressView(progressWheel);

        recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onCreateView - swiperefresh");
                loadData();
            }
        });
        swiperefresh.setColorSchemeResources(R.color.colorPrimary);

        if (userActivityList == null || userActivityList.isEmpty()) {
            loadData();
        } else {
            setUserActivityList(userActivityList);
        }

        return view;
    }

    private void loadData() {
        showProgressDialog();
        GetUserActivityService.startForRequest(getContext(), GET_USER_ACTIVITY_LIST_REQUEST_ID, user.getId());
    }

    public void setUserActivityList(ArrayList<UserActivity> userActivityList) {
        mAdapter = new UserActivityListAdapter(getContext(), userActivityList, ((MainActivity) getActivity()).getLastPosition(), new UserActivityListAdapter.IClickOnActivityItem() {
            @Override
            public void onClick(int itemId) {
                TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(itemId);
                getBaseActivity().replaceFragment(trashDetailFragment);
            }
        });
        recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.home_userActivities));
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return UserActivityListFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetUserActivityService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_USER_ACTIVITY_LIST_REQUEST_ID) {
            dismissProgressDialog();

            if (swiperefresh != null && swiperefresh.isRefreshing())
                swiperefresh.setRefreshing(false);

            if (apiResult.isValidResponse()) {
                ApiGetUserActivityResult apiGetNewsListResult = (ApiGetUserActivityResult) apiResult.getResult();

                if (userActivityList == null) {
                    userActivityList = new ArrayList<>();
                    setUserActivityList(userActivityList);
                }

                userActivityList.clear();
                userActivityList.addAll(apiGetNewsListResult.getUserActivities());
                mAdapter.notifyDataSetChanged();

            }
            recyclerview.setLoading(false);
        }

    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

}

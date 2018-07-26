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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.adapter.NewsListAdapter;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.request.ApiGetNewsListRequest;
import me.trashout.api.result.ApiGetNewsListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.INewsFragment;
import me.trashout.model.News;
import me.trashout.service.GetNewsListService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.EmptyRecyclerView;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 08.12.2016
 */
public class NewsListFragment extends BaseFragment implements INewsFragment, NewsListAdapter.OnNewsItemClickListener, BaseService.UpdateServiceListener {

    private static final int GET_NEWS_LIST_REQUEST_ID = 831;

    @BindView(R.id.recyclerview)
    EmptyRecyclerView recyclerview;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    private boolean needRefresh = false;

    private ArrayList<News> newsList;
    private NewsListAdapter mAdapter;
    private LayoutInflater inflater;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int previousTotal = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        this.inflater = inflater;
        ButterKnife.bind(this, view);

        recyclerview.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        emptyView.setText(R.string.news_empty);
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
                    /*if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems + Configuration.LIST_LIMIT_SIZE) && !(totalItemCount > 0 && totalItemCount < Configuration.LIST_LIMIT_SIZE)) {
                        loadData(newsList.size() / Configuration.LIST_LIMIT_SIZE);
                        loading = true;
                    }*/
                    if (!loading && (pastVisiblesItems + visibleItemCount) >= totalItemCount) {
                        loadData((newsList.size() / Configuration.NEWS_LIST_LIMIT_SIZE) + 1);
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

        if (newsList == null || newsList.isEmpty() || needRefresh) {
            if (newsList == null || needRefresh) {
                newsList = new ArrayList<>();
            }
            needRefresh = false;
            loadData(1);
        }

        mAdapter = new NewsListAdapter(getContext(), newsList, this);
        recyclerview.setAdapter(mAdapter);

        return view;
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
     * @param newsList
     */
    /*public void setNewsList(ArrayList<News> newsList) {
        Collections.sort(newsList, new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                return o2.getCreated().compareTo(o1.getCreated());
            }
        });

    }*/
    @Override
    public void onNewsClick(News news) {
        NewsDetailFragment collectionPointDetailFragment = NewsDetailFragment.newInstance(news.getId());
        getBaseActivity().replaceFragment(collectionPointDetailFragment);
    }

    /**
     * Load dumps data from server
     */
    public void loadData(int page) {
        if (page == 1) {
            if (newsList.size() < 1) {
                recyclerview.setLoading(true);
                previousTotal = 0;
                loading = true;
                GetNewsListService.startForRequest(getActivity(), GET_NEWS_LIST_REQUEST_ID, page);
            } else {
                recyclerview.setLoading(false);
                loading = false;
                swiperefresh.setRefreshing(false);
            }
        } else {
            if (newsList.size() < Configuration.NEWS_LIST_LIMIT_SIZE) {
                recyclerview.setLoading(false);
                loading = false;
                swiperefresh.setRefreshing(false);
            } else {
                GetNewsListService.startForRequest(getActivity(), GET_NEWS_LIST_REQUEST_ID, page);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.tab_news));
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return NewsListFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetNewsListService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_NEWS_LIST_REQUEST_ID) {
            dismissProgressDialog();

            if (swiperefresh != null && swiperefresh.isRefreshing())
                swiperefresh.setRefreshing(false);

            if (apiResult.isValidResponse()) {
                ApiGetNewsListResult apiGetNewsListResult = (ApiGetNewsListResult) apiResult.getResult();
                ApiGetNewsListRequest apiGetNewsListRequest = (ApiGetNewsListRequest) apiResult.getRequest();

                if (apiGetNewsListRequest != null && apiGetNewsListResult != null) {

                    if (newsList == null) {
                        newsList = new ArrayList<>();
                    }
                    if (apiGetNewsListRequest.getPage() < 1) {
                        newsList.clear();
                    }

                    if (apiGetNewsListResult.getNewsList() != null && !apiGetNewsListResult.getNewsList().isEmpty()) {
                        for (News newNews : apiGetNewsListResult.getNewsList()) {
                            boolean match = false;
                            for (News oldNews : newsList) {
                                if (newNews.getId() == oldNews.getId()) {
                                    match = true;
                                    break;
                                }
                            }
                            if (!match) {
                                newsList.add(newNews);
                            }
                        }
                    } else {
                        GetNewsListService.startForRequest(getActivity(), GET_NEWS_LIST_REQUEST_ID, -1);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            } else {
                getBaseActivity().showToast(R.string.global_error_api_text);
            }

            recyclerview.setLoading(false);
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }
}

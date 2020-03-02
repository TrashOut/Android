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

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetHomeScreenDataResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Constants;
import me.trashout.model.Event;
import me.trashout.model.Image;
import me.trashout.model.News;
import me.trashout.model.Trash;
import me.trashout.model.TrashHunterState;
import me.trashout.model.User;
import me.trashout.model.UserActivity;
import me.trashout.service.GetHomeScreenDataService;
import me.trashout.service.IRemoteService;
import me.trashout.service.ITrashHunterChangeListener;
import me.trashout.service.JoinUserToEventService;
import me.trashout.service.TrashHunterService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.SquareImageView;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GeocoderTask;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PositionUtils;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import me.trashout.utils.ViewUtils;

import static android.content.Context.BIND_AUTO_CREATE;

public class DashboardFragment extends BaseFragment implements BaseService.UpdateServiceListener, TrashHunterStartFragment.OnTrashHunterStartListener {

    private static final int GET_HOME_SCREEN_DATA_REQUEST_ID = 550;
    private static final int JOIN_TO_EVENT_REQUEST_ID = 551;
    private static final String EVENT_ID_TAG = "Event_id:";

    private static final int TRASH_HUNTER_TRASH_COUNT = 1;
    private static final int TRASH_HUNTER_CHANGE_STATE = 2;

    @BindView(R.id.dashboard_nearest_trash_title)
    TextView homeNearestTrashTitle;
    @BindView(R.id.dashboard_nearest_trash_more)
    TextView homeNearestTrashMore;
    @BindView(R.id.dashboard_nearest_trash_first_image)
    SquareImageView homeNearestTrashFirstImage;
    @BindView(R.id.dashboard_nearest_trash_first_distance)
    TextView homeNearestTrashFirstDistance;
    @BindView(R.id.dashboard_nearest_trash_first)
    FrameLayout homeNearestTrashFirst;
    @BindView(R.id.dashboard_nearest_trash_second_image)
    SquareImageView homeNearestTrashSecondImage;
    @BindView(R.id.dashboard_nearest_trash_Second_distance)
    TextView homeNearestTrashSecondDistance;
    @BindView(R.id.dashboard_nearest_trash_second)
    FrameLayout homeNearestTrashSecond;
    @BindView(R.id.dashboard_nearest_collection_point_title)
    TextView homeNearestCollectionPointTitle;
    @BindView(R.id.dashboard_nearest_collection_point_more)
    TextView homeNearestCollectionPointMore;
    @BindView(R.id.dashboard_nearest_collection_point_dustbin_image)
    ImageView homeNearestCollectionPointDustbinImage;
    //@BindView(R.id.dashboard_nearest_collection_point_dustbin_name)
    //TextView homeNearestCollectionPointDustbinName;
    @BindView(R.id.dashboard_nearest_collection_point_dustbin_distance)
    TextView homeNearestCollectionPointDustbinDistance;
    @BindView(R.id.dashboard_nearest_collection_point_dustbin_card_view)
    CardView homeNearestCollectionPointDustbinCardView;
    @BindView(R.id.dashboard_nearest_collection_point_scrapyard_image)
    ImageView homeNearestCollectionPointScrapyardImage;
    //@BindView(R.id.dashboard_nearest_collection_point_scrapyard_name)
    //TextView homeNearestCollectionPointScrapyardName;
    @BindView(R.id.dashboard_nearest_collection_point_scrapyard_distance)
    TextView homeNearestCollectionPointScrapyardDistance;
    @BindView(R.id.dashboard_nearest_collection_point_scrapyard_card_view)
    CardView homeNearestCollectionPointScrapyardCardView;
    @BindView(R.id.dashboard_nearest_trash_title_layout)
    LinearLayout homeNearestTrashTitleLayout;
    @BindView(R.id.dashboard_nearest_trash_title_description)
    TextView homeNearestTrashTitleDescription;
    @BindView(R.id.dashboard_nearest_trash_container)
    LinearLayout homeNearestTrashContainer;
    @BindView(R.id.dashboard_nearest_collection_point_title_layout)
    LinearLayout dashboardNearestCollectionPointTitleLayout;
    @BindView(R.id.dashboard_statistic_title)
    TextView dashboardStatisticTitle;
    @BindView(R.id.dashboard_statistics_more)
    TextView dashboardStatisticMore;
    @BindView(R.id.dashboard_statistic_title_layout)
    LinearLayout dashboardStatisticTitleLayout;
    @BindView(R.id.dashboard_statistic_area)
    TextView dashboardStatisticArea;
    @BindView(R.id.dashboard_statistics_reported)
    TextView dashboardStatisticsReported;
    @BindView(R.id.dashboard_statistics_cleaned)
    TextView dashboardStatisticsCleaned;
    @BindView(R.id.dashboard_trash_hunter_title_description)
    TextView dashboardTrashHunterTitleDescription;
    @BindView(R.id.dashboard_trash_hunter_progress_bar)
    CircularProgressBar dashboardTrashHunterProgressBar;
    @BindView(R.id.dashboard_trash_hunter_found_dumps)
    TextView dashboardTrashHunterFoundDumps;
    @BindView(R.id.dashboard_trash_hunter_more)
    TextView dashboardTrashHunterMore;
    @BindView(R.id.dashboard_trash_hunter_on_layout)
    RelativeLayout dashboardTrashHunterOnLayout;
    @BindView(R.id.dashboard_trash_hunter_btn)
    AppCompatButton dashboardTrashHunterBtn;
    @BindView(R.id.dashboard_trash_hunter_timer)
    TextView dashboardTrashHunterTimer;
    @BindView(R.id.dashboard_news_section_title)
    TextView dashboardNewsSectionTitle;
    @BindView(R.id.dashboard_news_more)
    TextView dashboardNewsMore;
    @BindView(R.id.dashboard_news_title_layout)
    LinearLayout dashboardNewsTitleLayout;
    @BindView(R.id.dashboard_news_perex)
    TextView dashboardNewsPerex;
    @BindView(R.id.dashboard_news_read_more_btn)
    AppCompatButton dashboardNewsReadMoreBtn;
    @BindView(R.id.dashboard_news_image)
    ImageView dashboardNewsImage;
    @BindView(R.id.dashboard_news_title)
    TextView dashboardNewsTitle;
    @BindView(R.id.dashboard_news_image_container)
    FrameLayout dashboardNewsImageContainer;
    @BindView(R.id.dashboard_news_date)
    TextView dashboardNewsDate;
    @BindView(R.id.dashboard_news_card_view)
    CardView dashboardNewsCardView;
    @BindView(R.id.dashboard_recent_activity_title)
    TextView dashboardRecentActivityTitle;
    @BindView(R.id.dashboard_recent_activity_container)
    LinearLayout dashboardRecentActivityContainer;
    @BindView(R.id.dashboard_recent_activity_card_view)
    CardView dashboardRecentActivityCardView;
    @BindView(R.id.dashboard_events_title)
    TextView dashboardEventsTitle;
    @BindView(R.id.dashboard_events_title_description)
    TextView dashboardEventsTitleDescription;
    @BindView(R.id.dashboard_events_container)
    LinearLayout dashboardEventsContainer;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.layout_hideable_container)
    LinearLayout layoutHideableContainer;
    @BindView(R.id.add_dump_fab)
    FloatingActionButton addDumpFab;
    @BindView(R.id.create_report_layout)
    FrameLayout createReportLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    @BindView(R.id.dashboard_support_btn)
    AppCompatButton dashboardSupportBtn;
    @BindView(R.id.dashboard_order_trash_pickup)
    AppCompatButton orderTrashPickupButton;

    private List<Trash> dashboardTrashList;
    private CollectionPoint dashboardCollectionPointDustbin;
    private CollectionPoint dashboardCollectionPointScrapyard;
    private int dashboardStatisticsCleanedCount = -1;
    private int dashboardStatisticsReportedCount = -1;
    private List<UserActivity> dashboardUserActivityList;
    private News dashboardNews;
    private List<Event> dashboardEventList;

    private LatLng lastPosition;
    private CountDownTimer countDownTimer;
    private boolean hunterMode = false;

    private LayoutInflater inflater;
    private User user;
    private boolean showFab = false;

    private Event joinedEvent;
    private int amountOfFoundTrash;

    int scrollYPosition = 0;
    private boolean needRefresh = true;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity() != null && ((MainActivity) getActivity()).getAuth().getCurrentUser() == null) {
            ((MainActivity) getActivity()).signInAnonymously();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        this.inflater = inflater;
        joinedEvent = null;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        user = PreferencesHandler.getUserData(getContext());

        if (user == null) {
            return view;
        }

        setLastPosition();
        Log.d(TAG, "onCreateView: lastPosition = " + lastPosition);

        if ((dashboardTrashList != null || dashboardCollectionPointDustbin != null || dashboardCollectionPointScrapyard != null) && !needRefresh) {
            if (isAdded()) {
                setupDashboard(dashboardTrashList, dashboardCollectionPointDustbin, dashboardCollectionPointScrapyard, dashboardStatisticsCleanedCount, dashboardStatisticsReportedCount, dashboardUserActivityList, dashboardNews, dashboardEventList);
            }
        } else {
            needRefresh = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDashboardData();
                }
            }, lastPosition != null ? 0 : 300);
        }

        if (scrollYPosition > 0) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, scrollYPosition);
                }
            });
        }

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                showFab();
            }
        });

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onCreateView - swiperefresh");
                getDashboardData();
            }
        });
        swiperefresh.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    public void showFab() {
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (/*!showFab || */createReportLayout.getLocalVisibleRect(scrollBounds) || dashboardStatisticsCleaned.getLocalVisibleRect(scrollBounds)) {
            addDumpFab.hide();
        } else {
            addDumpFab.show();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void addDump() {
        if (isNetworkAvailable()) {
            TrashReportOrEditFragment trashReportOrEditFragment = new TrashReportOrEditFragment();
            getBaseActivity().replaceFragment(trashReportOrEditFragment);
        } else {
            showToast(R.string.global_internet_error_offline);
        }
    }

    @OnClick({R.id.add_dump_fab, R.id.create_report_layout, R.id.create_report_btn})
    public void onClick() {
        addDump();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about:
                AboutFragment aboutFragment = new AboutFragment();
                getBaseActivity().replaceFragment(aboutFragment);
                return true;
            default:
                break;
        }

        return false;
    }

    /**
     * refresh dashboard data
     */
    public void getDashboardData() {
        if (!isNetworkAvailable()) {
            showToast(R.string.global_internet_offline);

            if (swiperefresh.isRefreshing()) {
                swiperefresh.setRefreshing(false);
            }

            setupDashboard(null,
                    null,
                    null,
                    0,
                    0,
                    null,
                    null,
                    null);
            return;
        }

        showProgressDialog();
        setLastPosition();
        GetHomeScreenDataService.startForRequest(getContext(), GET_HOME_SCREEN_DATA_REQUEST_ID, lastPosition, user != null ? user.getId() : -1);

        handleLayoutsVisibility(user != null);
    }

    private void handleLayoutsVisibility(boolean areDataLayoutsVisible) {
        homeNearestTrashContainer.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        dashboardNewsImageContainer.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        dashboardRecentActivityContainer.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        dashboardEventsContainer.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        dashboardNewsCardView.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        homeNearestCollectionPointDustbinCardView.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        homeNearestCollectionPointScrapyardCardView.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        layoutHideableContainer.setVisibility(areDataLayoutsVisible ? View.VISIBLE : View.GONE);
        //showFab = FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous();
        //createReportLayout.setVisibility(showFab ? View.VISIBLE : View.GONE);
        showFab();
        dismissProgressDialog();
    }

    private void setupDashboard(List<Trash> dashboardTrashList, CollectionPoint dashboardCollectionPointDustbin, CollectionPoint dashboardCollectionPointScrapyard, int dashboardStatisticsCleanedCount, int dashboardStatisticsReportedCount, List<UserActivity> dashboardUserActivityList, News dashboardNews, List<Event> dashboardEventList) {
        Log.d(TAG, "setupDashboard: " + dashboardTrashList);
        Log.d(TAG, "setupDashboard: " + dashboardCollectionPointDustbin);
        Log.d(TAG, "setupDashboard: " + dashboardCollectionPointScrapyard);

        if (dashboardTrashList != null && !dashboardTrashList.isEmpty()) {
            homeNearestTrashTitleLayout.setVisibility(View.VISIBLE);
            homeNearestTrashTitleDescription.setVisibility(View.VISIBLE);
            homeNearestTrashContainer.setVisibility(View.VISIBLE);

            Trash trashFirst = dashboardTrashList.get(0);
            homeNearestTrashFirst.setVisibility(View.VISIBLE);
            homeNearestTrashFirstDistance.setText(String.format(getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getRoundedFormattedDistance(getContext(), (int) PositionUtils.computeDistance(lastPosition, trashFirst.getPosition())) : "?", getString(R.string.global_distanceAttribute_away)));

            if (trashFirst.getImages() != null && !trashFirst.getImages().isEmpty() && ViewUtils.checkImageStorage(trashFirst.getImages().get(0))) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(trashFirst.getImages().get(0).getSmallestImage());
                GlideApp.with(this)
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(0.2f)
                        .placeholder(R.drawable.ic_image_placeholder_square)
                        .into(homeNearestTrashFirstImage);
            }


            if (dashboardTrashList.size() > 1) {
                Trash trashTwo = dashboardTrashList.get(1);
                homeNearestTrashSecond.setVisibility(View.VISIBLE);
                homeNearestTrashSecondDistance.setText(String.format(getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getRoundedFormattedDistance(getContext(), (int) PositionUtils.computeDistance(lastPosition, trashTwo.getPosition())) : "?", getString(R.string.global_distanceAttribute_away)));

                if (trashTwo.getImages() != null && !trashTwo.getImages().isEmpty() && ViewUtils.checkImageStorage(trashTwo.getImages().get(0))) {
                    StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(trashTwo.getImages().get(0).getSmallestImage());
                    GlideApp.with(this)
                            .load(mImageRef)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .placeholder(R.drawable.ic_image_placeholder_square)
                            .into(homeNearestTrashSecondImage);
                }
            } else {
                homeNearestTrashSecond.setVisibility(View.GONE);
            }
        } else {
            homeNearestTrashTitleLayout.setVisibility(View.GONE);
            homeNearestTrashTitleDescription.setVisibility(View.GONE);
            homeNearestTrashContainer.setVisibility(View.GONE);
        }

        if (dashboardCollectionPointDustbin != null) {
            dashboardNearestCollectionPointTitleLayout.setVisibility(View.VISIBLE);
            homeNearestCollectionPointDustbinDistance.setText(String.format(getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, dashboardCollectionPointDustbin.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));
            //homeNearestCollectionPointDustbinName.setText(dashboardCollectionPointDustbin.getName());
            if (dashboardCollectionPointDustbin.getImages() != null && !dashboardCollectionPointDustbin.getImages().isEmpty()) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(dashboardCollectionPointDustbin.getImages().get(0).getSmallestImage());
                GlideApp.with(this)
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_image_placeholder_square)
                        .into(homeNearestCollectionPointDustbinImage);
            }
        } else {
            homeNearestCollectionPointDustbinCardView.setVisibility(View.GONE);
        }

        if (dashboardCollectionPointScrapyard != null) {
            homeNearestCollectionPointScrapyardCardView.setVisibility(View.VISIBLE);
            homeNearestCollectionPointScrapyardDistance.setText(String.format(getString(R.string.distance_away_formatter), lastPosition != null ? PositionUtils.getFormattedComputeDistance(getContext(), lastPosition, dashboardCollectionPointScrapyard.getPosition()) : "?", getString(R.string.global_distanceAttribute_away)));
            //homeNearestCollectionPointScrapyardName.setText(dashboardCollectionPointScrapyard.getName());
            if (dashboardCollectionPointScrapyard.getImages() != null && !dashboardCollectionPointScrapyard.getImages().isEmpty() && ViewUtils.checkImageStorage(dashboardCollectionPointScrapyard.getImages().get(0))) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(dashboardCollectionPointScrapyard.getImages().get(0).getSmallestImage());
                GlideApp.with(this)
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_image_placeholder_square)
                        .into(homeNearestCollectionPointScrapyardImage);
            }
        } else {
            homeNearestCollectionPointScrapyardCardView.setVisibility(View.GONE);

            if (dashboardCollectionPointDustbin == null) {
                dashboardNearestCollectionPointTitleLayout.setVisibility(View.GONE);
            }
        }

        dashboardStatisticsCleaned.setText(dashboardStatisticsCleanedCount > -1 ? String.valueOf(dashboardStatisticsCleanedCount) : "?");
        dashboardStatisticsReported.setText(dashboardStatisticsReportedCount > -1 ? String.valueOf(dashboardStatisticsReportedCount) : "?");

        dashboardRecentActivityContainer.removeAllViews();
        // if user is anonymous, dont show recent activity
        if (dashboardUserActivityList != null && !dashboardUserActivityList.isEmpty() && user.getEmail() != null && !user.getEmail().isEmpty()) {
            List<UserActivity> finalDashboardUserActivityList = dashboardUserActivityList.size() < 3 ? dashboardUserActivityList : dashboardUserActivityList.subList(0, 3);
            for (UserActivity userActivity : finalDashboardUserActivityList) {
                if (dashboardRecentActivityContainer.getChildCount() > 0)
                    dashboardRecentActivityContainer.addView(ViewUtils.getDividerView(getContext()));

                dashboardRecentActivityContainer.addView(getRecentActivityView(userActivity));
            }
            dashboardRecentActivityTitle.setVisibility(View.VISIBLE);
            dashboardRecentActivityCardView.setVisibility(View.VISIBLE);
        } else {
            dashboardRecentActivityTitle.setVisibility(View.GONE);
            dashboardRecentActivityCardView.setVisibility(View.GONE);
        }

        if (dashboardNews != null) {
            dashboardNewsSectionTitle.setVisibility(View.VISIBLE);
            dashboardNewsMore.setVisibility(View.VISIBLE);
            dashboardNewsCardView.setVisibility(View.VISIBLE);

            dashboardNewsTitle.setText(dashboardNews.getTitle().replaceAll("<[^>]*>", ""));
            dashboardNewsDate.setText(DateTimeUtils.DATE_FORMAT.format(dashboardNews.getCreated()));
            dashboardNewsPerex.setText(dashboardNews.getBody().replaceAll("<[^>]*>", ""));

//            if (dashboardNews.getNewsConvertedImages() != null && !dashboardNews.getNewsConvertedImages().isEmpty() && ViewUtils.checkImageStorage(dashboardNews.getNewsConvertedImages().get(0))) {
            if (dashboardNews.getImages() != null && !dashboardNews.getImages().isEmpty() && ViewUtils.checkImageStorage(dashboardNews.getImages().get(0))) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(dashboardNews.getImages().get(0).getSmallestImage());
                GlideApp.with(this)
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(GlideApp.with(this).load(R.drawable.ic_image_placeholder_rectangle).centerCrop())
                        .into(dashboardNewsImage);
            }
        } else {
            dashboardNewsSectionTitle.setVisibility(View.GONE);
            dashboardNewsCardView.setVisibility(View.GONE);
            dashboardNewsMore.setVisibility(View.GONE);
        }

        dashboardEventsContainer.removeAllViews();
        if (dashboardEventList != null && !dashboardEventList.isEmpty()) {
            dashboardEventsTitle.setVisibility(View.VISIBLE);
            dashboardEventsTitleDescription.setVisibility(View.VISIBLE);
            dashboardEventsContainer.setVisibility(View.VISIBLE);

            for (Event event : dashboardEventList) {
                if (dashboardEventsContainer.getChildCount() > 0){
                    dashboardEventsContainer.addView(ViewUtils.getDividerView(getContext()));
                }

                dashboardEventsContainer.addView(getDashboardEventView(event));
            }
        } else {
            dashboardEventsTitle.setVisibility(View.GONE);
            dashboardEventsTitleDescription.setVisibility(View.GONE);
            dashboardEventsContainer.setVisibility(View.GONE);
        }
    }


    /**
     * Create and return recent activity view
     *
     * @param userActivity
     * @return
     */
    private View getRecentActivityView(final UserActivity userActivity) {
        View userActivityView = inflater.inflate(R.layout.item_list_user_activity, null);

        userActivityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(Integer.parseInt(userActivity.getId()));
                    getBaseActivity().replaceFragment(trashDetailFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView userActivityImage = userActivityView.findViewById(R.id.user_activity_image);
        ImageView userActivityType = userActivityView.findViewById(R.id.user_activity_type);
        TextView userActivityName = userActivityView.findViewById(R.id.user_activity_name);
        TextView userActivityDate = userActivityView.findViewById(R.id.user_activity_date);
        final TextView userActivityDistance = userActivityView.findViewById(R.id.user_activity_distance);
        final TextView userActivityPosition = userActivityView.findViewById(R.id.user_activity_position);

        if (userActivity.getActivity().getImages() != null && !userActivity.getActivity().getImages().isEmpty() && ViewUtils.checkImageStorage(userActivity.getActivity().getImages().get(0))) {
            Image image = userActivity.getActivity().getImages().get(0);
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image.getSmallestImage());
            GlideApp.with(this)
                    .load(mImageRef)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder_square)
                    .into(userActivityImage);
        }


        if (Constants.ActivityAction.CREATE.getName().equals(userActivity.getAction())) {
            userActivityType.setImageResource(Constants.ActivityAction.CREATE.getIconUpdateActionResId());
        } else {
            userActivityType.setImageResource(userActivity.getActivity().getStatus().getIconUpdatehistoryResId());
        }

        String userName = user == null || userActivity.getUserInfo().getUserId() != user.getId() ? (userActivity.getUserInfo().getFirstName() == null || userActivity.getActivity().isAnonymous()) ? getString(R.string.trash_anonymous) : userActivity.getUserInfo().getFirstName() : getString(R.string.home_recentActivity_you).substring(0, 1).toUpperCase() + getString(R.string.home_recentActivity_you).substring(1);

        if (Constants.ActivityAction.CREATE.getName().equals(userActivity.getAction())) {
            userActivityName.setText(getString(R.string.reported_activity_placeholder,
                    userName,
                    getString(Constants.ActivityAction.CREATE.getStringUpdateActionResId()).toLowerCase(),
                    getString(R.string.user_activity_title_thisDump),
                    "",
                    "")
            );
        } else {
            userActivityName.setText(getString(R.string.reported_activity_placeholder,
                    userName,
                    getString(userActivity.getActivity().getStatus().getStringUpdateHistoryResId()).toLowerCase(),
                    getString(R.string.user_activity_title_thisDump),
                    getString(R.string.home_recentActivity_as),
                    getString(userActivity.getActivity().getStatus().getStringResId()).toLowerCase())
            );
        }
        userActivityDate.setText(DateTimeUtils.DATE_FORMAT.format(userActivity.getCreated()));

        if (lastPosition != null) {
            userActivityDistance.setText(String.format(getString(R.string.distance_away_formatter), (lastPosition != null && userActivity.getPosition() != null) ? PositionUtils.getRoundedFormattedDistance(getActivity(), (int) PositionUtils.computeDistance(lastPosition, userActivity.getPosition())) : "?", getString(R.string.global_distanceAttribute_away)));
        } else {
            userActivityDistance.setVisibility(View.GONE);
        }

        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        new GeocoderTask(geocoder, userActivity.getGps().getLat(), userActivity.getGps().getLng(), new GeocoderTask.Callback() {
            @Override
            public void onAddressComplete(GeocoderTask.GeocoderResult geocoderResult) {
                if (!TextUtils.isEmpty(geocoderResult.getFormattedShortAddress())) {
                    userActivityPosition.setText(geocoderResult.getFormattedShortAddress());
                    userActivityPosition.setVisibility(View.VISIBLE);
                } else {
                    userActivityPosition.setVisibility(View.GONE);
                }
            }
        }).execute();

        return userActivityView;
    }

    /**
     * Create and return Event view
     *
     * @param event
     * @return
     */
    private View getDashboardEventView(final Event event) {
        View trashEventView = inflater.inflate(R.layout.layout_trash_event, null);

        TextView trashEventName = trashEventView.findViewById(R.id.trash_event_name);
        TextView trashEventTime = trashEventView.findViewById(R.id.trash_event_time);
        TextView trashEventDescription = trashEventView.findViewById(R.id.trash_event_description);
        Button trashEventJoinBtn = trashEventView.findViewById(R.id.trash_event_join_btn);
        Button trashEventDetailBtn = trashEventView.findViewById(R.id.trash_event_detail_btn);

        trashEventName.setText(event.getName());
        trashEventTime.setText(DateTimeUtils.DATE_FORMAT.format(event.getStart()));
        trashEventDescription.setText(event.getDescription());

        //trashEventJoinBtn.setVisibility(user == null || event.getUserId() == user.getId() ? View.GONE : View.VISIBLE);

        if (user == null || event.getUserId() == user.getId()) {
            trashEventJoinBtn.setVisibility(View.GONE);

        } else {
            int visibility = View.VISIBLE;
            for (User usr : event.getUsers()) {
                if (usr.getId() == user.getId()) {
                    visibility = View.GONE;
                    break;
                }
            }

            trashEventJoinBtn.setVisibility(visibility);
        }

        trashEventJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    showToast(R.string.event_signToJoin);
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.event_event_joinEventTitle)
                            .content(R.string.event_joinEventConfirmationMessage)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .autoDismiss(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    joinedEvent = event;
                                    showProgressDialog();
                                    JoinUserToEventService.startForRequest(getContext(), JOIN_TO_EVENT_REQUEST_ID, event.getId(), Collections.singletonList(user.getId()));
                                }
                            })
                            .build();

                    dialog.show();
                }
            }
        });

        trashEventDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinedEvent = event;
                EventDetailFragment eventDetailFragment = EventDetailFragment.newInstance(event.getId());
                getBaseActivity().replaceFragment(eventDetailFragment);
            }
        });

        trashEventView.setTag(EVENT_ID_TAG + event.getId());

        return trashEventView;
    }



    /**
     * Hide event view join button
     *
     * @param event
     */
    public void hideEventJoinButton(Event event) {
        View eventView = dashboardEventsContainer.findViewWithTag(EVENT_ID_TAG + event.getId());

        if (eventView == null)
            return;

        AppCompatButton trashEventJoinBtn = eventView.findViewById(R.id.trash_event_join_btn);
        trashEventJoinBtn.setVisibility(View.GONE);
    }


    private void setLastPosition() {
        lastPosition = ((MainActivity) getActivity()).getLastPosition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (((MainActivity) getActivity()).getGoogleApiClient() != null) {
            ((MainActivity) getActivity()).getGoogleApiClient().connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.app_name));
        checkTrashHunterState();
        dashboardTrashHunterFoundDumps.setText(String.format(getString(R.string.trash_foundDumps_X), amountOfFoundTrash));

        if(dashboardEventList != null){
            if(!dashboardEventList.isEmpty()) {
                getDashboardData();
                checkEvents(dashboardEventList);
            }
        }

    }



    private void checkEvents(List<Event> eventList){
        if(eventList != null && !eventList.isEmpty()){
            for(Event ev : eventList){
                if(ev != null){
                    if (user == null || ev.getUserId() == user.getId()) {

                        View eventView = dashboardEventsContainer.findViewWithTag(EVENT_ID_TAG + ev.getId());
                        if (eventView == null){
                            return;
                        }

                        AppCompatButton trashEventJoinBtn = eventView.findViewById(R.id.trash_event_join_btn);
                        trashEventJoinBtn.setVisibility(View.GONE);


                    } else {
                        int visibility = View.VISIBLE;
                        for (User usr : ev.getUsers()) {
                            if (usr.getId() == user.getId()) {
                                visibility = View.GONE;
                                break;
                            }
                        }
                        View eventView = dashboardEventsContainer.findViewWithTag(EVENT_ID_TAG + ev.getId());

                        if (eventView == null) {
                            return;
                        }

                        AppCompatButton trashEventJoinBtn = eventView.findViewById(R.id.trash_event_join_btn);
                        trashEventJoinBtn.setVisibility(visibility);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (scrollView != null)
            scrollYPosition = scrollView.getScrollY();
    }

    public void dashboardNeedsRefresh() {
        this.needRefresh = true;
    }

    @OnClick({R.id.dashboard_nearest_trash_first, R.id.dashboard_nearest_trash_second, R.id.dashboard_nearest_trash_more, R.id.dashboard_nearest_collection_point_dustbin_card_view, R.id.dashboard_nearest_collection_point_scrapyard_card_view, R.id.dashboard_statistics_more, R.id.dashboard_news_read_more_btn, R.id.dashboard_news_more, R.id.dashboard_trash_hunter_more, R.id.dashboard_support_btn, R.id.dashboard_order_trash_pickup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dashboard_nearest_trash_first:
                if (dashboardTrashList != null && !dashboardTrashList.isEmpty()) {
                    TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(dashboardTrashList.get(0).getId());
                    getBaseActivity().replaceFragment(trashDetailFragment);
                }
                break;
            case R.id.dashboard_nearest_trash_second:
                if (dashboardTrashList != null && dashboardTrashList.size() > 1) {
                    TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(dashboardTrashList.get(1).getId());
                    getBaseActivity().replaceFragment(trashDetailFragment);
                }
                break;
            case R.id.dashboard_nearest_trash_more:
                TrashListFragment trashListFragment = new TrashListFragment();
                getBaseActivity().replaceFragment(trashListFragment);
                break;
            case R.id.dashboard_nearest_collection_point_dustbin_card_view:
                if (dashboardCollectionPointDustbin != null) {
                    CollectionPointDetailFragment collectionPointDustbinDetailFragment = CollectionPointDetailFragment.newInstance(dashboardCollectionPointDustbin.getId());
                    getBaseActivity().replaceFragment(collectionPointDustbinDetailFragment);
                }
                break;
            case R.id.dashboard_nearest_collection_point_scrapyard_card_view:
                if (dashboardCollectionPointScrapyard != null) {
                    CollectionPointDetailFragment collectionPointScrapyardDetailFragment = CollectionPointDetailFragment.newInstance(dashboardCollectionPointScrapyard.getId());
                    getBaseActivity().replaceFragment(collectionPointScrapyardDetailFragment);
                }
                break;
            case R.id.dashboard_statistics_more:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                getBaseActivity().replaceFragment(statisticsFragment);
                break;

            case R.id.dashboard_news_read_more_btn:
                if (dashboardNews != null) {
                    NewsDetailFragment newsDetailFragment = NewsDetailFragment.newInstance(dashboardNews.getId());
                    getBaseActivity().replaceFragment(newsDetailFragment);
                }
                break;

            case R.id.dashboard_news_more:
                NewsListFragment newsListFragment = new NewsListFragment();
                getBaseActivity().replaceFragment(newsListFragment);
                break;

            case R.id.dashboard_trash_hunter_more:
                TrashHunterState trashHunterState = PreferencesHandler.getTrashHunterState(getContext());
                TrashListFragment hunterTrashListFragment = TrashListFragment.newInstance(true, trashHunterState != null ? trashHunterState.getAreaSize() : -1);
                getBaseActivity().replaceFragment(hunterTrashListFragment);
                break;


            case R.id.dashboard_order_trash_pickup:
                Bundle params1 = new Bundle();
                params1.putString("order_trash_pickup_clicked", "clicked");
                mFirebaseAnalytics.logEvent("order_trash_pickup_button_dashboard", params1);

                switch (Utils.getCurrentLanguage(getResources())) {
                    case "sk":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_SK);
                        break;
                    case "cs":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_CS);
                        break;
                    case "de":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_DE);
                        break;
                    case "es":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_ES);
                        break;
                    case "fr":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_FR);
                        break;
                    case "ru":
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK_RU);
                        break;
                    default:
                        Utils.browseUrl(getActivity(), Constants.ORDER_TRASH_PICKUP_HYPERLINK);
                        break;
                }
                break;


            case R.id.dashboard_support_btn:
                Bundle params2 = new Bundle();
                params2.putString("support_us_button_clicked", "clicked");
                mFirebaseAnalytics.logEvent("support_us_button_dashboard", params2);

                switch (Utils.getCurrentLanguage(getResources())) {
                    case "sk":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_SK);
                        break;
                    case "cs":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_CS);
                        break;
                    case "de":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_DE);
                        break;
                    case "es":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_ES);
                        break;
                    case "fr":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_FR);
                        break;
                    case "ru":
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK_RU);
                        break;
                    default:
                        Utils.browseUrl(getActivity(), Constants.SUPPORT_HYPERLINK);
                        break;
                }
                break;
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return DashboardFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetHomeScreenDataService.class);
        serviceClass.add(JoinUserToEventService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_HOME_SCREEN_DATA_REQUEST_ID) {
            dismissProgressDialog();

            if (swiperefresh != null && swiperefresh.isRefreshing())
                swiperefresh.setRefreshing(false);

            if (apiResult.isValidResponse()) {
                ApiGetHomeScreenDataResult apiGetHomeScreenDataResult = (ApiGetHomeScreenDataResult) apiResult.getResult();
                dashboardTrashList = apiGetHomeScreenDataResult.getTrashList();
                dashboardCollectionPointDustbin = apiGetHomeScreenDataResult.getCollectionPointDustbin();
                dashboardCollectionPointScrapyard = apiGetHomeScreenDataResult.getCollectionPointScrapyard();
                dashboardStatisticsCleanedCount = apiGetHomeScreenDataResult.getStatisticCleaned();
                dashboardStatisticsReportedCount = apiGetHomeScreenDataResult.getStatisticsReported();
                dashboardUserActivityList = apiGetHomeScreenDataResult.getDashboardUserActivityList();
                dashboardNews = apiGetHomeScreenDataResult.getDashboardNews();
                dashboardEventList = apiGetHomeScreenDataResult.getDashboardEventList();

                if (isAdded()) {
                    setupDashboard(dashboardTrashList, dashboardCollectionPointDustbin, dashboardCollectionPointScrapyard, dashboardStatisticsCleanedCount, dashboardStatisticsReportedCount, dashboardUserActivityList, dashboardNews, dashboardEventList);
                }
            } else {
                showToast(R.string.global_fetchError);
            }
        } else if (apiResult.getRequestId() == JOIN_TO_EVENT_REQUEST_ID) {
            dismissProgressDialog();
            showToast(apiResult.isValidResponse() ? R.string.event_joinEventSuccessful : R.string.event_joinEventFailed);

            if (apiResult.isValidResponse()) {
                hideEventJoinButton(joinedEvent);
                openAddEventToCalendarDialog(joinedEvent);

                joinedEvent = null;
            } else {
                showToast(R.string.global_fetchError);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.dashboard_trash_hunter_btn)
    public void onTrashHunterToogleClick() {
        Log.d(TAG, "onTrashHunterToogleClick: hunterMode = " + hunterMode);
        this.hunterMode = !this.hunterMode;
        Log.d(TAG, "onTrashHunterToogleClick: hunterMode2 = " + hunterMode);

        amountOfFoundTrash = 0;
        dashboardTrashHunterFoundDumps.setText(String.format(getString(R.string.trash_foundDumps_X), amountOfFoundTrash));

        if (this.hunterMode) {
            TrashHunterStartFragment trashHunterStartFragment = new TrashHunterStartFragment();
            trashHunterStartFragment.setTargetFragment(this, 0);
            getBaseActivity().replaceFragment(trashHunterStartFragment);
        } else {
            setHunterMode(false, 0, 0);
        }
    }

    private void setHunterMode(boolean hunterMode, long durationTime, long huntingTime) {
        this.hunterMode = hunterMode;

        if (this.hunterMode) {
            startHuntingMode(durationTime, huntingTime);
            dashboardTrashHunterOnLayout.setVisibility(View.VISIBLE);
            dashboardTrashHunterBtn.setText(R.string.trashHunter_stopHunting);
            dashboardTrashHunterTitleDescription.setText(R.string.trashHunter_turnedOnInfo);
        } else {
            dashboardTrashHunterOnLayout.setVisibility(View.GONE);
            dashboardTrashHunterBtn.setText(R.string.trashHunter_startHunting);
            dashboardTrashHunterTitleDescription.setText(R.string.trashHunter_text);

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }

            if (mTrashHunterBound) {
                try {
                    mService.removeOnTrashHunterChangeListener(onTrashHunterChangeListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                getActivity().unbindService(svcConn);
                mTrashHunterBound = false;
            }

            Intent i = new Intent(getContext(), TrashHunterService.class);
            getActivity().stopService(i);

            PreferencesHandler.setTrashHunterState(getContext(), null);
        }
    }

    private void startHuntingMode(final long totalDurationTimeInMilis, final long counterTimeInMilis) {
        dashboardTrashHunterProgressBar.setProgress(100);

        int barVal = (int) ((((double) counterTimeInMilis) / (double) (totalDurationTimeInMilis)) * 100.0);
        dashboardTrashHunterProgressBar.setProgress(barVal);

        Log.d(TAG, "startHuntingMode: barVal = " + barVal);

        long seconds = counterTimeInMilis / 1000;
        dashboardTrashHunterTimer.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));

        if (countDownTimer != null)
            countDownTimer.cancel();
        countDownTimer = new CountDownTimer(counterTimeInMilis, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                int barVal = (int) ((((double) leftTimeInMilliseconds) / (double) (totalDurationTimeInMilis)) * 100.0);
                dashboardTrashHunterProgressBar.setProgressWithAnimation(barVal, 2500);

                long seconds = leftTimeInMilliseconds / 1000;
                dashboardTrashHunterTimer.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {
                showToast(R.string.trashHunter_finished);
                dashboardTrashHunterTimer.setText("");
                setHunterMode(false, 0, 0);
            }
        }.start();

        countDownTimer.onTick(counterTimeInMilis);
    }

    @Override
    public void onTrashHunterStart(TrashHunterState trashHunterState) {
        checkTrashHunterState();
    }

    private void checkTrashHunterState() {

        TrashHunterState trashHunterState = PreferencesHandler.getTrashHunterState(getContext());
        if (trashHunterState != null && trashHunterState.isTrashHunterActive()) {
            Intent i = new Intent(getContext(), TrashHunterService.class);
            i.putExtra(TrashHunterService.BUNDLE_LAST_POSITION, lastPosition);
            if (!isTrashHunterServiceRunning())
                getActivity().startService(i);
            getActivity().bindService(i, svcConn, BIND_AUTO_CREATE);

            long remainingTime;
            if (trashHunterState.isTrashHunterActive() && (remainingTime = trashHunterState.getTrashHunterRemainingTime()) > 0) {
                setHunterMode(true, trashHunterState.getTrashHunterDutration(), remainingTime);
            }

        } else {
            setHunterMode(false, 0, 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mTrashHunterBound) {
            try {
                mService.removeOnTrashHunterChangeListener(onTrashHunterChangeListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getActivity().unbindService(svcConn);
            mTrashHunterBound = false;
        }
    }

    private boolean isTrashHunterServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrashHunterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private TrashHunterService service;
    private boolean mTrashHunterBound;
    IRemoteService mService = null;

    private ServiceConnection svcConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {

            mService = IRemoteService.Stub.asInterface(binder);
            try {
                mService.addOnTrashHunterChangeListener(onTrashHunterChangeListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

//            TrashHunterService.LocalBinder localBinder = (TrashHunterService.LocalBinder) binder;
//            service = localBinder.getService();
//            try {
//                service.addOnTrashHunterChangeListener(onTrashHunterChangeListener);
//            } catch (Throwable t) {
//            }

            mTrashHunterBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
            mTrashHunterBound = false;
        }
    };

    private ITrashHunterChangeListener.Stub onTrashHunterChangeListener = new ITrashHunterChangeListener.Stub() {
        @Override
        public void onTrashHunterStateChange() {
            mHandler.sendMessage(mHandler.obtainMessage(TRASH_HUNTER_CHANGE_STATE, 0, 0));
        }

        @Override
        public void onTrashHunterTrashCountChange(int trashCount) {
            mHandler.sendMessage(mHandler.obtainMessage(TRASH_HUNTER_TRASH_COUNT, trashCount, 0));

        }
    };

    private void openAddEventToCalendarDialog(final Event event) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.event_addToCalendar)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utils.addEventToCalender(getContext(), event);
                    }
                })
                .build();

        dialog.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRASH_HUNTER_TRASH_COUNT:
                    amountOfFoundTrash = msg.arg1;
                    dashboardTrashHunterFoundDumps.setText(String.format(getString(R.string.trash_foundDumps_X), amountOfFoundTrash));
                    break;

                case TRASH_HUNTER_CHANGE_STATE:
                    TrashHunterState trashHunterState = PreferencesHandler.getTrashHunterState(getContext());
                    long remainingTime;
                    if (trashHunterState != null && trashHunterState.isTrashHunterActive() && (remainingTime = trashHunterState.getTrashHunterRemainingTime()) > 0) {
                        Log.d("TrashHunter", "onTrashHunterStateChange - remainingTime = " + remainingTime);
                        setHunterMode(true, trashHunterState.getTrashHunterDutration(), remainingTime);
                    } else {
                        Log.d("TrashHunter", "onTrashHunterStateChange - inactive");
                        setHunterMode(false, 0, 0);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

}

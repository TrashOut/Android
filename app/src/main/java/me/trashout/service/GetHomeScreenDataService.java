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

package me.trashout.service;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.param.ApiGetCollectionPointListParam;
import me.trashout.api.request.ApiGetHomeScreenDataRequest;
import me.trashout.api.result.ApiGetHomeScreenDataResult;
import me.trashout.api.result.ApiGetTrashCountResult;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Constants;
import me.trashout.model.Event;
import me.trashout.model.News;
import me.trashout.model.Trash;
import me.trashout.model.UserActivity;
import me.trashout.service.base.BaseService;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.Utils;
import retrofit2.Call;
import retrofit2.Response;

/**
 * TrashOutNGO
 *
 * @package me.trashout.service.base
 * @since 20.10.2016
 */
public class GetHomeScreenDataService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, LatLng lastPosition, long userId) {
        ApiGetHomeScreenDataRequest apiGetHomeScreenDataRequest = new ApiGetHomeScreenDataRequest(requestId, lastPosition, userId);
        addRequest(context, GetHomeScreenDataService.class, apiGetHomeScreenDataRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);
        Exception lastException = null;

        ApiGetHomeScreenDataRequest apiGetHomeScreenDataRequest = (ApiGetHomeScreenDataRequest) apiBaseRequest;

        ApiGetCollectionPointListParam apiGetCollectionPointListParam = apiGetHomeScreenDataRequest.getApiGetCollectionPointListParam();

        List<Trash> trashList = new ArrayList<>();
        CollectionPoint collectionPointDustbin = null;
        CollectionPoint collectionPointScrapyard = null;
        List<Event> eventList = new ArrayList<>();
        List<UserActivity> userActivityList = new ArrayList<>();
        News news = null;


        if (apiGetHomeScreenDataRequest.getApiGetTrashListParam().getTrashListOptions().get("userPosition") != null) {
            Call<List<Trash>> callTrashlist = mApiServer.getTrashList(apiGetHomeScreenDataRequest.getApiGetTrashListParam().getTrashListOptions());
            try {
                Response<List<Trash>> trashlistResponse = callTrashlist.execute();
                if (trashlistResponse.isSuccessful()) {
                    trashList = trashlistResponse.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        if (apiGetCollectionPointListParam.getCollectionPointListOptions().get("userPosition") != null) {
            apiGetCollectionPointListParam.setCollectionPointSize(Constants.CollectionPointSize.DUSTBIN);
            Call<List<CollectionPoint>> collCallectionPointDustbin = mApiServer.getCollectionPointList(apiGetCollectionPointListParam.getCollectionPointListOptions());
            try {
                Response<List<CollectionPoint>> callectionPointDustbinResponse = collCallectionPointDustbin.execute();
                if (callectionPointDustbinResponse.isSuccessful() && !callectionPointDustbinResponse.body().isEmpty()) {
                    collectionPointDustbin = callectionPointDustbinResponse.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }


            apiGetCollectionPointListParam.setCollectionPointSize(Constants.CollectionPointSize.SCRAPYARD);

            Call<List<CollectionPoint>> collCollectionPointScrapyard = mApiServer.getCollectionPointList(apiGetCollectionPointListParam.getCollectionPointListOptions());
            try {
                Response<List<CollectionPoint>> collCollectionPointScrapyardResponse = collCollectionPointScrapyard.execute();
                if (collCollectionPointScrapyardResponse.isSuccessful() && !collCollectionPointScrapyardResponse.body().isEmpty()) {
                    collectionPointScrapyard = collCollectionPointScrapyardResponse.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        int countTrashStillHere = -1;
        int countTrashCleaned = -1;

        try {
            Call<ApiGetTrashCountResult> call = mApiServer.getTrashCount(getStatisticsRequestOptions(Constants.TrashStatus.STILL_HERE), null);
            Response<ApiGetTrashCountResult> countTrashStillHereResult = call.execute();
            if (countTrashStillHereResult != null && countTrashStillHereResult.body() != null) {
                countTrashStillHere = countTrashStillHereResult.body().getCount();
            }

            Call<ApiGetTrashCountResult> callCleaned = mApiServer.getTrashCount(getStatisticsRequestOptions(Constants.TrashStatus.CLEANED), null);
            Response<ApiGetTrashCountResult> countTrashCleanedResult = callCleaned.execute();
            if (countTrashCleanedResult != null && countTrashCleanedResult.body() != null) {
                countTrashCleaned = countTrashCleanedResult.body().getCount();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String attributesNeeded = "id,name,start,description";
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        Calendar afterWeek = Calendar.getInstance();
        afterWeek.add(Calendar.DAY_OF_YEAR, 7);
        Call<List<Event>> callEventList = mApiServer.getEventList(DateTimeUtils.TIMESTAMP_FORMAT.format(today.getTime()), DateTimeUtils.TIMESTAMP_FORMAT.format(afterWeek.getTime()), apiGetHomeScreenDataRequest.getQueryUserPosition(), attributesNeeded);
        try {
            Response<List<Event>> eventListResponse = callEventList.execute();
            if (eventListResponse.isSuccessful()) {

                eventList = eventListResponse.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
            lastException = e;
        }

        for (int i = 0; i < eventList.size(); i++) {
            Call<Event> callEventDetail = mApiServer.getEventDetail(eventList.get(i).getId());

            try {
                Response<Event> eventResponse = callEventDetail.execute();
                if (eventResponse.isSuccessful()) {
                    eventList.set(i, eventResponse.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        if (apiGetHomeScreenDataRequest.getUserId() > 0) {
            Call<List<UserActivity>> callUserActivityList = mApiServer.getUsersActivity(apiGetHomeScreenDataRequest.getUserId());
            try {
                Response<List<UserActivity>> userActivityListResponse = callUserActivityList.execute();
                if (userActivityListResponse.isSuccessful()) {
                    userActivityList = userActivityListResponse.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        Call<List<News>> callNewsList = mApiServer.getNewsList(Utils.getLocaleString(),0, 1, "-created"); // page and limit
        try {
            Response<List<News>> userActivityListResponse = callNewsList.execute();
            if (userActivityListResponse.isSuccessful()) {
                List<News> newsList = userActivityListResponse.body();
                if (newsList != null && !newsList.isEmpty()) {
                    news = newsList.get(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            lastException = e;
        }

        if (news == null && !"en_US".equals(Utils.getLocaleString())) {
            callNewsList = mApiServer.getNewsList("en_US", 0, 1, "-created");

            try {
                Response<List<News>> userActivityListResponse = callNewsList.execute();
                if (userActivityListResponse.isSuccessful()) {
                    List<News> newsList = userActivityListResponse.body();
                    if (newsList != null && !newsList.isEmpty()) {
                        news = newsList.get(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        if (lastException == null || (trashList != null || collectionPointDustbin != null || collectionPointScrapyard != null)) {
            ApiGetHomeScreenDataResult apiGetHomeScreenDataResult = new ApiGetHomeScreenDataResult(trashList, collectionPointDustbin, collectionPointScrapyard, countTrashCleaned, countTrashStillHere, userActivityList, news, eventList);
            apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
            notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, apiGetHomeScreenDataResult, null, null);
        } else {
            apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
            notifyResultListener(apiBaseRequest.getId(), null, null, lastException);
        }
    }


    private String getStatisticsRequestOptions(Constants.TrashStatus trashStatus) {

        ArrayList<String> trashStatusString = new ArrayList<>();

        if (trashStatus.equals(Constants.TrashStatus.STILL_HERE)) {
            trashStatusString.add(Constants.TrashStatus.STILL_HERE.getName());
            trashStatusString.add(Constants.TrashStatus.LESS.getName());
            trashStatusString.add(Constants.TrashStatus.MORE.getName());
        } else {
            trashStatusString.add(Constants.TrashStatus.CLEANED.getName());
        }


        return TextUtils.join(",", trashStatusString);

    }
}

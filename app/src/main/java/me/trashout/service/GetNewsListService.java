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
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.trashout.Configuration;
import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.request.ApiGetNewsListRequest;
import me.trashout.api.result.ApiGetNewsListResult;
import me.trashout.model.News;
import me.trashout.service.base.BaseService;
import me.trashout.utils.Utils;
import retrofit2.Call;
import retrofit2.Response;

import static me.trashout.model.Constants.EN_LOCALE;

/**
 * TrashOutNGO
 *
 * @package me.trashout.service.base
 * @since 20.10.2016
 */
public class GetNewsListService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, int page) {
        ApiGetNewsListRequest apiGetNewsListRequest = new ApiGetNewsListRequest(requestId, page);
        addRequest(context, GetNewsListService.class, apiGetNewsListRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);
        Exception lastException = null;

        ApiGetNewsListRequest apiGetCollectionPointListRequest = (ApiGetNewsListRequest) apiBaseRequest;

        // Chaining calls because we always need news in english language
        // TBH api should be handling this

        List<News> newsList = new ArrayList<>();
        Call<List<News>> call = mApiServer.getNewsList(Utils.getLocaleString(), apiGetCollectionPointListRequest.getPage(), Configuration.NEWS_LIST_LIMIT_SIZE, "-created");

        try {
            Response<List<News>> response = call.execute();
            if (response.isSuccessful()) {
                newsList = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
            lastException = e;
        }

        if (!EN_LOCALE.equals(Utils.getLocaleString())) {
            call = mApiServer.getNewsList(EN_LOCALE, apiGetCollectionPointListRequest.getPage(), Configuration.NEWS_LIST_LIMIT_SIZE, "-created");

            try {
                Response<List<News>> response = call.execute();
                if (response.isSuccessful()) {
                    if (newsList != null && response.body() != null) {
                        newsList.addAll(response.body());

                        sortNewsByDateCreated(newsList);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                lastException = e;
            }
        }

        if (lastException == null && newsList != null && !newsList.isEmpty()) {
            ApiBaseDataResult result = new ApiGetNewsListResult(newsList);

            apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
            notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, result, Response.success(newsList), null);
        } else {
            Log.d(TAG, "RequestProcess - FAIL \n" + (lastException != null ? lastException.toString() : null));
            apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
            notifyResultListener(apiBaseRequest.getId(), null, null, lastException);
        }
    }

    private void sortNewsByDateCreated (List<News> newsList)
    {
        Collections.sort(newsList, new Comparator<News>()
        {
            @Override
            public int compare (News o1, News o2)
            {
                return o2.getCreated().compareTo(o1.getCreated());
            }
        });
    }
}

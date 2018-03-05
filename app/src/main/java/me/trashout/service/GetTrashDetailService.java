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
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiGetTrashDetailRequest;
import me.trashout.api.result.ApiGetTrashDetailResult;
import me.trashout.model.Event;
import me.trashout.model.Trash;
import me.trashout.service.base.BaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTrashDetailService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, long trashId) {
        ApiGetTrashDetailRequest apiBaseRequest = new ApiGetTrashDetailRequest(requestId, trashId);
        addRequest(context, GetTrashDetailService.class, apiBaseRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiGetTrashDetailRequest apiGetTrashListRequest = (ApiGetTrashDetailRequest) apiBaseRequest;

        Exception lastException = null;
        Trash trash = null;

        Call<Trash> call = mApiServer.getTrashDetail(apiGetTrashListRequest.getTrashId());
        try {
            Response<Trash> trashResponse = call.execute();
            if (trashResponse.isSuccessful()) {
                trash = trashResponse.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
            lastException = e;
        }

        List<Event> eventList;
        if (trash != null) {
            eventList = trash.getEvents();

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
            trash.setEvents(eventList);
        }

        if (lastException == null && trash != null) {
            ApiGetTrashDetailResult apiGetTrashDetailResult = new ApiGetTrashDetailResult(trash);

            apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
            notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, apiGetTrashDetailResult, null, null);
        } else {
            apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
            notifyResultListener(apiBaseRequest.getId(), null, null, lastException);

            Log.d(TAG, "RequestProcess - FAIL \n" + lastException.toString());
            Log.d(TAG, "RequestProcess - FAIL \n" + trash);
            apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
            notifyResultListener(apiBaseRequest.getId(), null, null, lastException);
        }

//        call.enqueue(new Callback<Trash>() {
//            @Override
//            public void onResponse(Call<Trash> call, Response<Trash> response) {
//
//
//                ApiBaseDataResult result = null;
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.d(TAG, "onResponse: isSuccessful");
//
//                    result = new ApiGetTrashDetailResult(response.body());
//                } else {
//                    Log.d(TAG, "onResponse: fail");
//                    result = new ApiSimpleErrorResult(getBaseContext());
//                }
//
//
//                apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
//                notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, result, response, null);
//            }
//
//            @Override
//            public void onFailure(Call<Trash> call, Throwable retrofitError) {
//                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
//                apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
//                notifyResultListener(apiBaseRequest.getId(), null, null, retrofitError);
//            }
//        });
    }
}


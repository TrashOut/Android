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

import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiGetUserByFirebaseTokenRequest;
import me.trashout.api.result.ApiGetUserResult;
import me.trashout.model.User;
import me.trashout.service.base.BaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUserByFirebaseTokenService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId) {
        ApiGetUserByFirebaseTokenRequest apiGetUserByFirebaseTokenRequest = new ApiGetUserByFirebaseTokenRequest(requestId, false, null, null, null, null);
        addRequest(context, GetUserByFirebaseTokenService.class, apiGetUserByFirebaseTokenRequest, mRequestList);
    }

    public static void startForRequest(Context context, int requestId, boolean signUp, String firtName, String lastName, String email, String facebookToken) {
        ApiGetUserByFirebaseTokenRequest apiGetUserByFirebaseTokenRequest = new ApiGetUserByFirebaseTokenRequest(requestId, signUp, firtName, lastName, email, facebookToken);
        addRequest(context, GetUserByFirebaseTokenService.class, apiGetUserByFirebaseTokenRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        final ApiGetUserByFirebaseTokenRequest apiGetUserByFirebaseTokenRequest = (ApiGetUserByFirebaseTokenRequest) apiBaseRequest;

        Call<User> call = mApiServer.getUserByFirebaseToken();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {


                ApiBaseDataResult result = null;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: isSuccessful");

                    result = new ApiGetUserResult(response.body());
                } else {
                    Log.d(TAG, "onResponse: fail");
                    result = new ApiSimpleErrorResult(getBaseContext());
                }


                apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
                notifyResultListener(apiBaseRequest.getId(), apiGetUserByFirebaseTokenRequest, result, response, null);
            }

            @Override
            public void onFailure(Call<User> call, Throwable retrofitError) {
                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
                apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
                notifyResultListener(apiBaseRequest.getId(), null, null, retrofitError);
            }
        });

    }
}


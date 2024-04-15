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

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.base.ApiSimpleResult;
import me.trashout.api.request.ApiDeleteAccountRequest;
import me.trashout.notification.TrashoutFirebaseInstanceIdService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, long userId) {
        ApiDeleteAccountRequest apiDeleteAccountRequest = new ApiDeleteAccountRequest(requestId, userId);
        addRequest(context, DeleteAccountService.class, apiDeleteAccountRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiDeleteAccountRequest apiDeleteAccountRequest = (ApiDeleteAccountRequest) apiBaseRequest;


        Call<ResponseBody> call = mApiServer.deleteAccount(apiDeleteAccountRequest.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                ApiBaseDataResult result = null;
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: isSuccessful");

                    result = new ApiSimpleResult();

                    // Firebase delete token
                    TrashoutFirebaseInstanceIdService.deleteFcmToken(getBaseContext());
                    PreferencesHandler.setUserData(getBaseContext(), null);
                    FirebaseAuth.getInstance().signOut();
                    Utils.resetFcmToken();

                    // Facebook sign out
                    if (FacebookSdk.isInitialized()) {
                        LoginManager.getInstance().logOut();
                    }

                } else {
                    Log.d(TAG, "onResponse: fail");
                    result = new ApiSimpleErrorResult(getBaseContext());
                }


                apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
                notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, result, response, null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable retrofitError) {
                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
                apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
                notifyResultListener(apiBaseRequest.getId(), null, null, retrofitError);
            }
        });

    }
}


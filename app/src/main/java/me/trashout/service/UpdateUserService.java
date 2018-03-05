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
import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import me.trashout.Configuration;
import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiConfirmResult;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiUserRequest;
import me.trashout.model.Image;
import me.trashout.model.User;
import me.trashout.service.base.BaseService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, User user, Uri photoUri) {
        ApiUserRequest apiUserRequest = new ApiUserRequest(requestId, user, photoUri);
        addRequest(context, UpdateUserService.class, apiUserRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        final ApiUserRequest apiUserRequest = (ApiUserRequest) apiBaseRequest;

        final User user = apiUserRequest.getUser();

        if (apiUserRequest.getPhotoUri() != null) {


            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Configuration.FIREBASE_IMAGE_FOLDER_REFERENCE);

            final ArrayList<Image> userImage = new ArrayList<>(1);
            final ArrayList<Uri> userUriImage = new ArrayList<Uri>();
            userUriImage.add(apiUserRequest.getPhotoUri());

            uploadImages(mStorageRef, userUriImage, 0, userImage, new OnImageUploadListener() {

                @Override
                public void onComplete(UploadStatus status, ArrayList<Image> images) {
                    if (status.equals(UploadStatus.SUCCESS)) {
                        if (!images.isEmpty())
                            user.setImage(images.get(0));

                        updateUser(apiUserRequest, user);
                    } else {
                        apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
                        notifyResultListener(apiBaseRequest.getId(), null, null, null);
                    }
                }
            });

        } else {
            updateUser(apiUserRequest, user);
        }
    }

    private void updateUser(final ApiUserRequest apiUserRequest, final User user) {

        Call<ResponseBody> call = mApiServer.updateUser(apiUserRequest.getUser().getId(), apiUserRequest.getUser());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ApiBaseDataResult result = null;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: isSuccessful");

                    result = new ApiConfirmResult();
                } else {
                    Log.d(TAG, "onResponse: fail");
                    result = new ApiSimpleErrorResult(getBaseContext());
                }


                apiUserRequest.setStatus(ApiBaseRequest.Status.DONE);
                notifyResultListener(apiUserRequest.getId(), apiUserRequest, result, response, null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable retrofitError) {
                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
                apiUserRequest.setStatus(ApiBaseRequest.Status.ERROR);
                notifyResultListener(apiUserRequest.getId(), null, null, retrofitError);
            }
        });
    }
}


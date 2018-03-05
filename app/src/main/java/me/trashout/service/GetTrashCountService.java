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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiTrashCountRequest;
import me.trashout.api.result.ApiGetTrashCountResult;
import me.trashout.model.Constants;
import me.trashout.service.base.BaseService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * TrashOutNGO
 *
 * @package me.trashout.service.base
 * @since 20.10.2016
 */
public class GetTrashCountService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, String areaCountry) {
        ApiTrashCountRequest apiTrashCountRequest = new ApiTrashCountRequest(requestId, areaCountry);
        addRequest(context, GetTrashCountService.class, apiTrashCountRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiTrashCountRequest apiTrashCountRequest = (ApiTrashCountRequest) apiBaseRequest;

        ApiBaseDataResult result = null;
        Response<ApiGetTrashCountResult> countTrashStillHere = null;
        Exception exception = null;

        try {
            Call<ApiGetTrashCountResult> call = mApiServer.getTrashCount(getRequestOptions(Constants.TrashStatus.STILL_HERE), apiTrashCountRequest.getAreaCountry());
            countTrashStillHere = call.execute();

            Call<ApiGetTrashCountResult> callCleaned = mApiServer.getTrashCount(getRequestOptions(Constants.TrashStatus.CLEANED), apiTrashCountRequest.getAreaCountry());
            Response<ApiGetTrashCountResult> countTrashCleaned = callCleaned.execute();

            result = new ApiGetTrashCountResult(countTrashStillHere.body().getCount(), countTrashCleaned.body().getCount());

        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
            result = new ApiSimpleErrorResult();
        }


        apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
        notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, result, countTrashStillHere, exception);

    }

    private String getRequestOptions(Constants.TrashStatus trashStatus) {

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

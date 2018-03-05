package me.trashout.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiGetOrganizationsListRequest;
import me.trashout.api.result.ApiGetOrganizationsListResult;
import me.trashout.model.Organization;
import me.trashout.service.base.BaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by filip.tomasovych on 23. 1. 2018.
 */

public class GetOrganizationsListService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId) {
        ApiGetOrganizationsListRequest apiGetOrganizationsListRequest = new ApiGetOrganizationsListRequest(requestId);
        addRequest(context, GetOrganizationsListService.class, apiGetOrganizationsListRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiGetOrganizationsListRequest apiGetOrganizationsListRequest = (ApiGetOrganizationsListRequest) apiBaseRequest;

//        Call<List<Organization>> call = mApiServer.getOrganizationsList(0, 20, "-created");
        Call<List<Organization>> call = mApiServer.getOrganizationsList("-created");
        call.enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {

                ApiBaseDataResult result = null;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: isSuccessful");

                    result = new ApiGetOrganizationsListResult(response.body());
                } else {
                    Log.d(TAG, "onResponse: fail");
                    result = new ApiSimpleErrorResult(getBaseContext());
                }


                apiBaseRequest.setStatus(ApiBaseRequest.Status.DONE);
                notifyResultListener(apiBaseRequest.getId(), apiBaseRequest, result, response, null);
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable retrofitError) {
                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
                apiBaseRequest.setStatus(ApiBaseRequest.Status.ERROR);
                notifyResultListener(apiBaseRequest.getId(), null, null, retrofitError);
            }
        });

    }
}

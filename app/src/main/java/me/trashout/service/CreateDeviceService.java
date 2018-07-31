package me.trashout.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.request.ApiUserDeviceRequest;
import me.trashout.model.UserDevice;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PreferencesHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDeviceService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, UserDevice userDevice) {
        ApiUserDeviceRequest apiUserDeviceRequest = new ApiUserDeviceRequest(requestId, userDevice);
        addRequest(context, CreateDeviceService.class, apiUserDeviceRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiUserDeviceRequest apiUserDeviceRequest = (ApiUserDeviceRequest) apiBaseRequest;

        Call<UserDevice> call = mApiServer.createUserDevice(apiUserDeviceRequest.getUserDevice());
        call.enqueue(new Callback<UserDevice>() {
            @Override
            public void onResponse(Call<UserDevice> call, Response<UserDevice> response) {
                ApiBaseDataResult result;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: isSuccessful");
                    PreferencesHandler.setFcmRegistered(getApplicationContext(), true);
                } else {
                    Log.d(TAG, "onResponse: fail");
                    PreferencesHandler.setFcmRegistered(getApplicationContext(), false);
                }
            }

            @Override
            public void onFailure(Call<UserDevice> call, Throwable retrofitError) {
                Log.d(TAG, "RequestProcess - FAIL \n" + retrofitError.toString());
                PreferencesHandler.setFcmRegistered(getApplicationContext(), false);
            }
        });
    }
}

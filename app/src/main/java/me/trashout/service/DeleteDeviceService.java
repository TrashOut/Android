package me.trashout.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.base.ApiSimpleErrorResult;
import me.trashout.api.request.ApiDeleteUserDeviceRequest;
import me.trashout.service.base.BaseService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteDeviceService extends BaseService {

    private static List<ApiBaseRequest> mRequestList = new ArrayList<>();

    public static void startForRequest(Context context, int requestId, String fcmToken) {
        ApiDeleteUserDeviceRequest apiBaseRequest = new ApiDeleteUserDeviceRequest(requestId, fcmToken);
        addRequest(context, DeleteDeviceService.class, apiBaseRequest, mRequestList);
    }

    @Override
    protected List<ApiBaseRequest> getRequestList() {
        return mRequestList;
    }

    @Override
    protected void requestProcess(final ApiBaseRequest apiBaseRequest) {
        apiBaseRequest.setStatus(ApiBaseRequest.Status.PENDING);

        ApiDeleteUserDeviceRequest apiDeleteUserDeviceRequest = (ApiDeleteUserDeviceRequest) apiBaseRequest;


        Call<ResponseBody> call = mApiServer.deleteUserDevice(apiDeleteUserDeviceRequest.getTokenFCM());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                ApiBaseDataResult result = null;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: isSuccessful");
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

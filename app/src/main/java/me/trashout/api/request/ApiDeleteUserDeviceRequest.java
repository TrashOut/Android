package me.trashout.api.request;

import me.trashout.api.base.ApiBaseRequest;

public class ApiDeleteUserDeviceRequest extends ApiBaseRequest {

    private String tokenFCM;

    public ApiDeleteUserDeviceRequest(int id, String tokenFCM) {
        super(id);
        this.tokenFCM = tokenFCM;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }
}
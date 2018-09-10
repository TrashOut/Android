package me.trashout.api.request;

import me.trashout.api.base.ApiBaseRequest;
import me.trashout.model.UserDevice;

public class ApiUserDeviceRequest extends ApiBaseRequest {

    private UserDevice userDevice;

    public ApiUserDeviceRequest(int id, UserDevice userDevice) {
        super(id);
        this.userDevice = userDevice;
    }

    public UserDevice getUserDevice() {
        return userDevice;
    }
}

package me.trashout.api.request;

import com.google.android.gms.maps.model.LatLng;

import me.trashout.api.base.ApiBaseRequest;
import me.trashout.api.param.ApiGetCollectionPointListParam;
import me.trashout.api.param.ApiGetTrashListParam;


public class ApiGetHomeScreenDataRequest extends ApiBaseRequest {

    private ApiGetTrashListParam apiGetTrashListParam;
    private ApiGetCollectionPointListParam apiGetCollectionPointListParam;
    private LatLng userPosition;
    private long userId;

    public ApiGetHomeScreenDataRequest(int id, LatLng userPosition, long userId) {
        super(id);
        this.apiGetTrashListParam = ApiGetTrashListParam.createHomeScreenTrashListParam(userPosition);
        this.apiGetCollectionPointListParam = ApiGetCollectionPointListParam.createHomeScreenCollectionPointListParam(userPosition);
        this.userPosition = userPosition;
        this.userId = userId;
    }

    public ApiGetTrashListParam getApiGetTrashListParam() {
        return apiGetTrashListParam;
    }

    public ApiGetCollectionPointListParam getApiGetCollectionPointListParam() {
        return apiGetCollectionPointListParam;
    }

    public LatLng getUserPosition() {
        return userPosition;
    }

    public String getQueryUserPosition(){
        return userPosition != null ? userPosition.latitude + ","+userPosition.longitude : null;
    }

    public long getUserId() {
        return userId;
    }
}

package me.trashout.api.request;

import me.trashout.api.base.ApiBaseRequest;

/**
 * Created by filip.tomasovych on 23. 1. 2018.
 */

public class ApiGetOrganizationsListRequest extends ApiBaseRequest {

    private int page = 1;
    private int limit = 20;

    public ApiGetOrganizationsListRequest(int id, int page) {
        super(id);
        this.page = page;
    }

    public ApiGetOrganizationsListRequest(int id) {
        super(id);
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}

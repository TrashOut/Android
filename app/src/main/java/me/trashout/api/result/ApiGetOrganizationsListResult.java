package me.trashout.api.result;

import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.model.Organization;

/**
 * Created by filip.tomasovych on 23. 1. 2018.
 */

public class ApiGetOrganizationsListResult extends ApiBaseDataResult {

    private List<Organization> organizationList;

    public ApiGetOrganizationsListResult(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }

    public List<Organization> getOrganizationList() {
        return organizationList;
    }
}

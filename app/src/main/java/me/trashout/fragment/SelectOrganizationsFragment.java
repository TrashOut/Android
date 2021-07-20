package me.trashout.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetOrganizationsListResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.model.Organization;
import me.trashout.service.GetOrganizationsListService;
import me.trashout.service.base.BaseService;

/**
 * Created by filip.tomasovych on 24. 1. 2018.
 */

public class SelectOrganizationsFragment extends BaseFragment implements IProfileFragment, BaseService.UpdateServiceListener {

    private static final int GET_ORGANIZATIONS_LIST_REQUEST = 752;
    public static final String KEY_SELECTED_ORGANIZATIONS = "KEY_SELECTED_ORGANIZATIONS";

    @BindView(R.id.organizations_container)
    LinearLayout organizationsContainer;

    private List<Organization> mSelectedOrganizations;

    private LayoutInflater inflater;

    private ProfileEditFragment.OnSaveOrganizationsListener mCallback;


    public static SelectOrganizationsFragment newInstance(List<Organization> selectedOrganizations) {
        SelectOrganizationsFragment fragment = new SelectOrganizationsFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(KEY_SELECTED_ORGANIZATIONS, gson.toJson(selectedOrganizations));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (ProfileEditFragment.OnSaveOrganizationsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSaveOrganizationsListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String gsonString = (String) bundle.getSerializable(KEY_SELECTED_ORGANIZATIONS);
            Gson gson = new Gson();
            mSelectedOrganizations = gson.fromJson(gsonString, new TypeToken<List<Organization>>() {
            }.getType());
        }

        if (mSelectedOrganizations == null) {
            mSelectedOrganizations = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_organizations, container, false);
        ButterKnife.bind(this, view);

        this.inflater = inflater;

        setupOrganizations(null);

        GetOrganizationsListService.startForRequest(getActivity(), GET_ORGANIZATIONS_LIST_REQUEST);
        showProgressDialog();

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                saveOrganizations();
                finish();
                return true;
            default:
                break;
        }

        return false;
    }


    private void saveOrganizations() {
        List<Organization> organizationList = new ArrayList<>();

        for (int i = 0; i < organizationsContainer.getChildCount(); i++) {
            View collectionPointFilterTypeView = organizationsContainer.getChildAt(i);
            AppCompatCheckBox collectionPointTypeCheckBox;
            collectionPointTypeCheckBox = collectionPointFilterTypeView.findViewById(R.id.collection_point_filter_type_checkbox);
            if (collectionPointTypeCheckBox.isChecked()) {
                Organization org = (Organization) collectionPointTypeCheckBox.getTag();
                organizationList.add(org);
            }
        }

        mCallback.onSaveOrganizationsListener(organizationList);
    }

    private void setupOrganizations(List<Organization> organizationList) {

        organizationsContainer.removeAllViews();

        if (organizationList == null)
            return;

        Collections.sort(organizationList, new Comparator<Organization>() {
            @Override
            public int compare(final Organization object1, final Organization object2) {
                return object2.getName().compareTo(object1.getName());
            }
        });

        // Api returns only descending order not ascending
        for (int i = organizationList.size() - 1; i >= 0; i--) {
            boolean checked = mSelectedOrganizations.contains(organizationList.get(i));
            organizationsContainer.addView(getOrganizationView(organizationList.get(i), checked));
        }
    }

    private View getOrganizationView(Organization organization, boolean checked) {
        View collectionPointTypeView = inflater.inflate(R.layout.layout_collection_point_filter_type, null);

        TextView collectionPointTypeName = collectionPointTypeView.findViewById(R.id.collection_point_filter_type_name);
        final AppCompatCheckBox collectionPointTypeCheckBox = collectionPointTypeView.findViewById(R.id.collection_point_filter_type_checkbox);

        collectionPointTypeName.setText(organization.getName());
        collectionPointTypeCheckBox.setChecked(checked);
        collectionPointTypeCheckBox.setTag(organization);

        collectionPointTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionPointTypeCheckBox.toggle();
            }
        });

        return collectionPointTypeView;
    }

    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return SelectOrganizationsFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetOrganizationsListService.class);
        return serviceClass;
    }

    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_ORGANIZATIONS_LIST_REQUEST) {
            if (apiResult.isValidResponse()) {
                ApiGetOrganizationsListResult apiGetOrganizationsListResult = (ApiGetOrganizationsListResult) apiResult.getResult();
                setupOrganizations(apiGetOrganizationsListResult.getOrganizationList());
            } else {
                setupOrganizations(mSelectedOrganizations);
            }
        }
        dismissProgressDialog();
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }
}

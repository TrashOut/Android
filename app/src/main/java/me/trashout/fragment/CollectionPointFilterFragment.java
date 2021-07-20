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

package me.trashout.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ICollectionPointFragment;
import me.trashout.model.CollectionPointFilter;
import me.trashout.model.Constants;
import me.trashout.utils.PreferencesHandler;


public class CollectionPointFilterFragment extends BaseFragment implements ICollectionPointFragment {


    @BindView(R.id.collection_point_filter_display_title)
    TextView collectionPointFilterDisplayTitle;
    @BindView(R.id.collection_point_filter_display_spinner)
    AppCompatSpinner collectionPointFilterDisplaySpinner;
    @BindView(R.id.collection_point_filter_type_title)
    TextView collectionPointFilterTypeTitle;
    @BindView(R.id.collection_point_filter_type_container)
    LinearLayout collectionPointFilterTypeContainer;

    private CollectionPointListFragment.OnRefreshCollectionPointListListener mCallback;
    private LayoutInflater inflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (CollectionPointListFragment.OnRefreshCollectionPointListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_point_filter, container, false);
        ButterKnife.bind(this, view);

        this.inflater = inflater;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.collection_point_filter_size_array, R.layout.view_spiner_item);
        collectionPointFilterDisplaySpinner.setAdapter(adapter);

        setupCollectionPointsFilter();

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
                saveCollectionPointsFilter();
                finish();
                return true;
            default:
                break;
        }

        return false;
    }

    private void saveCollectionPointsFilter() {
        CollectionPointFilter collectionPointFilter = new CollectionPointFilter();

        Constants.CollectionPointSize collectionPointSize = null;
        switch (collectionPointFilterDisplaySpinner.getSelectedItemPosition()) {
            case 0:
                collectionPointSize = Constants.CollectionPointSize.ALL;
                break;
            case 1:
                collectionPointSize = Constants.CollectionPointSize.DUSTBIN;
                break;
            case 2:
                collectionPointSize = Constants.CollectionPointSize.SCRAPYARD;
                break;
        }

        collectionPointFilter.setCollectionPointSize(collectionPointSize);

        ArrayList<Constants.CollectionPointType> collectionPointTypeList = new ArrayList<>();

        for (int i = 0; i < collectionPointFilterTypeContainer.getChildCount(); i++) {
            View collectionPointFiltrTypeView = collectionPointFilterTypeContainer.getChildAt(i);
            AppCompatCheckBox collectionPointTypeCheckBox = (AppCompatCheckBox) collectionPointFiltrTypeView.findViewById(R.id.collection_point_filter_type_checkbox);
            if (collectionPointTypeCheckBox.isChecked())
                collectionPointTypeList.add((Constants.CollectionPointType) collectionPointTypeCheckBox.getTag());
        }

        collectionPointFilter.setCollectionPointTypes(collectionPointTypeList);

        PreferencesHandler.setCollectionPointFilterData(getContext(), collectionPointFilter);

        mCallback.onRefreshCollectionPointList();
    }


    private void setupCollectionPointsFilter() {
        CollectionPointFilter collectionPointFilter = PreferencesHandler.getCollectionPointFilterData(getContext());

        if (collectionPointFilter != null) {

            if (collectionPointFilter.getCollectionPointSize() != null) {
                switch (collectionPointFilter.getCollectionPointSize()) {
                    case ALL:
                        collectionPointFilterDisplaySpinner.setSelection(0);
                        break;
                    case DUSTBIN:
                        collectionPointFilterDisplaySpinner.setSelection(1);
                        break;
                    case SCRAPYARD:
                        collectionPointFilterDisplaySpinner.setSelection(2);
                        break;
                }
            }
        }

        collectionPointFilterTypeContainer.removeAllViews();

        List<Constants.CollectionPointType> collectionPointTypes = Arrays.asList(Constants.CollectionPointType.values());
        for (Constants.CollectionPointType collectionPointType : collectionPointTypes) {
            collectionPointFilterTypeContainer.addView(getCollectionPointTypeView(collectionPointType, (collectionPointFilter != null && collectionPointFilter.getCollectionPointTypes() != null && collectionPointFilter.getCollectionPointTypes().contains(collectionPointType))));
        }
    }

    private View getCollectionPointTypeView(Constants.CollectionPointType collectionPointType, boolean checked) {
        View collectionPointTypeView = inflater.inflate(R.layout.layout_collection_point_filter_type, null);

        TextView collectionPointTypeName = (TextView) collectionPointTypeView.findViewById(R.id.collection_point_filter_type_name);
        final AppCompatCheckBox collectionPointTypeCheckBox = (AppCompatCheckBox) collectionPointTypeView.findViewById(R.id.collection_point_filter_type_checkbox);

        collectionPointTypeName.setText(collectionPointType.getStringResId());
        collectionPointTypeCheckBox.setChecked(checked);
        collectionPointTypeCheckBox.setTag(collectionPointType);

        collectionPointTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionPointTypeCheckBox.toggle();
            }
        });

        return collectionPointTypeView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.collectionPoint_filter_header));
    }
}
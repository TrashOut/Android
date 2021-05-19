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

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.adapter.AreaSpinnerAdapter;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetAreaListResult;
import me.trashout.api.result.ApiGetTrashCountResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Area;
import me.trashout.service.GetAreaListService;
import me.trashout.service.GetTrashCountService;
import me.trashout.service.base.BaseService;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 17.02.2017
 */
public class StatisticsFragment extends BaseFragment implements ITrashFragment, BaseService.UpdateServiceListener {

    private static final int GET_TRASH_COUNT_REQUEST_ID = 990;
    private static final int GET_AREA_LIST_REQUEST_ID = 991;

    @BindView(R.id.statistic_area_spinner)
    AppCompatSpinner statisticAreaSpinner;
    @BindView(R.id.statistics_reported)
    TextView statisticsReported;
    @BindView(R.id.statistics_cleaned)
    TextView statisticsCleaned;
    @BindView(R.id.statistic_chart)
    PieChart statisticChart;

    private String mSelectedAreaCounty;

    private Integer mCleaned;
    private Integer mReported;

    private List<Area> mAreaList;

    private AreaSpinnerAdapter adapter;
    private boolean loadingArea = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);

        statisticChart.highlightValues(null);
        statisticChart.getDescription().setEnabled(false);
        statisticChart.setUsePercentValues(true);
        statisticChart.getLegend().setEnabled(false);
        statisticChart.setRotationEnabled(false);
        statisticChart.setHighlightPerTapEnabled(false);
        statisticChart.setExtraOffsets(20, 20, 20, 20);

        loadingArea = false;

        statisticChart.setNoDataText("");
//        statisticChart.setDrawHoleEnabled(false);
//        statisticChart.setHoleRadius(0);
//        statisticChart.setTransparentCircleRadius(0);

        if (mAreaList != null) {
            setAreaListData(mAreaList);
        } else {
            loadingArea = true;
            GetAreaListService.startForRequest(getActivity(), GET_AREA_LIST_REQUEST_ID);
        }

        return view;
    }

    private void setStatisticData(int reportedCount, int cleanedCount) {
        statisticsReported.setText(String.valueOf(reportedCount));
        statisticsCleaned.setText(String.valueOf(cleanedCount));


        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(cleanedCount));
        entries.add(new PieEntry(reportedCount));

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(getContext(), R.color.statistics_chart_cleaned));
        colors.add(ContextCompat.getColor(getContext(), R.color.statistics_chart_reported));


        PieDataSet set = new PieDataSet(entries, null);
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        set.setDrawValues(false);

        set.setColors(colors);

        PieData data = new PieData(set);

        statisticChart.setData(data);
        statisticChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        statisticChart.invalidate(); // refresh
    }

    private void setAreaListData(List<Area> areaList) {
        Area[] areasArray = new Area[areaList.size()];
        adapter = new AreaSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, areaList.toArray(areasArray));
        statisticAreaSpinner.setAdapter(adapter);
        statisticAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Area selectArea = adapter.getItem(position);
                if (position == 0 || selectArea == null)
                    mSelectedAreaCounty = null;
                else
                    mSelectedAreaCounty = selectArea.getCountry();
                GetTrashCountService.startForRequest(getActivity(), GET_TRASH_COUNT_REQUEST_ID, mSelectedAreaCounty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.statistics_header));
    }


    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return StatisticsFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetTrashCountService.class);
        serviceClass.add(GetAreaListService.class);
        return serviceClass;
    }

    @Override
    public void onNewResult(ApiResult apiResult) {
        if (getContext() == null)
            return;

        if (apiResult.getRequestId() == GET_TRASH_COUNT_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                ApiGetTrashCountResult apiGetTrashCountResult = (ApiGetTrashCountResult) apiResult.getResult();
                mCleaned = apiGetTrashCountResult.getCleanedCount();
                mReported = apiGetTrashCountResult.getReportedCount();
                setStatisticData(mReported, mCleaned);
            } else
                statisticChart.setNoDataText(getString(R.string.global_noData));
        } else if (apiResult.getRequestId() == GET_AREA_LIST_REQUEST_ID) {
            loadingArea = false;
            if (apiResult.isValidResponse()) {
                ApiGetAreaListResult apiGetAreaListResult = (ApiGetAreaListResult) apiResult.getResult();
                mAreaList = apiGetAreaListResult.getAreas();
                mAreaList.add(0, Area.createWorldwideArea(getContext()));
                setAreaListData(mAreaList);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }
}

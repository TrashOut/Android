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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.TrashHunterState;
import me.trashout.utils.PreferencesHandler;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 01.03.2017
 */
public class TrashHunterStartFragment extends BaseFragment {

    @BindView(R.id.trash_hunter_start_area_500)
    AppCompatRadioButton trashHunterStartArea500;
    @BindView(R.id.trash_hunter_start_area_500_layout)
    LinearLayout trashHunterStartArea500Layout;
    @BindView(R.id.trash_hunter_start_area_1000)
    AppCompatRadioButton trashHunterStartArea1000;
    @BindView(R.id.trash_hunter_start_area_1000_layout)
    LinearLayout trashHunterStartArea1000Layout;
    @BindView(R.id.trash_hunter_start_area_5000)
    AppCompatRadioButton trashHunterStartArea5000;
    @BindView(R.id.trash_hunter_start_area_5000_layout)
    LinearLayout trashHunterStartArea5000Layout;
    @BindView(R.id.trash_hunter_start_area_20000)
    AppCompatRadioButton trashHunterStartArea20000;
    @BindView(R.id.trash_hunter_start_area_20000_layout)
    LinearLayout trashHunterStartArea20000Layout;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.trash_hunter_start_duration_10_min)
    AppCompatRadioButton trashHunterStartDuration10Min;
    @BindView(R.id.trash_hunter_start_duration_10_min_layout)
    LinearLayout trashHunterStartDuration10MinLayout;
    @BindView(R.id.trash_hunter_start_duration_30_min)
    AppCompatRadioButton trashHunterStartDuration30Min;
    @BindView(R.id.trash_hunter_start_duration_30_min_layout)
    LinearLayout trashHunterStartDuration30MinLayout;
    @BindView(R.id.trash_hunter_start_duration_60_min)
    AppCompatRadioButton trashHunterStartDuration60Min;
    @BindView(R.id.trash_hunter_start_duration_60_min_layout)
    LinearLayout trashHunterStartDuration60MinLayout;
    @BindView(R.id.trash_hunter_start_btn)
    AppCompatButton trashHunterStartBtn;

    public interface OnTrashHunterStartListener{
        void onTrashHunterStart(TrashHunterState trashHunterState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash_hunter_start, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.trashHunter_startHunting));
    }

    @OnClick(R.id.trash_hunter_start_btn)
    public void onStartHunterModeClick() {

        int trashHunterArea = 500;
        long trashHunterDuration = 10 * 60 * 1000;
        if (trashHunterStartArea1000.isChecked()){
            trashHunterArea = 1000;
        }else  if (trashHunterStartArea5000.isChecked()){
            trashHunterArea = 5000;
        }else if (trashHunterStartArea20000.isChecked()){
            trashHunterArea = 20000;
        }

        if (trashHunterStartDuration30Min.isChecked()){
            trashHunterDuration = 30 * 60 * 1000;
        }else if (trashHunterStartDuration60Min.isChecked()){
            trashHunterDuration = 60 * 60 * 1000;
        }

        TrashHunterState trashHunterState = new TrashHunterState(trashHunterDuration,trashHunterArea);
        PreferencesHandler.setTrashHunterState(getContext(), trashHunterState);

        if (getTargetFragment() != null && getTargetFragment() instanceof  OnTrashHunterStartListener){
            ((OnTrashHunterStartListener) getTargetFragment()).onTrashHunterStart(trashHunterState);
        }
        finish();
    }

    @OnClick({R.id.trash_hunter_start_area_500, R.id.trash_hunter_start_area_1000, R.id.trash_hunter_start_area_5000, R.id.trash_hunter_start_area_20000, R.id.trash_hunter_start_area_500_layout, R.id.trash_hunter_start_area_1000_layout, R.id.trash_hunter_start_area_5000_layout, R.id.trash_hunter_start_area_20000_layout})
    public void onAreaChangeClick(View view) {
        trashHunterStartArea500.setChecked(view.getId() == R.id.trash_hunter_start_area_500_layout || view.getId() == R.id.trash_hunter_start_area_500);
        trashHunterStartArea1000.setChecked(view.getId() == R.id.trash_hunter_start_area_1000_layout || view.getId() == R.id.trash_hunter_start_area_1000);
        trashHunterStartArea5000.setChecked(view.getId() == R.id.trash_hunter_start_area_5000_layout || view.getId() == R.id.trash_hunter_start_area_5000);
        trashHunterStartArea20000.setChecked(view.getId() == R.id.trash_hunter_start_area_20000_layout || view.getId() == R.id.trash_hunter_start_area_20000);
    }

    @OnClick({R.id.trash_hunter_start_duration_10_min, R.id.trash_hunter_start_duration_30_min, R.id.trash_hunter_start_duration_60_min, R.id.trash_hunter_start_duration_10_min_layout, R.id.trash_hunter_start_duration_30_min_layout, R.id.trash_hunter_start_duration_60_min_layout})
    public void onDurationChangeClick(View view) {
        trashHunterStartDuration10Min.setChecked(view.getId() == R.id.trash_hunter_start_duration_10_min_layout || view.getId() == R.id.trash_hunter_start_duration_10_min);
        trashHunterStartDuration30Min.setChecked(view.getId() == R.id.trash_hunter_start_duration_30_min_layout || view.getId() == R.id.trash_hunter_start_duration_30_min);
        trashHunterStartDuration60Min.setChecked(view.getId() == R.id.trash_hunter_start_duration_60_min_layout || view.getId() == R.id.trash_hunter_start_duration_60_min);
    }
}

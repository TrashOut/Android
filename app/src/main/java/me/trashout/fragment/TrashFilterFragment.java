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
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Accessibility;
import me.trashout.model.Constants.LastUpdate;
import me.trashout.model.TrashFilter;
import me.trashout.ui.SelectableImageButton;
import me.trashout.utils.PreferencesHandler;

import static me.trashout.model.Constants.LastUpdate.LAST_MONTH;
import static me.trashout.model.Constants.LastUpdate.LAST_WEEK;
import static me.trashout.model.Constants.LastUpdate.LAST_YEAR;
import static me.trashout.model.Constants.LastUpdate.NO_LIMIT;
import static me.trashout.model.Constants.LastUpdate.TODAY;
import static me.trashout.model.Constants.TrashSize;
import static me.trashout.model.Constants.TrashStatus;
import static me.trashout.model.Constants.TrashType;


public class TrashFilterFragment extends BaseFragment implements ITrashFragment {

    @BindView(R.id.trash_filter_status_title)
    TextView trashFilterStatusTitle;
    @BindView(R.id.trash_filter_type_title)
    TextView trashFilterTypeTitle;
    @BindView(R.id.trash_report_type_household_btn)
    SelectableImageButton trashReportTypeHouseholdBtn;
    @BindView(R.id.trash_report_type_automotive_btn)
    SelectableImageButton trashReportTypeAutomotiveBtn;
    @BindView(R.id.trash_report_type_construction_btn)
    SelectableImageButton trashReportTypeConstructionBtn;
    @BindView(R.id.trash_report_type_plastic_btn)
    SelectableImageButton trashReportTypePlasticBtn;
    @BindView(R.id.trash_report_type_electronic_btn)
    SelectableImageButton trashReportTypeElectronicBtn;
    @BindView(R.id.trash_report_type_organic_btn)
    SelectableImageButton trashReportTypeOrganicBtn;
    @BindView(R.id.trash_report_type_metal_btn)
    SelectableImageButton trashReportTypeMetalBtn;
    @BindView(R.id.trash_report_type_liquid_btn)
    SelectableImageButton trashReportTypeLiquidBtn;
    @BindView(R.id.trash_report_type_dangerous_btn)
    SelectableImageButton trashReportTypeDangerousBtn;
    @BindView(R.id.trash_report_type_dead_animals_btn)
    SelectableImageButton trashReportTypeDeadAnimalsBtn;
    @BindView(R.id.trash_report_type_glass_btn)
    SelectableImageButton trashReportTypeGlassBtn;
    @BindView(R.id.trash_report_take_another_image)
    LinearLayout trashReportTakeAnotherImage;
    @BindView(R.id.trash_filter_status_updated_reported)
    AppCompatCheckBox trashFilterStatusUpdatedReported;
    @BindView(R.id.trash_filter_status_updated_needed)
    AppCompatCheckBox trashFilterStatusUpdatedNeeded;
    @BindView(R.id.trash_filter_status_cleaned)
    AppCompatCheckBox trashFilterStatusCleaned;
    @BindView(R.id.trash_filter_last_update_title)
    TextView trashFilterLastUpdateTitle;
    @BindView(R.id.trash_filter_last_update_text)
    TextView trashFilterLastUpdateText;
    @BindView(R.id.trash_filter_last_update_seek_bar)
    SeekBar trashFilterLastUpdateSeekBar;
    @BindView(R.id.trash_filter_accessibility_by_car)
    AppCompatCheckBox trashFilterAccessibilityByCar;
    @BindView(R.id.trash_filter_accessibility_in_cave)
    AppCompatCheckBox trashFilterAccessibilityInCave;
    @BindView(R.id.trash_filter_accessibility_under_water)
    AppCompatCheckBox trashFilterAccessibilityUnderWater;
    @BindView(R.id.trash_filter_accessibility_not_for_general_cleanup)
    AppCompatCheckBox trashFilterAccessibilityNotForGeneralCleanup;
    @BindView(R.id.trash_report_size_bag_btn)
    ImageButton trashReportSizeBagBtn;
    @BindView(R.id.trash_report_size_wheelbarrow_btn)
    ImageButton trashReportSizeWheelbarrowBtn;
    @BindView(R.id.trash_report_size_car_btn)
    ImageButton trashReportSizeCarBtn;

    private TrashListFragment.OnRefreshTrashListListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (TrashListFragment.OnRefreshTrashListListener) context;
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
        View view = inflater.inflate(R.layout.fragment_trash_filter, container, false);
        ButterKnife.bind(this, view);

        setupTrashFilter();

        trashFilterLastUpdateSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

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
                saveTrashFilter();
                finish();
                return true;
            default:
                break;
        }

        return false;
    }

    private void saveTrashFilter() {
        TrashFilter trashFilter = new TrashFilter();

        ArrayList<TrashStatus> trashStatusList = new ArrayList<>();

        if (trashFilterStatusUpdatedReported.isChecked())
            trashStatusList.add(TrashStatus.STILL_HERE);
        if (trashFilterStatusCleaned.isChecked())
            trashStatusList.add(TrashStatus.CLEANED);
        if (trashFilterStatusUpdatedNeeded.isChecked())
            trashStatusList.add(TrashStatus.UNKNOWN);

        trashFilter.setTrashStatusList(trashStatusList);

        LastUpdate lastUpdate = NO_LIMIT;
        switch (trashFilterLastUpdateSeekBar.getProgress()) {
            case 0:
                lastUpdate = TODAY;
                break;
            case 1:
                lastUpdate = LAST_WEEK;
                break;
            case 2:
                lastUpdate = LAST_MONTH;
                break;
            case 3:
                lastUpdate = LAST_YEAR;
                break;
        }
        trashFilter.setLastUpdate(lastUpdate);

        ArrayList<TrashSize> trashSizeList = new ArrayList<>();

        if (trashReportSizeBagBtn.isSelected())
            trashSizeList.add(TrashSize.BAG);
        if (trashReportSizeWheelbarrowBtn.isSelected())
            trashSizeList.add(TrashSize.WHEELBARROW);
        if (trashReportSizeCarBtn.isSelected())
            trashSizeList.add(TrashSize.CAR);

        trashFilter.setTrashSizeList(trashSizeList);

        Accessibility accessibility = new Accessibility();

        accessibility.setByCar(trashFilterAccessibilityByCar.isChecked());
        accessibility.setInCave(trashFilterAccessibilityInCave.isChecked());
        accessibility.setUnderWater(trashFilterAccessibilityUnderWater.isChecked());
        accessibility.setNotForGeneralCleanup(trashFilterAccessibilityNotForGeneralCleanup.isChecked());

        trashFilter.setAccessibility(accessibility);

        ArrayList<TrashType> trashTypeList = new ArrayList<>();

        if (trashReportTypeHouseholdBtn.isSelected())
            trashTypeList.add(TrashType.DOMESTIC);

        if (trashReportTypeAutomotiveBtn.isSelected())
            trashTypeList.add(TrashType.AUTOMOTIVE);

        if (trashReportTypeConstructionBtn.isSelected())
            trashTypeList.add(TrashType.CONSTRUCTION);

        if (trashReportTypePlasticBtn.isSelected())
            trashTypeList.add(TrashType.PLASTIC);

        if (trashReportTypeElectronicBtn.isSelected())
            trashTypeList.add(TrashType.ELECTRICS);

        if (trashReportTypeOrganicBtn.isSelected())
            trashTypeList.add(TrashType.ORGANIC);

        if (trashReportTypeMetalBtn.isSelected())
            trashTypeList.add(TrashType.METAL);

        if (trashReportTypeLiquidBtn.isSelected())
            trashTypeList.add(TrashType.LIQUID);

        if (trashReportTypeDangerousBtn.isSelected())
            trashTypeList.add(TrashType.DANGEROUS);

        if (trashReportTypeDeadAnimalsBtn.isSelected())
            trashTypeList.add(TrashType.DEAD_ANIMALS);

        if (trashReportTypeGlassBtn.isSelected())
            trashTypeList.add(TrashType.GLASS);

        trashFilter.setTrashTypes(trashTypeList);

        PreferencesHandler.setTrashFilterData(getContext(), trashFilter);

        mCallback.onRefreshTrashList();
    }


    private void setupTrashFilter() {
        TrashFilter trashFilter = PreferencesHandler.getTrashFilterData(getContext());

        if (trashFilter != null) {

            if (trashFilter.getTrashStatusList() != null) {
                trashFilterStatusUpdatedReported.setChecked(trashFilter.getTrashStatusList().contains(TrashStatus.STILL_HERE));
                trashFilterStatusCleaned.setChecked(trashFilter.getTrashStatusList().contains(TrashStatus.CLEANED));
                // TODO How status?
                trashFilterStatusUpdatedNeeded.setChecked(trashFilter.getTrashStatusList().contains(TrashStatus.UNKNOWN));
            }

            if (trashFilter.getLastUpdate() != null) {
                int lastUpdateProgress = 0;
                switch (trashFilter.getLastUpdate()) {
                    case TODAY:
                        lastUpdateProgress = 0;
                        trashFilterLastUpdateText.setText(R.string.trash_filter_lastUpdate_today);
                        break;
                    case LAST_WEEK:
                        lastUpdateProgress = 1;
                        trashFilterLastUpdateText.setText(R.string.trash_filter_lastUpdate_lastWeek);
                        break;
                    case LAST_MONTH:
                        lastUpdateProgress = 2;
                        trashFilterLastUpdateText.setText(R.string.trash_filter_lastUpdate_lastMonth);
                        break;
                    case LAST_YEAR:
                        lastUpdateProgress = 3;
                        trashFilterLastUpdateText.setText(R.string.trash_filter_lastUpdate_lastYear);
                        break;
                    default:
                        lastUpdateProgress = 4;
                        trashFilterLastUpdateText.setText(R.string.trash_filter_lastUpdate_noLimit);
                        break;
                }
                trashFilterLastUpdateSeekBar.setProgress(lastUpdateProgress);
            }

            if (trashFilter.getTrashSizeList() != null) {
                trashReportSizeBagBtn.setSelected(trashFilter.getTrashSizeList().contains(TrashSize.BAG));
                trashReportSizeWheelbarrowBtn.setSelected(trashFilter.getTrashSizeList().contains(TrashSize.WHEELBARROW));
                trashReportSizeCarBtn.setSelected(trashFilter.getTrashSizeList().contains(TrashSize.CAR));
            }


            if (trashFilter.getAccessibility() != null) {
                trashFilterAccessibilityByCar.setChecked(trashFilter.getAccessibility().isByCar());
                trashFilterAccessibilityInCave.setChecked(trashFilter.getAccessibility().isInCave());
                trashFilterAccessibilityUnderWater.setChecked(trashFilter.getAccessibility().isUnderWater());
                trashFilterAccessibilityNotForGeneralCleanup.setChecked(trashFilter.getAccessibility().isNotForGeneralCleanup());
            }


            if (trashFilter.getTrashTypes() != null) {
                trashReportTypeHouseholdBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.DOMESTIC));
                trashReportTypeAutomotiveBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.AUTOMOTIVE));
                trashReportTypeConstructionBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.CONSTRUCTION));
                trashReportTypePlasticBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.PLASTIC));
                trashReportTypeElectronicBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.ELECTRICS));
                trashReportTypeOrganicBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.ORGANIC));
                trashReportTypeMetalBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.METAL));
                trashReportTypeLiquidBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.LIQUID));
                trashReportTypeDangerousBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.DANGEROUS));
                trashReportTypeDeadAnimalsBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.DEAD_ANIMALS));
                trashReportTypeGlassBtn.setSelected(trashFilter.getTrashTypes().contains(TrashType.GLASS));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.global_filter));
    }


    @OnClick({R.id.trash_report_type_household_btn, R.id.trash_report_type_automotive_btn, R.id.trash_report_type_construction_btn, R.id.trash_report_type_plastic_btn, R.id.trash_report_type_electronic_btn, R.id.trash_report_type_organic_btn, R.id.trash_report_type_metal_btn, R.id.trash_report_type_liquid_btn, R.id.trash_report_type_dangerous_btn, R.id.trash_report_type_dead_animals_btn, R.id.trash_report_type_glass_btn})
    public void onTrashTypeClick(View view) {
        view.setSelected(!view.isSelected());
        /*switch (view.getId()) {
            case R.id.trash_report_type_household_btn:
                trashReportTypeHouseholdBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_automotive_btn:
                trashReportTypeAutomotiveBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_construction_btn:
                trashReportTypeConstructionBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_plastic_btn:
                trashReportTypePlasticBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_electronic_btn:
                trashReportTypeElectronicBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_organic_btn:
                trashReportTypeOrganicBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_metal_btn:
                trashReportTypeMetalBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_liquid_btn:
                trashReportTypeLiquidBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_dangerous_btn:
                trashReportTypeDangerousBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_dead_animals_btn:
                trashReportTypeDeadAnimalsBtn.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_type_glass_btn:
                trashReportTypeGlassBtn.setSelected(!view.isSelected());
                break;
        }*/
    }

    @OnClick({R.id.trash_report_size_bag_btn, R.id.trash_report_size_wheelbarrow_btn, R.id.trash_report_size_car_btn})
    public void onTrashSizeClick(View view) {

        switch (view.getId()) {
            case R.id.trash_report_size_bag_btn:
                view.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_size_wheelbarrow_btn:
                view.setSelected(!view.isSelected());
                break;
            case R.id.trash_report_size_car_btn:
                view.setSelected(!view.isSelected());
                break;
        }
    }


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int selectedLastUpdateText = R.string.trash_filter_lastUpdate_noLimit;
            switch (i) {
                case 0:
                    selectedLastUpdateText = R.string.trash_filter_lastUpdate_today;
                    break;
                case 1:
                    selectedLastUpdateText = R.string.trash_filter_lastUpdate_lastWeek;
                    break;
                case 2:
                    selectedLastUpdateText = R.string.trash_filter_lastUpdate_lastMonth;
                    break;
                case 3:
                    selectedLastUpdateText = R.string.trash_filter_lastUpdate_lastYear;
                    break;
                case 4:
                    selectedLastUpdateText = R.string.trash_filter_lastUpdate_noLimit;
                    break;
            }

            trashFilterLastUpdateText.setText(selectedLastUpdateText);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
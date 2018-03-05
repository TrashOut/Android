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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.BuildConfig;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.Constants;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.Utils;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 15.12.2016
 */
public class AboutFragment extends BaseFragment {

    @BindView(R.id.about_feedback_btn)
    AppCompatButton aboutFeedbackBtn;
    @BindView(R.id.about_privacy_policy_btn)
    AppCompatButton aboutPrivacyPolicyBtn;
    @BindView(R.id.about_terms_btn)
    AppCompatButton aboutTermsBtn;
    @BindView(R.id.about_version_date)
    TextView aboutVersionDate;
    @BindView(R.id.about_version_name)
    TextView aboutVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        aboutVersionDate.setText(String.format(getString(R.string.about_version_date_formatted), DateTimeUtils.VERSION_DATE_FORMAT.format(BuildConfig.buildDate)));
        aboutVersionName.setText(String.format(getString(R.string.about_version_formatted), Utils.getAppVersionName(getContext())));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.info_header_aboutApp));
    }

    @OnClick({R.id.about_feedback_btn, R.id.about_privacy_policy_btn, R.id.about_terms_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_feedback_btn:
                Utils.sendEmail(getActivity(), Constants.FEEDBACK_EMAIL_ADDRESS);
                break;
            case R.id.about_privacy_policy_btn:
                Utils.browseUrl(getActivity(), Constants.PRIVACY_POLICY_HYPERLINK);
                break;
            case R.id.about_terms_btn:
                Utils.browseUrl(getActivity(), Constants.TERMS_CONDITIONS_HYPERLINK);
                break;
        }
    }
}

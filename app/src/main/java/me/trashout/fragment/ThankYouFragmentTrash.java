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

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.TrashResponse;

public class ThankYouFragmentTrash extends BaseFragment {

    public static final String BUNDLE_TRASH_RESPONSE = "BUNDLE_TRASH_RESPONSE";

    @BindView(R.id.txt_title)
    TextView thankYouTitle;

    private TrashResponse trashResponse;

    public static ThankYouFragmentTrash newInstance(TrashResponse trashResponse) {
        Bundle b = new Bundle();
        b.putParcelable(BUNDLE_TRASH_RESPONSE, trashResponse);
        ThankYouFragmentTrash ret = new ThankYouFragmentTrash();
        ret.setArguments(b);
        return ret;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thank_you_trash, container, false);
        ButterKnife.bind(this, view);

        this.trashResponse = getArguments().getParcelable(BUNDLE_TRASH_RESPONSE);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/capriola.ttf");
        thankYouTitle.setTypeface(typeface);

        return view;
    }

    @OnClick(R.id.share_with_others_btn)
    public void clickOnShare() {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(Configuration.ADMIN_TRASH_DETAIL_URL + trashResponse.getId())
                .startChooser();
    }



    @OnClick({R.id.go_to_trash_detail_btn})
    public void onClick() {
        TrashDetailFragment trashDetailFragment = TrashDetailFragment.newInstance(trashResponse.getId());
        getBaseActivity().replaceFragment(trashDetailFragment, false);
    }

}

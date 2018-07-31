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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.Image;
import me.trashout.ui.HackyViewPager;
import me.trashout.ui.SquarePhotoView;
import me.trashout.utils.DateTimeUtils;

public class PhotoFullscreenFragment extends BaseFragment {

    private static final String BUNDLE_PHOTOS_URL = "BUNDLE_PHOTOS_URL";
    private static final String BUNDLE_REPORTER_NAME = "BUNDLE_REPORTER_NAME";
    private static final String BUNDLE_REPORT_DATE = "BUNDLE_REPORT_DATE";
    private static final String BUNDLE_EXACT_DATE = "BUNDLE_EXACT_DATE";
    private static final String BUNDLE_CURRENT_PHOTO_POSITION = "BUNDLE_CURRENT_PHOTO_POSITION";

    @BindView(R.id.pager)
    HackyViewPager pager;
    @BindView(R.id.photo_fullscreen_reporter_name)
    TextView photoFullscreenReporterName;
    @BindView(R.id.photo_fullscreen_report_date)
    TextView photoFullscreenReportDate;
    @BindView(R.id.photo_fullscreen_toolbar_back)
    ImageView photoFullscreenToolbarBack;
    @BindView(R.id.photo_fullscreen_toolbar_title)
    TextView photoFullscreenToolbarTitle;
    @BindView(R.id.photo_fullscreen_toolbar)
    Toolbar photoFullscreenToolbar;

    private ArrayList<Image> mPhotos;
    private String mReporterName;
    private Date mReportDate;
    private Boolean mIsExactDate;

    public static PhotoFullscreenFragment newInstance(ArrayList<Image> photosUrl, String reporterName, Date reportDate, int selectedPhotoPosition) {
        return newInstance(photosUrl, reporterName, reportDate, selectedPhotoPosition, false);
    }

    public static PhotoFullscreenFragment newInstance(ArrayList<Image> photosUrl, String reporterName, Date reportDate,  int selectedPhotoPosition, boolean exactDate) {
        Bundle b = new Bundle();
        b.putParcelableArrayList(BUNDLE_PHOTOS_URL, photosUrl);
        b.putString(BUNDLE_REPORTER_NAME, reporterName);
        b.putSerializable(BUNDLE_REPORT_DATE, reportDate);
        b.putBoolean(BUNDLE_EXACT_DATE, exactDate);
        b.putInt(BUNDLE_CURRENT_PHOTO_POSITION, selectedPhotoPosition);
        PhotoFullscreenFragment ret = new PhotoFullscreenFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    protected boolean useCustomFragmentToolbar() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_fullscreen, container, false);
        ButterKnife.bind(this, view);

        pager.setAdapter(new PhotoPagerAdapter(getContext(), getPhotos()));
        pager.setCurrentItem(getCurrentPhotoPosition());
        pager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.photo_fullscreen_page_margin));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                photoFullscreenToolbarTitle.setText(String.format(getString(R.string.photo_fullscreen_title_formatted), position + 1, getPhotos().size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        photoFullscreenToolbarTitle.setText(String.format(getString(R.string.photo_fullscreen_title_formatted), getCurrentPhotoPosition() + 1, getPhotos().size()));

        photoFullscreenReporterName.setText(getReporterName());
        if (isExactDate()) {
            photoFullscreenReportDate.setText(DateTimeUtils.DATE_FORMAT.format(getReportDate()));
        } else {
            photoFullscreenReportDate.setText(getString(R.string.notifications_reported) + " " + DateTimeUtils.getRoundedTimeAgo(getContext(), getReportDate()));
        }

        return view;
    }

    /**
     * Get position of selected photo by user
     * @return
     */
    private int getCurrentPhotoPosition() {
        return getArguments().getInt(BUNDLE_CURRENT_PHOTO_POSITION, 0);
    }

    /**
     * Get photos url
     *
     * @return
     */
    private ArrayList<Image> getPhotos() {
        if (mPhotos == null)
            mPhotos = getArguments().getParcelableArrayList(BUNDLE_PHOTOS_URL);
        return mPhotos;
    }

    /**
     * Get reported name
     *
     * @return
     */
    private String getReporterName() {
        if (mReporterName == null)
            mReporterName = getArguments().getString(BUNDLE_REPORTER_NAME);
        return mReporterName;
    }


    /**
     * get reported date
     *
     * @return
     */
    private Date getReportDate() {
        if (mReportDate == null)
            mReportDate = (Date) getArguments().getSerializable(BUNDLE_REPORT_DATE);
        return mReportDate;
    }

    private Boolean isExactDate() {
        if (mIsExactDate == null)
            mIsExactDate = getArguments().getBoolean(BUNDLE_EXACT_DATE, false);
        return mIsExactDate;
    }

    @OnClick(R.id.photo_fullscreen_toolbar_back)
    public void onBackClick() {
        finish();
    }

    static class PhotoPagerAdapter extends PagerAdapter {

        private final ArrayList<Image> photos;
        private final Context context;

        PhotoPagerAdapter(Context context, ArrayList<Image> photos) {
            this.context = context;
            this.photos = photos;
        }


        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final SquarePhotoView photoView = new SquarePhotoView(container.getContext());
            photoView.setAdjustViewBounds(true);

            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photos.get(position).getFullStorageLocation());
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(mImageRef)
                    .centerCrop()
                    .crossFade()
                    .placeholder(R.drawable.ic_image_placeholder_rectangle)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            photoView.setImageDrawable(resource);
                        }
                    });

            container.addView(photoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}

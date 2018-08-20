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

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.presentation.FullScreenImage;
import me.trashout.ui.HackyViewPager;
import me.trashout.ui.SquarePhotoView;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GlideApp;
import me.trashout.utils.ViewUtils;

public class PhotoFullscreenFragment extends BaseFragment {

    private static final String BUNDLE_PHOTOS_URL = "BUNDLE_PHOTOS_URL";
    private static final String BUNDLE_FULLSCREEN_IMAGES = "BUNDLE_FULLSCREEN_IMAGES";
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

    private ArrayList<FullScreenImage> mImages;
    private String mReporterName;
    private Date mReportDate;
    private Boolean mIsExactDate;


    public static PhotoFullscreenFragment newInstance(ArrayList<FullScreenImage> images, int selectedPhotoPosition, boolean exactDate) {
        Bundle b = new Bundle();

        b.putParcelableArrayList(BUNDLE_FULLSCREEN_IMAGES, images);
        b.putInt(BUNDLE_CURRENT_PHOTO_POSITION, selectedPhotoPosition);
        b.putBoolean(BUNDLE_EXACT_DATE, exactDate);

        PhotoFullscreenFragment ret = new PhotoFullscreenFragment();
        ret.setArguments(b);
        return ret;
    }

    public static PhotoFullscreenFragment newInstance(ArrayList<FullScreenImage> images, int selectedPhotoPosition) {
        return newInstance(images, selectedPhotoPosition, false);
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
                photoFullscreenToolbarTitle.setText(String.format(getString(R.string.photo_fullscreen_title_formatted), position + 1, mImages.size()));
                renderReporterNameAndDate(mImages.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        renderReporterNameAndDate(mImages.get(getCurrentPhotoPosition()));
        photoFullscreenToolbarTitle.setText(String.format(getString(R.string.photo_fullscreen_title_formatted), getCurrentPhotoPosition() + 1, getPhotos().size()));

        return view;
    }

    private void renderReporterNameAndDate (FullScreenImage image)
    {
        photoFullscreenReporterName.setText(image.getUserName());
        if (isExactDate()) {
            photoFullscreenReportDate.setText(DateTimeUtils.DATE_FORMAT.format(image.getImageCreated()));
        } else {
            photoFullscreenReportDate.setText(getString(R.string.notifications_reported) + " " + DateTimeUtils.getRoundedTimeAgo(getContext(), image.getImageCreated()));
        }
    }

    /**
     * Get position of selected photo by user
     *
     * @return
     */
    private int getCurrentPhotoPosition() {
        return getArguments().getInt(BUNDLE_CURRENT_PHOTO_POSITION, 0);
    }

    /**
     * Get images url
     *
     * @return
     */
    private ArrayList<FullScreenImage> getPhotos() {
        if (mImages == null)
            mImages = getArguments().getParcelableArrayList(BUNDLE_FULLSCREEN_IMAGES);
        return mImages;
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

        private final ArrayList<FullScreenImage> images;
        private final Context context;

        PhotoPagerAdapter(Context context, ArrayList<FullScreenImage> images) {
            this.context = context;
            this.images = images;
        }


        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final SquarePhotoView photoView = new SquarePhotoView(container.getContext());
            photoView.setAdjustViewBounds(true);

            if (images != null && !images.isEmpty() && ViewUtils.checkImageStorage(images.get(position).getImage())) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(images.get(position).getImage().getFullStorageLocation());
                GlideApp.with(context)
                        .load(mImageRef)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_image_placeholder_rectangle)
                        .into(photoView);
            }

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

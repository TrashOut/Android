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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetNewsDetailResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.INewsFragment;
import me.trashout.model.Constants;
import me.trashout.model.Image;
import me.trashout.model.NewsDetail;
import me.trashout.model.NewsVideo;
import me.trashout.model.presentation.FullScreenImage;
import me.trashout.service.GetNewsDetailService;
import me.trashout.service.base.BaseService;
import me.trashout.ui.SquareImageView;
import me.trashout.utils.DateTimeUtils;
import me.trashout.utils.GlideApp;
import me.trashout.utils.Utils;
import me.trashout.utils.ViewUtils;
import ru.noties.markwon.Markwon;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 13.02.2017
 */
public class NewsDetailFragment extends BaseFragment implements INewsFragment, BaseService.UpdateServiceListener {


    private static final int GET_NEWS_DETAIL_REQUEST_ID = 861;

    private static final String BUNDLE_NEWS_ID = "BUNDLE_NEWS_ID";
    @BindView(R.id.news_detail_body)
    TextView newsDetailBody;
    @BindView(R.id.news_detail_tags)
    TextView newsDetailTags;
    @BindView(R.id.news_detail_author)
    TextView newsDetailAuthor;
    @BindView(R.id.news_detail_area)
    TextView newsDetailArea;
    @BindView(R.id.news_detail_attached_photo)
    TextView newsDetailAttachedPhoto;
    @BindView(R.id.news_detail_attached_image_container)
    LinearLayout newsDetailAttachedImageContainer;
    @BindView(R.id.news_detail_attached_video)
    TextView newsDetailAttachedVideo;
    @BindView(R.id.news_detail_attached_video_container)
    LinearLayout newsDetailAttachedVideoContainer;
    @BindView(R.id.news_detail_edit_fab)
    FloatingActionButton newsDetailEditFab;
    @BindView(R.id.news_detail_image)
    ImageView newsDetailImage;
    @BindView(R.id.news_detail_date)
    TextView newsDetailDate;
    @BindView(R.id.news_detail_title)
    TextView newsDetailTitle;
    @BindView(R.id.news_detail_toolbar_back)
    ImageView newsDetailToolbarBack;
    @BindView(R.id.news_detail_toolbar)
    Toolbar newsDetailToolbar;
    @BindView(R.id.news_detail_collapsing)
    CollapsingToolbarLayout newsDetailCollapsing;
    @BindView(R.id.news_detail_appbar)
    AppBarLayout newsDetailAppbar;

    private Long mNewsId;
    private NewsDetail mNews;
    private ArrayList<FullScreenImage> mFullscreenImages = new ArrayListAccumulator<>();

    public static NewsDetailFragment newInstance(Long newsId) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_NEWS_ID, newsId);
        NewsDetailFragment ret = new NewsDetailFragment();
        ret.setArguments(b);
        return ret;
    }

    @Override
    protected boolean useCustomFragmentToolbar() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        ButterKnife.bind(this, view);

        newsDetailToolbar.inflateMenu(R.menu.menu_trash_detail);
        newsDetailToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_share) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mNews.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.global_share)));
                    return true;
                }
                return false;
            }
        });

        if (mNews == null) {
            showProgressDialog();
            GetNewsDetailService.startForRequest(getActivity(), GET_NEWS_DETAIL_REQUEST_ID, getNewsId());
        } else {
            setupNewsData(mNews);
        }

        return view;
    }

    /**
     * Get collection point id
     *
     * @return
     */
    private Long getNewsId() {
        if (mNewsId == null)
            mNewsId = getArguments().getLong(BUNDLE_NEWS_ID);
        return mNewsId;
    }


    public void setupNewsData(NewsDetail news) {
        if (isAdded()) {
            newsDetailTitle.setText(news.getTitle());
            if (news.getBodyMarkdown() != null && !news.getBodyMarkdown().isEmpty()) {
                Markwon.setMarkdown(newsDetailBody, news.getBodyMarkdown());
            }
            newsDetailDate.setText(DateTimeUtils.DATE_FORMAT.format(news.getCreated()));
            newsDetailTags.setText(Html.fromHtml(String.format(getString(R.string.news_detail_placeholder), getString(R.string.news_tags), news.getTags())));

            if (news.getUser().getFullName() == null || news.getUser().getFullName().isEmpty()) {
                newsDetailAuthor.setVisibility(View.GONE);
            } else {
                newsDetailAuthor.setText(Html.fromHtml(String.format(getString(R.string.news_detail_placeholder), getString(R.string.news_author), news.getUser().getFullName())));
                newsDetailAuthor.setVisibility(View.VISIBLE);
            }
            if (news.getArea() == null || news.getArea().getContinent() == null || news.getArea().getCountry() == null || news.getArea().getCountry().isEmpty()) {
                newsDetailArea.setVisibility(View.GONE);
            } else {
                newsDetailArea.setText(Html.fromHtml(String.format(getString(R.string.news_detail_placeholder), getString(R.string.global_menu_geoAreas), news.getArea().getContinent() + ", " + news.getArea().getCountry())));
                newsDetailArea.setVisibility(View.VISIBLE);
            }

            if (news.getImages() != null && !news.getImages().isEmpty()) {
                for (Image image : news.getImages()) {
                    if (ViewUtils.checkImageStorage(image) && image.isMain()) {
                        StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(news.getImages().get(0).getFullStorageLocation());
                        GlideApp.with(this)
                                .load(mImageRef)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .thumbnail(GlideApp.with(this).load(R.drawable.ic_image_placeholder_rectangle).centerCrop())
                                .into(newsDetailImage);

                        break;
                    }
                }
            }


            setNewsImages(news);


            if (news.getImages() != null && !news.getImages().isEmpty()) {
                newsDetailAttachedPhoto.setVisibility(View.VISIBLE);
            } else {
                newsDetailAttachedPhoto.setVisibility(View.GONE);
            }
            if (news.getNewsVideos() != null && !news.getNewsVideos().isEmpty()) {
                newsDetailAttachedVideo.setVisibility(View.VISIBLE);
            } else {
                newsDetailAttachedVideo.setVisibility(View.GONE);
            }
            setNewsVideos(news);
        }

    }

    private void setNewsVideos(NewsDetail news) {
        if (news.isContainVideos()) {
            newsDetailAttachedVideoContainer.removeAllViews();
            for (final NewsVideo newsVideo : news.getNewsVideos()) {
                SquareImageView squareImageView = new SquareImageView(getContext());
                squareImageView.setAdjustViewBounds(true);
                int imageSize = getResources().getDimensionPixelSize(R.dimen.news_image_size);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                layoutParams.setMargins(0, 0, 20, 0);
                GlideApp.with(this)
                        .load(Constants.YOUTUBE_URL_PREFIX + Utils.getYoutubeVideoId(newsVideo.getUrl()) + Constants.YOUTUBE_URL_SUFIX)
                        .centerCrop()
                        .thumbnail(0.2f)
                        .override(imageSize, imageSize)
                        .placeholder(R.drawable.ic_image_placeholder_square)
                        .into(squareImageView);
                squareImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsVideo.getUrl()));
                        startActivity(browserIntent);
                    }
                });
                newsDetailAttachedVideoContainer.addView(squareImageView, layoutParams);
            }
            newsDetailAttachedVideoContainer.setVisibility(newsDetailAttachedVideoContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE);
        } else {
            newsDetailAttachedVideoContainer.setVisibility(View.GONE);
        }
    }

    private void setNewsImages(NewsDetail news) {
        mFullscreenImages.clear();
        if (news.isContainImages()) {
            newsDetailAttachedImageContainer.removeAllViews();
            for (Image image : news.getImages()) {
                if (ViewUtils.checkImageStorage(image)) {
                    SquareImageView squareImageView = new SquareImageView(getContext());
                    squareImageView.setAdjustViewBounds(true);
                    int imageSize = getResources().getDimensionPixelSize(R.dimen.news_image_size);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                    layoutParams.setMargins(0, 0, 20, 0);
                    StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image.getFullStorageLocation());
                    GlideApp.with(this)
                            .load(mImageRef)
                            .centerCrop()
                            .thumbnail(0.2f)
                            .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_image_placeholder_square)
                            .into(squareImageView);
                    squareImageView.setOnClickListener(onAttachedImageClickListener);
                    newsDetailAttachedImageContainer.addView(squareImageView, layoutParams);
                }

                mFullscreenImages.add(new FullScreenImage(image, mNews.getTitle(), mNews.getCreated()));
            }
            newsDetailAttachedImageContainer.setVisibility(newsDetailAttachedImageContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE);
        } else {
            newsDetailAttachedImageContainer.setVisibility(View.GONE);
        }

    }

    private View.OnClickListener onAttachedImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mNews != null) {
                PhotoFullscreenFragment photoFullscreenFragment = PhotoFullscreenFragment.newInstance(mFullscreenImages,
                        0,
                        true);
                getBaseActivity().replaceFragment(photoFullscreenFragment);
            }
        }
    };

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return NewsDetailFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetNewsDetailService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_NEWS_DETAIL_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                ApiGetNewsDetailResult apiGetNewsDetailResult = (ApiGetNewsDetailResult) apiResult.getResult();
                mNews = apiGetNewsDetailResult.getNews();
                setupNewsData(mNews);
            } else {
                showToast(R.string.global_fetchError);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.news_detail_toolbar_back)
    public void onBackClick() {
        finish();
    }
}

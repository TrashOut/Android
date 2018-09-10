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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetUserResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.model.Badge;
import me.trashout.model.Organization;
import me.trashout.model.User;
import me.trashout.notification.TrashoutFirebaseInstanceIdService;
import me.trashout.service.GetUserByIdService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;

public class ProfileFragment extends BaseFragment implements IProfileFragment, BaseService.UpdateServiceListener {

    private static final int GET_USER_REQUEST_ID = 957;

    @BindView(R.id.profile_no_company)
    TextView profileNoCompany;
    @BindView(R.id.txt_companies)
    TextView companiesTextView;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_your_activities_title)
    TextView profileYourActivitiesTitle;
    @BindView(R.id.profile_activity_reported)
    TextView profileActivityReported;
    @BindView(R.id.profile_activity_updated)
    TextView profileActivityUpdated;
    @BindView(R.id.profile_activity_cleaned)
    TextView profileActivityCleaned;
    @BindView(R.id.profile_your_badges_title)
    TextView profileYourBadgesTitle;
    @BindView(R.id.profile_your_badges_description)
    TextView profileYourBadgesDescription;
    /*@BindView(R.id.profile_badges_container)
    LinearLayout profileBadgesContainer;*/
    @BindView(R.id.profile_toolbar)
    Toolbar profileToolbar;
    @BindView(R.id.profile_edit_image_fab)
    FloatingActionButton profileEditFab;
    @BindView(R.id.profile_user_name)
    TextView profileUserName;
    @BindView(R.id.profile_organize_cleaning_action_checkbox)
    AppCompatCheckBox profileOrganizeCleaningActionCheckbox;
    @BindView(R.id.profile_cleaning_action_notification_checkbox)
    AppCompatCheckBox profileCleaningActionNotificationCheckbox;
    @BindView(R.id.profile_your_email)
    TextView profileYourEmail;
    @BindView(R.id.profile_your_phone)
    TextView profileYourPhone;
    @BindView(R.id.profile_your_activity_more)
    TextView profileYourActivityMore;
    @BindView(R.id.profile_your_email_phone)
    TextView profileYourEmailPhone;
    @BindView(R.id.txt_level)
    TextView levelTextView;

    private User mUser;
    private LayoutInflater inflater;

    @Override
    protected boolean useCustomFragmentToolbar() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        this.inflater = inflater;

        mUser = PreferencesHandler.getUserData(getContext());

        if (mUser == null) {
            // TODO - No user - get from API?, login?
            showToast("No user data.. Please login again");
            // start login activity
            getActivity().finish();
        } else {
            setupUserData(mUser);
        }

        profileToolbar.inflateMenu(R.menu.menu_profile);
        profileToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_logout) {
                    TrashoutFirebaseInstanceIdService.deleteFcmToken(getContext());
                    PreferencesHandler.setUserData(getContext(), null);
                    FirebaseAuth.getInstance().signOut();
                    Utils.resetFcmToken();

                    // Facebook sign out
                    if (FacebookSdk.isInitialized()) {
                        LoginManager.getInstance().logOut();
                    }

                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
                        finish();
                    }
                    LoginFragment loginFragment = new LoginFragment();
                    getBaseActivity().replaceFragment(loginFragment, false);
                    ((MainActivity) getBaseActivity()).setUserNavigationState();
                    //startActivity(new Intent(getActivity(), LoginActivity.class));
                    //getActivity().completed();

                    return true;
                }
                return false;
            }
        });

        // update user data and give stat
        loadData();

        return view;
    }

    private void loadData() {
        if (!isNetworkAvailable()) {
            showToast(R.string.global_internet_offline);
            return;
        }

        if (mUser != null) {
            GetUserByIdService.startForRequest(getContext(), GET_USER_REQUEST_ID, mUser.getId());
        }
    }

    private void setupUserData(User user) {
        if (user.getImage() != null) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getImage().getFullStorageLocation());
            GlideApp.with(this)
                    .load(mImageRef)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder_rectangle)
                    .into(profileImage);
        }
        profileUserName.setText(user.getFullName());

        profileYourBadgesDescription.setText(Html.fromHtml(getString(R.string.profile_earnedPoints_X, user.getPoints())));
        levelTextView.setText(getString(R.string.user_level) + " " + calculateLevel(user.getPoints()));
        // TODO setting activities value + usee company + badges

        if (user.getOrganizations() != null && user.getOrganizations().size() > 0) {
            profileNoCompany.setVisibility(View.GONE);
            companiesTextView.setVisibility(View.VISIBLE);
            StringBuilder organizationsStringBuilder = new StringBuilder();
            for (Organization organization : user.getOrganizations()) {
                organizationsStringBuilder.append(organization.getName()).append(", ");
            }
            companiesTextView.setText(organizationsStringBuilder.toString().substring(0, organizationsStringBuilder.length() - 2));
        } else {
            profileNoCompany.setVisibility(View.VISIBLE);
            companiesTextView.setVisibility(View.GONE);
        }

        if (user.getStats() != null) {
            profileActivityReported.setText(String.format("%sx", user.getStats().getReported()));
            profileActivityCleaned.setText(String.format("%sx", user.getStats().getCleaned()));
            profileActivityUpdated.setText(String.format("%sx", user.getStats().getUpdated()));
        }

        profileYourEmail.setText(user.getEmail());
        profileYourPhone.setText(user.getPhoneNumber());

        profileCleaningActionNotificationCheckbox.setChecked(user.isVolunteerCleanup());
        profileOrganizeCleaningActionCheckbox.setChecked(user.isEventOrganizer());


        /*profileBadgesContainer.removeAllViews();
        if (user.getBadges() != null && !user.getBadges().isEmpty()) {
            for (Badge badge : user.getBadges()) {
                profileBadgesContainer.addView(getBadgeView(badge));
            }
        }*/
    }

    private String calculateLevel(int points) {
        if (points == 0) return "0";
        int a = 0;
        int b = 1;
        int c = 0;
        int index = -1;
        while (c < (points / 10)) {
            c = a + b;
            a = b;
            b = c;
            index += 1;
        }
        return String.valueOf(index);
    }

    /**
     * Get badge view
     *
     * @param badge
     * @return
     */
    private View getBadgeView(final Badge badge) {
        View trashTypeView = inflater.inflate(R.layout.layout_badge, null);

        TextView badgeName = trashTypeView.findViewById(R.id.badge_name);
        View badgeInfoBtn = trashTypeView.findViewById(R.id.badge_info_btn);
        View badgeBackground = trashTypeView.findViewById(R.id.badge_bg);

        badgeName.setText(badge.getName());
//        badgeBackground.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dashboard_collection_point_dustbin_background));
        badgeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(badge.getName());
            }
        });

        return trashTypeView;
    }

    @OnClick(R.id.profile_edit_image_fab)
    public void onProfileEditClick() {
        ProfileEditFragment profileEditFragment = new ProfileEditFragment();
        getBaseActivity().replaceFragment(profileEditFragment);
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return ProfileFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetUserByIdService.class);
        return serviceClass;
    }

    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_USER_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                ApiGetUserResult apiGetUserResult = (ApiGetUserResult) apiResult.getResult();
                mUser = apiGetUserResult.getUser();
                setupUserData(mUser);
            } else {
                showToast(R.string.global_error_api_text);
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.profile_your_activity_more)
    public void onYourActivityMoreClick() {
        UserActivityListFragment userActivityListFragment = new UserActivityListFragment();
        getBaseActivity().replaceFragment(userActivityListFragment);
    }
}

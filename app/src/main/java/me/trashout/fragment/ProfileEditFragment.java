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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import me.trashout.Configuration;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.model.Organization;
import me.trashout.model.User;
import me.trashout.service.UpdateUserService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.GlideApp;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.ViewUtils;

import static android.app.Activity.RESULT_OK;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.fragment
 * @since 10.12.2016
 */
public class ProfileEditFragment extends BaseFragment implements IProfileFragment, BaseService.UpdateServiceListener {

    private static final int UPDATE_USER_REQUEST_ID = 750;

    @BindView(R.id.profile_edit_image_fab)
    FloatingActionButton profileEditFab;
    @BindView(R.id.profile_edit_image)
    ImageView profileEditImage;
    @BindView(R.id.profile_edit_your_profile_title)
    TextView profileEditYourProfileTitle;
    @BindView(R.id.profile_edit_first_name)
    TextInputEditText profileEditFirstName;
    @BindView(R.id.profile_edit_last_name)
    TextInputEditText profileEditLastName;
    @BindView(R.id.profile_edit_organization_title)
    TextView profileEditOrganizationTitle;
    @BindView(R.id.profile_edit_organizations)
    EditText profileEditOrganizations;
    @BindView(R.id.profile_edit_gps_format_title)
    TextView profileEditGpsFormatTitle;
    @BindView(R.id.profile_edit_gps_format_degrees)
    AppCompatRadioButton profileEditGpsFormatDegrees;
    @BindView(R.id.profile_edit_gps_format_dms)
    AppCompatRadioButton profileEditGpsFormatDms;
    @BindView(R.id.profile_edit_image_container)
    LinearLayout profileEditImageContainer;
    @BindView(R.id.profile_edit_first_name_layout)
    TextInputLayout profileEditFirstNameLayout;
    @BindView(R.id.profile_edit_last_name_layout)
    TextInputLayout profileEditLastNameLayout;
    @BindView(R.id.profile_edit_email)
    TextInputEditText profileEditEmail;
    @BindView(R.id.profile_edit_email_layout)
    TextInputLayout profileEditEmailLayout;
    @BindView(R.id.profile_edit_phone)
    TextInputEditText profileEditPhone;
    @BindView(R.id.profile_edit_phone_layout)
    TextInputLayout profileEditPhoneLayout;
    @BindView(R.id.profile_edit_organize_cleaning_action_checkbox)
    AppCompatCheckBox profileEditOrganizeCleaningActionCheckbox;
    @BindView(R.id.profile_edit_cleaning_action_notification_checkbox)
    AppCompatCheckBox profileEditCleaningActionNotificationCheckbox;

    private User mUser;
    private Uri mCropImageUri;
    private Uri photoResultImage;

    List<Organization> mSelectedOrganizations;


    public interface OnSaveOrganizationsListener {
        void onSaveOrganizationsListener(List<Organization> organizationList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        ButterKnife.bind(this, view);

        mUser = PreferencesHandler.getUserData(getContext());

        if (mSelectedOrganizations == null)
            mSelectedOrganizations = mUser.getOrganizations();

        setupUserProfile(mUser);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.profile_header_editProfile_header));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                saveProfile();
                return true;
            default:
                break;
        }

        return false;
    }

    private void setupUserProfile(User user) {
        if (user.getImage() != null && !TextUtils.isEmpty(user.getImage().getFullStorageLocation())) {
            StorageReference mImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getImage().getFullStorageLocation());
            GlideApp.with(this)
                    .asBitmap()
                    .load(mImageRef)
                    .placeholder(R.drawable.ic_image_placeholder_rectangle)
                    .into(new BitmapImageViewTarget(profileEditImage) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profileEditImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        profileEditFirstName.setText(user.getFirstName());
        profileEditLastName.setText(user.getLastName());

        profileEditEmail.setText(user.getEmail());
        profileEditPhone.setText(user.getPhoneNumber());

        // post da
        profileEditOrganizations.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSelectedOrganizations != null && !mSelectedOrganizations.isEmpty()) {
                    StringBuilder orgText = new StringBuilder();
                    for (Organization organization : mSelectedOrganizations) {
                        orgText.append(organization.getName()).append(", ");
                    }
                    profileEditOrganizations.setText(orgText.toString().substring(0, orgText.length() - 2));
                } else {
                    profileEditOrganizations.setText("");
                }
            }
        }, 100);


        profileEditCleaningActionNotificationCheckbox.setChecked(user.isVolunteerCleanup());
        profileEditOrganizeCleaningActionCheckbox.setChecked(user.isEventOrganizer());

        profileEditGpsFormatDegrees.setChecked(PreferencesHandler.getUserPreferredLocationFormat(getContext()) == 0);
        profileEditGpsFormatDms.setChecked(PreferencesHandler.getUserPreferredLocationFormat(getContext()) == 1);

    }

    private void saveProfile() {
        showProgressDialog();
        boolean valid = true;

        if (TextUtils.isEmpty(profileEditFirstName.getText().toString())) {
            profileEditFirstNameLayout.setError(getString(R.string.user_validation_firstNameRequired));
            valid = false;
        }


        if (TextUtils.isEmpty(profileEditLastName.getText().toString())) {
            profileEditLastNameLayout.setError(getString(R.string.user_validation_lastNameRequired));
            valid = false;
        }

        if (TextUtils.isEmpty(profileEditEmail.getText().toString()) && ViewUtils.isValidEmail(profileEditEmail.getText())) {
            profileEditEmailLayout.setError(getString(R.string.profile_validation_emailRequired));
            valid = false;
        }

        if (valid) {
            mUser.setFirstName(profileEditFirstName.getText().toString());
            mUser.setLastName(profileEditLastName.getText().toString());
            mUser.setEmail(profileEditEmail.getText().toString());

            if (!TextUtils.isEmpty(profileEditPhone.getText().toString()) && !profileEditPhone.getText().toString().equalsIgnoreCase("null")) {
                mUser.setPhoneNumber(profileEditPhone.getText().toString());
            }

            mUser.setVolunteerCleanup(profileEditCleaningActionNotificationCheckbox.isChecked());
            mUser.setEventOrganizer(profileEditOrganizeCleaningActionCheckbox.isChecked());

            PreferencesHandler.setUserPreferredLocationFormat(getContext(), profileEditGpsFormatDms.isChecked() ? 1 : 0);

            // TODO hotfix so far (user update needs "organizationId" not "id")
            // TODO refactor -> two different organizations types
            if (mSelectedOrganizations == null || mSelectedOrganizations.isEmpty()) {
                mSelectedOrganizations = new ArrayList<>();
                mSelectedOrganizations.add(new Organization());
            } else {
                for (Organization organization : mSelectedOrganizations) {
                    organization.setOrganizationRoleId(1);
                    organization.setOrganizationId(organization.getId());
                }
            }

            mUser.setOrganizations(mSelectedOrganizations);

            UpdateUserService.startForRequest(getContext(), UPDATE_USER_REQUEST_ID, mUser, photoResultImage);
        } else {
            dismissProgressDialog();
        }
    }

    @OnCheckedChanged({R.id.profile_edit_gps_format_degrees, R.id.profile_edit_gps_format_dms})
    public void onCheckedChange(CompoundButton view, boolean checked) {
        switch (view.getId()) {
            case R.id.profile_edit_gps_format_degrees:
                if (checked)
                    profileEditGpsFormatDms.setChecked(false);
                break;
            case R.id.profile_edit_gps_format_dms:
                if (checked)
                    profileEditGpsFormatDegrees.setChecked(false);
                break;
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoResultImage = result.getUri();

                GlideApp.with(this)
                        .asBitmap()
                        .load(photoResultImage)
                        .placeholder(R.drawable.ic_image_placeholder_rectangle)
                        .into(new BitmapImageViewTarget(profileEditImage) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                profileEditImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setRequestedSize(Configuration.TAKE_PHOTO_SIZE, Configuration.TAKE_PHOTO_SIZE)
                .setFixAspectRatio(true)
                .setBackgroundColor(R.color.colorPrimary)
                .setActivityTitle(getString(R.string.global_cropImage))
                .start(getActivity());
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return ProfileEditFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(UpdateUserService.class);
        return serviceClass;
    }

    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == UPDATE_USER_REQUEST_ID) {
            dismissProgressDialog();

            if (apiResult.isValidResponse()) {
                showToast("Update success");
                PreferencesHandler.setUserData(getContext(), mUser);
                finish();
            } else {
                showToast(R.string.global_error_api_text);
            }

        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    @OnClick(R.id.profile_edit_image_fab)
    public void onClick() {
        if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(getActivity());
        }
    }

    @OnClick(R.id.profile_edit_organizations)
    public void onClickSelectOrganisations() {
        SelectOrganizationsFragment selectOrganizationsFragment = SelectOrganizationsFragment.newInstance(mSelectedOrganizations);
        getBaseActivity().replaceFragment(selectOrganizationsFragment);
    }

    public void onSaveOrganizationsListener(List<Organization> organizationList) {
        mSelectedOrganizations = organizationList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                showToast("Cancelling, required permissions are not granted");
            }
        } else if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                showToast("Cancelling, required permissions are not granted");
            }
        }
    }
}

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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.activity.StartActivity;
import me.trashout.activity.base.BaseActivity;
import me.trashout.activity.TutorialActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.result.ApiGetUserResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.User;
import me.trashout.service.CreateUserService;
import me.trashout.service.GetUserByFirebaseTokenService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PreferencesHandler;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static me.trashout.activity.base.BaseActivity.EXTRA_ARGUMENTS;

public class StartFragment extends BaseFragment implements BaseService.UpdateServiceListener {

    private static final int RC_SIGN_IN = 1;
    public static final int GET_USER_BY_FIREBASE_REQUEST_ID = 701;
    public static final int CREATE_USER_REQUEST_ID = 702;

    private static boolean startDashborad;

    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getBoolean(EXTRA_ARGUMENTS, false)) {
            startDashborad = getArguments().getBoolean(EXTRA_ARGUMENTS, false);
        } else {
            startDashborad = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.bind(this, view);

        if (!PreferencesHandler.isTutorialWasShown(getActivity())) {
            startTutorialActivity();
        } else if (isPlayServicesAvailable()) {
            auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                getGetUserTokenAndUserdata();
            } else {
                // not signed in
                if (startDashborad) {
                    auth.signInAnonymously()
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        getGetUserTokenAndUserdata();
                                    } else {
                                        Log.w(TAG, "signInAnonymously", task.getException());
                                        Toast.makeText(getContext(), R.string.user_login_validation_autentification_filed, Toast.LENGTH_SHORT).show();
                                        startDashborad = false;
                                        startLoginFragment();
                                    }
                                }
                            });
                } else {
                    startLoginFragment();
                }
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StartActivity) getActivity()).setTitle(getString(R.string.app_name));
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // user is signed in!
            getGetUserTokenAndUserdata();
            return;
        }

        // Sign in canceled
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), R.string.user_login_canceled, Toast.LENGTH_SHORT).show();
            return;
        }

        // No network
        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            Toast.makeText(getContext(), R.string.global_validation_noNetwork, Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void getGetUserTokenAndUserdata() {
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().getToken(false).addOnCompleteListener(getActivity(), new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "auth.getCurrentUser().getToken: = " + task.getResult().getToken());
                        showProgressDialog();
                        PreferencesHandler.setFirebaseToken(getContext(), task.getResult().getToken());
                        GetUserByFirebaseTokenService.startForRequest(getContext(), GET_USER_BY_FIREBASE_REQUEST_ID);
                    } else {
                        startDashborad = false;
                        startLoginFragment();
                    }
                }
            });
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return StartFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetUserByFirebaseTokenService.class);
        serviceClass.add(CreateUserService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (isAdded()) {
            if (apiResult.getRequestId() == GET_USER_BY_FIREBASE_REQUEST_ID) {
                if (apiResult.isValidResponse()) {
                    dismissProgressDialog();
                    ApiGetUserResult apiGetUserResult = (ApiGetUserResult) apiResult.getResult();
                    Log.d(TAG, "onNewResult: User = " + apiGetUserResult.getUser());
                    PreferencesHandler.setUserData(getContext(), apiGetUserResult.getUser());

                    startActivity(new Intent(getContext(), MainActivity.class));
                    finish();
                } else {
                    if (apiResult.getResponse() != null && apiResult.getResponse().code() == 401) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Name, email address, and profile photo Url
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();

                            User newUser = new User();
                            newUser.setFirstName(name);
                            newUser.setEmail(email);
                            newUser.setUid(uid);

                            CreateUserService.startForRequest(getActivity(), CREATE_USER_REQUEST_ID, newUser);
                        } else {
                            Toast.makeText(getContext(), R.string.user_login_validation_notFirebaseUser, Toast.LENGTH_SHORT).show();
                            startDashborad = false;
                            startLoginFragment();
                        }

                    } else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), R.string.user_login_validation_unknownError, Toast.LENGTH_SHORT).show();
                        startDashborad = false;
                        startLoginFragment();
                    }
                }
            } else if (apiResult.getRequestId() == CREATE_USER_REQUEST_ID) {
                dismissProgressDialog();
                if (apiResult.isValidResponse()) {
                    dismissProgressDialog();
                    ApiGetUserResult apiGetUserResult = (ApiGetUserResult) apiResult.getResult();
                    Log.d(TAG, "onNewResult: User = " + apiGetUserResult.getUser());
                    PreferencesHandler.setUserData(getContext(), apiGetUserResult.getUser());

                    startActivity(new Intent(getContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getContext(), R.string.user_login_create_error, Toast.LENGTH_SHORT).show();
                    startDashborad = false;
                    startLoginFragment();
                }
            }
        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    private void startLoginFragment() {
        if (startDashborad) {
            startActivity(BaseActivity.generateIntent(getContext(), DashboardFragment.class.getName(), null, MainActivity.class));
        } else {
            startActivity(BaseActivity.generateIntent(getContext(), LoginFragment.class.getName(), null, MainActivity.class));
        }
        finish();
    }

    private void startTutorialActivity() {
        startActivity(new Intent(getContext(), TutorialActivity.class));
        finish();
    }

    private boolean isPlayServicesAvailable() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog playServiceNotAvailableDialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), resultCode, 1, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            playServiceNotAvailableDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            playServiceNotAvailableDialog.show();
            return false;
        }
        return true;
    }
}

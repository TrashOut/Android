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

package me.trashout.fragment.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;

import me.trashout.R;
import me.trashout.activity.base.BaseActivity;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PreferencesHandler;

public class BaseFragment extends Fragment implements IBaseFragment {
    public final String TAG = this.getClass().getSimpleName();

    private MaterialDialog progressDialogFragment;
    private FirebaseAuth firebaseAuth;

    public BaseFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFirebaseToken();
    }

    private void updateFirebaseToken() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().getIdToken(false).addOnCompleteListener(getActivity(), new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        PreferencesHandler.setFirebaseToken(getContext(), task.getResult().getToken());
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
        super.onDestroy();
    }


    protected void setTitle(int title) {
        getActivity().setTitle(title);
        setSubtitle(null);
    }

    protected void setTitle(String title) {
        getActivity().setTitle(title);
        setSubtitle(null);
    }

    protected void setSubtitle(String subTitle) {
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setSubtitle(subTitle);
    }

    protected void showProgressDialog() {
        showProgressDialog(getString(R.string.global_processing));
    }

    protected void showProgressDialog(String message) {
        if (progressDialogFragment == null)
            progressDialogFragment = new MaterialDialog.Builder(getContext())
                    .content(message)
                    .widgetColorRes(R.color.colorPrimary)
                    .progress(true, 0).build();
        progressDialogFragment.show();
    }

    protected void dismissProgressDialog() {
        if (progressDialogFragment != null)
            progressDialogFragment.dismiss();
    }

    protected boolean useCustomFragmentToolbar(){
        return false;
    }

    public void hideMainToolbar() {
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().hide();
    }

    public void showMainToolbar() {
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().show();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void finish() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {

    }

    // ------------service connection-----------------------

    private ArrayList<Class<?>> serviceClassArray = new ArrayList<>();
    private BaseService.UpdateServiceListener updateServiceListener = null;

    private BaseService mService;
    private boolean mBound;


    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return null;
    }

    protected ArrayList<Class<?>> getServiceClass() {
        return null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("binding", "onServiceConnected");
            BaseService.LocalBinder binder = (BaseService.LocalBinder) service;
            mService = binder.getService();
            if (updateServiceListener != null)
                mService.addUpdateListener(updateServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("binding", "onServiceDisconnected");
            mService = null;
        }
    };

    private void bindService() {
        if (serviceClassArray != null && serviceClassArray.size() > 0 && !mBound) {
            for (Class<?> serviceClass : serviceClassArray) {
                getActivity().bindService(new Intent(getActivity(), serviceClass), mConnection, Context.BIND_AUTO_CREATE);
            }
            mBound = true;
        }
    }

    private void unbindService() {
        if (mService != null) {
            mService.removeUpdateListener(updateServiceListener);
        }
        getActivity().unbindService(mConnection);
        mBound = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceClassArray = getServiceClass();
        updateServiceListener = getUpdateServiceListener();
        bindService();

        if (useCustomFragmentToolbar()){
            hideMainToolbar();
        }else{
            showMainToolbar();
        }
    }

    public void showToast(@StringRes int message) {
        if (getBaseActivity() != null) {
            getBaseActivity().showToast(message);
        }
    }

    public void showToast(String message) {
        if (getBaseActivity() != null) {
            getBaseActivity().showToast(message);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            unbindService();
        }
    }
}

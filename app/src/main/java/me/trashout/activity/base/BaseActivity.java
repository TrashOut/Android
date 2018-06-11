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

package me.trashout.activity.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import me.trashout.R;
import me.trashout.fragment.CollectionPointListFragment;
import me.trashout.fragment.DashboardFragment;
import me.trashout.fragment.LoginFragment;
import me.trashout.fragment.NewsListFragment;
import me.trashout.fragment.ProfileFragment;
import me.trashout.fragment.TrashListFragment;
import me.trashout.fragment.TrashMapFragment;
import me.trashout.fragment.base.IBaseFragment;

public class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    protected static final String EXTRA_FRAGMENT_NAME = "fragment";
    public static final String EXTRA_ARGUMENTS = "arguments";
    public static final int CONTENT_VIEW_ID = R.id.content_view;
    protected Toolbar toolbar;

    private Toast toast;

    /**
     * gets intent for starting new activity with fragment defined by fragment name and passes extras to the starting intent
     *
     * @param ctx
     * @param fragmentName fragment to instantiate
     * @param args         to pass to the instantiated fragment
     */
    public static Intent generateIntent(Context ctx, String fragmentName, Bundle args, Class<?> activityClass) {
        return new Intent(ctx, activityClass).putExtra(EXTRA_FRAGMENT_NAME, fragmentName).putExtra(EXTRA_ARGUMENTS, args);
    }


    /**
     * Start specific activity and open fragment defined by name
     *
     * @param ctx
     * @param fragmentName
     * @param activityClass
     */
    public static void startActivity(Context ctx, String fragmentName, Class<?> activityClass) {
        Intent intent = new Intent(ctx, activityClass).putExtra(EXTRA_FRAGMENT_NAME, fragmentName);
        ctx.startActivity(intent);
    }

    /**
     * Start specific activity and open fragment defined by name
     *
     * @param ctx
     * @param fragmentName
     * @param activityClass
     */
    public static void startActivity(Context ctx, String fragmentName, Class<?> activityClass, Bundle args) {
        Intent intent = new Intent(ctx, activityClass).putExtra(EXTRA_FRAGMENT_NAME, fragmentName).putExtra(EXTRA_ARGUMENTS, args);
        ctx.startActivity(intent);
    }


    private FrameLayout mContentView;

    public View getContentView() {
        return mContentView;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(CONTENT_VIEW_ID);
    }

    /**
     * returns the name of the fragment to be instantiated
     *
     * @return
     */

    protected String getFragmentName() {
        return getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
    }

    /**
     * returns the id main layout
     *
     * @return
     */
    protected int getMainLayoutId() {
        return R.layout.activity_base;
    }

    /**
     * instantiates the fragment
     *
     * @return
     */
    protected Fragment instantiateFragment(String fragmentName) {
        return Fragment.instantiate(this, fragmentName);
    }

    protected FrameLayout onCreateContentView() {
        return new FrameLayout(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getMainLayoutId());

        toolbar = findViewById(R.id.material_toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_home_up_indicator);
        setSupportActionBar(toolbar);

        mContentView = findViewById(R.id.content_view);

        String fragmentName = getFragmentName();
        if (fragmentName == null) {
            finish();
            return;
        }

        Bundle args = getIntent().getBundleExtra(EXTRA_ARGUMENTS);

        Log.d("BaseActivity", "onCreate: fragmentName = " + fragmentName + ", Bundle = " + args);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if ((fragment == null) && (savedInstanceState == null)) {
            fragment = instantiateFragment(fragmentName);
            if (args != null) {
                fragment.setArguments(args);
            }
            getSupportFragmentManager().beginTransaction().add(CONTENT_VIEW_ID, fragment, ((Object) fragment).getClass().getName()).commit();
        }

        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        onBackStackChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * replace fragment with a new fragment, add it to the back stack and use fragment name as a
     * transaction tag
     *
     * @param fragment for container to be replaced with
     */
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, ((Object) fragment).getClass().getName(), true);
    }

    /**
     * replaces fragment with a new fragment and uses fragment name as a
     * transaction tag
     *
     * @param fragment for container to be replaced with
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, ((Object) fragment).getClass().getName(), addToBackStack);
    }

    /**
     * @param fragment       fragment for container to be replaced with
     * @param name           of the transaction, null if not needed
     * @param addToBackStack
     */

    public void replaceFragment(Fragment fragment, String name, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(CONTENT_VIEW_ID, fragment, ((Object) fragment).getClass().getName());
            if (addToBackStack) {
                transaction.addToBackStack(name);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * replace fragment with a new fragment, add it to the back stack and use fragment name as a
     * transaction tag
     *
     * @param fragment for container to be add with
     */
    public void addFragment(Fragment fragment) {
        addFragment(fragment, ((Object) fragment).getClass().getName(), true);
    }

    /**
     * replaces fragment with a new fragment and uses fragment name as a
     * transaction tag
     *
     * @param fragment for container to be add with
     */
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(fragment, ((Object) fragment).getClass().getName(), addToBackStack);
    }

    /**
     * @param fragment       fragment for container to be add with
     * @param name           of the transaction, null if not needed
     * @param addToBackStack
     */
    public void addFragment(Fragment fragment, String name, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(CONTENT_VIEW_ID, fragment, ((Object) fragment).getClass().getName());
            if (addToBackStack) {
                transaction.addToBackStack(name);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Refresh current fragment by Tag
     *
     * @param fragmentTag
     */
    public void refreshFragment(String fragmentTag) {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    /**
     * Remove current fragment and replace another fragment
     *
     * @param fragmentToRemove
     * @param fragmentToReplace
     */
    public void removeAndReplaceFragment(Fragment fragmentToRemove, Fragment fragmentToReplace) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().remove(fragmentToRemove).replace(CONTENT_VIEW_ID, fragmentToReplace, ((Object) fragmentToReplace).getClass().getName());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setContentViewInternal(View view, ViewGroup.LayoutParams params) {
        setContentView(view, params);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() == null
                || (getCurrentFragment() != null && (!(getCurrentFragment() instanceof IBaseFragment) || ((getCurrentFragment() instanceof IBaseFragment) && !((IBaseFragment) getCurrentFragment())
                .onBackPressed())))) {
            super.onBackPressed();
        }
    }

    /**
     * clear back stack from support fragment manager
     */
    public void clearBackStack() {
        while (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStackImmediate(null, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getCurrentFragment() != null && ((getCurrentFragment() instanceof IBaseFragment))) {
            ((IBaseFragment) getCurrentFragment()).onActivityResultFragment(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get actual fragment
     *
     * @return actual fragment
     */
    protected Fragment getActualFragment() {
        return getSupportFragmentManager().findFragmentById(CONTENT_VIEW_ID);
    }


    @Override
    public void onBackStackChanged() {
        syncActionBarArrowState();
    }

    protected void syncActionBarArrowState() {
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0 && !(getActualFragment() instanceof TrashListFragment || getActualFragment() instanceof TrashMapFragment || getActualFragment() instanceof DashboardFragment || getActualFragment() instanceof CollectionPointListFragment
                || getActualFragment() instanceof ProfileFragment || getActualFragment() instanceof NewsListFragment || getActualFragment() instanceof LoginFragment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(!canback);
            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showToast(@StringRes int message) {
        if (getApplicationContext() != null) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void showToast(String message) {
        if (getApplicationContext() != null) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

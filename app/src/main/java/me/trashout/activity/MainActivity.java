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

package me.trashout.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.R;
import me.trashout.activity.base.BaseActivity;
import me.trashout.fragment.CollectionPointListFragment;
import me.trashout.fragment.DashboardFragment;
import me.trashout.fragment.EventCreateFragment;
import me.trashout.fragment.EventDetailFragment;
import me.trashout.fragment.LoginFragment;
import me.trashout.fragment.NewsDetailFragment;
import me.trashout.fragment.NewsListFragment;
import me.trashout.fragment.PhotoFullscreenFragment;
import me.trashout.fragment.ProfileEditFragment;
import me.trashout.fragment.ProfileFragment;
import me.trashout.fragment.TrashDetailFragment;
import me.trashout.fragment.TrashListFragment;
import me.trashout.fragment.TrashMapFragment;
import me.trashout.fragment.TrashReportOrEditFragment;
import me.trashout.fragment.base.ICollectionPointFragment;
import me.trashout.fragment.base.INewsFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.fragment.base.ITrashFragment;
import me.trashout.model.Organization;
import me.trashout.notification.PushNotification;
import me.trashout.notification.TrashoutFirebaseInstanceIdService;
import me.trashout.utils.BottomNavigationViewHelper;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import pub.devrel.easypermissions.EasyPermissions;

import static me.trashout.notification.PushNotification.TRASH_DETAIL_NOTIFICATION;
import static me.trashout.notification.PushNotification.TRASH_EVENT_NOTIFICATION;
import static me.trashout.notification.PushNotification.TRASH_NEWS_NOTIFICATION;
import static me.trashout.utils.PreferencesHandler.isFcmRegistered;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener,
        TrashListFragment.OnRefreshTrashListListener,
        CollectionPointListFragment.OnRefreshCollectionPointListListener,
        ProfileEditFragment.OnSaveOrganizationsListener,
        TrashReportOrEditFragment.OnTrashChangedListener,
        TrashReportOrEditFragment.OnDashboardChangedListener,
        EventDetailFragment.OnEventJoinedListener,
        EventCreateFragment.OnSelectTrashIdsOnMapListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_SIGN_IN = 324;

    public static final int NAVIGATION_HOME_ITEM = 0;
    private static final int NAVIGATION_DUMPS_ITEM = 1;
    private static final int NAVIGATION_NEWS_ITEM = 2;
    private static final int NAVIGATION_JUNKYARDS_ITEM = 3;
    public static final int NAVIGATION_PROFILE_ITEM = 4;

    private static final int RC_LOCATION = 122;
    private static final int OPEN_MAP_FRAGMENT_REQUEST_CODE = 33;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_spinner)
    Spinner toolbarSpinner;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth auth;

    private MenuItem navigationProfileItem;
    private LatLng lastPosition;
    private MenuItem navigationItemSelected;

    @Override
    protected int getMainLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getFragmentName() {
        return getIntent().getStringExtra(EXTRA_FRAGMENT_NAME) != null ? getIntent().getStringExtra(EXTRA_FRAGMENT_NAME) : DashboardFragment.class.getName();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        if (intent.getStringExtra(EXTRA_FRAGMENT_NAME) != null) {
            Fragment currentFragment = getCurrentFragment();
            if (!(currentFragment != null && currentFragment.getTag().equalsIgnoreCase(intent.getStringExtra(EXTRA_FRAGMENT_NAME)))) {
                Bundle args = intent.getBundleExtra(EXTRA_ARGUMENTS);
                Fragment fragment = instantiateFragment(intent.getStringExtra(EXTRA_FRAGMENT_NAME));
                if (args != null) {
                    fragment.setArguments(args);
                }
                replaceFragment(fragment);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        getAuth();
        navigationProfileItem = navigation.getMenu().findItem(R.id.action_main_profile);
        setUserNavigationState();

        prepareGoogleApiLocationClient();

        navigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        toolbarSpinner.setSelection(0, false);
        toolbarSpinner.setOnItemSelectedListener(this);

        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        if (!isFcmRegistered(getApplicationContext())) {
            TrashoutFirebaseInstanceIdService.registerFcmToken(getApplicationContext());
        }

        onBackStackChanged();
    }

    /**
     * Handle notification intent routing
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("type")) {
            switch (PushNotification.getNotificationType(intent.getStringExtra("type"))) {
                case TRASH_NEWS_NOTIFICATION:
                    if (intent.hasExtra(TRASH_NEWS_NOTIFICATION.getId())) {
                        replaceFragment(NewsDetailFragment.newInstance(Long.valueOf(intent.getStringExtra(TRASH_NEWS_NOTIFICATION.getId()))));
                    }
                    break;
                case TRASH_EVENT_NOTIFICATION:
                    if (intent.hasExtra(TRASH_EVENT_NOTIFICATION.getId())) {
                        replaceFragment(EventDetailFragment.newInstance(Long.valueOf(intent.getStringExtra(TRASH_EVENT_NOTIFICATION.getId()))));
                    }
                    break;
                case TRASH_DETAIL_NOTIFICATION:
                    if (intent.hasExtra(TRASH_DETAIL_NOTIFICATION.getId())) {
                        replaceFragment(TrashDetailFragment.newInstance(Long.valueOf(intent.getStringExtra(TRASH_DETAIL_NOTIFICATION.getId()))));
                    }
                    break;
            }
        }
    }

    /**
     * Prepare Google Api Location Client
     */
    private void prepareGoogleApiLocationClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            Log.d(TAG, "prepareGoogleApiLocationClient: ");
        }
    }

    /**
     * getting Google api client
     *
     * @return
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public LatLng getLastPosition() {
        updatePosition();
        return PreferencesHandler.getUserLastLocation(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationItemSelected = null;
        if (auth.getCurrentUser() != null || item.getItemId() == R.id.action_main_profile) {
            handleClickOnMenuItem(item);
        } else {
            // not signed in
            navigationItemSelected = item;
            signInAnonymously();
        }
        return false;
    }

    public FirebaseAuth getAuth() {
        if (auth == null)
            auth = FirebaseAuth.getInstance();
        return auth;
    }


    public void signInAnonymously() {
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            getGetUserTokenAndUserdata();
                        } else {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            showToast(R.string.user_login_validation_autentification_filed);
                        }
                    }
                });
    }

    private void handleClickOnMenuItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_home:
                if (getCurrentFragment() instanceof DashboardFragment) break;
                DashboardFragment dashboardFragment = new DashboardFragment();
                replaceFragment(dashboardFragment);
                setNavigationBottomViewCheckedItem(NAVIGATION_HOME_ITEM);
                break;
            case R.id.action_main_dumps:
                if (getCurrentFragment() instanceof TrashMapFragment) break;
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                            OPEN_MAP_FRAGMENT_REQUEST_CODE);
                } else {
                    TrashMapFragment trashMapFragment = new TrashMapFragment();
                    replaceFragment(trashMapFragment);
                    setNavigationBottomViewCheckedItem(NAVIGATION_DUMPS_ITEM);
                }
                break;
            case R.id.action_main_news:
                if (getCurrentFragment() instanceof NewsListFragment) break;
                NewsListFragment newsListFragment = new NewsListFragment();
                replaceFragment(newsListFragment);
                setNavigationBottomViewCheckedItem(NAVIGATION_NEWS_ITEM);
                break;
            case R.id.action_main_recycling_point:
                if (getCurrentFragment() instanceof CollectionPointListFragment) break;
                CollectionPointListFragment collectionPointListFragment = new CollectionPointListFragment();
                replaceFragment(collectionPointListFragment);
                setNavigationBottomViewCheckedItem(NAVIGATION_JUNKYARDS_ITEM);
                break;
            case R.id.action_main_profile:
                if (getCurrentFragment() instanceof ProfileFragment || getCurrentFragment() instanceof LoginFragment)
                    break;

                if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    replaceFragment(profileFragment);
                    setNavigationBottomViewCheckedItem(NAVIGATION_PROFILE_ITEM);
                } else {
//                    startActivity(new Intent(this, LoginActivity.class));

                    LoginFragment loginFragment = new LoginFragment();
                    replaceFragment(loginFragment);
                    setNavigationBottomViewCheckedItem(NAVIGATION_PROFILE_ITEM);
                }

                break;
        }
        BottomNavigationViewHelper.disableShiftMode(navigation);
    }

    /**
     * Setting toolbar title
     *
     * @param title
     */
    public void setToolbarTitle(String title) {
        if (toolbarTitle != null)
            toolbarTitle.setText(title);
    }

    public void setUserNavigationState() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            navigationProfileItem.setTitle(R.string.tab_profile);
        } else {
            navigationProfileItem.setTitle(R.string.global_login);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLocationEnabledCheck();

        if (auth.getCurrentUser() != null) {
            handleLocaleChange();
        }
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();


        Fragment actualFragment = getActualFragment();

        Log.d(TAG, "onBackStackChanged: toolbarSpinner - " + toolbarSpinner + ", actualFragment = " + actualFragment);

        if (toolbarSpinner != null) {

            if (actualFragment != null && (actualFragment instanceof ITrashFragment)) {

                if (actualFragment instanceof TrashListFragment || actualFragment instanceof TrashMapFragment) {
                    toolbarSpinner.setVisibility(View.VISIBLE);
                    toolbarSpinner.setSelection(actualFragment instanceof TrashListFragment ? 0 : 1);
                } else {
                    toolbarSpinner.setVisibility(View.GONE);
                }
                setNavigationBottomViewCheckedItem(NAVIGATION_DUMPS_ITEM);
            } else if (actualFragment != null && (actualFragment instanceof ICollectionPointFragment)) {
                toolbarSpinner.setVisibility(View.GONE);
                setNavigationBottomViewCheckedItem(NAVIGATION_JUNKYARDS_ITEM);
            } else if (actualFragment != null && (actualFragment instanceof IProfileFragment)) {
                toolbarSpinner.setVisibility(View.GONE);
                setNavigationBottomViewCheckedItem(NAVIGATION_PROFILE_ITEM);
            } else if (actualFragment != null && (actualFragment instanceof INewsFragment)) {
                toolbarSpinner.setVisibility(View.GONE);
                setNavigationBottomViewCheckedItem(NAVIGATION_NEWS_ITEM);
            } else {
                toolbarSpinner.setVisibility(View.GONE);
                setNavigationBottomViewCheckedItem(NAVIGATION_HOME_ITEM);
            }
        }

        if (navigation != null) {
            if (actualFragment != null && actualFragment instanceof PhotoFullscreenFragment) {
                navigation.setVisibility(View.GONE);
            } else {
                navigation.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setNavigationBottomViewCheckedItem(int navigationItem) {
        if (navigation != null) {
            switch (navigationItem) {
                case NAVIGATION_HOME_ITEM:
                    navigation.getMenu().findItem(R.id.action_main_home).setChecked(true);
                    break;
                case NAVIGATION_DUMPS_ITEM:
                    navigation.getMenu().findItem(R.id.action_main_dumps).setChecked(true);
                    break;
                case NAVIGATION_NEWS_ITEM:
                    navigation.getMenu().findItem(R.id.action_main_news).setChecked(true);
                    break;
                case NAVIGATION_JUNKYARDS_ITEM:
                    navigation.getMenu().findItem(R.id.action_main_recycling_point).setChecked(true);
                    break;
                case NAVIGATION_PROFILE_ITEM:
                    navigation.getMenu().findItem(R.id.action_main_profile).setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (getSupportFragmentManager() != null && getSupportFragmentManager().findFragmentByTag(i == 0 ? TrashListFragment.class.getName() : TrashMapFragment.class.getName()) != null) {
            getSupportFragmentManager().popBackStackImmediate(i == 0 ? TrashListFragment.class.getName() : TrashMapFragment.class.getName(), 0);
        } else {
            Fragment fragment = (i == 0 ? new TrashListFragment() : new TrashMapFragment());
            replaceFragment(fragment, i == 0 ? TrashListFragment.class.getName() : TrashMapFragment.class.getName(), true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                getGetUserTokenAndUserdata();
                setUserNavigationState();
                return;
            }
            // No network
            if (resultCode == ErrorCodes.NO_NETWORK) {
                showToast("No network");
                return;
            }
        }
    }

    private void getGetUserTokenAndUserdata() {
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().getToken(false).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "auth.getCurrentUser().getToken: = " + task.getResult().getToken());
                        PreferencesHandler.setFirebaseToken(MainActivity.this, task.getResult().getToken());

                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onRefreshTrashList() {
        TrashListFragment trashListFragment = (TrashListFragment)
                getSupportFragmentManager().findFragmentByTag(TrashListFragment.class.getName());

        if (trashListFragment != null) {
            trashListFragment.onRefreshTrashList();
        }
    }

    @Override
    public void onRefreshCollectionPointList() {
        CollectionPointListFragment collectionPointListFragment = (CollectionPointListFragment)
                getSupportFragmentManager().findFragmentByTag(CollectionPointListFragment.class.getName());

        if (collectionPointListFragment != null) {
            collectionPointListFragment.onRefreshCollectionPointList();
        }
    }

    @Override
    public void onSaveOrganizationsListener(List<Organization> organizationList) {
        ProfileEditFragment profileEditFragment = (ProfileEditFragment)
                getSupportFragmentManager().findFragmentByTag(ProfileEditFragment.class.getName());

        if (profileEditFragment != null) {
            profileEditFragment.onSaveOrganizationsListener(organizationList);
        }

    }

    @Override
    public void onTrashChanged() {
        TrashDetailFragment trashDetailFragment = (TrashDetailFragment)
                getSupportFragmentManager().findFragmentByTag(TrashDetailFragment.class.getName());

        if (trashDetailFragment != null) {
            trashDetailFragment.onTrashChanged();
        }

        onDashboardChanged();
    }

    @Override
    public void onDashboardChanged() {
        DashboardFragment dashboardFragment = (DashboardFragment)
                getSupportFragmentManager().findFragmentByTag(DashboardFragment.class.getName());

        if (dashboardFragment != null) {
            dashboardFragment.dashboardNeedsRefresh();
        }
    }

    @Override
    public void onEventJoined() {
        TrashDetailFragment trashDetailFragment = (TrashDetailFragment)
                getSupportFragmentManager().findFragmentByTag(TrashDetailFragment.class.getName());

        if (trashDetailFragment != null) {
            trashDetailFragment.onEventJoined();
        }
    }

    @Override
    public void onTrashIdsOnMapSelected(ArrayList<Long> selectedTrash) {
        EventCreateFragment eventCreateFragment = (EventCreateFragment)
                getSupportFragmentManager().findFragmentByTag(EventCreateFragment.class.getName());

        if (eventCreateFragment != null) {
            eventCreateFragment.onTrashOnMapSelected(selectedTrash);
        }
    }

    private void refreshDashboardData() {
        DashboardFragment dashboardFragment = getActualFragment() instanceof DashboardFragment ? (DashboardFragment) getActualFragment() : null;

        if (dashboardFragment != null) {
            dashboardFragment.getDashboardData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updatePosition();
            if (requestCode == OPEN_MAP_FRAGMENT_REQUEST_CODE) {
                TrashMapFragment trashMapFragment = new TrashMapFragment();
                replaceFragment(trashMapFragment);
                setNavigationBottomViewCheckedItem(NAVIGATION_DUMPS_ITEM);
            } else {
                refreshDashboardData();
            }
        } else {
            Log.d(TAG, "onPermissionsGranted: no permitions");
            Toast.makeText(this, R.string.global_allowGpsInPhone, Toast.LENGTH_LONG).show();
            refreshDashboardData();
        }
    }

    public void isLocationEnabledCheck() {
//        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps();
//        }
    }

    // TODO HARDCODED STRINGS
    private void buildAlertMessageNoGps() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.global_enableGps)
                .content("Your GPS seems to be disabled, do you want to enable it?")
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .build();

        dialog.show();
    }

    public void onConnected(Bundle dataBundle) throws SecurityException {

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            updatePosition();
            refreshDashboardData();
        } else {
            // Do not have permissions, request them now
            ActivityCompat.requestPermissions(this, perms, RC_LOCATION);
        }
    }

    private void updatePosition() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Note that this can be NULL if last location isn't already known.
            if (mCurrentLocation != null) {
                // Print current location if not null
                Log.d("DEBUG", "current location: " + mCurrentLocation.toString());

                lastPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                PreferencesHandler.setUserLastLocation(this, lastPosition);
            }

            isLocationEnabledCheck();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            showToast("Disconnected. Please re-connect.");
        } else if (i == CAUSE_NETWORK_LOST) {
            showToast("Network lost. Please re-connect.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleLocaleChange() {
        String locale = Utils.getLocaleString();
        if (!locale.equals(PreferencesHandler.getDeviceLocale(this))) {
            TrashoutFirebaseInstanceIdService.deleteFcmToken(this);
            Utils.resetFcmToken();
            PreferencesHandler.setDeviceLocale(this, locale);
        }
    }
}

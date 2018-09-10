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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.util.data.TaskFailureLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.MainActivity;
import me.trashout.api.base.ApiResult;
import me.trashout.api.base.ApiUpdate;
import me.trashout.api.request.ApiGetUserByFirebaseTokenRequest;
import me.trashout.api.result.ApiGetUserResult;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.fragment.base.IProfileFragment;
import me.trashout.model.Constants;
import me.trashout.model.FacebookProfile;
import me.trashout.model.User;
import me.trashout.service.CreateUserService;
import me.trashout.service.GetUserByFirebaseTokenService;
import me.trashout.service.UpdateUserService;
import me.trashout.service.base.BaseService;
import me.trashout.utils.PreferencesHandler;
import me.trashout.utils.Utils;
import me.trashout.utils.ViewUtils;

import static me.trashout.activity.MainActivity.NAVIGATION_PROFILE_ITEM;

public class LoginFragment extends BaseFragment implements BaseService.UpdateServiceListener, IProfileFragment {

    private static final int GET_USER_BY_FIREBASE_AFTER_LOGIN_REQUEST_ID = 901;
    private static final int GET_USER_BY_FIREBASE_AFTER_SIGN_UP_REQUEST_ID = 902;
    private static final int GET_USER_BY_FIREBASE_AFTER_LINKING_ACCOUNT_REQUEST_ID = 903;
    private static final int CREATE_USER_REQUEST_ID = 903;
    private static final int UPDATE_USER_REQUEST_ID = 904;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.login_email)
    TextInputEditText loginEmail;
    @BindView(R.id.login_password)
    TextInputEditText loginPassword;
    @BindView(R.id.login_forgot_password)
    TextView loginForgotPassword;
    @BindView(R.id.login_btn)
    AppCompatButton loginBtn;
    @BindView(R.id.login_facebook_btn)
    AppCompatButton loginFacebookBtn;
    @BindView(R.id.page_login)
    LinearLayout pageLogin;
    @BindView(R.id.sign_up_first_name)
    TextInputEditText signUpFirstName;
    @BindView(R.id.sign_up_last_name)
    TextInputEditText signUpLastName;
    @BindView(R.id.sign_up_email)
    TextInputEditText signUpEmail;
    @BindView(R.id.sign_up_password)
    TextInputEditText signUpPassword;
    @BindView(R.id.sign_up_reenter_password)
    TextInputEditText signUpReenterPassword;
    @BindView(R.id.sign_up_accept_user_data_collection)
    AppCompatCheckBox signUpAccpetUserDataCollectionCheckBox;
    @BindView(R.id.sign_up_accept_user_data_collection_text)
    TextView signUpAccpetUserDataCollectionTextView;
    @BindView(R.id.sign_up_btn)
    AppCompatButton signUpBtn;
    @BindView(R.id.sign_up_facebook_btn)
    AppCompatButton signUpFacebookBtn;
    @BindView(R.id.page_sign_up)
    LinearLayout pageSignUp;
    @BindView(R.id.login_email_layout)
    TextInputLayout loginEmailLayout;
    @BindView(R.id.login_password_layout)
    TextInputLayout loginPasswordLayout;
    @BindView(R.id.sign_up_first_name_layout)
    TextInputLayout signUpFirstNameLayout;
    @BindView(R.id.sign_up_last_name_layout)
    TextInputLayout signUpLastNameLayout;
    @BindView(R.id.sign_up_email_layout)
    TextInputLayout signUpEmailLayout;
    @BindView(R.id.sign_up_password_layout)
    TextInputLayout signUpPasswordLayout;
    @BindView(R.id.sign_up_reenter_password_layout)
    TextInputLayout signUpReenterPasswordLayout;
    private View view;

    private FirebaseAuth auth;
    private User user;

    private CallbackManager callbackManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        user = PreferencesHandler.getUserData(getContext());
        auth = FirebaseAuth.getInstance();

        // Set the ViewPager adapter
        LoginOrSignUpPagerAdapter adapter = new LoginOrSignUpPagerAdapter();
        pager.setAdapter(adapter);

        tabs.setupWithViewPager(pager);


        signUpAccpetUserDataCollectionTextView.setText(Html.fromHtml(getString(R.string.global_signUp_acceptRegister_startSentense)
                + " <a href=\"http://trashout.ngo/policy\">" + getString(R.string.global_signUp_acceptRegister_privatePolicy) +"</a> "
                + getString(R.string.global_signUp_acceptRegister_and)
                + " <a href=\"http://trashout.ngo/terms\">" + getString(R.string.global_signUp_acceptRegister_terms) +"</a>"
        ));
        signUpAccpetUserDataCollectionTextView.setMovementMethod(LinkMovementMethod.getInstance());


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.global_login));
            ((MainActivity) getActivity()).setNavigationBottomViewCheckedItem(NAVIGATION_PROFILE_ITEM);
        }
    }

    @OnClick({R.id.login_facebook_btn, R.id.sign_up_facebook_btn})
    public void onFacebookLoginClick(View view) {
        if (view.getId() == R.id.sign_up_facebook_btn) {
            resetLayoutError();
            if (!signUpAccpetUserDataCollectionCheckBox.isChecked()) {
                signUpAccpetUserDataCollectionCheckBox.setError("");
                return;
            }
        }

//        Toast.makeText(LoginFragment.this.getContext(), R.string.comming_soon, Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        Log.d(TAG, "FB login success - loginResult = " + loginResult);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        final FacebookProfile facebookProfile = new Gson().fromJson(object.toString(), FacebookProfile.class);
                                        facebookProfile.setAccessToken(loginResult.getAccessToken());

                                        // TODO chceck facebook login
                                        if (auth.getCurrentUser() != null && user != null) {
                                            linkWithAnonymousUserByFacebook(auth.getCurrentUser(), facebookProfile);
                                        } else {
                                            handleFacebookAccessToken(facebookProfile);
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "FB login onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "FB login onError");
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @OnClick(R.id.login_forgot_password)
    public void onForgotPasswordClick() {
        boolean valid = true;
        resetLayoutError();

        if (TextUtils.isEmpty(loginEmail.getText().toString())) {
            loginEmailLayout.setError(getString(R.string.profile_validation_emailRequired));
            valid = false;
        } else if (!ViewUtils.isValidEmail(loginEmail.getText().toString())) {
            loginEmailLayout.setError(getString(R.string.profile_validation_invalidEmail));
            valid = false;
        }

        if (valid) {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.global_validation_warning)
                    .content(R.string.user_resetPassword_message)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .autoDismiss(true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            auth.sendPasswordResetEmail(loginEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showToast(R.string.user_validation_resetPassword_success);
                                            } else {
                                                showToast(R.string.user_validation_resetPassword_failed);
                                            }
                                        }
                                    });
                        }
                    })
                    .build();

            dialog.show();
        }
    }

    @OnClick(R.id.sign_up_btn)
    public void onSignUpClick() {
        boolean valid = true;
        resetLayoutError();
        View errorView = null;

        if (TextUtils.isEmpty(signUpEmail.getText().toString())) {
            signUpEmailLayout.setError(getString(R.string.profile_validation_emailRequired));
            valid = false;
            errorView = signUpEmail;
        } else if (!ViewUtils.isValidEmail(signUpEmail.getText().toString())) {
            signUpEmailLayout.setError(getString(R.string.profile_validation_invalidEmail));
            valid = false;
            errorView = signUpEmail;
        }

        if (TextUtils.isEmpty(signUpFirstName.getText().toString())) {
            signUpFirstNameLayout.setError(getString(R.string.user_validation_firstNameRequired));
            valid = false;
            errorView = signUpFirstName;
        }

        if (TextUtils.isEmpty(signUpLastName.getText().toString())) {
            signUpLastNameLayout.setError(getString(R.string.user_validation_lastNameRequired));
            valid = false;
            errorView = signUpLastName;
        }

        if (TextUtils.isEmpty(signUpPassword.getText().toString())) {
            signUpPasswordLayout.setError(getString(R.string.profile_validation_passwordRequired));
            valid = false;
            errorView = signUpPassword;
        }

        if (TextUtils.isEmpty(signUpReenterPassword.getText().toString())) {
            signUpReenterPasswordLayout.setError(getString(R.string.user_validation_confirmPasswordRequired));
            valid = false;
            errorView = signUpReenterPassword;
        }

        if (!TextUtils.isEmpty(signUpPassword.getText().toString()) && !TextUtils.isEmpty(signUpReenterPassword.getText().toString()) && !signUpPassword.getText().toString().equals(signUpReenterPassword.getText().toString())) {
            signUpReenterPasswordLayout.setError(getString(R.string.user_validation_passwordsNotMatch));
            valid = false;
            errorView = signUpReenterPassword;
        }

        if (signUpPassword.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            signUpPasswordLayout.setError(getString(R.string.global_validation_passwordTooLong));
            valid = false;
            errorView = signUpPassword;
        } else if (signUpPassword.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            signUpPasswordLayout.setError(getString(R.string.user_validation_passwordTooShort));
            valid = false;
            errorView = signUpPassword;
        } else if (!ViewUtils.isValidPassword(signUpPassword.getText().toString())) {
            signUpPasswordLayout.setError(getString(R.string.user_validation_passwordShouldContain));
            valid = false;
            errorView = signUpPassword;
        }

        if (!signUpAccpetUserDataCollectionCheckBox.isChecked()) {
            signUpAccpetUserDataCollectionCheckBox.setError("");
            valid = false;
            errorView = signUpAccpetUserDataCollectionCheckBox;
        }

        if (!valid) {
            errorView.requestFocus();
        }

        if (valid) {
            Utils.resetFcmToken();
            if (auth.getCurrentUser() != null && user != null) {
                linkWithAnonymousUser(auth.getCurrentUser(), signUpEmail.getText().toString(), signUpPassword.getText().toString(), signUpFirstName.getText().toString(), signUpLastName.getText().toString());
            } else {
                registerUser(signUpEmail.getText().toString(), signUpFirstName.getText().toString(), signUpLastName.getText().toString(), signUpPassword.getText().toString());
            }
        }

    }

    private void resetLayoutError() {
        signUpEmailLayout.setError(null);
        signUpFirstNameLayout.setError(null);
        signUpLastNameLayout.setError(null);
        signUpPasswordLayout.setError(null);
        signUpReenterPasswordLayout.setError(null);

        loginEmailLayout.setError(null);
        loginPasswordLayout.setError(null);

        signUpAccpetUserDataCollectionCheckBox.setError(null);
    }

    @OnClick(R.id.login_btn)
    public void onLoginClick() {
        boolean valid = true;
        resetLayoutError();

        if (TextUtils.isEmpty(loginEmail.getText().toString())) {
            loginEmailLayout.setError(getString(R.string.profile_validation_emailRequired));
            valid = false;
        } else if (!ViewUtils.isValidEmail(loginEmail.getText().toString())) {
            loginEmailLayout.setError(getString(R.string.profile_validation_invalidEmail));
            valid = false;
        }

        if (TextUtils.isEmpty(loginPassword.getText().toString())) {
            loginPasswordLayout.setError(getString(R.string.profile_validation_passwordRequired));
            valid = false;
        }

        if (valid) {
            if (!isNetworkAvailable()) {
                showToast(R.string.global_internet_offline);
                return;
            }

            checkAccountExists(loginEmail.getText().toString(), loginPassword.getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    class LoginOrSignUpPagerAdapter extends PagerAdapter {

        public View instantiateItem(ViewGroup collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.page_login;
                    break;
                case 1:
                    resId = R.id.page_sign_up;
                    break;
            }
            return view.findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.global_login);
                case 1:
                    return getString(R.string.global_signup);
            }
            return super.getPageTitle(position);
        }
    }

    // SERVICE
    @Override
    protected BaseService.UpdateServiceListener getUpdateServiceListener() {
        return LoginFragment.this;
    }

    @Override
    protected ArrayList<Class<?>> getServiceClass() {
        ArrayList<Class<?>> serviceClass = new ArrayList<>();
        serviceClass.add(GetUserByFirebaseTokenService.class);
        serviceClass.add(CreateUserService.class);
        serviceClass.add(UpdateUserService.class);
        return serviceClass;
    }


    @Override
    public void onNewResult(ApiResult apiResult) {
        if (apiResult.getRequestId() == GET_USER_BY_FIREBASE_AFTER_LOGIN_REQUEST_ID || apiResult.getRequestId() == GET_USER_BY_FIREBASE_AFTER_SIGN_UP_REQUEST_ID || apiResult.getRequestId() == GET_USER_BY_FIREBASE_AFTER_LINKING_ACCOUNT_REQUEST_ID) {

            if (apiResult.isValidResponse()) {
                dismissProgressDialog();
                ApiGetUserResult apiGetUserResult = (ApiGetUserResult) apiResult.getResult();
                Log.d(TAG, "onNewResult: User = " + apiGetUserResult.getUser());
                if (getContext() != null) {
                    PreferencesHandler.setUserData(getContext(), apiGetUserResult.getUser());
                    goToMainActivity();
                }
            } else {
                if (apiResult.getResponse() != null && apiResult.getResponse().code() == 401 && apiResult.getRequestId() != GET_USER_BY_FIREBASE_AFTER_LINKING_ACCOUNT_REQUEST_ID) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {

                        ApiGetUserByFirebaseTokenRequest apiGetUserByFirebaseTokenRequest = (ApiGetUserByFirebaseTokenRequest) apiResult.getRequest();

                        // Name, email address, and profile photo Url
                        String firstName = user.getDisplayName();
                        String lastName = "";
                        String email = user.getEmail();
//                        String facebookToken = apiGetUserByFirebaseTokenRequest.getFacebookToken();

                        if (!TextUtils.isEmpty(apiGetUserByFirebaseTokenRequest.getFirtName())) {
                            firstName = apiGetUserByFirebaseTokenRequest.getFirtName();
                        }

                        if (!TextUtils.isEmpty(apiGetUserByFirebaseTokenRequest.getLastName())) {
                            lastName = apiGetUserByFirebaseTokenRequest.getLastName();
                        }

                        if (!TextUtils.isEmpty(apiGetUserByFirebaseTokenRequest.getEmail())) {
                            email = apiGetUserByFirebaseTokenRequest.getEmail();
                        }

                        String uid = user.getUid();

                        User newUser = new User();
                        newUser.setFirstName(firstName);
                        newUser.setLastName(lastName);
                        newUser.setEmail(email);
                        newUser.setUid(uid);

                        CreateUserService.startForRequest(getActivity(), CREATE_USER_REQUEST_ID, newUser);
                    } else {
                        showToast(R.string.user_login_validation_notFirebaseUser);
                    }

                } else {
                    dismissProgressDialog();
                    showToast(R.string.user_login_validation_unknownError);
                }
            }
        } else if (apiResult.getRequestId() == CREATE_USER_REQUEST_ID) {
            dismissProgressDialog();
            if (apiResult.isValidResponse()) {
                dismissProgressDialog();
                ApiGetUserResult apiGetUserResult = (ApiGetUserResult) apiResult.getResult();
                Log.d(TAG, "onNewResult: User = " + apiGetUserResult.getUser());
                PreferencesHandler.setUserData(getContext(), apiGetUserResult.getUser());

                goToMainActivity();
            } else {
                showToast(R.string.user_login_create_error);
            }
        } else if (apiResult.getRequestId() == UPDATE_USER_REQUEST_ID) {

            if (apiResult.isValidResponse()) {
                Log.d(TAG, "onNewResult: Linking success");
                GetUserByFirebaseTokenService.startForRequest(getContext(), GET_USER_BY_FIREBASE_AFTER_LINKING_ACCOUNT_REQUEST_ID, false, null, null, null, null);
            } else {
                dismissProgressDialog();
                showToast(R.string.user_login_linkError);
            }

        }
    }

    @Override
    public void onNewUpdate(ApiUpdate apiUpdate) {

    }

    private void goToMainActivity() {
        ProfileFragment profileFragment = new ProfileFragment();
        getBaseActivity().replaceFragment(profileFragment, false);
        ((MainActivity) getBaseActivity()).setUserNavigationState();
    }

    // FIREBASE

    public void checkAccountExists(final String email, final String password) {
        showProgressDialog();
        if (!TextUtils.isEmpty(email)) {
            auth.fetchProvidersForEmail(email)
                    .addOnFailureListener(
                            new TaskFailureLogger(TAG, "Error fetching providers for email"))
                    .addOnCompleteListener(
                            new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        List<String> providers = task.getResult().getProviders();
                                        if (providers != null && !providers.isEmpty() && providers.get(0).equalsIgnoreCase(EmailAuthProvider.PROVIDER_ID)) {
                                            // user exist
                                            Utils.resetFcmToken();
                                            login(email, password);
                                        } else {
                                            // no email provider
                                            dismissProgressDialog();
                                            showToast(R.string.user_register_notExists);
                                        }

                                    } else {
                                        dismissProgressDialog();
                                        showToast(R.string.user_login_validation_unknownError);
                                    }
                                }
                            });
        }
    }


    private void getGetUserTokenAndUserdata(final boolean signUp, final String firtName, final String lastName, final String email, final String facebookToken) {
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().getToken(false).addOnCompleteListener(getActivity(), new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "auth.getCurrentUser().getToken: = " + task.getResult().getToken());
                        showProgressDialog();
                        PreferencesHandler.setFirebaseToken(getContext(), task.getResult().getToken());

                        if (signUp) {
                            Log.d(TAG, "getGetUserTokenAndUserdata - onComplete: SignUp success");
                            GetUserByFirebaseTokenService.startForRequest(getContext(), GET_USER_BY_FIREBASE_AFTER_SIGN_UP_REQUEST_ID, signUp, firtName, lastName, email, facebookToken);
                        } else {
                            Log.d(TAG, "getGetUserTokenAndUserdata - onComplete: Login success");
                            GetUserByFirebaseTokenService.startForRequest(getContext(), GET_USER_BY_FIREBASE_AFTER_LOGIN_REQUEST_ID, signUp, firtName, lastName, email, facebookToken);
                        }
                    } else {
                        dismissProgressDialog();
                        showToast(R.string.user_login_getToken);
                    }
                }
            });
        } else {
            dismissProgressDialog();
            showToast(R.string.user_login_validation_unknownError);
        }
    }


    // Handle email

    private void login(final String email, final String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(
                        new TaskFailureLogger(TAG, "Error signing in with email and password"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // Login success
                        getGetUserTokenAndUserdata(false, null, null, email, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressDialog();
                        // Show error message
                        loginPasswordLayout.setError(getString(com.firebase.ui.auth.R.string.fui_error_invalid_password));
                    }
                });
    }


    private void registerUser(final String email, final String firstName, final String lastName, final String password) {
        // create the user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error creating user"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                          @Override
                                          public void onSuccess(AuthResult authResult) {
                                              final FirebaseUser firebaseUser = authResult.getUser();

                                              firebaseUser.sendEmailVerification();

                                              UserProfileChangeRequest changeNameRequest =
                                                      new UserProfileChangeRequest.Builder()
                                                              .setDisplayName(firstName + " " + lastName).build();

                                              // Set display name
                                              firebaseUser.updateProfile(changeNameRequest)
                                                      .addOnFailureListener(new TaskFailureLogger(TAG, "Error setting display name"))
                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {

                                                              // register succes
                                                              getGetUserTokenAndUserdata(true, firstName, lastName, email, null);
                                                          }
                                                      });
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();

                if (e instanceof FirebaseAuthWeakPasswordException) {
                    // Password too weak
                    signUpPasswordLayout.setError(getResources().getQuantityString(com.firebase.ui.auth.R.plurals.fui_error_weak_password, 1));
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Email address is malformed
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_invalid_email_address));
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    // Collision with existing user email
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_email_account_creation_error));
                } else {
                    // General error message, this branch should not be invoked but
                    // covers future API changes
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_email_account_creation_error));
                }
            }
        });
    }

    private void linkWithAnonymousUser(FirebaseUser currentUser, final String email, final String password, final String firstName, final String lastName) {
        showProgressDialog();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        currentUser.linkWithCredential(credential)
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error creating user"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        user.setUid(authResult.getUser().getUid());
                        if (!TextUtils.isEmpty(firstName)) {
                            user.setFirstName(firstName);
                        }

                        if (!TextUtils.isEmpty(lastName)) {
                            user.setLastName(lastName);
                        }

                        UpdateUserService.startForRequest(getContext(), UPDATE_USER_REQUEST_ID, user, null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();
                e.printStackTrace();

                if (e instanceof FirebaseAuthWeakPasswordException) {
                    // Password too weak
                    signUpPasswordLayout.setError(getResources().getQuantityString(com.firebase.ui.auth.R.plurals.fui_error_weak_password, 1));
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Email address is malformed
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_invalid_email_address));
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    // Collision with existing user email
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_email_account_creation_error));
                } else {
                    // General error message, this branch should not be invoked but
                    // covers future API changes
                    signUpEmailLayout.setError(getString(com.firebase.ui.auth.R.string.fui_email_account_creation_error));
                }

                showToast(R.string.user_login_anonymous_linkError);
            }
        });
    }

    // Handle Facebook
    private void handleFacebookAccessToken(final FacebookProfile facebookProfile) {
        Log.d(TAG, "handleFacebookAccessToken:" + facebookProfile);
        showProgressDialog();
        Utils.resetFcmToken();

        AuthCredential credential = FacebookAuthProvider.getCredential(facebookProfile.getAccessToken().getToken());
        auth.signInWithCredential(credential)
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error login by facebook"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        getGetUserTokenAndUserdata(false, facebookProfile.getFirst_name(), facebookProfile.getLast_name(), facebookProfile.getEmail(), facebookProfile.getAccessToken().getToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + e.getMessage());

                        dismissProgressDialog();

                        String exceptionMessage = getString(R.string.user_login_anonymous_linkError);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Facebook token is credential is malformed or expired
                            exceptionMessage = getString(R.string.user_validation_authFbMalformed);
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            // Collision with existing user
                            exceptionMessage = getString(R.string.user_validation_authFbCollision);
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            //  User's account has been disabled, deleted, or its credentials are no longer valid
                            exceptionMessage = getString(R.string.user_validation_authFbInvalid);
                        } else {
                            // General error message, this branch should not be invoked but
                            // covers future API changes
                        }

                        showToast(exceptionMessage);
                    }
                });
    }

    private void linkWithAnonymousUserByFacebook(FirebaseUser currentUser, final FacebookProfile facebookProfile) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(facebookProfile.getAccessToken().getToken());

        currentUser.linkWithCredential(credential)
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error creating user"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        user.setUid(authResult.getUser().getUid());
                        if (!TextUtils.isEmpty(facebookProfile.getFirst_name())) {
                            user.setFirstName(facebookProfile.getFirst_name());
                        }

                        if (!TextUtils.isEmpty(facebookProfile.getLast_name())) {
                            user.setLastName(facebookProfile.getLast_name());
                        }

                        if (!TextUtils.isEmpty(facebookProfile.getEmail())) {
                            user.setEmail(facebookProfile.getEmail());
                        }

                        UpdateUserService.startForRequest(getContext(), UPDATE_USER_REQUEST_ID, user, null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();

                String exceptionMessage = getString(R.string.user_login_anonymous_linkError);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Facebook token is credential is malformed or expired
                    exceptionMessage = getString(R.string.user_validation_authFbMalformed);
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    // Collision with existing user
                    exceptionMessage = getString(R.string.user_validation_authFbCollision);
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    //  User's account has been disabled, deleted, or its credentials are no longer valid
                    exceptionMessage = getString(R.string.user_validation_authFbInvalid);
                } else {
                    // General error message, this branch should not be invoked but
                    // covers future API changes
                }

                if (e instanceof FirebaseAuthUserCollisionException) {
                    handleFacebookAccessToken(facebookProfile);
                } else {
                    showToast(exceptionMessage);
                }
            }
        });
    }
}
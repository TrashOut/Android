package me.trashout.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.activity.StartActivity;
import me.trashout.fragment.base.BaseFragment;
import me.trashout.model.FacebookProfile;
import me.trashout.model.User;
import me.trashout.service.GetUserByFirebaseTokenService;
import me.trashout.service.UpdateUserService;
import me.trashout.utils.PreferencesHandler;

import static me.trashout.activity.base.BaseActivity.EXTRA_ARGUMENTS;

/**
 * Created by Ondrej Hoos on 04.12.2017.
 * Appmine.cz
 */

public class TutorialFragmentItem extends BaseFragment {

    private static final int GET_USER_BY_FIREBASE_AFTER_LOGIN_REQUEST_ID = 901;
    private static final int GET_USER_BY_FIREBASE_AFTER_SIGN_UP_REQUEST_ID = 902;
    private static final int UPDATE_USER_REQUEST_ID = 904;

    public static final String TUTORIAL_PAGE = "tutorial_page";
    public static final int TUTORIAL_PAGE_1 = 1;
    public static final int TUTORIAL_PAGE_2 = 2;
    public static final int TUTORIAL_PAGE_3 = 3;
    public static final int TUTORIAL_PAGE_4 = 4;
    public static final int TUTORIAL_PAGE_5 = 5;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.text_layout_top)
    LinearLayout textLayoutTop;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.top_text)
    TextView topText;

    @BindView(R.id.text_layout_bottom)
    LinearLayout textLayoutBottom;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.login_layout)
    LinearLayout loginLayout;
    @BindView(R.id.btn_without_sign_in)
    TextView btnWithoutSignIn;

    @BindView(R.id.tutorial_sign_up_accept_user_data_collection)
    AppCompatCheckBox acceptTermsAndPolicyCheckBox;
    @BindView(R.id.tutorial_sign_up_accept_user_data_collection_text)
    TextView acceptTermsAndPolicyTextView;

    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    private User user;
    private int page;

    public static TutorialFragmentItem newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(TUTORIAL_PAGE, page);

        TutorialFragmentItem fragment = new TutorialFragmentItem();
        fragment.setArguments(args);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            page = bundle.getInt(TUTORIAL_PAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_item, container, false);

        readBundle(getArguments());
        auth = FirebaseAuth.getInstance();
        user = PreferencesHandler.getUserData(getContext());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        acceptTermsAndPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());

        initPage();
    }

    public void initPage() {
        switch (page) {
            case TUTORIAL_PAGE_1:
                setData(R.drawable.tutorial1, R.string.tutorial_title_1, R.string.tutorial_text_1, false);
                break;
            case TUTORIAL_PAGE_2:
                setData(R.drawable.tutorial2, R.string.tutorial_title_2, R.string.tutorial_text_2, false);
                break;
            case TUTORIAL_PAGE_3:
                setData(R.drawable.tutorial3, R.string.tutorial_title_3, R.string.tutorial_text_3, false);
                break;
            case TUTORIAL_PAGE_4:
                setData(R.drawable.tutorial4, R.string.tutorial_title_4, R.string.tutorial_text_4, false);
                break;
            case TUTORIAL_PAGE_5:
                setData(R.drawable.tutorial5, R.string.tutorial_title_5, R.string.tutorial_text_5, true);
                break;
            default:
                break;
        }
    }

    public void setData(int imageId, int titleId, int textId, boolean showTop) {
        if (showTop) {
            topTitle.setText(titleId);
            topText.setText(textId);
            btnWithoutSignIn.setText(Html.fromHtml(getString(R.string.tutorial_signup_withoutSignIn)));
        } else {
            title.setText(titleId);
            text.setText(textId);
        }
        imageView.setImageResource(imageId);
        textLayoutTop.setVisibility(showTop ? View.VISIBLE : View.GONE);
        textLayoutBottom.setVisibility(showTop ? View.GONE : View.VISIBLE);
        loginLayout.setVisibility(showTop ? View.VISIBLE : View.GONE);
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
                    Toast.makeText(TutorialFragmentItem.this.getContext(), exceptionMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handle Facebook
    private void handleFacebookAccessToken(final FacebookProfile facebookProfile) {
        Log.d(TAG, "handleFacebookAccessToken:" + facebookProfile);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(facebookProfile.getAccessToken().getToken());
        auth.signInWithCredential(credential)
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error login by facebook"))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        tutorialCompleted();
                        startActivity(new Intent(getContext(), StartActivity.class));
                        finish();
                        //getGetUserTokenAndUserdata(false, facebookProfile.getFirst_name(), facebookProfile.getLast_name(), facebookProfile.getEmail(), facebookProfile.getAccessToken().getToken());
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

                        Toast.makeText(TutorialFragmentItem.this.getContext(), exceptionMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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
                        Toast.makeText(TutorialFragmentItem.this.getContext(), R.string.user_login_getToken, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            dismissProgressDialog();
            Toast.makeText(TutorialFragmentItem.this.getContext(), R.string.user_login_validation_unknownError, Toast.LENGTH_SHORT).show();
        }
    }

    public void tutorialCompleted() {
        PreferencesHandler.setTutorialWasShown(TutorialFragmentItem.this.getContext(), true);
    }

    @OnClick(R.id.sign_up_facebook_btn)
    public void clickOnSignUpFacebook() {
        if (!handleTermsAndPolicyAgreement()) return;

        tutorialCompleted();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

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

    private boolean handleTermsAndPolicyAgreement() {
        if (!acceptTermsAndPolicyCheckBox.isChecked()) {
            acceptTermsAndPolicyCheckBox.setError("");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.sign_up_btn)
    public void clickOnSignUp() {
        if (!handleTermsAndPolicyAgreement()) return;

        tutorialCompleted();
        //startActivity(BaseActivity.generateIntent(getContext(), LoginFragment.class.getName(), null, MainActivity.class));
        startActivity(new Intent(getContext(), StartActivity.class));
        finish();
    }

    @OnClick(R.id.btn_without_sign_in)
    public void clickOnWithoutSignIn() {
        if (!handleTermsAndPolicyAgreement()) return;

        tutorialCompleted();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_ARGUMENTS, true);
        Intent intent = new Intent(getContext(), StartActivity.class).putExtra(EXTRA_ARGUMENTS, bundle);
        startActivity(intent);
        finish();
    }
}

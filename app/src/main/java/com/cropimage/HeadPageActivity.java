package com.cropimage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adlocus.Ad;
import com.adlocus.AdListener;
import com.adlocus.AdLocusLayout;
import com.adlocus.InterstitialAd;
import com.adlocus.InterstitialVideoReq;
import com.adlocus.PushAd;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhengweipan.Guandisignonehundred.R;


public class HeadPageActivity extends Activity {
	boolean change=true;
	private TextView test;
	private LoginButton loginButton;
	private InterstitialVideoReq mInterstitialVideoReq;
	CallbackManager callbackManager;
	AccessTokenTracker accessTokenTracker ;
	ProfileTracker  profileTracker;
	private static final String TAG = "HeadPageActivity";
	private ImageView fbImg;
	private  TextView fbName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 開啟全螢幕 
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 設定隱藏APP標題
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		setContentView(R.layout.activity_head_page);
		fbLogin();


		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.jhengweipan.AssistantGUI",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}


		test=(TextView)findViewById(R.id.textView1);


		RelativeLayout myLayout=(RelativeLayout)findViewById(R.id.myLayout);
		Timer timer = new Timer();
		final InterstitialAd interstitialAd = new InterstitialAd(this, "2879057fde4e8158577744b379d544d98b30457d");
		interstitialAd.loadAd();
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);


		TimerTask task = new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (change) {
							change = false;
							test.setTextColor(Color.TRANSPARENT); // 讓文字透明
						} else {
							change = true;
							test.setTextColor(Color.DKGRAY);
						}
					}
				});
			}
		};
		timer.schedule(task, 1, 800);
		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {



				final ProgressDialog  myDialog = ProgressDialog.show(HeadPageActivity.this, "請稍後片刻....",
						"正在載入中", true);
				new Thread() {
					public void run() {
						try {
							sleep(3000);
							Intent intent = new Intent();
							intent.setClass(HeadPageActivity.this, MainActivity.class);
							startActivity(intent);
							interstitialAd.show();
							finish();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {

							myDialog.dismiss();
						}
					}
				}.start();

			}
		});



	}

	@Override
	protected void onResume() {
		super.onResume();


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	private  void fbLogin(){
		 List<String> PERMISSIONS_PUBLISH = Arrays.asList("public_profile", "email","user_friends");
		loginButton = (LoginButton) findViewById(R.id.login_button);
		fbImg  =(ImageView )findViewById(R.id.fdimg);
		fbName  =(TextView ) findViewById(R.id.fbname);
		loginButton.setReadPermissions(PERMISSIONS_PUBLISH);


//		accessTokenTracker = new AccessTokenTracker() {
//			@Override
//			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//			}
//		};

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				if(oldProfile!=null){
					//登出後
					fbName.setText("");
					 fbImg.setImageBitmap(null);
					Log.d(TAG, "oldProfile: "+oldProfile.getProfilePictureUri(150, 150));
					Log.d(TAG, "oldProfile: " + oldProfile.getId());
					Log.d(TAG, "oldProfile: " + oldProfile.getFirstName());
					Log.d(TAG, "oldProfile: " + oldProfile.getLastName());
					Log.d(TAG, "oldProfile: " + oldProfile.getLinkUri());

				}

				if(currentProfile!=null){
					 //登入
					fbName.setText(currentProfile.getName());
					MyApi.loadImage(String.valueOf(currentProfile.getProfilePictureUri(150,150)),fbImg,HeadPageActivity.this);
					Log.d(TAG, "currentProfile: "+currentProfile.getProfilePictureUri(150,150));
					Log.d(TAG, "currentProfile: "+currentProfile.getId());
					Log.d(TAG, "currentProfile: "+currentProfile.getFirstName());
					Log.d(TAG, "currentProfile: "+currentProfile.getLastName());
					Log.d(TAG, "currentProfile: "+currentProfile.getLinkUri());


				}

			}
		};
		profileTracker.startTracking();
		if(profileTracker.isTracking()){
			Log.d(getClass().getSimpleName(), "profile currentProfile Tracking: " + "yes");
			if(Profile.getCurrentProfile().getName()!=null)	fbName.setText(Profile.getCurrentProfile().getName());
			if(Profile.getCurrentProfile().getProfilePictureUri(150, 150)!=null)	MyApi.loadImage(String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(150, 150)),fbImg, HeadPageActivity.this);
		}


		else
			Log.d(getClass().getSimpleName(), "profile currentProfile Tracking: " + "no");
//		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//			@Override
//				public void onSuccess(LoginResult loginResult) {
//
////				/* make the API call */
////				new GraphRequest(
////						AccessToken.getCurrentAccessToken(),
////						"/me/friends",
////						null,
////						HttpMethod.GET,
////						new GraphRequest.Callback() {
////							public void onCompleted(GraphResponse response) {
////								Log.d(TAG, "onCompleted: "+response.toString());
////			/* handle the result */
////							}
////						}
////				).executeAsync();
////
//
//			}
//
//			@Override
//			public void onCancel() {
//
//			}
//
//			@Override
//			public void onError(FacebookException error) {
//
//			}
//		});

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		profileTracker.stopTracking();

	}
}


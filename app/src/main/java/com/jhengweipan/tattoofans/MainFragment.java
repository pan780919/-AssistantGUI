package com.jhengweipan.tattoofans;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhengweipan.Guandisignonehundred.R;
import com.jhengweipan.MyAPI.VersionChecker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private WebView mWebView;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
//    private String uri = "http://ur.stage.hxcld.com/stores/cj/login";
    //test
    private String uri = "https://www.etax.nat.gov.tw/etw-main/front/ETW183W2_10507/";
    ProgressDialog myDialog = null;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mLayoutWeb;
    private WebView mWebView2;
    private boolean isFirst = true;
    private boolean isCheckNet = false;
    private ImageButton mPreviousBtn, mNextBtn, mRefreshBtn;
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mWebView = (WebView) rootView.findViewById(R.id.mwebview_oil);
        mLayoutWeb = (RelativeLayout) rootView.findViewById(R.id.rl_webview);
        mPreviousBtn = (ImageButton) rootView.findViewById(R.id.button);
        mNextBtn = (ImageButton) rootView.findViewById(R.id.button2);
        mRefreshBtn = (ImageButton) rootView.findViewById(R.id.button3);


        rootView.findViewById(R.id.button).setOnClickListener(this);
        rootView.findViewById(R.id.button2).setOnClickListener(this);
        rootView.findViewById(R.id.button3).setOnClickListener(this);
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setUpWebViewDefaults(mWebView);
//        setUpWebViewDefaults(mWebView2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebView,true);
        }
        getBundle();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onResume() {

        if (isCheckNet == true) {
            mWebView.reload();
            isCheckNet = false;
        } else {
            mWebView.onResume();
        }


        super.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 120) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == 160) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 240) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }


        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowContentAccess(true);
        settings.setSupportMultipleWindows(true);
//        settings.setLoadsImagesAutomatically(true);
        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);//自適螢幕大小
//        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
//        mWebView.setWebViewClient(new MyWebViewClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (isFirst) {
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage("讀取中...");
                    mProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
                    mProgressDialog.show();

                }

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                handler.sendEmptyMessage(0);
                mProgressDialog.dismiss();
                isFirst = false;
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                String cookies = CookieManager.getInstance().getCookie(url);
//                Log.d(TAG, "shouldOverrideUrlLoading: "+cookies);
//                CookieSyncManager.createInstance(getActivity());
//                CookieManager cookieManager = CookieManager.getInstance();
//                cookieManager.setAcceptCookie(true);
//                cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
//                CookieSyncManager.getInstance().sync();
                view.loadUrl(url);
                return false;
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == -2) {
                    checkNetWork();

                } else {
                    otherError();
                }


                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        int sysVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "sysVersion: "+sysVersion);
        if (sysVersion <= 20) {

            webView.setWebChromeClient(new MyWebClient());

        } else {

            webView.setWebChromeClient(new WebChromeClient() {


                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams) {

                    if (mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(new Uri[0]);
                    }
                    mFilePathCallback = filePathCallback;

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.e(TAG, "Unable to create Image File", ex);
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");

                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                    startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                    return true;
                }

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                    mWebView.setVisibility(View.GONE);

                    mWebView2 = new WebView(getActivity());
                    setUpWebViewDefaults(mWebView2);
                    mLayoutWeb.addView(mWebView2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    mLayoutWeb.setVisibility(View.VISIBLE);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(null);
                    transport.setWebView(mWebView2);
                    resultMsg.sendToTarget();
                    return true;
                }

                @Override
                public void onCloseWindow(WebView window) {

                    if (mWebView2 != null) {
                        mWebView.setVisibility(View.VISIBLE);
                        mLayoutWeb.setVisibility(View.GONE);
                        mLayoutWeb.removeAllViews();
                        mWebView2.loadUrl("about:blank");
                        mWebView2.clearCache(false);
                        mWebView2 = null;
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//          for 4.1
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:

                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }

                break;
            case R.id.button2:
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }

                break;
            case R.id.button3:
                mWebView.reload();

                break;

        }

    }
    private void configVersionCheck() {

//        if (!GtApi.checkNetwork(IndexActivity.this)) return;

        VersionChecker.checkOnce(getActivity(), new VersionChecker.DoneAdapter() {

            @Override
            public void onHasNewVersion() {
                new AlertDialog.Builder(getActivity())
                        .setTitle("已有最新版本!")
                        .setMessage("目前有最新版本上架,請盡快更新")
                        .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(VersionChecker.openMartketIntent());
                                dialog.dismiss();
                            }
                        })
                        .show();
            }


        });

    }


    public class MyWebClient extends WebChromeClient {

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            Log.e("in", "android3.0-");
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            Log.e("in", "android3.0");
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
        }

        // For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Log.e("in", "android4.1");
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            mWebView.setVisibility(View.GONE);

            mWebView2 = new WebView(getActivity());
            setUpWebViewDefaults(mWebView2);
            mLayoutWeb.addView(mWebView2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mLayoutWeb.setVisibility(View.VISIBLE);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(null);
            transport.setWebView(mWebView2);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {

            if (mWebView2 != null) {
                mWebView.setVisibility(View.VISIBLE);
                mLayoutWeb.setVisibility(View.GONE);
                mLayoutWeb.removeAllViews();
                mWebView2.loadUrl("about:blank");
                mWebView2.clearCache(false);
                mWebView2 = null;
            }
        }


    }

    public boolean checkInternet(android.content.Context context) {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            Log.d("webview", "info == null || !info.isConnected()");
            result = false;
        } else {
            if (!info.isAvailable()) {

                result = false;
            } else {

                result = true;
            }
        }
        return result;
    }

    private void checkNetWork() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.Network_status))
                .setMessage(getString(R.string.no_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent settintIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        startActivity(settintIntent);
                        isCheckNet = true;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    private void otherError() {
        new AlertDialog.Builder(getActivity())
                .setTitle("網路連線逾時")
                .setMessage("是否要重新連線")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWebView.reload();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();

    }

    public void fun_thread() {
        //為了能與handler建立關聯進而控制Progress Dialog / Progress Bar的關閉
        Message m = new Message();
        Bundle b = m.getData();
        b.putInt("WHICH", 1);
        m.setData(b);
        handler.sendMessageDelayed(m, 30000);
    }

    /*
   * 透過Handler將Progress Dialog / Progress Bar關閉
   */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int which = msg.getData().getInt("WHICH");
//            progressbar.setVisibility(View.GONE);
            myDialog.dismiss();
            if (which == 1) {
                Log.e("netWork", "no");

            } else {
                Log.e("netWork", "yes");
            }
        }
    };


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
    private  void getBundle(){
        Bundle bundle =getActivity().getIntent().getExtras();
        String murl = bundle.getString("url");
        Log.d(TAG, "getBundle: "+murl);
        mWebView.loadUrl(murl);




    }
}
package com.cropimage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.adlocus.PushAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.jhengweipan.Guandisignonehundred.R;
import com.jhengweipan.MyAPI.VersionChecker;
import com.jhengweipan.ga.MyGAManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import myAppKey.mykey;


public class MainActivity extends Activity {
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    private Bitmap bitmap;
    private static final int PICKER = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 200;
    private Handler handler;
    private Button buttonObj, buttonObj2;
    private static final String TAG = "MainActivity";
    private InterstitialAd interstitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyGAManager.sendScreenName(MainActivity.this,"開啟檔案頁面");
        AdView mAdView = (AdView) findViewById(R.id.adView);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(mykey.ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        mAdView.loadAd(adRequest);
        Intent promotionIntent = new Intent(this, MainActivity.class);
        PushAd.enablePush(MainActivity.this, mykey.AdLoucsKey, promotionIntent);
        getAppList();
        //讀取手機解析度
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        mImg = (ImageView) findViewById(R.id.imageViewObj);
        buttonObj = (Button) findViewById(R.id.buttonObj);
        buttonObj2 = (Button) findViewById(R.id.button1);
        buttonObj2.setVisibility(View.GONE);
        buttonObj.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("開啟方式!!")
                        .setMessage("選擇上傳方式!!")
                        .setPositiveButton("開起相機照一張!!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                getPermissionCAMERA();
                                MyGAManager.sendActionName(MainActivity.this,"開啟方式","開啟相機");
                            }

                        })
                        .setNegativeButton("開啟相簿找一張!!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                READEXTERNALSTORAGE();
                                MyGAManager.sendActionName(MainActivity.this,"開啟方式","開啟相簿");
                            }

                        }).show();


            }


        });
        buttonObj2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //View view = findViewById(R.id.LinearLayout1);
                //Bitmap bm = bitmapFromView(view);
                savePicture(bitmap);
//				sharePicWithUri(uri);
                String uri = "sdcard/req_images/temp.jpg";
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, page_2_Activity.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("Uri", uri);
                //將Bundle物件assign給intent
                intent.putExtras(bundle);
                //切換Activity
                startActivity(intent);
            }
        });


    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKER) {
            if (resultCode == Activity.RESULT_OK) {


            }
        }
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
//			sharePicWithUri(uri);
//			if (true) return;
            ContentResolver cr = this.getContentResolver();
            try {
                //讀取照片，型態為Bitmap
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap,
                        mPhone.heightPixels);
                else ScalePic(bitmap, mPhone.widthPixels);
            } catch (FileNotFoundException e) {
            }
            buttonObj2.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //縮放照片
    private void ScalePic(Bitmap bitmap, int phone) {
        //縮放比例預設為1
        float mScale = 1;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
            if (bitmap.getWidth() > phone) {
            //判斷縮放比例
            mScale = (float) phone / (float) bitmap.getWidth();

            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            mImg.setImageBitmap(mScaleBitmap);
        } else mImg.setImageBitmap(bitmap);
    }

    //儲存圖片
    public Uri savePicture(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        String fname = "temp.jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    private void getPermissionCAMERA() {

        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage("我真的沒有要做壞事, 給我權限吧?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_EXTERNAL_STORAGE

                );
            }

        } else {
            //開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且
            // 帶入
            //requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult
            ContentValues value = new ContentValues();
            value.put(Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI,
                    value);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
            startActivityForResult(intent, CAMERA);

        }

    }

    private void READEXTERNALSTORAGE() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage("我真的沒有要做壞事, 給我權限吧?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE

                );
            }

        } else {
            //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
            //為點選相片後返回程式呼叫onActivityResult
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PHOTO);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getAppList() {
        PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        final JSONArray jsonArray = new JSONArray();
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        for (PackageInfo packageInfo : packageInfoList) {
//            Log.d(TAG, "getAppList: "+packageInfo.applicationInfo);

            try {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("Address",addresss);
//                jsonObject.put("DeviceId",MyApi.getEncodedDeviceId(getApplication()));
//                jsonObject.put("Imei",MyApi.getImeiandSim(getApplication()));
                /**
                 包名獲取方法：packageInfo.packageName

                 icon獲取獲取方法：packageManager.getApplicationIcon(applicationInfo)

                 應用名稱獲取方法：packageManager.getApplicationLabel(applicationInfo)

                 使用許可權獲取方法：packageManager.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS)
                 *
                 * */
//                packageManager.getApplicationIcon(packageInfo.applicationInfo);
                jsonObject.put("PackageName", packageManager.getApplicationLabel(packageInfo.applicationInfo) + "");
                String key = packageInfo.applicationInfo + "";
                hashMap.put(key, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "getAppList: " + packageManager.getApplicationLabel(packageInfo.applicationInfo));
//            Log.d(TAG, "getAppList: " + lstAddress);
        }
        for (JSONObject object : hashMap.values()) {
            jsonArray.put(object);
        }
        Log.d(TAG, "getAppList: " + jsonArray.toString());

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵

            ConfirmExit(); //呼叫ConfirmExit()函數

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    public void ConfirmExit() {

        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this); //創建訊息方塊

        ad.setTitle("離開");

        ad.setMessage("確定要離開?");

        ad.setPositiveButton("是", new DialogInterface.OnClickListener() { //按"是",則退出應用程式

            public void onClick(DialogInterface dialog, int i) {
                interstitial.show();
                MainActivity.this.finish();//關閉activity

            }

        });

        ad.setNegativeButton("否", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作

            public void onClick(DialogInterface dialog, int i) {

            }

        });

        ad.show();//顯示訊息視窗


    }

}
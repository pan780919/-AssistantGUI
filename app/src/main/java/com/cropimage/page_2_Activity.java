package com.cropimage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.jhengweipan.Guandisignonehundred.R;


public class page_2_Activity extends Activity {
    private Button shareBtn, shareFbBtn;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private TextView mTextView;
    private static final String TAG = "page_2_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_2);
        Bundle photo = this.getIntent().getExtras();

        String strPath = photo.getString("Uri");
        Log.d(TAG, strPath);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        ImageView imag = (ImageView) findViewById(R.id.imageView1);

        shareFbBtn = (Button) findViewById(R.id.button2);
        shareBtn = (Button) findViewById(R.id.button1);
        mTextView = (TextView) findViewById(R.id.textview);
        mTextView.setText(String.valueOf((int)(Math.random()* 99)));
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bm = BitmapFactory.decodeFile(strPath, options);
        imag.setImageBitmap(bm);
        shareBtn.setVisibility(View.VISIBLE);
        shareBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                View view = findViewById(R.id.RelativeLayout1);
                shareBtn.setVisibility(View.GONE);
                shareFbBtn.setVisibility(View.GONE);
                Bitmap bitamp = bitmapFromView(view);
                sharePicWithUri(savePicture(bitamp));

            }
        });

        shareFbBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = findViewById(R.id.RelativeLayout1);
                shareBtn.setVisibility(View.GONE);
                shareFbBtn.setVisibility(View.GONE);
                Bitmap bitamp = bitmapFromView(view);
//				if (ShareDialog.canShow(ShareLinkContent.class)) {
//					ShareLinkContent linkContent = new ShareLinkContent.Builder()
//							.setContentTitle("請支持認養代替購買")
//							.setContentDescription("台灣流浪動物認養APP")
//							.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.jackpan.TaiwanpetadoptionApp"))
//							.setImageUrl(savePicture(bitamp))
//
//							.build();
//					shareDialog.show(linkContent);
//				}
                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitamp)
                            .build();
                    SharePhotoContent sharePhoto = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.jackpan.TaiwanpetadoptionApp"))
                            .build();
                    shareDialog.show(sharePhoto);
                }


            }
        });

    }

    //截圖
    private Bitmap bitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = view.getDrawingCache();
        Bitmap ret = bm.copy(bm.getConfig(), false);
        view.setDrawingCacheEnabled(false);
        bm.recycle();
        bm = null;
        System.gc();
        return ret;
    }

    //分享到
    private void sharePicWithUri(Uri uri) {
        Log.d(TAG, "sharePicWithUri: " + uri);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "傳送到"));
    }

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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

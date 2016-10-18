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
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.jhengweipan.Guandisignonehundred.R;


public class page_2_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_2);
		Bundle photo =this.getIntent().getExtras();

	       String strPath = photo.getString("Uri");
	      Log.d("Jack", strPath);
	      
	      ImageView imag = (ImageView)findViewById(R.id.imageView1);
	      
	  
	      final Button btn =(Button)findViewById(R.id.button1);
	      final BitmapFactory.Options options = new BitmapFactory.Options();
	      options.inSampleSize = 4;

	      Bitmap bm = BitmapFactory.decodeFile(strPath,options);

	      imag.setImageBitmap(bm);
	      btn.setVisibility(View.VISIBLE);
	      btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view =findViewById(R.id.RelativeLayout1);
				btn.setVisibility(View.GONE);
				Bitmap bitamp=bitmapFromView(view);				
				sharePicWithUri(savePicture(bitamp));
				
			}
		});
	}
	
	//截圖
	private Bitmap bitmapFromView(View view)
	{
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
	private void sharePicWithUri(Uri uri)
	{
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("image/jpeg");
		startActivity(Intent.createChooser(shareIntent, "傳送到"));
	}
	public Uri savePicture(Bitmap bitmap)
	{
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
}

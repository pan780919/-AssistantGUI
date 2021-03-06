package com.cropimage;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * 底图缩放，浮层不变
 * @author yanglonghui
 *
 */
public class CropImageView extends View {
	
	//单点触摸的时候
	private float oldX=0;
	private float oldY=0;
	
	//多点触摸的时候
	private float oldx_0=0;
	private float oldy_0=0;
	
	private float oldx_1=0;
	private float oldy_1=0;
	
	//状态
	private final int STATUS_Touch_SINGLE = 1;//单点
	private final int STATUS_TOUCH_MULTI_START = 2;//多点开始
	private final int STATUS_TOUCH_MULTI_TOUCHING = 3;//多点拖拽中
	
	private int mStatus=STATUS_Touch_SINGLE;
	
	//默认的裁剪图片宽度与高度
	private final int defaultCropWidth = 300;
	private final int defaultCropHeight = 300;
	private int cropWidth = defaultCropWidth;
	private int cropHeight = defaultCropHeight;
	
	protected float oriRationWH = 0;//原始宽高比率
	protected final float maxZoomOut = 5.0f;//最大扩大到多少倍
	protected final float minZoomIn = 0.333333f;//最小缩小到多少倍
	
	protected Drawable mDrawable;//原图
	protected FloatDrawable mFloatDrawable;//浮层
	protected Rect mDrawableSrc = new Rect();
	protected Rect mDrawableDst = new Rect();
	protected Rect mDrawableFloat = new Rect();//浮层选择框，就是头像选择框
	protected boolean isFrist = true;
	
	protected Context mContext;
    
	public CropImageView(Context context) {
		super(context);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
			
	}
	
	private void init(Context context)
	{
		this.mContext = context;
		try {  
            if(android.os.Build.VERSION.SDK_INT >= 11)  
            {  
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);  
            }  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
		mFloatDrawable = new FloatDrawable(context);//头像选择框
	}

	public void setDrawable(String pathName, int cropWidth, int cropHeight)
	{
		Bitmap bmp = null;
		bmp = ImageHelper.fixBitmapRotate(mContext, pathName, ImageHelper.getBitmapFromPath(pathName));
        this.mDrawable = new BitmapDrawable(getResources(), bmp);
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
		this.isFrist = true;
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getPointerCount()>1)
		{
			if(mStatus==STATUS_Touch_SINGLE)
			{
				mStatus=STATUS_TOUCH_MULTI_START;
				
				oldx_0 = event.getX(0);
				oldy_0 = event.getY(0);
				
				oldx_1 = event.getX(1);
				oldy_1 = event.getY(1);
			}
			else if(mStatus==STATUS_TOUCH_MULTI_START)
			{
				mStatus=STATUS_TOUCH_MULTI_TOUCHING;
			}
		}
		else
		{
			if(mStatus == STATUS_TOUCH_MULTI_START||mStatus==STATUS_TOUCH_MULTI_TOUCHING)
			{
				oldx_0 = 0;
				oldy_0 = 0;
				
				oldx_1 = 0;
				oldy_1 = 0;
				
				oldX = event.getX();
				oldY = event.getY();
			}
			
			mStatus = STATUS_Touch_SINGLE;
		}
			

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				oldX = event.getX();
				oldY = event.getY();
	            break;
	            
			case MotionEvent.ACTION_UP:
				checkBounds();
	            break;
				
			case MotionEvent.ACTION_POINTER_UP:
				break;
	            
			case MotionEvent.ACTION_MOVE:
				if(mStatus==STATUS_TOUCH_MULTI_TOUCHING)
				{
					float newx_0=event.getX(0);
					float newy_0=event.getY(0);
					
					float newx_1=event.getX(1);
					float newy_1=event.getY(1);
					
					float oldWidth= Math.abs(oldx_1-oldx_0);
					float oldHeight= Math.abs(oldy_1-oldy_0);
					
					float newWidth= Math.abs(newx_1-newx_0);
					float newHeight= Math.abs(newy_1-newy_0);
					
					boolean isDependHeight= Math.abs(newHeight-oldHeight)> Math.abs(newWidth-oldWidth);
					
			        float ration=isDependHeight?((float)newHeight/(float)oldHeight):((float)newWidth/(float)oldWidth);
			        int centerX=mDrawableDst.centerX();
			        int centerY=mDrawableDst.centerY();
			        int _newWidth=(int) (mDrawableDst.width()*ration);
			        int _newHeight=(int) ((float)_newWidth/oriRationWH);
			        
			        float tmpZoomRation=(float)_newWidth/(float)mDrawableSrc.width();
			        if(tmpZoomRation>=maxZoomOut)
			        {
			        	_newWidth=(int) (maxZoomOut*mDrawableSrc.width());
			        	_newHeight=(int) ((float)_newWidth/oriRationWH);
			        }
			        else if(tmpZoomRation<=minZoomIn)
			        {
			        	_newWidth=(int) (minZoomIn*mDrawableSrc.width());
			        	_newHeight=(int) ((float)_newWidth/oriRationWH);
			        }
			        
			        mDrawableDst.set(centerX-_newWidth/2, centerY-_newHeight/2, centerX+_newWidth/2, centerY+_newHeight/2);
			        invalidate();
			        
					Log.v("width():"+(mDrawableSrc.width())+"height():"+(mDrawableSrc.height()), "new width():"+(mDrawableDst.width())+"new height():"+(mDrawableDst.height()));
					Log.v(""+(float)mDrawableSrc.height()/(float)mDrawableSrc.width(), "mDrawableDst:"+(float)mDrawableDst.height()/(float)mDrawableDst.width());
					
					oldx_0=newx_0;
					oldy_0=newy_0;
					
					oldx_1=newx_1;
					oldy_1=newy_1;
				}
				else if(mStatus==STATUS_Touch_SINGLE)
				{
					int dx=(int)(event.getX()-oldX);
					int dy=(int)(event.getY()-oldY);
					
					oldX=event.getX();
					oldY=event.getY();
					
					if(!(dx==0&&dy==0))
					{
						mDrawableDst.offset((int)dx, (int)dy);
						invalidate();
					}
				}
	            break;
		}
		
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }
        
        configureBounds();
        
		mDrawable.draw(canvas);
		
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
	}
	
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	protected void configureBounds()
	{
		if(isFrist)
		{
			//真實寬除以真實高 目的?
			oriRationWH = ((float)mDrawable.getIntrinsicWidth()) / ((float)mDrawable.getIntrinsicHeight());
			//測試反過來 圖片方向相同 字體變寬
//			oriRationWH = ((float)mDrawable.getIntrinsicHeight()) / ((float)mDrawable.getIntrinsicWidth());

			//取得dp 這樣會根據不同裝置縮放
			final float scale = mContext.getResources().getDisplayMetrics().density;
			
			//取得寬  乘上scale 假設寬是300 就類似  300dp
			int w = Math.min(getWidth(), (int)(mDrawable.getIntrinsicWidth()*scale + 0.5f));
			int h = (int) (w / oriRationWH);
			
			//為什麼要除以2
			int left = (getWidth() - w) / 2;
			int top = (getHeight() - h) / 2;
			int right = left + w;
			int bottom = top + h;
			
			mDrawableSrc.set(left, top, right, bottom);
//			mDrawableSrc.set(bottom, left, top, right);
			mDrawableDst.set(mDrawableSrc);
			
			int floatWidth = dipTopx(mContext, cropWidth);
			int floatHeight = dipTopx(mContext, cropHeight);
			
			if(floatWidth > getWidth())
			{
				floatWidth = getWidth();
				floatHeight = cropHeight * floatWidth / cropWidth;
			}
			
			if(floatHeight > getHeight())
			{
				floatHeight = getHeight();
				floatWidth = cropWidth * floatHeight / cropHeight;
			}
			
			int floatLeft = (getWidth() - floatWidth) / 2;
			int floatTop = (getHeight() - floatHeight) / 2;
			mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + floatHeight);
			
	        isFrist = false;
		}
        
		mDrawable.setBounds(mDrawableDst);
		mFloatDrawable.setBounds(mDrawableFloat);
	}
	
	protected void checkBounds()
	{
		int newLeft = mDrawableDst.left;
		int newTop = mDrawableDst.top;
		
		boolean isChange = false;
		if(mDrawableDst.left < -mDrawableDst.width())
		{
			newLeft = -mDrawableDst.width();
			isChange = true;
		}
		
		if(mDrawableDst.top < -mDrawableDst.height())
		{
			newTop = -mDrawableDst.height();
			isChange = true;
		}
		
		if(mDrawableDst.left>getWidth())
		{
			newLeft = getWidth();
			isChange = true;
		}
		
		if(mDrawableDst.top > getHeight())
		{
			newTop = getHeight();
			isChange = true;
		}
		
		mDrawableDst.offsetTo(newLeft, newTop);
		if(isChange)
		{
			invalidate();
		}
	}
	
	public Drawable getDrawable(){
		return mDrawable;
	}
	
	public Bitmap getCropImage()
	{
		Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(tmpBitmap);
		mDrawable.draw(canvas);

		Matrix matrix = new Matrix();
		float scale=(float)(mDrawableSrc.width())/(float)(mDrawableDst.width());
//		float scale = 1;
		matrix.postScale(scale, scale);
//		Uri tempUri = getImageUri(mContext, tmpBitmap);
//	    String pathName = getRealPathFromURI(tempUri);
//	    tmpBitmap = ImageHelper.fixBitmapRotate(mContext, pathName, tmpBitmap);
//	    int degree = ImageHelper.getCameraPhotoOrientation(mContext, pathName);
//	    Logger.e("!!<degree:" + degree);
//		matrix.setRotate(-degree);
	    Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.width(), mDrawableFloat.height(), matrix, true);
//	    Bitmap ret = Bitmap.createScaledBitmap(tmpBitmap, cropWidth, cropHeight, false);
	    tmpBitmap.recycle();
	    tmpBitmap = null;   
	    
//	    Bitmap newRet=Bitmap.createScaledBitmap(ret, cropWidth, cropHeight, false);
//    	ret.recycle();
//    	ret=newRet;
	    
		return ret;
	}
	
    public int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
       return (int) (dpValue * scale + 0.5f);  
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(Images.ImageColumns.DATA);
        return cursor.getString(idx); 
    }
}

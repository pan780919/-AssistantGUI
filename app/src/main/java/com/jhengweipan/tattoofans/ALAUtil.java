package com.jhengweipan.tattoofans;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ALAUtil
{
	public static final String KEY_ADLOCUS = "3a2947195b7443dca48e48a8b7e270f7972abc33";
	
	public static final String SENDER_ID = "671159585643";

	//	"id, gender, age_range, email, read_friendlists, user_online_presence, user_education_history, user_hometown,
	//	user_interests, user_location, user_status, user_work_history"
	public static String[] FB_PERMISSIONS = new String[]
			{
		"email",
		"read_friendlists",
		"user_online_presence",
		"user_education_history",
		"user_hometown",
		"user_interests",
		"user_location",
		"user_status",
		"user_work_history"
			};

	/**
	 * Gets the hashed device id.
	 *
	 * @param context
	 *          the application context.
	 *
	 * @return The encoded device id.
	 */
	public static String getEncodedDeviceId(Context context)
	{
		String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

		String hashedId;
		if ((androidId == null) || isEmulator())
		{
			hashedId = sha1("emulator");
		}
		else
		{
			hashedId = sha1(androidId);
		}

		if (hashedId == null)
		{
			return null;
		}

		return hashedId;
	}


	/**
	 * Checks whether or not the running device is an emulator.
	 *
	 * @return Boolean indicating if the app is currently running in an emulator.
	 */
	public static boolean isEmulator()
	{
		return (Build.BOARD.equals("unknown") && Build.DEVICE.equals("generic") && Build.BRAND.equals("generic"));
	}


	/**
	 * Method for returning an sha1 hash of a string.
	 *
	 * @param val
	 *          the string to hash.
	 *
	 * @return A hex string representing the sha1 hash of the input.
	 */
	private static String sha1(String val)
	{ 
		String result = null;

		if ((val != null) && (val.length() > 0))
		{
			try
			{
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(val.getBytes());
				return String.format("and://%040x", new BigInteger(1, md.digest()));
			}
			catch (NoSuchAlgorithmException nsae)
			{
				result = val.substring(0, 40);
			}
		}
		return result;
	}
	
//	public static void showNotificationAuth(Context context, String text)
//	{
//		if(TaskSharedPreferences.isCloseAuth(context))
//		{
//			return;
//		}
//
//		int icon = R.drawable.icon_app;
//		long when = System.currentTimeMillis();
//		NotificationManager notificationManager = (NotificationManager)
//				context.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification(icon, text, when);
//		String title = context.getString(R.string.app_name);
//		Intent notificationIntent = new Intent(context, TaskLogin.class);
//		notificationIntent.putExtra("pushtext", text);
//		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		// set intent so it does not start a new activity
//		//   notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context, title, text, intent);
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notificationManager.notify(0, notification);
//	}
	

	
	public static void showNotificationDiscount(Context context, String text, String shopId)
	{
//		//開指定頁面
//		MembershipCardInfo ci = null;
//		MembershipCardDBAdapter db = new MembershipCardDBAdapter(context);
//		db.open();
//		Cursor dataCursor = db.getCardFromCardId(shopId);
//		if(dataCursor.moveToFirst())
//		{
//			ci = new MembershipCardInfo();
//			ci.rowId = dataCursor.getInt(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_ROWID));
//			ci.storeId = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_CARDID));
//			ci.barcodeNumber = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_BARCODE_NUMBER));
//			ci.barcodeFormat = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_BARCODE_FORMAT));
//			ci.storeName = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_STORE_NAME));
//			ci.note = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_STORE_NOTE));
//			ci.storeAddress = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_STORE_ADDRESS));
//			ci.storePhone = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_STORE_PHONE));
//			ci.storePicture = dataCursor.getString(dataCursor.getColumnIndex(MembershipCardDBAdapter.KEY_STORE_PICTURE));
//		}
//		dataCursor.close();
//		db.close();
//
//		if (ci != null) 
//		{
//			int icon = R.drawable.icon_57;
//			long when = System.currentTimeMillis();
//			NotificationManager notificationManager = (NotificationManager)
//					context.getSystemService(Context.NOTIFICATION_SERVICE);
//			Notification notification = new Notification(icon, text, when);
//			String title = context.getString(R.string.app_name);
//			
//			Intent intentDiscount = new Intent(context, MembershipCardDiscountNews.class);
//			Bundle bundleDiscount = new Bundle();
//			ci.storeBundle(bundleDiscount);
//			intentDiscount.putExtras(bundleDiscount);
//			intentDiscount.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			
//			// set intent so it does not start a new activity
//			//   notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			PendingIntent intent = PendingIntent.getActivity(context, 0, intentDiscount,PendingIntent.FLAG_UPDATE_CURRENT);
//			notification.setLatestEventInfo(context, title, text, intent);
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
//			notificationManager.notify(0, notification);
//		}
	}

	public static void cancelNotification(Context context)
	{
		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}
}

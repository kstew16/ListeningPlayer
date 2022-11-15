package com.logicsoft.myapplication30.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// import kr.logicsoft.listeningplayer.common.VideoData;
import com.logicsoft.myapplication30.common.VideoData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 而⑦���ㅽ�몃�� ����濡� ���� 湲곕�κ낵 愿��⑤�� ���몃━�� �대����
 * @author YJKim
 *
 */


public class ContextUtil {
	private static final String TAG = ContextUtil.class.getSimpleName();

	/**
	 * �명�곕�룹�� �곌껐 媛��ν��吏� 寃��ы����.
	 * @param context 而⑦���ㅽ��
	 * @return �곌껐 媛��ν��硫� true, ����硫� false
	 */
	public static int checkConnectivity(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// boolean isWifiAvailable = ni.isAvailable();
		boolean isWifiConnected = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// boolean isMobileAvailable = ni.isAvailable();
		boolean isMobileConnected = ni.isConnected();

		if (isWifiConnected)
			return 1;
		if (isMobileConnected)
			return 2;
		return 3;
	}
	
	
	/**
	 * AndroidManifest.xml �� �깅��� 踰��� 臾몄���댁�� 媛��몄�⑤��.
	 * @param context 而⑦���ㅽ��
	 * @return 踰��� 臾몄����
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * ���� 湲곌린�� 誘몃���� �ㅼ����� ��猷��� 鍮����� 由ъ�ㅽ�몃�� 媛��몄�⑤��.
	 * @param context 而⑦���ㅽ��
	 * @return 鍮����� �곗�댄�� 由ъ�ㅽ��
	 */
	public static List<VideoData> getVideoList(Context context) {
		String[] proj = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE, MediaStore.Video.Media.TITLE,
				MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.DATE_ADDED,
				MediaStore.Video.Media.DATE_MODIFIED,
				MediaStore.Video.Media.RESOLUTION };
		List<VideoData> result = new ArrayList<VideoData>();
		Cursor c = null;

		try {
			c = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null,
					null, null);
			if (c != null && c.moveToFirst()) {
				int id = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
				int path = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
				int name = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
				int size = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
				int title = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
				int duration = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
				int dateAdded = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
				int dateModified = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
				int resolution = c
						.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);
				do {
					VideoData v = new VideoData();
					v.setId(c.getLong(id));
					v.setPath(c.getString(path));
					v.setName(c.getString(name));
					v.setSize(c.getLong(size));
					v.setTitle(c.getString(title));
					v.setDuration(c.getLong(duration));
					v.setAddedDate(new Date(c.getLong(dateAdded) * 1000));
					v.setModifiedDate(new Date(c.getLong(dateModified) * 1000));

					String res = c.getString(resolution);
					int pos = res.indexOf('x');
					v.setWidth(Integer.parseInt(res.substring(0, pos)));
					v.setHeight(Integer.parseInt(res.substring(pos + 1)));
					
					v.setThumbnail(ContextUtil.getVideoThumbnail(context, v.getId()));
					
					Log.d("!!!", v.toString());

					result.add(v);
				} while (c.moveToNext());
			}

		} catch (Exception e) {
			if (c != null) {
				c.close();
				c = null;
			}

			e.printStackTrace();
		}

		if (c != null)
			c.close();

		return result;
	}

	/**
	 * �뱀�� 鍮����ㅼ�� �몃�ㅼ�쇱�� 媛��몄�⑤��.
	 * @param context 而⑦���ㅽ��
	 * @param id 鍮����ㅼ�� id
	 * @return 鍮��몃㏊ 媛�泥�
	 */
	public static Bitmap getVideoThumbnail(Context context, long id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;

		// MICRO_KIND : ���� �대�몄�(���ш���)
		// MINI_KIND : 以�媛� �대�몄�
		Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
				context.getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, options);

		return thumbnail;
	}
	
}

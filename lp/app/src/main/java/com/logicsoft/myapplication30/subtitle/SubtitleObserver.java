//package kr.logicsoft.listeningplayer.subtitle;

package com.logicsoft.myapplication30.subtitle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.logicsoft.myapplication30.MainActivity;
import com.logicsoft.myapplication30.VideoActivity;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;

import java.util.Observable;
import java.util.Observer;

//import kr.logicsoft.listeningplayer.MainActivity;
//import kr.logicsoft.listeningplayer.R;
//import kr.logicsoft.listeningplayer.VideoActivity;
//import kr.logicsoft.listeningplayer.VideoControllerView;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;

/**
 * �뀓�뒪�듃 酉� �븯�굹�� �뿰寃곕릺�뼱, �옄留� 湲곕뒫�쓣 �닔�뻾�븳�떎.
 * �샃��踰� �뙣�꽩�뿉�꽌�쓽 �샃��踰� 媛앹껜媛� �맂�떎.
 * @author YJKim
 *
 */
public class SubtitleObserver implements Observer {

	public TextView mTextView;

	private ISubtitleFeatures feature = null;
	private static String TAG = "logicsoft";
	private int index2=0;
	private  int index3=0;
    static boolean isVisited = false;
	
	public SubtitleObserver(TextView textView) {
		this.mTextView = textView;
	}
	
	public TextView getTextView() {
		return this.mTextView;
	}
	
	/**
	 * �옄留� �븯�굹(�븯�굹�쓽 �뼵�뼱)�뿉 ���쓳�릺�뒗 �듅�젙 湲곕뒫�쓣 �꽕�젙
	 * @param feature
	 */
	public void setFeature(ISubtitleFeatures feature) {
		this.feature = feature;
	}
	
	/**
	 * �옄留� �븯�굹(�븯�굹�쓽 �뼵�뼱)�뿉 ���쓳�릺�뒗 �듅�젙 湲곕뒫�쓣 媛��졇�샂
	 * @return
	 */
	public ISubtitleFeatures getFeature() {
		return this.feature;
	}


	
	/**
	 * �씠 硫붿꽌�뱶�뿉�꽌 吏곸젒 UI瑜� 而⑦듃濡ㅽ븯�뒗 肄붾뱶瑜� �샇異쒗븯硫� �븞�맖
	 */
	@Override
	public void update(Observable observable, Object data) {
		Log.e(TAG, "data " + data);
		if (data != null) Log.e(TAG, "data " + data.toString());
		Log.d(TAG, "observable : " + observable.toString());
	    Log.e(TAG, "msg-----------" + "observable  " + observable.toString());
		Message msg = mHandler.obtainMessage();
		msg.obj = observable;
		Log.e(TAG, "send message hello" + index2++);
		mHandler.sendMessage(msg);
	}

	/**
	 * �옄留됱쓣 �씠�슜�빐 �뀓�뒪�듃酉곗뿉 �뀓�뒪�듃瑜� �븷�떦�븯怨�, �뵆�젅�씠�뼱瑜� 而⑦듃濡� �븯�뒗 �빖�뱾�윭
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {


		    Log.e(TAG, "subtitle observer mhandler hello" + index3++); // + msg.obj.toString());
			switch (msg.what) {
			default:
				Subtitle sub = (Subtitle)msg.obj;
				Log.d(TAG, "sub : " + sub);
				String text = sub.getCurrentText();
				Log.d(TAG, "sub curent context: " + text);

				//feature.pauseAndShowMenu(sub.getCurrentExtraText());
				//Log.e(TAG, "get current extra text");


				if ((feature != null) && (Subtitle.fin == true)&&VideoActivity.mModeCheckBox.isChecked()&&VideoActivity.mSubtitleCheckBox.isChecked())
				{
					//Log.e(TAG, "feature");

					//feature.pauseAndShowMenu(sub.getCurrentExtraText());

					Log.e(TAG, "feature pause : " + MainActivity.playmode);

					MainActivity.playmode = 1;
					if(MainActivity.playmode==1||MainActivity.playmode==3)	{ Log.e("hello", "playmode 1");

						String x = sub.getCurrentExtraText();
						// String y = sub.MixCurrentExtraText();

						//if (!x.equals(y)) {Log.e("equality:", "equal");};
						feature.pauseAndShowMenu(x);};


					if(MainActivity.playmode==2)	{ Log.e("hello", "playmode 2");
						feature.pauseAndShowMenu(sub.MixCurrentExtraText());};


				}
				mTextView.setText(Html.fromHtml(text));
				break;
			}
			return false;
		}
	});	
}

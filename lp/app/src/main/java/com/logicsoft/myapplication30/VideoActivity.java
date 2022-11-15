//package kr.logicsoft.listeningplayer;
package com.logicsoft.myapplication30;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.logicsoft.myapplication30.subtitle.ISubtitleFeatures;
import com.logicsoft.myapplication30.subtitle.SubtitleObserver;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;
import com.logicsoft.myapplication30.subtitle.data.SubtitleList;
import com.logicsoft.myapplication30.subtitle.parser.SubtitleParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import kr.logicsoft.listeningplayer.subtitle.ISubtitleFeatures;
//import kr.logicsoft.listeningplayer.subtitle.SubtitleObserver;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;
//import kr.logicsoft.listeningplayer.subtitle.parser.SubtitleParser;
/**
 * 鍮꾨뵒�삤 �뵆�젅�씠瑜� �떞�떦�븯�뒗 �븸�떚鍮꾪떚.
 * @author YJKim
 *
 */
public class VideoActivity extends Activity implements OnGestureListener, OnDoubleTapListener, ISubtitleFeatures {

    private static String TAG = "logicsoft";

	public static boolean FillClick;
	public static boolean prevClick;
	public static CheckBox mModeCheckBox;
	public static CheckBox mSubtitleCheckBox;
	public static CheckBox mMeaning;
	
	private VideoControllerView mVideoController;
	
	private GestureDetector mGestureDetector;
	
	private SubtitleList mSubtitleList;
	private List<Subtitle> mObservables = new ArrayList<Subtitle>();
	private List<SubtitleObserver> mObservers = new ArrayList<SubtitleObserver>();
	private VideoView mVideoView;
	private FrameLayout mMainFrame;
	private LinearLayout mSubtitleLayout;
	private LinearLayout mGoStopLayout;
	private LinearLayout mselectmode;
	private LinearLayout mSettingMenu;
	
	private Button mPreBtn;
	private Button mFillBtn;
	private Button mNextBtn;
	private TextView mExtra;
	private TextView mSubtitle;
	private TextView mLevel;
	
	
	
	private int mControlHeight = 0;
	private String mPath;
	private Timer mTimer = null;
	private boolean mHasSubtitle = false;
	
	private int prevTime = 0;
	private int oldTime=0;
	private  int index1=0;

	public static VideoActivity ins=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Log.e(TAG, "start video activity1");
		VideoActivity.ins = this;

		Log.d(TAG, "before db connection");
		//new DbConnection();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );  //.SCREEN_ORIENTATION_UNSPECIFIED);


        Log.e(TAG, "start video activit2y");
		
		mMainFrame = (FrameLayout)findViewById(R.id.mainFrame);
		mVideoView = (VideoView)findViewById(R.id.videoView);
		mSubtitleLayout = (LinearLayout)findViewById(R.id.subtitleLayout);
		
		mGoStopLayout = (LinearLayout)findViewById(R.id.goStopLayout);
		//mselectmode = (LinearLayout)findViewById(R.id.selectmode);
		mSettingMenu = (LinearLayout)findViewById(R.id.SettingMenu);
		mSettingMenu.setVisibility(View.GONE);

		Log.e(TAG, "mPrebBtn before");
		mPreBtn = (Button)findViewById(R.id.btnPre);
        Log.e(TAG, "mPrebBtn after");

		mFillBtn = (Button)findViewById(R.id.btnFill);

		mNextBtn = (Button)findViewById(R.id.btnNext);
	
		mExtra = (TextView)findViewById(R.id.txtExtra);
		mSubtitle = (TextView)findViewById(R.id.txtsubtitle);
		mLevel = (TextView)findViewById(R.id.level);
		
		mPath = getIntent().getExtras().getString("path");
					
		mGestureDetector = new GestureDetector(this, this);
		mGestureDetector.setOnDoubleTapListener(this);
		
		mModeCheckBox = (CheckBox)findViewById(R.id.StudyModeCheckBox);
        mModeCheckBox.setChecked(true);
        
        mSubtitleCheckBox=(CheckBox)findViewById(R.id.SubtileCheckBox);
        mSubtitleCheckBox.setChecked(true);

        Log.e(TAG, "start video activity2");

        mMeaning = (CheckBox)findViewById(R.id.MeaningCheckBox);
        mMeaning.setChecked(true);
	/*	mSubtitleLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					
					Toast.makeText(VideoActivity.this, "ACITON_DOWN", Toast.LENGTH_SHORT).show();					
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					Toast.makeText(VideoActivity.this, "ACITON_UP", Toast.LENGTH_SHORT).show();
				}
				
				return true;
			}
		});*/



	    Log.e(TAG, "before initMediaPlayer");

		initMediaPlayer(mPath);
		Log.e(TAG, "after initMediaPlayer");

		setGoStopViewEvent(); // pre, menu, next 踰꾪듉�뿉 愿��븳 �씠踰ㅽ듃 泥섎━.
		Log.e(TAG, "after setstopandgo");

		// true is assumed
		if (!MainActivity.convertedFileExists) mPreBtn.setVisibility(View.INVISIBLE);
		if (!MainActivity.convertedFileExists) mFillBtn.setVisibility(View.INVISIBLE);
		if (!MainActivity.convertedFileExists) mNextBtn.setVisibility(View.INVISIBLE);

	}
	protected void onResume(){
		super.onResume();
	}
	protected void onPause(){
		if(mVideoView!=null)	mVideoView.pause();
		super.onPause();
	}
	
	private void setGoStopViewEvent()
	{

            Log.e(TAG, "set go stop event1");
     		mPreBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mVideoView.seekTo(Subtitle.startTime-1000);
				Log.e(TAG, "set go stop event2");
				mVideoView.start();
				Log.e(TAG, "set go stop event3");
				// deleted 6-2 mGoStopLayout.setVisibility(View.GONE);
				prevClick=true;
				Log.e(TAG, "set go stop event4");

			}
		});


		
		mNextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				prevTime = mVideoView.getCurrentPosition();
				int timegap = prevTime - oldTime;

				if (timegap < 500) mVideoView.seekTo(oldTime);

					//mVideoView.seekTo(oldTime);
				Log.d("LogicSoft","nextTime : "+prevTime);
				mGoStopLayout.setVisibility(View.GONE);

				Log.e(TAG, "prev " + prevTime + "old " + oldTime);
				Log.e(TAG, "time gap " + (prevTime-oldTime));
				//oldTime = prevTime;

				Log.e(TAG, "mVideoView start");
				mVideoView.start();
				Log.e(TAG, "mVideoView end");

			}
		});
		
		mFillBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mGoStopLayout.setVisibility(View.VISIBLE);
				mSubtitle.setText(Html.fromHtml(Subtitle.realsubtitle));
			}
		});
	}

	/**
	 * 而ㅼ뒪�� 誘몃뵒�뼱 而⑦듃濡ㅻ윭瑜� �궗�슜�븯湲� �쐞�빐 �븘�슂�븳 硫붿꽌�뱶.
	 * @param path
	 */
	protected void initMediaPlayer(String path) {

		//湲곕낯 誘몃뵒�뼱 而⑦듃濡ㅻ윭瑜� �궗�슜�븯怨좎옄 �븷 �븣
		//mMediaController = new MediaController(this);
		//mMediaController.setAnchorView(mVideoView);
		//mVideoView.setMediaController(mMediaController);
		mVideoController = new VideoControllerView(this);
		mVideoController.setAnchorView(mMainFrame);
		mVideoController.setPrevNextListeners(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "Next", Toast.LENGTH_SHORT).show();

			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Prev", Toast.LENGTH_SHORT).show();
			}			
		});
		mVideoController.setSettingListeners(new OnClickListener() {
			boolean settingclick=true;
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				if(settingclick)
				{
					settingclick=false;
					mSettingMenu.setVisibility(View.VISIBLE);
				}
				else
				{
					settingclick=true;
					mSettingMenu.setVisibility(View.GONE);
				}
			}
		});
		mVideoController.setLevelupdownListeners(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Subtitle.level<3)	Subtitle.level++;
				if(Subtitle.level==1)
				{
					mLevel.setText("level:Easy");
					Toast.makeText(getApplicationContext(), "level:Easy", Toast.LENGTH_SHORT).show();
				}
				if(Subtitle.level==2)
				{
					mLevel.setText("level:Medium");
					Toast.makeText(getApplicationContext(), "level:Medium", Toast.LENGTH_SHORT).show();
				}
				if(Subtitle.level==3)	
				{
					mLevel.setText("level:Hard");
					Toast.makeText(getApplicationContext(), "level:Hard", Toast.LENGTH_SHORT).show();
				}
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Subtitle.level>1)	Subtitle.level--;
				if(Subtitle.level==1)
				{
					mLevel.setText("level:Easy");			
					Toast.makeText(getApplicationContext(), "level:Easy", Toast.LENGTH_SHORT).show();
				}
				if(Subtitle.level==2)
				{
					mLevel.setText("level:Midium");
					Toast.makeText(getApplicationContext(), "level:Medium", Toast.LENGTH_SHORT).show();
				}
				if(Subtitle.level==3)
				{
					mLevel.setText("level:Hard");
					Toast.makeText(getApplicationContext(), "level:Hard", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mVideoController.setVisibilityChangedListener(new VideoControllerView.OnVisibilityChangedListener() {
			
			@Override
			public void onShow() {

			    Log.e(TAG, "m video controller set visibility change listner");
				if (mControlHeight == 0) {
					// 理쒖큹�뿉 mVideoController.getHeight() 媛� 0�쓣 由ы꽩�븯湲� �븣臾몄뿉
					// 酉� �겕湲곕�� 媛��졇�삱 �븣 �븘�옒�� 媛숈� �궡�슜�씠 �븘�슂�븿
					final ViewTreeObserver obs = mVideoController.getViewTreeObserver();
					obs.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							mControlHeight = mVideoController.getMeasuredHeight();
							mSubtitleLayout.setPadding(0, 0, 0, mControlHeight);
							obs.removeGlobalOnLayoutListener(this);
							Log.e(TAG,"remvoe global layout listerner");
						}
					});
				} else {
					// 怨꾩궛�맂 媛� �궗�슜�븿
					mSubtitleLayout.setPadding(0, 0, 0, mControlHeight);
					Log.e(TAG, "mSubtitleLayout set padding");
				}
			}
			
			@Override
			public void onHide() {
				mSubtitleLayout.setPadding(0, 0, 0, 0);
				Log.e(TAG, "on hode set padding");
			}
		});
		
		mVideoController.setMediaPlayer(new VideoControllerView.MediaPlayerControl() {
			
			@Override
			public void toggleFullScreen() {
				
			}
			
			@Override
			public void start() {
				mVideoView.start();
			}
			
			@Override
			public void seekTo(int pos) {
				Subtitle.compare_index=-1;
				prevClick=false;
				mVideoView.seekTo(pos);
			}
			
			@Override
			public void pause() {
				mVideoView.pause();
			}
			
			@Override
			public boolean isPlaying() {
				return mVideoView.isPlaying();
			}
			
			@Override
			public boolean isFullScreen() {
				return true;
			}
			
			@Override
			public int getDuration() {
				return mVideoView.getDuration();
			}
			
			@Override
			public int getCurrentPosition() {
				return mVideoView.getCurrentPosition();
			}
			
			@Override
			public int getBufferPercentage() {
				return mVideoView.getBufferPercentage();
			}
			
			@Override
			public boolean canSeekForward() {
				return mVideoView.canSeekForward();
			}
			
			@Override
			public boolean canSeekBackward() {
				return mVideoView.canSeekBackward();
			}
			
			@Override
			public boolean canPause() {
				return mVideoView.canPause();
			}
		});
		
		mVideoView.setVideoPath(path);
		mVideoView.requestFocus();

	//	false is assumed
		if (MainActivity.convertedFileExists)	mVideoView.start();


		Log.e(TAG, "check subtitles : " + path);
				
		checkSubtitles(path);

		initTimerTask();

		Log.e(TAG, "check and timered");
	}
	
/*
	protected TextView addTextView() {
		TextView textView = new TextView(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		//textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
		mSubtitleLayout.addView(textView, params);
		
		textView.setTextSize(24);
		textView.setTextColor(getResources().getColor(R.color.white));
		textView.setShadowLayer(2, 1, 1, getResources().getColor(R.color.black));
		
		return textView;
	}*/

	private void checkSubtitles(String path) {
	    Log.e(TAG, "check subtitles" + path);
		mSubtitleList = SubtitleParser.parseAll(path);
		mHasSubtitle = mSubtitleList.count() > 0;
		Log.i(TAG, "subtitle-count: " + mSubtitleList.count());
		if (mHasSubtitle) {
			for (Subtitle sub : mSubtitleList) {
				Log.d(TAG, "subtitle-hello" + sub);
				SubtitleObserver observer = new SubtitleObserver(mSubtitle);
				Log.d(TAG, "observer" + observer);
				// 蹂��솚�맂 �옄留됱씪 寃쎌슦留� 硫덉땄 湲곕뒫 �궗�슜

				Log.d("converted", "isConverted"+ sub.isConverted());
				if (sub.isConverted())
					observer.setFeature(this);

				sub.addObserver(observer);

				// �쁽�옱 �븸�떚鍮꾪떚�뿉�꽌 媛�吏�怨� �엳湲� �쐞�븿
				mObservers.add(observer);
				mObservables.add(sub);
			}
		}
	}
	
	/**
	 * 二쇱젣 媛앹껜�뿉寃� �떆媛� �젙蹂대�� 二쇨린 �쐞�븳 ���씠癒몃�� 珥덇린�솕�븯�뿬 �룞�옉�떆�궓�떎.
	 */
	private void initTimerTask() {
		if (mHasSubtitle && mTimer == null) {
			mTimer = new Timer();
			mTimer.scheduleAtFixedRate(mTimerTask, 200, 200);
		}
	}

	/**
	 * ���씠癒몃�� �빐�젣�떆�궓�떎.
	 */
	private void cancelTimerTask() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}	
	
	/**
	 * 鍮꾨뵒�삤 �뵆�젅�씠�뼱�쓽 �쁽�옱 �떆媛꾩쓣 媛��졇�� 二쇱젣 媛앹껜�뿉 �꽆寃⑥��떎.
	 */
	private TimerTask mTimerTask = new TimerTask() {
		
		@Override
		public void run() {
			final int time = mVideoView.getCurrentPosition();
			for (Subtitle s : mObservables) {
				if (time == index1) return;
				index1 = time;
				Log.e(TAG, "subtitle observers: " + time);
				s.observe(time);
			}
		}
		
	};
	
	protected void pause() {
		mVideoView.pause();
	}
	
	protected void stop() {
		mVideoView.stopPlayback();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelTimerTask();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Toast.makeText(this, "길게 누름", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d("!!!", "onScroll");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		//Toast.makeText(this, "onShowPress", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Toast.makeText(this, "더블클릭을 어따 써야할까...??", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.i("CHO", "onSingleTab()");
		if (mVideoController.isShowing()) {
			mVideoController.hide();
		} else {
			mVideoController.show();
		}
		return false;
	}

	/**
	 * ISubtitleFeature�쓽 硫붿꽌�뱶瑜� 援ы쁽�븳 寃�,
	 * �옄留됱쓽 �궡�슜�씠 諛붾�� �븣留덈떎, �샇異쒕릺�뼱 �솕硫댁쓣 硫덉텛�뒗 �벑�쓽 湲곕뒫�쓣 �떞�떦�븯寃� �맂�떎.
	 */
	@Override
	public void pauseAndShowMenu(Object obj) {

		// �뿬湲곗뿉�꽌 �씪�떆 �젙吏� 諛�, prev, next, menu 蹂댁뿬二쇰뒗 湲곕뒫 援ы쁽
		Log.e(TAG, "pause and show menu");
		Log.e(TAG, "prevTime " + prevTime + "  oldtime" + oldTime);
/*
			if (prevTime == oldTime ) {
				Log.i(TAG, "good morning, " + prevTime);
				return;
			}
*/
				mVideoView.pause();
			   Log.i(TAG, "m video pause");


				mVideoController.updatePausePlay();
				Log.i(TAG, "m video controller update pause play");
				Log.i("CHO", "pauseAndShowMenu");

				//	Log.i("CHO", "prevTime = " + prevTime);

				mGoStopLayout.setVisibility(View.VISIBLE);
				if (MainActivity.playmode != 3) mExtra.setText((String) obj);
				oldTime = prevTime;

	}
	
	public void justPause()
	{
		mVideoView.pause();

		//false is assumed
		if (MainActivity.convertedFileExists) mVideoController.updatePausePlay();
		//false is assumed
		if (MainActivity.convertedFileExists) mGoStopLayout.setVisibility(View.VISIBLE);
	}
	
}

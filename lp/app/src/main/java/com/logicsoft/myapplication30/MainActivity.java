//package kr.logicsoft.listeningplayer;
package com.logicsoft.myapplication30;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

//import javax.swing.JOptionPane;



//import kr.logicsoft.listeningplayer.util.ContextUtil;

public class MainActivity<dbManager> extends Activity
		//Activity
{

	public static DBManager dbManager;
	public static String addr = "192.168.0.4";
	public static int pNum = 3030;
	public static MyClient serverManager;


	public static MainActivity ins;
	public static int playmode=0;
	private static String TAG = "logicsoft";

	ArrayList<String> js = new ArrayList<>();
	ArrayList<String> mp = new ArrayList<>();
	ArrayList<String> keys = new ArrayList<>();
    ArrayList<String> smis = new ArrayList<>();
    ArrayList<String> avi = new ArrayList<>();




	// private VideoAdapter mAdapter = null;
	// private List<VideoData> mVideoList = null;
	private LinearLayout mselectmode;
	private LinearLayout mHelpMessage;
	private Button mStandardButton;
	private Button mMixButton;
	private Button mNonButton;
	private Button mMyvideoButton;
	private Button mBackButton;
	private ImageButton mButtonHelp;
	private ImageButton mBackButton2;
	private Button mRefreshButton;
	private Button mRequestFileButton;
	//private EditText mFileNameText;
	private Spinner mFileList;
	private ArrayList<String> itemList = new ArrayList<String>();
	SpinnerAdapter mFileListSpinnerAdapter;




	String[][] urls = new String[10][20];


	private ListView mListView = null;
//	private String urls[2, 20];
			/*{
			{
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVVkxlbkJuc1lIc1k",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVSFJBTUs0N0NscFU",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVQmVkR1ZHWFhBVnc",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVUVA1X3NuX2hrbmc",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVYzlLaDBNaThhODQ",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVNjItdE5tZE9NZXc",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVWWk2bExHM0ExNHc",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVT21HeGt5UlBvWDA",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVUjJzMi1UX2F0N00",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVS2EzUnRueFpsU3c"
			},
			{
				    "Before_We_Go_Trailer.mp4",
                    "KungFu_Panda3_Trailer.mp4",
                    "Mad_Max_Trailer_xxx.mp4",
                    "Cooties_Trailer.mp4"
			}
			};

*/



	private String subs[][] = {
			{
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVWktRcGhLWDZkMlE",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVZ09pdk9IWlVSZVU",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVRG5zNk5pQUw4T00",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVbkhueHVWUVJXODQ",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVa3BoanZyZ0VLbkk",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVenY1Y1JQQmItZkk",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVWTE5TVVyZlFVLXM",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVbmZDdVlVbTBFdUU",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVZUxYZ2lZMDZSajQ",
				"https://docs.google.com/uc?export=download&id=0B-HB7J8-HTfVR08yMVJVbVdSQU0"},
            {
                    //"Before_We_Go_Trailer.json",
                    "KungFu_Panda3_Trailer.json",
                    "Mad_Max_Trailer.json",
                    "Cooties_Trailer.json"
            }

			};



	public String movie_url;
	public String subtitle_url;
	public String MOVIE_SAVE_PATH;
	public String MOVIE_SAVE_NAME;
	public String SUBTITLE_SAVE_NAME;
	public File file;
	public File subfile;
	public String prjName = "ListeningPlayer1";
	public long urlNum;

	public static boolean convertedFileExists = true;

	ArrayList<TitleList> Titles = new ArrayList<TitleList>();




	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Context mContext = this.getApplicationContext();
		dbManager = new DBManager(mContext);
		serverManager = new MyClient(addr,3030);





        // mAdapter = new VideoAdapter(getApplicationContext());
		// mVideoList = ContextUtil.getVideoList(getApplicationContext());
		mselectmode = (LinearLayout)findViewById(R.id.selectmode);
		mHelpMessage = (LinearLayout)findViewById(R.id.HelpMessage);
		mStandardButton = (Button)findViewById(R.id.StandardButton);
		mMixButton = (Button)findViewById(R.id.MixButton);
		mNonButton = (Button)findViewById(R.id.NonButton);
		mMyvideoButton = (Button)findViewById(R.id.Myvideo);
		mBackButton = (Button)findViewById(R.id.BackButton);
		mBackButton2 = (ImageButton)findViewById(R.id.BackButton2);
		mButtonHelp = (ImageButton)findViewById(R.id.ButtonHelp);
		mRefreshButton = (Button)findViewById(R.id.button22);
        mListView = (ListView) findViewById(R.id.listView);

		//mFileNameText = (EditText)findViewById(R.id.fileNameText);
		mFileList = (Spinner)findViewById(R.id.fileNameSpinner);

		mRequestFileButton = (Button)findViewById(R.id.requestButton);
		SpinnerAdapter mFileListSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.listitem_file_list,itemList);





        ins = this;


		Log.d(TAG, "hello log");


		Log.e(TAG, "hellp errors");

		final Builder m= new AlertDialog.Builder(this);

		// mListView.setAdapter(mAdapter);

		//Titles.add(new TitleList(0, "Before_We_Go_Trailer")); ////American Ultra Red Band Trailer"));
		//Titles.add(new TitleList(1, "KungFu_Panda3_Trailer")); //efore We Go Trailer"));
		//Titles.add(new TitleList(2, "Mad Max Trailer"));
		//Titles.add(new TitleList(3, "Cooties_Trailer")); //The Hunger Games-Mockingjay Part 2 Trailer"));
		//Titles.add(new TitleList(8, "The Martian Trailer"));
		//Titles.add(new TitleList(9, "The 5th Wave Trailer"));
		/*
		try {

			//db가 이미 존재하는지 체크
			boolean bResult = isCheckDB(mContext);
			Log.d("prj", "DB Check=" + bResult);
			if (!bResult) {
				//db가 없으면 db를 지정된 경로에 복사
				copyDB(mContext);
			}


		} catch (Exception e) {
			Log.d("prj", "failed to copy db: " + e.getMessage());
		}
		*/
//assets/databases/test.db 파일을 직접 기기 내부의 앱 저장소에 추가하는 코드
		/*
		try {
			String path = dbManager.openDB("test.db");
			Log.d("prj", "opendb");

			Log.d("prj", "dbpath : "+ path);
		String str = "SELECT * FROM company"; //예시 쿼리문
			dbManager.query(str);
			// Log.d("prj", "query exec");
		} catch (Exception e) {
			Log.d("prj", "hello abc: " + e.getMessage());
		}
		*/



		mListView.setAdapter(new CustomArrayAdapter(this,
				R.layout.listitem_download, Titles));

		mListView.setOnItemClickListener(mItemClickListener);
		mMyvideoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(MainActivity.this, UserFileList.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				finish();
			}
		});



		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHelpMessage.setVisibility(View.GONE);
			}
		});

        mRefreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)  {
                refresh();
            }
        });

		mRequestFileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//v.setEnabled(false);
				new Thread(() -> {
					int select =mFileList.getSelectedItemPosition();
					if(select != mFileList.INVALID_POSITION) request(itemList.get(select));
					else{
						Handler handler = new Handler(Looper.getMainLooper());
						handler.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								Toast.makeText(MainActivity.this, "There are no files to get on the server", Toast.LENGTH_SHORT).show();
							}
						}, 0);
					}
				}).start();
				//v.setEnabled(true);
			}
		});

		//mFileList.setAdapter(mFileListSpinnerAdapter);
		mFileList.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				mFileList.setAdapter(mFileListSpinnerAdapter);
				return false;
			}
		});
		TedPermission.with(this)
				.setPermissionListener(new PermissionListener() {
					@Override
					public void onPermissionGranted() {


						File sdpath = Environment.getExternalStorageDirectory();
						MOVIE_SAVE_PATH = sdpath.getPath() + "/" + prjName;
						Log.e("MOVIE_SAVE_PATH", MOVIE_SAVE_PATH);


						File savePath = new File(MOVIE_SAVE_PATH);
						System.out.println("savePath_exists : " + savePath.exists());
						Log.d(TAG,"1");


						if (!savePath.exists())
						{
							try
							{
								savePath.mkdirs();
								new File(MOVIE_SAVE_PATH + "/" + "aaa").mkdirs();
								Log.d(TAG, "mkdirs");
								// Log.d(TAG, "savePath_Make : " + savePath.mkdirs());
								//       new File(MOVIE_SAVE_PATH + "/" + "aaa").mkdirs();
							} catch (Exception er)
							{
								Log.e("Make Error", "Error : " + er);
							}
						}

						Log.d(TAG,"2");
		/*


			subtitle_url = subs[0][Titles.get(position).id];
			SUBTITLE_SAVE_NAME = subs[1][Titles.get(position).id];

			file = new File(MOVIE_SAVE_PATH + "/" + SUBTITLE_SAVE_NAME);
			Log.e("filepath", file.getPath());
			System.out.println("fileExists : " + file.exists());

			if (!file.exists())
			{
		//		Log.e("movie_url", movie_url);
				startDownload();
			}

		*/


						mselectmode.setVisibility(View.GONE);
						mHelpMessage.setVisibility(View.GONE);

						List<String> a = FileList(prjName);

						int index=0;

						for (String x : a) {
							if (x.endsWith("mp4")) {
								Log.d(TAG, x.toString() + "zzz");
								mp.add(x.substring(0, x.length() - 4));
								keys.add(x.substring(0, x.length() - 4));
								urls[1][index++] = x;
							}
							if (x.endsWith("avi")) {
								avi.add(x.substring(0, x.length() - 4));
								keys.add(x.substring(0, x.length() - 4));
								urls[1][index++] = x;
							}
							if (x.endsWith("json")) {
								Log.d(TAG, x.toString() + "yyy");
								js.add(x.substring(0, x.length() - 5));
							}
							if (x.endsWith("smi")) {
								Log.d(TAG, x.toString() + "smi file found");
								smis.add(x.substring(0, x.length() - 4));
							}

						}

						int ind=0;

						for(String y : keys)  {

							Titles.add(new TitleList(ind++, y));
						}

						Titles.add(new TitleList(ind++,"New"));
						urls[1][ind-1] = "New";
						//     Titles.clear();

					}

					@Override
					public void onPermissionDenied(List<String> deniedPermissions) {

					}
				})
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
				.check();


	}

	private File getSaveFolder(String folderName) {
		Log.d(TAG, "path: "+Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName);
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +  "/"  + folderName);
		if(!dir.exists()) {
			Log.d(TAG, "파일 생성!!!");
			dir.mkdirs();
		}else{
			Log.d(TAG, "파일 존재!!!");
		}
		return dir;
	}
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(dst);
			try {
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}
	void request(String fileName){
		File fileDir = getFilesDir();
		//"/data/user/0/com.logicsoft.myapplication30/files"
		Handler handler = new Handler(Looper.getMainLooper());
		if(fileName=="None"){
			handler.postDelayed(new Runnable() {
				@Override
				public void run()
				{
					Toast.makeText(MainActivity.this, "There are no files to get on the server", Toast.LENGTH_SHORT).show();
				}
			}, 0);
		}
		if(serverManager.getFile(fileName,fileDir)){
			handler.postDelayed(new Runnable() {
				@Override
				public void run()
				{
					Toast.makeText(MainActivity.this, "getting " + fileName, Toast.LENGTH_SHORT).show();
				}
			}, 0);
		}else{
			handler.postDelayed(new Runnable() {
				@Override
				public void run()
				{
					Toast.makeText(MainActivity.this, "failed to get " + fileName, Toast.LENGTH_SHORT).show();
				}
			}, 0);}
	}
    void refresh() {
   //     Log.d(TAG, "good : " + v.toString());
		Toast.makeText(getApplicationContext(), "folder name : " + getSaveFolder("listeningplayer").getAbsolutePath(), Toast.LENGTH_SHORT).show();
		File src = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies");
		File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/listeningplayer");
		try {
			copy(src, dst);
		} catch (IOException e) {
			System.out.println (e.toString());

		}


		keys.clear();
        List<String> a = ins.FileList(prjName);

        int index=0;


        for (String x : a) {
            if (x.endsWith("mp4")) {
                Log.d(TAG, "mp4: file: "+x.toString());
                mp.add(x.substring(0, x.length() - 4));
                keys.add(x.substring(0, x.length() - 4) );
                urls[1][index++]=x;

            }
            if (x.endsWith("avi")) {
                avi.add(x.substring(0, x.length() - 4));
                keys.add(x.substring(0, x.length() - 4));
                urls[1][index++] = x;
            }
            if (x.endsWith("json")) {
                Log.d(TAG, "mp4: file:"+ x.toString());
                js.add(x.substring(0, x.length() - 5));
            }
            if (x.endsWith("smi")) {
                Log.d(TAG, "smi file: "+x.toString());
                smis.add(x.substring(0, x.length() - 4));
            }

        }

        int ind=0;
        int test=0;


        Titles.clear();


        for(String y : keys)  {

            //   Titles.add(new TitleList(ind++, y));
            Log.d(TAG, "key " + y + (test++));
            Titles.add(new TitleList(ind++, y));

        } //for
        // Titles.clear();

		Titles.add(new TitleList(ind++,"New"));


        mListView.setAdapter(new CustomArrayAdapter(ins,
                R.layout.listitem_download, Titles));
		new Thread(() -> {
			itemList.clear();
			List<String> returnList = serverManager.getList();
			if(returnList.size()==0){
				itemList.add("None");
				Handler handler = new Handler(Looper.getMainLooper());
				handler.postDelayed(new Runnable() {
					@Override
					public void run()
					{
						Toast.makeText(MainActivity.this, "There are no files to get on the server.\n Please click refresh button to refresh file list.", Toast.LENGTH_SHORT).show();
					}
				}, 0);
			}
			for(int i=0; i<returnList.size();i++){
				itemList.add(returnList.get(i));
			}
		}).start();
    }

	private List<String> getListFileNames(String strFolderName){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + strFolderName;
		File directory = new File(path);
		File[] files = directory.listFiles();

		List <String> filesNameList = new ArrayList<>();

		for(int i = 0; i < files.length; i++){

			filesNameList.add(files[i].getName());
		}
		return filesNameList;
	}
    private List<String> FileList(String strFolderName)
    {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
        Log.d(TAG, "good path: " + path);
        Log.d(TAG, "src folder: " + strFolderName);

        File directory = new File(path);
        File[] files = directory.listFiles();

        List<String> filesNameList = getListFileNames("ListeningPlayer");
		Log.d(TAG, "List Length: " + filesNameList.size());


        return  filesNameList;
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public OnItemClickListener mItemClickListener = new OnItemClickListener()
	{


		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id)
		{
			// refresh();
			String urlnumber = Integer.toString(Titles.get(position).id);
			Log.e("urlnumber", urlnumber);
			Log.d(TAG, "position:" + position);

			Log.d(TAG, "onItemClick");

			Log.d(TAG, "new:" + position + " title: "+Titles.get(position).title );
			if (Titles.get(position).title.equals("New"))
				new MessageBox().show("알림", "동영상과 자막파일(영어, 한영통합)을 listeningPlayer1 폴더에 탑재하시요");

			//show("mItemClickListner");
			// show("file : " + file.getPath());

            //Intent intent = new Intent(MainActivity.this,VideoActivity.class);
            // intent.putExtra("path", mVideoList.get(position).getPath());
            // intent.putExtra("path", file.getPath());
            //startActivity(intent);

			
		//	String urlNum1 = Integer.toString(Titles.get(position).id);
		//	Log.e("urlNum", urlNum1);
			movie_url = urls[0][Titles.get(position).id];

			Log.d(TAG, "title: " + Titles.get(position).id);
			MOVIE_SAVE_NAME = urls[1][Titles.get(position).id];

			file = new File(MOVIE_SAVE_PATH + "/" + MOVIE_SAVE_NAME);
			Log.e("filepath", file.getPath());
			System.out.println("fileExists : " + file.exists());
			
			subtitle_url = subs[0][Titles.get(position).id];
			SUBTITLE_SAVE_NAME = subs[1][Titles.get(position).id];

			subfile = new File(MOVIE_SAVE_PATH + "/" + SUBTITLE_SAVE_NAME);
			Log.e("filepath", file.getPath());
			System.out.println("fileExists : " + file.exists());

			for (String c : js) Log.d(TAG, c +"hello123");
			Log.d(TAG, MOVIE_SAVE_NAME + "hello1234");
//            show("file : " + file.getPath());
			String xx = MOVIE_SAVE_NAME.substring(0, MOVIE_SAVE_NAME.length() - 4);
			Log.d(TAG, xx + "hello12345");
			Log.d(TAG, xx.toString() + " found");

			Log.d(TAG, smis.size() + " tested");

            Log.d(TAG, "check: "+ js.contains(xx) + "size: " + js.size());

			if (js.contains(xx)) {

				Intent intent = new Intent(MainActivity.this, VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());


				intent.putExtra("path", file.getPath());
				startActivity(intent);
			}
			else if (!smis.contains(xx)){


                Log.d(TAG, xx.toString() + "smi file doesn't exist");

                new MessageBox().show("알림", (xx +".smi 자막파일을 listeningPlayer1 폴더에 탑재하시요"));

            }
            else {
                new MessageBox().show("알림", (xx + "을 변환합니다."));
                moveFile(MOVIE_SAVE_PATH + "/",  xx + ".smi", MOVIE_SAVE_PATH + "/" + "aaa" +"/");
                refresh();

				moveFile(MOVIE_SAVE_PATH.substring(0, 20) + "ListeningPlayer/", xx+".json", MOVIE_SAVE_PATH + "/");
				Log.d(TAG, "abc1" +  MOVIE_SAVE_PATH.substring(0, 20) + "ListeningPlayer/" );
				Log.d(TAG, "abc2" + xx+".json");
				Log.d(TAG, "abc3" + MOVIE_SAVE_PATH) ;

				refresh();
				Log.d(TAG, "create file");

            }

		}
	};


    public class MessageBox
    {
        void show(String title, String message)
        {
            dialog = new AlertDialog.Builder(ins) // Pass a reference to your main activity here
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        private AlertDialog dialog;
    }



	private void setModeViewEvent() {
		mBackButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mselectmode.setVisibility(View.GONE);
			}
		});

		mButtonHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHelpMessage.setVisibility(View.VISIBLE);
			}
		});

		mStandardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playmode = 1;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(MainActivity.this, VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", file.getPath());
				startActivity(intent);
			}
		});


		mMixButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playmode = 2;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(MainActivity.this, VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", file.getPath());
				startActivity(intent);
			}
		});

		mNonButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playmode = 3;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(MainActivity.this, VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", file.getPath());
				startActivity(intent);
			}
		});
	}

		public boolean isCheckDB(Context mContext) {
		String filePath = "/data/data/com.logicsoft.myapplication30/databases/test.db";
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		}
		return false;
	}

    private void moveFile(String inputPath, String inputFile, String outputPath) {
        Log.d(TAG, "move file");
        Log.d(TAG, "input path: "+ inputPath);
        Log.d(TAG, "input file: " + inputFile);
        Log.d(TAG, "output path: " + outputPath );
        InputStream in = null;
        OutputStream out = null;
        try {

            Log.d(TAG, "move file-2");
        //    Log.d(TAG, "write output  file");
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            Log.d(TAG, "move file-3");
            Log.d(TAG, "inputPath: " + inputPath);
            Log.d(TAG, "inputFile: " + inputFile);
            Log.d(TAG, "ouputpath:" + outputPath);
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            Log.d(TAG, "move file-4");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            Log.d(TAG, "move file-5");
            in.close();
            in = null;


            // write the output file
            out.flush();
            out.close();
            out = null;


            // delete the original file

            new File(inputPath + inputFile).delete();
            Log.d(TAG, "delete original file");

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }



	public void copyDB(Context mContext){
		Log.d("prj", "copyDB");
		AssetManager manager = mContext.getAssets();
		String folderPath = "/data/data/com.logicsoft.myapplication30/databases";
		String filePath = "/data/data/com.logicsoft.myapplication30/databases/test.db";

		//String folderPath = "/storage/emulated/0/legacy/ListeningPlayer";
		//String filePath = "/storage/emulated/0/ListeningPlayer/test.db";

		File folder = new File(folderPath);
		File file = new File(filePath);
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			InputStream is = manager.open("databases/test.db");
			BufferedInputStream bis = new BufferedInputStream(is);
			if (folder.exists()) {

			} else{
				folder.mkdirs();
			} if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			int read = -1;
			byte[] buffer = new byte[1024];
			while ((read = bis.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, read);
			}
			bos.flush();
			bos.close();
			fos.close();
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e("ErrorMessage : ", e.getMessage());
		}
	}





	
	public class TitleList
	{
		public String title;
		public int id;

		public TitleList()
		{
		}

		public TitleList(int id, String title)
		{
			this.id = id;
			this.title = title;
		}
	}

	// CustomArrayAdapter Set

	public class CustomArrayAdapter extends ArrayAdapter<TitleList>
	{
		private ArrayList<TitleList> titleList = null;

		public CustomArrayAdapter(Context c, int textViewResourceId,
				ArrayList<TitleList> arrays)
		{
			super(c, textViewResourceId, arrays);
			LayoutInflater.from(c);
		}

		@Override
		public int getCount()
		{
			return super.getCount();
		}

		@Override
		public TitleList getItem(int position)
		{
			return super.getItem(position);
		}

		@Override
		public long getItemId(int position)
		{
			return super.getItemId(position);
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent)
		{

			View v = convertview;

			if (v == null)
			{
				v = getLayoutInflater().inflate(R.layout.listitem_download, null);
			}
			TextView tv_Title = (TextView) v.findViewById(R.id.Tv_Title);
			tv_Title.setText(getItem(position).title);
			urlNum = getItemId(position);
			tv_Title.setTextColor(Color.BLACK);
			return v;
		}

		public void setArrayList(ArrayList<TitleList> arrays)
		{
			this.titleList = arrays;
		}

		public ArrayList<TitleList> getArrayList()
		{
			return titleList;
		}
	}
	
	class DownloadFileAsync extends AsyncTask<String, String, String>
	{

		private ProgressDialog mDlg;

		@Override
		protected void onPreExecute()
		{
			mDlg = new ProgressDialog(MainActivity.this);
			mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDlg.setMessage("Downloading...");
			mDlg.setCancelable(false);
			mDlg.show();

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params)
		{
			// String Length = null;
			int count = 0;

			try
			{
				URL url = new URL(movie_url);
				URLConnection con = url.openConnection();
				con.connect();

				int lengthOfFile = con.getContentLength();
				Log.e("Length of file", "length = " + lengthOfFile);
				Log.e("getPath", file.getPath());

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(file.getPath());

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1)
				{
					total += count;

					publishProgress("" + (int) ((total * 100) / lengthOfFile));

					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (Exception e)
			{
				Log.e("VideoManager", "Error: " + e);
			}
			// TODO Auto-generated method stub
			return params[0];
		}

		@Override
		protected void onProgressUpdate(String... progress)
		{
			// TODO Auto-generated method stub
						
			mDlg.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String result)
		{
			mDlg.setCancelable(true);
			mDlg.dismiss();
			Toast.makeText(MainActivity.this, "File Download Complete", Toast.LENGTH_LONG).show();
		}
	}
	
	class DownloadSubtitleFileAsync extends AsyncTask<String, String, String>
	{

		private ProgressDialog mDlg;

		@Override
		protected void onPreExecute()
		{
			mDlg = new ProgressDialog(MainActivity.this);
			mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDlg.setMessage("Downloading...");
			mDlg.setCancelable(false);
			mDlg.setProgress(0);
			mDlg.show();

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params)
		{
			// String Length = null;

			int count = 0;

			try
			{
				params[0]=subtitle_url;
				URL url = new URL(subtitle_url);
				URLConnection con = url.openConnection();
				con.connect();

				int lengthOfFile = con.getContentLength();
				Log.e("Length of file", "length = " + lengthOfFile);
				Log.e("getPath", subfile.getPath());

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(subfile.getPath());

				byte data[] = new byte[1024];

				long total = 0;
				//mDlg.setProgress((int) ((total * 100) / lengthOfFile));
				while ((count = input.read(data)) != -1)
				{
					total += count;

					//publishProgress("" + (int) ((total * 100) / lengthOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();

			} catch (Exception e)
			{
				Log.e("VideoManager", "Error: " + e);
			}
			// TODO Auto-generated method stub
			return null;
		}
		protected void onProgressUpdate(Integer... progress)
		{
			mDlg.setProgress(progress[0]);
			// TODO Auto-generated method stub
		}

		@Override
		protected void onPostExecute(String result)
		{
			mDlg.setCancelable(true);
			mDlg.dismiss();
			Toast.makeText(MainActivity.this, "Subtitle Download Complete",
					Toast.LENGTH_LONG).show();
		}
	}

	private void startMovieDownload()
	{
		Log.e("movie_url", movie_url);
		String url = movie_url;
		new DownloadFileAsync().execute(url);
	}
	
	private void startSubtitleDownload()
	{
		String url = subtitle_url;
		new DownloadSubtitleFileAsync().execute(url);
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed(); //������ �����

		 Builder d = new AlertDialog.Builder(this);


		d.setMessage("작업을 중단하겠습니까?");
		d.setPositiveButton("네", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// process��ü ����
				finish();
			}
		});
		d.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		d.show();


	} 

	void createFile () {
		try {
			String rootPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/ListeningPlayer/";
			Log.d(TAG, "rootPath: " + rootPath);
			File root = new File(rootPath);
			if (!root.exists()) {
				root.mkdirs();
			}
			File f = new File(rootPath + "Cooties_Trailer.json");

			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public class DownloadThread extends Thread
	// {
	// private String movieDownURL;
	// public DownloadThread(String tempURL)
	// {
	// this.movieDownURL = tempURL;
	// Log.e("tempURL", tempURL);
	// }
	// @Override
	// public void run() {
	// int read, size = 0;
	// URL mURL;
	//
	// try {
	// mURL = new URL(movieDownURL);
	// HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
	// int len = conn.getContentLength();
	// byte[] tempByte = new byte[len];
	// InputStream is = conn.getInputStream();
	// OutputStream fos = new FileOutputStream(file);
	//
	// while (!isInterrupted()) {
	// read = is.read(tempByte);
	// if (read <= 0) {
	// break;
	// }
	//
	// size += read;
	// fos.write(tempByte, 0, read);
	// }
	//
	// is.close();
	// fos.close();
	// conn.disconnect();
	// } catch (Exception e) {
	// }
	// }
	// }

	// public class VideoAdapter extends BaseAdapter {
	// private Context mContext;
	//
	// public VideoAdapter(Context c) {
	// mContext = c;
	// }
	//
	// public int getCount() {
	// return mVideoList.size();
	// }
	//
	// public Object getItem(int position) {
	// return mVideoList.get(position);
	// }
	//
	// public long getItemId(int position) {
	// return mVideoList.get(position).getId();
	// }
	//
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View v = convertView;
	// VideoData video = mVideoList.get(position);
	// if (v == null) {
	// v = getLayoutInflater().inflate(R.layout.listitem_video, null);
	// }
	// ImageView ivThumbnail = (ImageView)v.findViewById(R.id.ivThumbnail);
	// TextView tvResolution = (TextView)v.findViewById(R.id.tvResolution);
	//
	// TextView tvFilename = (TextView)v.findViewById(R.id.tvFilename);
	// TextView tvDirectory = (TextView)v.findViewById(R.id.tvDirectory);
	// TextView tvDuration = (TextView)v.findViewById(R.id.tvDuration);
	// TextView tvDate = (TextView)v.findViewById(R.id.tvDate);
	//
	// tvDirectory.setText(new File(video.getPath()).getParent());
	// tvFilename.setText(video.getName());
	// tvDate.setText(video.getModifiedDate().toLocaleString());
	// tvResolution.setText(video.getResolution());
	// tvDuration.setText(video.getDurationString());
	// ivThumbnail.setImageBitmap(video.getThumbnail());
	//
	//
	// return v;
	// }
	// }

}

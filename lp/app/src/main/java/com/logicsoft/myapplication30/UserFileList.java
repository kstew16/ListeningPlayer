/*package kr.logicsoft.listeningplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserFileList extends Activity
{
    private String mFileName;
    private ListView lvFileControl;
    private Context mContext = this;

    private List<String> lItem = null;
    private List<String> lPath = null;
    private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ListeningPlayer"+File.separator;
    private TextView mPath;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userfilelist);
        mPath = (TextView) findViewById(R.id.tvPath);
        lvFileControl = (ListView)findViewById(R.id.lvFileControl);
        getDir(mRoot);
        
        lvFileControl.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				File file = new File(lPath.get(position));


		        if (file.isDirectory())
		        {
		            if (file.canRead())
		                getDir(lPath.get(position));
		            else
		            {
		            	Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
		            }
		        }
		        else
		        {
		            mFileName = file.getName();
		            Log.i("Test","ext:"+mFileName.substring(mFileName.lastIndexOf('.') + 1, mFileName.length()));
		        }
			}
		});
    }

    private void getDir(String dirPath)
    {
        mPath.setText("Location: " + dirPath);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();


        for (int i = 0; i < files.length; i++)
        {
            File file = files[i];
            if(file.getName().contains(".mp4")||file.getName().contains("avi")
            		||file.getName().contains(".wmv")||file.getName().contains(".mkv")
            		||file.getName().contains(".flv")||file.getName().contains(".mov")
            		||file.getName().contains(".skm")||file.getName().contains(".mpeg")
            		||file.getName().contains(".mpg")||file.getName().contains(".asf"))
            {
	            lPath.add(file.getAbsolutePath());
	
	            if (file.isDirectory())
	                lItem.add(file.getName() + "/");
	            else
	                lItem.add(file.getName());
	        }
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItem);
        lvFileControl.setAdapter(fileList);
    }
}

||filename.toLowerCase().endsWith(".wmv")||filename.toLowerCase().endsWith(".mkv")
||filename.toLowerCase().endsWith(".flv")||filename.toLowerCase().endsWith(".mov")
||filename.toLowerCase().endsWith(".skm")||filename.toLowerCase().endsWith(".mpeg")
||filename.toLowerCase().endsWith(".mpg")||filename.toLowerCase().endsWith(".asf"))	check = true;
*/

package com.logicsoft.myapplication30;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class UserFileList extends Activity {
	private String filenames[];
	private String filepath;
	
	private ListView listview;
	private Button mStandardButton;
	private Button mMixButton;
	private Button mNonButton;
	private Button mOffervideoButton;
	private Button mBackButton;
	private ImageButton mConvertButton;
	private ImageButton mBackButton2;
	private ImageButton mButtonHelp;
	private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ListeningPlayer"+File.separator;
	private LinearLayout mHelpMessage;
	
	DataAdapter adapter;

	private LinearLayout mselectmode;
	ArrayList<CData> alist;

	void show(final String a1)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("AlertDialog Title");
		builder.setMessage("AlertDialog Content");
		builder.setPositiveButton("예",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(getApplicationContext(), a1,Toast.LENGTH_LONG).show();
					}
				});

		builder.show();
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userfilelist);

		show("onCreate of UserfileList");


		filenames=new String[100];
		
		mselectmode = (LinearLayout)findViewById(R.id.selectmode);
		mStandardButton = (Button)findViewById(R.id.StandardButton);
		mMixButton = (Button)findViewById(R.id.MixButton);
		mNonButton = (Button)findViewById(R.id.NonButton);
		mOffervideoButton = (Button)findViewById(R.id.Offervideo);
		mBackButton = (Button)findViewById(R.id.BackButton);
		mBackButton2 = (ImageButton)findViewById(R.id.BackButton2);
		mButtonHelp = (ImageButton)findViewById(R.id.ButtonHelp);
		mConvertButton = (ImageButton)findViewById(R.id.convertbutton);
		mHelpMessage = (LinearLayout)findViewById(R.id.HelpMessage);
		
		listview = (ListView) findViewById(R.id.lvFileControl);
		alist = new ArrayList<CData>();
		adapter = new DataAdapter(this, alist);
		listview.setAdapter(adapter);
		mselectmode.setVisibility(View.GONE);
		mHelpMessage.setVisibility(View.GONE);
		getDir(mRoot);
		
		final Builder m= new AlertDialog.Builder(this);
			
		mOffervideoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserFileList.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
				finish();
			}
		});
		
		mConvertButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// m.setMessage("���� ���� ���Դϴ�.\n���ŷο�ð�����\nhansangwon_@naver.com \n����� ���� �ֽø� ��ȯ�ؼ� �����帮�ڽ��ϴ�.");
				m.setMessage("hello");
				m.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				m.show();
			}
		});
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHelpMessage.setVisibility(View.GONE);
			}
		});
		

		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				filepath=mRoot+filenames[position];
				mselectmode.setVisibility(View.VISIBLE);
				setModeViewEvent();
			}
		});
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed(); //������ �����

		Builder d = new AlertDialog.Builder(this);
		d.setMessage("���� �����Ͻðڽ��ϱ�?");
		d.setPositiveButton("��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// process��ü ����
				finish();
			}
		});
		d.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		d.show();
	} 
	
	private void setModeViewEvent()
	{
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


		/* deleted 5-22-11-49
		mStandardButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.playmode=1;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(UserFileList.this,VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", filepath);
				startActivity(intent);
			}
		});
		
		
		mMixButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.playmode=2;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(UserFileList.this,VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", filepath);
				startActivity(intent);
			}
		});
		
		mNonButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.playmode=3;
				mselectmode.setVisibility(View.GONE);
				Intent intent = new Intent(UserFileList.this,VideoActivity.class);
				// intent.putExtra("path", mVideoList.get(position).getPath());
				intent.putExtra("path", filepath);
				startActivity(intent);
			}
		}); */
	}

	private void getDir(String dirPath)
    {
        File f = new File(dirPath);
        File[] files = f.listFiles();
        boolean subtitlecheck=false;
        long filesize;
        String filename, realfilename, strfilesize;
        int idx;
        int cnt=0;

        for (int i = 0; i < files.length; i++)
        {
            File file = files[i];
            if(file.getName().contains(".mp4")||file.getName().contains("avi")
            		||file.getName().contains(".wmv")||file.getName().contains(".mkv")
            		||file.getName().contains(".flv")||file.getName().contains(".mov")
            		||file.getName().contains(".skm")||file.getName().contains(".mpeg")
            		||file.getName().contains(".mpg")||file.getName().contains(".asf"))
            {
            	filesize=file.length();
            	filesize=filesize/1048576;
            	strfilesize=Long.toString(filesize)+"MB";
            	filename=file.getName();
            	idx=filename.lastIndexOf(".");
            	realfilename=filename.substring(0, idx);
            	for(int a=0;a<files.length;a++)
            	{
            		File file2=files[a];
            		if(file2.getName().contains(realfilename)&&file2.getName().contains(".json"))
            		{
            			subtitlecheck=true;
            			break;
            		}
            		else										subtitlecheck=false;
            	}
            	adapter.add(new CData(getApplicationContext(), file.getName(), strfilesize, "", subtitlecheck));
            	filenames[cnt]=file.getName();	cnt++;
	        }
        }  
        
    }

	private class DataAdapter extends ArrayAdapter<CData> {
		private LayoutInflater mInflater;

		public DataAdapter(Context context, ArrayList<CData> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			View view = null;


			if (v == null) {
				view = mInflater.inflate(R.layout.userfilelistform, null);
			} 
			else {
				view = v;
			}

			final CData data = this.getItem(position);

			if (data != null) {
				TextView filename = (TextView) view.findViewById(R.id.Movie_Title);
				TextView filesize = (TextView) view.findViewById(R.id.filesize);
				TextView playtime = (TextView) view.findViewById(R.id.playtime);
				TextView issubtitle = (TextView) view.findViewById(R.id.issubtitle);
				filename.setText(data.mMovietitle);
				filesize.setText(data.mFileSize);
				playtime.setText(data.mPlaytime);
				if(data.mIssubtitle)
				{
					issubtitle.setText("�ڸ�����");
					issubtitle.setTextColor(Color.GREEN);
				}
				else
				{
					issubtitle.setText("�ڸ�����");
					issubtitle.setTextColor(Color.RED);
				}
			}
			return view;
		}
	}

	// CData�ȿ� ���� ���� ���� �Ҵ�

	class CData {

		private String mMovietitle;
		private String mFileSize;
		private String mPlaytime;
		private boolean mIssubtitle;

		public CData(Context context, String pMovietitle, String pFileSize, String pPlaytime, boolean pIssubtitle) {

			mMovietitle = pMovietitle;

			mFileSize = pFileSize;

			mPlaytime = pPlaytime;
			
			mIssubtitle = pIssubtitle;
		}

		public String getMovietitle() {
			return mMovietitle;
		}

		public String getFileSize() {
			return mFileSize;
		}

		public String getPlaytime() {
			return mPlaytime;
		}
		
		public boolean getIssubtitle() {
			return mIssubtitle;
		}
	}
}

package com.logicsoft.myapplication30.common;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.graphics.Bitmap;

public class VideoData {

	private long mId;
	private String mPath;
	private String mName;
	private String mTitle;
	private long mSize;
	private long mDuration;
	private Date mAddedDate;
	private Date mModifiedDate;
	private int mWidth;
	private int mHeight;
	private Bitmap mThumbnail;

	public VideoData(long id, String path, String name, String title, long size,
			long duration, Date addedDate, Date modifiedDate, int width,
			int height, Bitmap thumbnail) {
		super();
		this.mId = id;
		this.mPath = path;
		this.mName = name;
		this.mTitle = title;
		this.mSize = size;
		this.mDuration = duration;
		this.mAddedDate = addedDate;
		this.mModifiedDate = modifiedDate;
		this.mWidth = width;
		this.mHeight = height;
		this.mThumbnail = thumbnail;
	}

	public VideoData() {
		mId = 0;
		mPath = "";
		mTitle = "";
		mSize = 0;
		mDuration = 0;
		mAddedDate = new Date(0);
		mModifiedDate = new Date(0);
		mWidth = 0;
		mHeight = 0;
		mThumbnail = null;
	}
	
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public long getSize() {
		return mSize;
	}

	public void setSize(long size) {
		this.mSize = size;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long duration) {
		this.mDuration = duration;
	}

	public Date getAddedDate() {
		return mAddedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.mAddedDate = addedDate;
	}

	public Date getModifiedDate() {
		return mModifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.mModifiedDate = modifiedDate;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		this.mWidth = width;
	}

	public int getHeight() {
		return mHeight;
	}

	public void setHeight(int height) {
		this.mHeight = height;
	}

	public Bitmap getThumbnail() {
		return mThumbnail;
	}
	
	public void setThumbnail(Bitmap thumbnail) {
		this.mThumbnail = thumbnail;
	}
	
	public String getResolution() {
		return this.getWidth() + "x" + this.getHeight();
	}
	
	public String getDurationString() {
        int totalSeconds = (int)(getDuration() / 1000);
        
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;
 
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
        	return String.format("%02d:%02d", minutes, seconds);
        }
	}
	
	@Override
	public String toString() {
		DateFormat df = DateFormat.getDateTimeInstance();
		return "VideoData [path=" + mPath + ", name=" + mName + ", size=" + mSize + ", duration=" + mDuration
				+ ", addedDate=" + df.format(mAddedDate) + ", modifiedDate="
				+ df.format(mModifiedDate) + ", resolution=" + mWidth + "x" + mHeight
				+ "]";
	}
}

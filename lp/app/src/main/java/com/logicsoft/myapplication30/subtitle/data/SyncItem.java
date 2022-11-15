//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
import java.util.ArrayList;
import java.util.List;

/**
 * 자막의 싱크 하나를 표현하는 클래스, 자막의 가장 하위 객체
 * @author YJKim
 *
 */
public class SyncItem {
	public String text = null;
	public long startTime = -1;
	public long endTime = -1;
	public List<ExtraItem> extras = new ArrayList<ExtraItem>();
	
	public SyncItem() {
		
	}
	
	public SyncItem(String text, long startTime, long endTime) {
		this.text = text;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public void addExtraItem(ExtraItem extraItem) {
		extras.add(extraItem);
	}
	
	public List<ExtraItem> getExtraItems() {
		return extras;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<" + this.startTime + ">" + this.text + "<" + this.endTime + ">");
		return sb.toString();
	}
}

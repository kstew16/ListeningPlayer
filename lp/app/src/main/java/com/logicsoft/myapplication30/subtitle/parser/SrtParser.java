//package kr.logicsoft.listeningplayer.subtitle.parser;
package com.logicsoft.myapplication30.subtitle.parser;
import com.logicsoft.myapplication30.subtitle.data.LanguageTag;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;
import com.logicsoft.myapplication30.subtitle.data.SubtitleList;
import com.logicsoft.myapplication30.subtitle.data.SyncItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import kr.logicsoft.listeningplayer.subtitle.data.LanguageTag;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;
//import kr.logicsoft.listeningplayer.subtitle.data.SyncItem;

public class SrtParser extends SubtitleParser {
	
	private static Pattern sTimePattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2}),(\\d{3})");  
	
	private static Pattern sSyncPattern = Pattern.compile("(\\d+)(?:\\r\\n?|\\n)(\\d{2}:\\d{2}:\\d{2},\\d{3}) --> (\\d{2}:\\d{2}:\\d{2},\\d{3})(?:\\r\\n?|\\n)(.*?)(?=(?:\\r\\n?|\\n)^\\s*$)", Pattern.DOTALL | Pattern.MULTILINE);  
	
	/**
	 * 시간 문자열을 밀리초 단위로 변환한다. 
	 * @param s 파싱할 시간 문자열
	 * @return 밀리초 단위의 시간
	 */
	private static long timeStringToMilliseconds(String s)
	{
		Matcher time = sTimePattern.matcher(s);
		if (time.find()) {
			int hour = Integer.parseInt(time.group(1));
			int min = Integer.parseInt(time.group(2));
			int sec = Integer.parseInt(time.group(3));
			int ms = Integer.parseInt(time.group(4));
			return ms + sec*1000 + min*1000*60 + hour*1000*60*60;
		}
		return -1;
	}
	
	/**
	 * srt 자막을 파싱한다.
	 * @param s 파싱할 srt 형식의 자막 문자열
	 * @return 통합 자막 객체
	 */
	public static SubtitleList parse(String s) {
		SubtitleList subs = new SubtitleList();
		Subtitle sub = new Subtitle();
		Matcher sync = sSyncPattern.matcher(s);
		while (sync.find()) {
			int number = Integer.parseInt(sync.group(1));
			String start = sync.group(2);
			String end = sync.group(3);
			long startMs = timeStringToMilliseconds(start);
			long endMs = timeStringToMilliseconds(end);
			String content = sync.group(4);
			sub.addSyncItem(new SyncItem(content, startMs, endMs));
			//System.out.println(number + "\r\n" + start + " --> " + end + "\r\n(" + content + ")");
		}
		//영어라고 가정함
		sub.setLanguage(LanguageTag.ENGLISH);
		sub.normalize();
		subs.addSubtitle(sub);
		return subs;
	}
	
}

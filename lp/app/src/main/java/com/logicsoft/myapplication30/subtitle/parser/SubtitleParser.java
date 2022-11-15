//package kr.logicsoft.listeningplayer.subtitle.parser;
package com.logicsoft.myapplication30.subtitle.parser;

import android.util.Log;

import com.logicsoft.myapplication30.subtitle.data.SubtitleList;
import com.logicsoft.myapplication30.subtitle.json.JsonCodec;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

//import kr.logicsoft.listeningplayer.subtitle.data.LanguageTag;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;
//import kr.logicsoft.listeningplayer.subtitle.json.JsonCodec;

/**
 * 자막 파서의 추상 클래스.
 * 하위 클래스에서 필요한 메서드들이나, 변수를 모아놓음
 * @author YJKim
 *
 */
public abstract class SubtitleParser {

    private static String TAG = "logicsoft";

	protected static Set<String> sVoidElements = new HashSet<String>();
	protected static Pattern sTagPattern = Pattern.compile("<(/?)(\\w+)\\s?([\\s\\S]*?)(/?)>");
	protected static Pattern sAttrPattern = Pattern.compile("(\\b\\w+\\b)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\"'<>\\s]+)"); 
	
	static {
		String[] voidElements = {"area", "base", "br", "col", "command", "embed", "hr", "img", "input",
				"keygen", "link", "meta", "param", "source", "track", "wbr"};
		for (String elem : voidElements)
			sVoidElements.add(elem);
	}
	
	public enum SubtitleType {
		Unknown, Smi, Srt, Json
	}
	
	/**
	 * 텍스트 파일의 인코딩을 파악한다.
	 * @param path 텍스트 파일의 경로
	 * @return 인코딩 문자열 (예: UTF-8, EUC-KR 등)
	 * @throws IOException
	 */
	private static String detectCharset(String path) throws IOException {
		byte[] buf = new byte[4096];
		FileInputStream fis = new FileInputStream(path);
		UniversalDetector detector = new UniversalDetector(null);
		int nread = 0;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		fis.close();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		return encoding;
	}
	
	/**
	 * 텍스트 파일을 읽어서, 자바 형식의 유니코드 문자열로 변환해준다.
	 * @param path 텍스트 파일의 경로
	 * @return 제대로 표현되는 자바 문자열 객체
	 * @throws IOException
	 */
	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		Log.d(TAG, "path -> " + path);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			fc.close();
			
			String charsetName = detectCharset(path);
			Charset detectedCharset = Charset.forName("EUC-KR");
			if (charsetName != null) {
				System.out.println(charsetName);
				try {
					detectedCharset = Charset.forName(charsetName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return detectedCharset.decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	/**
	 * 특정 경로의 확장자만을 가져온다.
	 * @param path
	 * @return 확장자 (예: smi, srt 등)
	 */
	private static String getExtension(String path) {
		String ext = "";
		int idx = path.lastIndexOf('.');
		if (idx > 0) {
			ext = path.substring(idx+1);
		}
		return ext;
	}
	
	/**
	 * 자막의 타입을 추측한다.
	 * @param path 자막 경로
	 * @param content 자막 파일의 내용
	 * @return 자막 타입
	 */
	private static SubtitleType detectType(String path, String content) {
		SubtitleType t = SubtitleType.Unknown;
		String ext = getExtension(path);
		if (ext.equalsIgnoreCase("smi") || ext.equalsIgnoreCase("sami") || ext.equalsIgnoreCase("html")) {
			t = SubtitleType.Smi;
		} else if (ext.equalsIgnoreCase("srt") || ext.equalsIgnoreCase("txt")) {
			t = SubtitleType.Srt; 
		} else if (ext.equalsIgnoreCase("json")) {
			t = SubtitleType.Json;
		}
		return t;
	}
	
	/**
	 * 비디오 경로의 확장자를 제외한, 앞 부분과 매치되는 자막 파일을 모두 파싱하여 통합 자막 객체로 돌려준다.
	 * 예를 들면, a.avi 파일을 경로로 주었을 때, a.srt 뿐만 아니라 a_k.smi와 같은 자막도 파싱한다.
	 * @param videoPath 비디오 경로
	 * @return 통합 자막 객체
	 */
	public static SubtitleList parseAll(String videoPath) {
        Log.e(TAG, "parse all" + "vidoePath : " + videoPath);
		String[] subExts = {"srt", "smi", "sub", "psb", "ssa", "ass", "json"};
		final Set<String> subExt = new HashSet<String>(); 
		for (String elem : subExts)
			subExt.add(elem);
		Log.e(TAG, "subExt : " + subExt.toString());

		File file = new File(videoPath);
		File dir = file.getParentFile();
		final String filename = file.getName();
		final String prefix = filename.substring(0, filename.lastIndexOf('.'));

		Log.e(TAG, "filename :" + filename);
		Log.e(TAG, "prefix: " + prefix);

        String ext = filename.substring(filename.lastIndexOf('.')+1);
		Log.e(TAG, "ext " + ext);

		Log.e(TAG, "dir directory : " + dir);

        String dirName = "//storage//emulated//0//ListeningPlayer";
        File abc = new File(dirName);
        Log.e(TAG, abc.toString());
        String files[] = abc.list();



        Log.e(TAG, "abc " +  abc);

		File[] matchingFiles = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				String ext = filename.substring(filename.lastIndexOf('.')+1);
				return filename.startsWith(prefix) && subExt.contains(ext);
			}
		});
		
		SubtitleList subs = new SubtitleList();

		Log.d(TAG, "matchingFiles" + matchingFiles.getClass().toString());

		for (File f : matchingFiles) {
			if (f.isFile() && f.canRead()) {
				SubtitleList sub = SubtitleParser.parse(f.getAbsolutePath());
				//Log.d("sub", "sub hello" + sub.getSubtitle(0));
				//Log.d("subtitle ", "subtitle : " + f.getAbsolutePath());
				if (sub != null)
					subs.addSubtitles(sub);
			}
		}
		
		return subs;
	}
	
	/**
	 * 자막 하나를 파싱한다.
	 * @param path 자막 파일의 경로
	 * @return 통합 자막 객체
	 */
	public static SubtitleList parse(String path) {
		SubtitleList subs = null;
		Log.d("parse", "path : " + path);
		try {
			String str = readFile(path);
			Log.d("file", "path :"+ path+ "file string :" + str);
			SubtitleType type = detectType(path, str);
			switch (type) {
			case Unknown:
				break;
			case Smi:
				subs = SmiParser.parse(str);
				Log.d("smi", "smi sub lengVideoActivity.ins.finish()");
				break;
			case Srt:
				subs = SrtParser.parse(str);
				Log.d("srt", "srt sub length: "+ subs.count());
				break;
			case Json:
				subs = JsonCodec.decode(str, SubtitleList.class);
				Log.d("json", "json sub length: "+ subs.count() + "sub string "+ subs.getSubtitle(0));
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		if (subs != null && subs.getLanguages() != null)
			for (String lang : subs.getLanguages())
				if (lang != null) System.out.println(lang);
		*/
		return subs;
	}

	/**
	 * 파서를 테스트 하기 위한 메인 메서드
	 * @param args
	 */
	public static void main(String[] args) {
		String smi = "C:\\Users\\YJKim\\Desktop\\강남스타일\\Gangnam.Style.smi";
		String srt = "C:\\Users\\YJKim\\Desktop\\The Bible - 01x06 - Hope.KILLERS.English.HI.C.orig.Addic7ed.com.srt";
		String html = "C:\\Users\\YJKim\\Desktop\\test.html";
		String smi2 = "D:\\jk\\The.Big.Bang.Theory.S06E17.HDTV.XviD-AFG_k.smi";
		SubtitleParser.parse(smi2);
	}
}

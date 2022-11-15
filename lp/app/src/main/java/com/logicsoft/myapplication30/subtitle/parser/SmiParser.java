//package kr.logicsoft.listeningplayer.subtitle.parser;
package com.logicsoft.myapplication30.subtitle.parser;

import android.util.Log;

import com.logicsoft.myapplication30.DBManager;
import com.logicsoft.myapplication30.MainActivity;
import com.logicsoft.myapplication30.VideoActivity;
import com.logicsoft.myapplication30.subtitle.converter.SubtitleConverter;
import com.logicsoft.myapplication30.subtitle.data.ExtraItem;
import com.logicsoft.myapplication30.subtitle.data.LanguageTag;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;
import com.logicsoft.myapplication30.subtitle.data.SubtitleList;
import com.logicsoft.myapplication30.subtitle.data.SyncItem;
import com.logicsoft.myapplication30.subtitle.data.WordItem;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import kr.logicsoft.listeningplayer.subtitle.data.LanguageTag;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;
//import kr.logicsoft.listeningplayer.subtitle.data.SyncItem;







public class SmiParser extends SubtitleParser {

	private DBManager dbManager;


	/**
	 *
	 * 문자열 맨 앞과 맨 뒤의 따옴표를 제거한다.
	 * @param s
	 * @return 따옴표가 제거된 문자열
	 */
	private static String trimQuotes(String s) {
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length()-1);
		} else if (s.startsWith("'") && s.endsWith("'")) {
			return s.substring(1, s.length()-1);
		}
		return s;
	}

	/**
	 * 공백이 두개 이상으로 이루어진 문자열을 공백 하나로 만든다.
	 * @param s
	 * @return
	 */
	private static String trimText(String s) {
		String result = s.replaceAll("\\s+", " ").trim();
		if (result.equals(""))
			return null;
		return result;
	}

	/**
	 * smi 자막을 토큰화 -> 정규화 -> 변환 작업을 거쳐 파싱한다.
	 * @param s smi 파일 내용
	 * @return 통합 자막 객체
	 */

	public static SubtitleList parse(String s) {
		try {
			Log.d("parse", "parse before : ");

			SubtitleList sl = convert(normalize(tokenize(s)));
			Log.d("parse", "parse after: " );
			return sl;
		} catch (Exception e) {
			Log.d("parse ", "error "+ e.toString());
		}
		;
		return null;
	}


		/**
	 * html 태그 하나를 표현하는 클래스
	 * @author YJKim
	 *
	 */
	public static class HtmlToken {
		public boolean voidElement = false;
		public boolean selfClose = false;
		public String tag = "";
		public boolean close = false;
		public Map<String, String> attributes = new HashMap<String, String>();
		public String text = "";
		
		public HtmlToken() {
			
		}
		
		public HtmlToken(String tag, boolean close) {
			this.tag = tag;
			this.close = close;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("<");
			sb.append(this.close ? "/" : "");
			if (this.tag != null)
				sb.append(this.tag);
			for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
				sb.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}
			sb.append(this.selfClose ? " /" : "");
			sb.append(">");
			if (this.text != null)
				sb.append(text);
			return sb.toString();
		}
	}
	
	private static LanguageTag guessLanguage(String clazz, List<HtmlToken> tokens) {
		String style = null;
		for (HtmlToken token : tokens) {
			if (!token.close && token.tag.equalsIgnoreCase("style")) {
				style = token.text;
				break;
			}
		}
		if (style == null) return LanguageTag.UNKNOWN;
		
		String classSelectorPattern = "\\.([\\w-]+?)\\s*?\\{\\s*([^}]+?)\\s*\\}";
		String attributePattern = "([\\w-]+?)\\s*?:\\s*?([^;]+?)\\s*?;";
		
		Matcher classSelector = Pattern.compile(classSelectorPattern).matcher(style);
		
		//style 태그 내의 ".KRCC { Name:한국어; lang:ko-KR; SAMIType:CC; }"와 같은 문자열을 찾음
		while (classSelector.find()) {
			String foundClazz = classSelector.group(1);
			String attributes = classSelector.group(2);
			
			//주어진 클래스가 일치할 때만
			if (clazz.equalsIgnoreCase(foundClazz)) {
				Matcher attribute = Pattern.compile(attributePattern).matcher(attributes);
				LanguageTag guessByName = LanguageTag.UNKNOWN;
				LanguageTag guessByLang  = LanguageTag.UNKNOWN;
			
				while (attribute.find()) {
					String name = attribute.group(1).toLowerCase(Locale.getDefault());
					String value = attribute.group(2).toLowerCase(Locale.getDefault());
				
					if (name.equals("name")) {
						if (value.equals("한국어")) {
							guessByName = LanguageTag.KOREAN;
						} else if (value.equals("영어")) {
							guessByName = LanguageTag.ENGLISH;
						} else if (value.equals("korean")) {
							guessByName = LanguageTag.KOREAN;
						} else if (value.equals("english")) {
							guessByName = LanguageTag.ENGLISH;
						} else if (value.contains("ko")) {
							guessByName = LanguageTag.KOREAN;					
						} else if (value.contains("eg")) {
							guessByName = LanguageTag.ENGLISH;
						} else if (value.contains("en")) {
							guessByName = LanguageTag.ENGLISH;
						}
					} else if (name.equals("lang")) {
						if (value.contains("ko")) {
							guessByLang = LanguageTag.KOREAN;
						} else if (value.contains("kr")) {
							guessByLang = LanguageTag.KOREAN;
						} else if (value.contains("en")) {
							guessByLang = LanguageTag.ENGLISH;
						} else if (value.contains("us")) {
							guessByLang = LanguageTag.ENGLISH;
						} else if (value.contains("uk")) {
							guessByLang = LanguageTag.ENGLISH;
						}
					}
				}
				
				if (guessByName == LanguageTag.ENGLISH || guessByLang == LanguageTag.ENGLISH)
					return LanguageTag.ENGLISH;
				else if (guessByName != LanguageTag.UNKNOWN && guessByLang == LanguageTag.UNKNOWN) 
					return guessByName;
				else if (guessByName == LanguageTag.UNKNOWN && guessByLang != LanguageTag.UNKNOWN)	
					return guessByLang;
				else if (guessByName != LanguageTag.UNKNOWN && guessByLang != LanguageTag.UNKNOWN)	
					return guessByName;
				else {
					// name과 lang으로도 못찾았을 때, clazz로 찾음
					if (clazz.equalsIgnoreCase("KRCC"))
						return LanguageTag.KOREAN;
					else if (clazz.equalsIgnoreCase("ENCC"))
						return LanguageTag.ENGLISH;
					else if (clazz.equalsIgnoreCase("EGCC"))
						return LanguageTag.ENGLISH;		
				}
				
			}
		}
		
		return LanguageTag.UNKNOWN; 
	}
	
	/**
	 * 토큰 리스트를 콘솔에 출력하는 테스트용 메서드
	 * @param tokens
	 */
	private static List<HtmlToken> print(List<HtmlToken> tokens) {
		for (HtmlToken tok : tokens) {
			System.out.println(tok.toString());
		}
		return tokens;
	}
	
	/**
	 * 토큰 리스트를 통합 자막 객체로 변환한다.
	 * @param tokens
	 * @return 통합 자막 객체
	 */
	private static SubtitleList convert(List<HtmlToken> tokens) throws IOException {

		Log.d("smiparser", "smiparser-convert");


		/*********************************************/


		Map<String, Subtitle> subs = new HashMap<String, Subtitle>();
		long ms = -1;
		String clazz = null;
		StringBuilder sb = new StringBuilder();
		for (HtmlToken tok : tokens) {
			// 여는 태그일 경우
			if (!tok.close) {
				if (tok.tag.equals("sync")) {
					ms = Long.parseLong(tok.attributes.get("start"));
				} else if (tok.tag.equals("p")) {
					clazz = tok.attributes.get("class");
					sb.setLength(0);
					if (ms > 0 && clazz != null) {
						sb.append(tok.text);
					}
				} else {
					if (ms > 0 && clazz != null) {
						sb.append(tok.toString());
					}
				}
			} else {
				if (tok.tag.equals("sync")) {
					ms = -1;
				} else if (tok.tag.equals("p")) {
					Subtitle sub = subs.get(clazz);
					if (sub == null) {
						sub = new Subtitle();
						subs.put(clazz, sub);
					}
					sub.addSyncItem(new SyncItem(sb.toString(), ms, -1));
					clazz = null;
				} else {
					if (ms > 0 && clazz != null) {
						sb.append(tok.toString());
					}
				}
			}
		}
		
		SubtitleList result = new SubtitleList();
		for (Map.Entry<String, Subtitle> entry : subs.entrySet()) {
			clazz = entry.getKey();
			Subtitle sub = entry.getValue();
			sub.setLanguage(guessLanguage(clazz, tokens));
			sub.normalize();
			result.addSubtitle(sub);
		}

		Log.d("smiparser", "smiparser result" + result.getSubtitle(0));


		// SubtitleConverter.convert(result.getSubtitle(0));
		Subtitle st = result.getSubtitle(0);
		SubtitleList sl = SubtitleConverter.convert(st);
		Subtitle sl2 = sl.getSubtitle(0);







		for (SyncItem si : sl2)
			for (ExtraItem ei : si.getExtraItems())
				Log.d("extra", "meaning:" + ei.getMeaning() + " type: " + ei.getType() +
						" variant: "+ ((WordItem)ei).getVariant() + " original: " + ((WordItem)ei).getOriginal());




		try {

			//Log.d("prj", "dbpath : "+ path);
			String str = "SELECT * FROM company"; //예시 쿼리문
			MainActivity.dbManager.query(str);
			Log.d("prj", "query exec");
		} catch (Exception e) {
			Log.d("prj", "hello abc: " + e.getMessage());
		}


		for (SyncItem sy: sl2.body.syncs) Log.d("smiparser", "smiparser body sync" +sy );
		StringBuilder sb1 = new StringBuilder();
		sb1.append("{");
		sb1.append("\"subtitles\":[{\"head\":{\"language\":");
		sb1.append("\"CONVERTED\",\"title\":null,");
		sb1.append("\"attributes\":{}},");
		sb1.append("\"body\":{\"syncs\":[");

		Subtitle su = result.getSubtitle(0);

		int ind2=0;
		Log.d("subtitle", "subtitle size: " + su.body.syncs.size());
		for (SyncItem sy : su.body.syncs) {
			String sya = sy.text.replaceAll("\"", "");


			sb1.append("{\"text\":" + "\"" + sya + "\",");
			sb1.append("\"startTime\":" + sy.startTime + ",");
			sb1.append("\"endTime\":" + sy.endTime + ",");
			sb1.append("\"extras\":[");

			Log.d("syncitem", "syncitem-hello:" + sy.text);
			int ind =0;
			for (ExtraItem ex: sy.getExtraItems()) {
				sb1.append("{\"type\":");
				sb1.append("\"WordItem\",");
				sb1.append("\"properties\":");
				sb1.append("{\"variant:\"" + ":\"" + ((WordItem) ex).getVariant() + "\",");
				sb1.append("\"original\"" + ":" +  "\"" + ((WordItem) ex).getOriginal() + "\",");
				sb1.append("\"startIndex" + "\":" + ((WordItem) ex).getStartIndex() + ",");
				sb1.append("\"order" + "\":" + ((WordItem) ex).getOrder() + ",");
				sb1.append("\"type" + "\":\"" + ((WordItem) ex).getType() + "\",");
				sb1.append("\"level" + "\":" + ((WordItem) ex).getLevel() + ",");
				sb1.append("\"meaning" + "\":" + "\"" + ((WordItem) ex).getMeaning() + "\"");


                if (ind < sy.getExtraItems().size() -1)
                    sb1.append("}},");  //for each text for end ]}
                else sb1.append("}}");
                ind++;

          //      sb1.append("}},"); //for each type

			 }
			if (ind2 < su.body.syncs.size() -1)
			sb1.append("]},");
			else sb1.append("]}");
			ind2++;
			}
			sb1.append("]}}]}"); // for end

        //{"text":"\u0026nbsp;","startTime":137294,"endTime":9223372036854775807,"extras":[]}]}}]}

       // sb1.append("{\"text\":\"\u0026nbsp;\",\"startTime\":137294,\"endTime\":9223372036854775807,\"extras\":[]}]}}]}");


		// Log.d("smi", "builder");
		FileWriter fw = null;

		try {
			fw = new FileWriter("/storage/emulated/0/ListeningPlayer/Friends.1x01.The.One.Where.Monica.Gets.A.New.Roommate-JOG.json");
			Log.d("smi", "file open");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		fw.write(sb1.toString());
		//fw.write("================hello====================hello===============hello===============hello");
		fw.flush();
		Log.d("print", "print" + fw.toString());

		fw.close();

		VideoActivity.ins.finish();
		return result;
	}

	/**
	 * 토큰 리스트를 표준 HTML 형식에 맞게 정규화 한다.
	 * @param tokens
	 * @return 정규화된 토큰 리스트
	 */
	private static List<HtmlToken> normalize(List<HtmlToken> tokens) {
		List<HtmlToken> result = new ArrayList<HtmlToken>();
		
		boolean isSyncOpen = false;
		
		Stack<HtmlToken> stack = new Stack<HtmlToken>();
		for (HtmlToken tok : tokens) {
			if (isSyncOpen &&
					((!tok.close && tok.tag.equals("sync")) || 
					  (tok.close && tok.tag.equals("body")))) {
				//싱크가 열려있는데
				//싱크 여는 태그가 또 나오거나
				//바디 닫는 태그가 나온 경우
				HtmlToken t = null;
				
				do { // 꺼낸 tag 가 void element (self closing tag) 가 아닐 경우에만 닫기 태그 추가
					t = stack.pop();
					if (!t.selfClose)
						result.add(new HtmlToken(t.tag, true));
				} while (!t.tag.equals("sync"));
				isSyncOpen = false;
			}
			
			if (!tok.close) {
				stack.push(tok);
				if (tok.voidElement) //void element 라면 self close 추가함
					tok.selfClose = true;
				result.add(tok);
				if (tok.tag.equals("sync"))
					isSyncOpen = true;
			} else {
				HtmlToken t = stack.pop();
				result.add(tok);
			}
		}
		
		return result;
	}
	
	/**
	 * smi 자막을 정규 표현식을 사용하여 파싱한다.
	 * @param s
	 * @return 토큰 리스트
	 */
	private static List<HtmlToken> tokenize(String s) {
		List<HtmlToken> html = new ArrayList<HtmlToken>();
		
		int end = -1;
		
		Matcher tags = sTagPattern.matcher(s);
		while (tags.find()) {
			HtmlToken tok = new HtmlToken();

			boolean close = tags.group(1).equals("/");
			String name = tags.group(2).toLowerCase(Locale.ENGLISH);
			boolean selfClose = tags.group(4).equals("/");
			
			tok.close = close;
			tok.tag = name;
			tok.selfClose = selfClose;
			tok.voidElement = sVoidElements.contains(tok.tag);
			
			String text = null; //innerText is Previous tag's
			if (end >= 0) {
				text = s.substring(end, tags.start());
				text = trimText(text);
				if (text != null && !text.equals("")) {
					html.get(html.size()-1).text = text;
				}
			}
			
			Matcher attrs = sAttrPattern.matcher(tags.group(3));
			while (attrs.find()) {
				String attr = attrs.group(1).toLowerCase(Locale.ENGLISH);
				String value = trimQuotes(attrs.group(2));
				
				tok.attributes.put(attr, value);
			}
			
			html.add(tok);
			
			end = tags.end(); //innerText를 뽑기 위해 태그의 마지막 위치 저장
		}
		
		return html;
	}
	
}

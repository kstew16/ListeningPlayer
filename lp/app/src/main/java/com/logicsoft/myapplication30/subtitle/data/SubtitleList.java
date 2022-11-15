//package kr.logicsoft.listeningplayer.subtitle.data;
//package com.logicsoft.myapplication30.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 통합 자막을 표현하는 클래스, Subtitle 여러개가 들어갈 수 있는 클래스다.
 * @author YJKim
 *
 */
public class SubtitleList implements Iterable<Subtitle>, Iterator<Subtitle> {
	
	private List<Subtitle> subtitles = new ArrayList<Subtitle>();
	
	public List<LanguageTag> getLanguages() {
		List<LanguageTag> langs = new ArrayList<LanguageTag>();
		for (Subtitle sub : subtitles) {
			langs.add(sub.getLanguage());
		}
		return langs;
	}

	public void addSubtitle(Subtitle sub) {
		subtitles.add(sub);
	}
	
	public void addSubtitles(SubtitleList subs) {
		for (Subtitle sub : subs)
			subtitles.add(sub);
	}

	public Subtitle getSubtitle(int index) {
		return subtitles.get(index);
	}
	
	public int count() {
		return subtitles.size();
	}
	
	public Subtitle getSubtitleByLanguage(String language) {
		for (Subtitle sub : subtitles)
			if (sub.getLanguage().equals(language)) return sub;
		return null;
	}
	
	public boolean containsConvertedSubtitle() {
		for (Subtitle sub : subtitles)
			if (sub.isConverted()) return true;
		return false;
	}
	
	/**
	 * 통합 자막으로부터 변환된 자막을 가져온다.
	 * @param level 가져올 자막의 레벨 (하 = 1, 중 = 2, 상 = 3)
	 * @return 변환된 자막이 있다면 자막 객체, 아니면 null
	 */
	public Subtitle getConvertedSubtitle(int level) {
		for (Subtitle sub : subtitles)
			if (sub.isConverted() && sub.getLevel() == level) 
				return sub;
		return null;
	}
	
	private int index = 0;
	
	@Override
	public boolean hasNext() {
		return index < subtitles.size();
	}

	@Override
	public Subtitle next() {
		if (index >= subtitles.size())
			throw new NoSuchElementException();
		return subtitles.get(index++);
	}

	@Override
	public void remove() {
		subtitles.remove(index);
	}

	@Override
	public Iterator<Subtitle> iterator() {
		return this;
	}
	
}

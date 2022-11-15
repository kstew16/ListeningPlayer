//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
import java.util.ArrayList;
import java.util.List;

/**
 * 숙어를 나타내는 클래스.
 * 단어 리스트와 숙어 중간에 추가된 단어의 인덱스가 포함되어 있다. 
 * @author YJKim
 *
 */
public class IdiomItem extends ExtraItem {
	
	private List<WordItem> words = new ArrayList<WordItem>();
	private List<Integer> attachedIndex = new ArrayList<Integer>();
	
	public IdiomItem() {
		super.setType("idiom");
	}
	
	public WordItem getWord(int index) {
		return words.get(index);
	}
	
	public void addWord(WordItem word) {
		this.words.add(word);
	}
		
	public List<WordItem> getWords() {
		return words;
	}

	public void setWords(List<WordItem> words) {
		this.words = words;
	}

	public List<Integer> getAttachedIndex() {
		return attachedIndex;
	}

	public void setAttachedIndex(List<Integer> attachedIndex) {
		this.attachedIndex = attachedIndex;
	}

	public Integer getAttachedIndex(int index) {
		return attachedIndex.get(index);
	}
	
	public void addAttachedIndex(Integer attachedIndex) {
		this.attachedIndex.add(attachedIndex);
	}
	
	private boolean isAttachedWord(int index)
	{
		for (Integer num : this.attachedIndex )
		{
			if (num == index) return true;
		}
		return false;
	}
	
	public String toString(boolean getVariant, boolean getOriginal, boolean getMeaning, boolean getAttached)
	{
		StringBuilder sb = new StringBuilder();
		boolean continuedAttach = false;
		
		for (int i = 0; i < this.words.size(); i++) {
			WordItem word = this.words.get(i);
			String completeFormOfWord = word.toString(getVariant, getOriginal, false);
			if (i == 0) {
				sb.append(completeFormOfWord);
			} else {
				if (isAttachedWord(i)) {
					if (getAttached) {
						if (continuedAttach) {
							sb.append(String.format(" %s", completeFormOfWord));							
						} else {
							sb.append(String.format(" (%s", completeFormOfWord));
							continuedAttach = true;
						}
					}
				} else {
					if (continuedAttach) {
						continuedAttach = false;
						sb.append(String.format(") %s",completeFormOfWord));
					} else {
						sb.append(String.format(" %s",completeFormOfWord));
					}
				}
			}
		}
		
		if (getMeaning)
			sb.append(String.format(": %s", getMeaning()));
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toString(true, false, true, true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attachedIndex == null) ? 0 : attachedIndex.hashCode());
		result = prime * result + ((words == null) ? 0 : words.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdiomItem other = (IdiomItem) obj;
		if (attachedIndex == null) {
			if (other.attachedIndex != null)
				return false;
		} else if (!attachedIndex.equals(other.attachedIndex))
			return false;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}
}

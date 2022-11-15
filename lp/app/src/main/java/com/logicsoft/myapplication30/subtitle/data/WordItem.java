//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
//import kr.logicsoft.listeningplayer.VideoActivity;
import com.logicsoft.myapplication30.VideoActivity;

//import kr.logicsoft.listeningplayer.VideoControllerView;
/**
 * �떒�뼱瑜� �굹���궡�뒗 �겢�옒�뒪.
 * 蹂��삎怨� �썝�삎瑜� 異붽�濡� 媛�吏�怨� �엳�떎.
 * @author YJKim
 *
 */
public class WordItem extends ExtraItem {
	public String variant = null;
	private String original = null;
	private int startIndex = -1;
	private int order = -1;

	public WordItem() {
		super.setType("word");
	}
	
	public WordItem(String variant) {
		this();
		this.variant = variant;
	}
	
	public String getVariant() {
		return variant;
	}
	
	public void setVariant(String variant) {
		this.variant = variant;
	}
	
	public String getOriginal() {
		return original;
	}
	
	public void setOriginal(String original) {
		this.original = original;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return startIndex + variant.length();
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public String toString(boolean getVariant, boolean getOriginal, boolean getMeaning) {
		boolean isOriginalForm = getVariant().equals(getOriginal());
		
		StringBuilder sb = new StringBuilder();
		if (isOriginalForm || (getOriginal()==null)){
			sb.append(getVariant());
		} else {
			if (getVariant) {
				sb.append(getVariant());
			}
			if (getOriginal) {
				if (getVariant)
					sb.append("[" + getOriginal() + "]");
				else 
					sb.append(getOriginal());
			}
		}
		
		if (getMeaning&&VideoActivity.mMeaning.isChecked()) {
			if (getVariant || getOriginal)
				sb.append("-" + getMeaning());
			else
				sb.append(getMeaning());
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toString(true, true, true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((original==null) ? 0 : original.hashCode());
		result = prime * result + ((variant == null) ? 0 : variant.hashCode());
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
		WordItem other = (WordItem) obj;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		if (variant == null) {
			if (other.variant != null)
				return false;
		} else if (!variant.equals(other.variant))
			return false;
		return true;
	}
}

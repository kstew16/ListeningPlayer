//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
/**
 * ��留��� 異�媛�濡� �ㅼ�닿� �� ���� ��紐⑹�� �����대�� �대����.
 * �⑥�대�� ���닿� �ㅼ�닿� �� ����.
 * �⑥�댁�� ���댁�� 怨듯�듭���� 硫ㅻ��� ��踰④낵 �살�� �ы�⑤���� ����.
 * @author YJKim
 *
 */
public abstract class ExtraItem {
	private String type = null;
	private int level = 0;
	private String meaning = null;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getMeaning() {
		return meaning;
	}
	
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
}

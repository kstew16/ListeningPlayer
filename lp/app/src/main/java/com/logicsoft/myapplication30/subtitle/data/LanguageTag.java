//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;
public enum LanguageTag {
	NONE (0),
	UNKNOWN (1),
	DEFAULT (2),
	ENGLISH (3),
	KOREAN (4),
	JAPANESE (5),
	SIMPLIFIED_CHINESE (6),
	TRADITIONAL_CHINESE (7),
	CONVERTED (8);
	
	public int value = 0;
	
	private LanguageTag(int value) {
		this.value = value;
	}
}

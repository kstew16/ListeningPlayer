//package kr.logicsoft.listeningplayer.subtitle.json;
package com.logicsoft.myapplication30.subtitle.json;

//import kr.logicsoft.listeningplayer.subtitle.data.ExtraItem;
import com.logicsoft.myapplication30.subtitle.data.ExtraItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonCodec {

	private static Gson gson = new GsonBuilder().serializeNulls()
			.registerTypeAdapter(ExtraItem.class, new ExtraItemAdapter())
			.setExclusionStrategies(new GsonExclusion()).create(); 
	
	public static String encode(Object obj) {
		return gson.toJson(obj);
	}
	
	public static <T> T decode(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
}

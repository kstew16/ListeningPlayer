//package kr.logicsoft.listeningplayer.subtitle.json;
package com.logicsoft.myapplication30.subtitle.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;
import com.logicsoft.myapplication30.subtitle.data.Subtitle.Body;
import com.logicsoft.myapplication30.subtitle.data.SubtitleList;

import java.util.Observable;

//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle.Body;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;

public class GsonExclusion implements ExclusionStrategy {

	public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
    	Class<?> clazz = f.getDeclaringClass();
    	String name = f.getName();
    	if (clazz == Subtitle.class) {
    		return (name.equals("indexCache") || name.equals("level") || name.equals("levelHandler"));
    	} else if (clazz == Observable.class) {
    		return (name.equals("changed") || name.equals("obs"));
    	} else if (clazz == SubtitleList.class) {
    		return (name.equals("index"));
    	} else if (clazz == Body.class) {
    		return (name.equals("index"));
    	}
    	return false;
    }

}

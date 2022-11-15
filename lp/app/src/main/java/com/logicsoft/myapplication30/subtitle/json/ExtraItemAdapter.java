//package kr.logicsoft.listeningplayer.subtitle.json;
package com.logicsoft.myapplication30.subtitle.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.logicsoft.myapplication30.subtitle.data.ExtraItem;

import java.lang.reflect.Type;

//import kr.logicsoft.listeningplayer.subtitle.data.ExtraItem;

public class ExtraItemAdapter implements JsonSerializer<ExtraItem>, JsonDeserializer<ExtraItem> {

	private static final String TYPE = "type";
	private static final String PROPERTIES = "properties";
	
    @Override
    public JsonElement serialize(ExtraItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add(TYPE, new JsonPrimitive(src.getClass().getSimpleName()));
        result.add(PROPERTIES, context.serialize(src, src.getClass()));
 
        return result;
    }
 
    @Override
    public ExtraItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get(TYPE).getAsString();
        JsonElement element = jsonObject.get(PROPERTIES);
 
        try {
            return context.deserialize(element, Class.forName(ExtraItem.class.getPackage().getName() + "." + type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }
}

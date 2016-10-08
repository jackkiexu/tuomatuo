package com.lami.tuomatuo.utils;

/**
 * Created by xujiankang on 2016/1/20.
 */
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {

    private static final Logger logger = Logger.getLogger(GsonUtils.class);

    private static Gson getGson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    public static String toGson(Object obj) {
        return getGson().toJson(obj);
    }

    public static String toGson(Object obj,Type targetType) {
        Gson gson = new Gson();
        return gson.toJson(obj, targetType);
    }

    public static <E extends Object> E getObjectFromJson(String json, Class<E> classOfT){
        E result = null;
        try {
            result = getGson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return result;
    }

    public static Set<String> getSetGson(String json){
        return getGson().fromJson(json, new TypeToken<Set<String>>() {}.getType());
    }

    public static Set<Integer> getSetIntegerGson(String json){
        return getGson().fromJson(json, new TypeToken<Set<Integer>>() {}.getType());
    }

    public static LinkedList<String> getLinkedListStringGson(String json){
        return getGson().fromJson(json, new TypeToken<LinkedList<String>>() {}.getType());
    }

    public static Map<String,String> getMapGson(String json){
        return getGson().fromJson(json, new TypeToken<Map<String,String>>() {}.getType() );
    }

    public static Map<String,HashSet<String>> getMapInnerSetGson(String json){
        return getGson().fromJson(json, new TypeToken<Map<String,HashSet<String>>>() {}.getType() );
    }

    public static List<String> getListGson(String json){
        return getGson().fromJson(json, new TypeToken<List<String>>() {}.getType() );
    }

    public static void main(String[] args) {
        String json = "";
    }
}

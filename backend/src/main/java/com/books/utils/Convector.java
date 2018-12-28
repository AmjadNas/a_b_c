package com.books.utils;

import com.books.objects.ActivityEvent;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public final class Convector {

    private Convector(){}

    public static String fromListToString(List<String> list){
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            int i = 0;
            for (String s : list) {
                sb.append(s);
                if (i < list.size()-1)
                    sb.append(", ");
                i++;
            }
        }
        return sb.toString();
    }

    public static String combineLines(List<String> list){
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            int i = 0;
            for (String s : list) {
                sb.append(s);
                if (i < list.size()-1)
                    sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }

    public static List<String> fromStringToLines(String lines) {
        String[] line = lines.split("\n");
        return Arrays.asList(line);
    }

    public static<T extends ToJSON> String toJson(String table, List<T> list){
        try {
            JSONObject object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (list == null) {
                return null;
            }
            if (list.size() == 0) {
                return null;
            }
            for (T it : list) {
                if (it != null) {
                    arr.add(it.toJson());
                }
            }
            object.put(table, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }
    }
}

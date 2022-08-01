package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @ClassName:JsonUtils
 * @author: daifei
 * @Description:
 * @Date: 2022/7/28 10:17
 * @Version: v1.0
 */

public class JsonUtils {

    public JsonUtils() {
    }

    public static JSONObject sort(JSONObject json) {
        return dropObject(json);
    }

    private static JSONObject dropObject(JSONObject json) {
        Iterator var3 = json.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var3.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JSONArray) {
                JSONArray jsonArray = dropArray((JSONArray)value);
                json.put(key, jsonArray);
            }

            if (value instanceof JSONObject) {
                JSONObject jsonObject = dropObject((JSONObject)value);
                json.put(key, jsonObject);
            }
        }

        return (JSONObject) JSON.toJSON(new TreeMap(json.getInnerMap()));
    }

    private static JSONArray dropArray(JSONArray jsonArray) {
        List<Integer> indexList = null;

        for(int i = 0; i < jsonArray.size(); ++i) {
            Object next = jsonArray.get(i);
            if (next instanceof JSONObject) {
                JSONObject jsonObject = dropObject((JSONObject)next);
                jsonArray.remove(i);
                jsonArray.add(i, jsonObject);
            } else if (next instanceof JSONArray) {
                dropArray((JSONArray)next);
            } else {
                if (indexList == null) {
                    indexList = new ArrayList(jsonArray.size());
                }

                indexList.add(i);
            }
        }

        if (indexList != null) {
            List<Object> list = new ArrayList(indexList.size());
            List<Object> objectList = jsonArray.toJavaList(Object.class);
            if (indexList.size() == objectList.size()) {
                list.addAll(objectList);
                objectList.clear();
            } else {
                int offset = 0;
                Iterator var6 = indexList.iterator();

                while(var6.hasNext()) {
                    Integer index = (Integer)var6.next();
                    Object o = objectList.remove(index - offset++);
                    list.add(o);
                }
            }

            list.sort(Comparator.comparing(Object::toString));
            jsonArray.clear();
            jsonArray.addAll(list);
            if (!objectList.isEmpty()) {
                jsonArray.addAll(objectList);
            }
        }

        return jsonArray;
    }
}

package com.example.lets_plan.logic.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Converter {

    public static <T> Map<String, Object> objectToMap(T obj) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                Log.e("Converter", "objectToMap - " + e.getMessage());
            }
        }
        return map;
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        T obj = null;
        try {
            Constructor<T> ctor = clazz.getConstructor();
            obj = ctor.newInstance();
            Map<String, Method> methods =  new HashMap<>();
            Arrays.asList(obj.getClass().getDeclaredMethods()).forEach(m -> methods.put(m.getName(), m));
            for (Entry<String, Object> entry : map.entrySet()) {
                String methodName = "set" + entry.getKey().substring(0, 1).toUpperCase();
                if (entry.getKey().length() > 1) {
                    methodName += entry.getKey().substring(1);
                }
                if (methods.containsKey(methodName)) {
                    methods.get(methodName).invoke(obj, entry.getValue());
                }
            }
        } catch (ReflectiveOperationException e) {
            Log.e("Converter", "mapToObject - " + e.getMessage());
        }
        finally {
            return obj;
        }
    }
}

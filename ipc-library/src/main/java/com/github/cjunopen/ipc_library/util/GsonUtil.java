package com.github.cjunopen.ipc_library.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GsonUtil {

    private static Gson sGson = createGson();

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

    /**
     * 格式化输出字符串
     */
    public static Gson getGson() {
        return sGson;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return sGson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type type) {
        return sGson.fromJson(json, type);
    }

    public static String toJson(Object object) {
        return sGson.toJson(object);
    }

    private static Gson createGson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setLenient()
//                .registerTypeAdapter(Date.class, new DateUtil.CustomDateAdapter(DateUtil.formatter1))
                .disableHtmlEscaping()
                .create();
        return gson;
    }

    /**
     * 得到泛型类的类型
     *
     * @return
     */
    public static <T> Type getGenericityType(Class<T> tClass) {
        Type type = tClass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) {
            return null;
        }

        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types == null || types.length == 0) {
            return null;
        }
        return types[0];
    }
}

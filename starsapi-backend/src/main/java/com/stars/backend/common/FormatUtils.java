package com.stars.backend.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * 格式工具
 * 用于处理日期格式的工具类，实现了Gson库的JsonDeserializer接口，用于将JSON数据中的时间戳转换为Date对象。
 *
 * @author stars
 */
public class FormatUtils implements JsonDeserializer<Date> {

    /**
     * 从JSON中的时间戳转换为Date对象
     *
     * @param jsonElement                JSON元素
     * @param type                       类型
     * @param jsonDeserializationContext JSON反序列化上下文
     * @return 转换后的Date对象
     * @throws JsonParseException 如果解析失败，则抛出JsonParseException异常
     */
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        // 获取时间戳
        long timestamp = jsonElement.getAsLong();
        // 转换为Date类型
        return new Date(timestamp);
    }
}

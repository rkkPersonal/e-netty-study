package com.netty.study.serializer;

import com.alibaba.fastjson.JSON;

/**
 * @author Steven
 * @date 2022年10月07日 23:43
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}

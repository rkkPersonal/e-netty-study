package com.netty.study.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.file.Charsets;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.codec.CharEncoding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @date 2022年10月08日 1:11
 */
public class HttpWrapper<T> {

    public static <T>T queryParameter(String uri,Class<T> tClass) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get(0));
        }
        return JSONObject.parseObject(JSON.toJSONString(map),tClass);
    }
}

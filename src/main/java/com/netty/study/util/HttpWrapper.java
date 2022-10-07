package com.netty.study.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.file.Charsets;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.commons.codec.CharEncoding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @date 2022年10月08日 1:11
 */
public class HttpWrapper<T> {

    public static <T> T queryParameter(String uri, Class<T> tClass) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get(0));
        }
        return JSONObject.parseObject(JSON.toJSONString(map), tClass);
    }

    public static Map<String, Object> queryBody(FullHttpRequest content) throws IOException {
        // 是POST请求
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(content);
        decoder.offer(content);
        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
        Map<String, Object> map = new HashMap<>();
        for (InterfaceHttpData parm : parmList) {
            Attribute data = (Attribute) parm;
            map.put(data.getName(), data.getValue());
        }
        return map;
    }
}

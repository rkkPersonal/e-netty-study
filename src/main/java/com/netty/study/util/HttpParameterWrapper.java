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
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.CharEncoding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steven
 * @date 2022年10月08日 1:11
 */
public class HttpParameterWrapper<T> {

    public static <T> T queryParameter(String uri, Class<T> tClass) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get(0));
        }
        return JSONObject.parseObject(JSON.toJSONString(map), tClass);
    }

    public static Map<String, List<String>> queryBody(FullHttpRequest req) throws IOException {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
        List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
        Map<String, List<String>> parameters = new HashMap<>();
        for (InterfaceHttpData data : postList) {
            List<String> values = new ArrayList<>();
            MixedAttribute value = (MixedAttribute) data;
            value.setCharset(CharsetUtil.UTF_8);
            values.add(value.getValue());
            parameters.put(data.getName(), values);
        }
        return parameters;
    }
}
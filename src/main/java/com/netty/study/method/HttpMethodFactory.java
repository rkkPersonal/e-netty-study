package com.netty.study.method;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Steven
 * @date 2022年10月08日 1:45
 */
public class HttpMethodFactory {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private static final Map<String, HttpMethod> methodMap = new ConcurrentHashMap<>();

    static {
        methodMap.put(GET, new GetMethod());
        methodMap.put(POST, new PostMethod());
    }

    public static HttpMethod getMethod(String httpMethod) {
        if (methodMap.containsKey(httpMethod)) {
            return methodMap.get(httpMethod);
        }
        throw new UnsupportedOperationException("Just support method " + GET + "," + POST);
    }


}

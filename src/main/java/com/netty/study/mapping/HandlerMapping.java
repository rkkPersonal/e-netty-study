package com.netty.study.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Steven
 * @date 2022年10月08日 16:12
 */
public class HandlerMapping {

    private static final Map<String, HandlerMapping> urlMapping = new ConcurrentHashMap<>();

    public void add(String url, HandlerMapping handlerMapping) {
        if (urlMapping.containsKey(url)) {
            return;
        }
        urlMapping.put(url, handlerMapping);
    }

}

package com.netty.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author Steven
 * @date 2022年10月02日 2:42
 */
@Slf4j
@Controller
@RequestMapping("/ui")
public class UIController {

    @Resource
    private RedisTemplate redisTemplate;


    @GetMapping("/ws")
    public String ws() {
        Set keys = redisTemplate.keys("*");

        keys.forEach(s -> {
            log.info("key:{}", s);
        });

        return "message";
    }
}

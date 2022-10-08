package com.netty.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Steven
 * @date 2022年10月02日 2:42
 */
@Controller
@RequestMapping("/ui")
public class UIController {

    @GetMapping("/ws")
    public String ws() {
        return "message";
    }
}

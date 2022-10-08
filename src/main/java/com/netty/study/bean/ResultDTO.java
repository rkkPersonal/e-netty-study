package com.netty.study.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Steven
 * @date 2022年09月25日 15:26
 */
@Data
@ToString
@EqualsAndHashCode

public class ResultDTO<T> {

    private int code;

    private String msg;

    private T data;

    public ResultDTO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultDTO<T> ok() {
        return new ResultDTO(0, "success", null);
    }

    public static <T> ResultDTO<T> success(T data) {
        return new ResultDTO(0, "success", data);
    }

    public static <T> ResultDTO<T> success(String msg, T data) {
        return new ResultDTO(0, msg, data);
    }

    public static <T> ResultDTO<T> failure(String msg) {
        return new ResultDTO(-1, msg, null);
    }

    public static <T> ResultDTO<T> failure(String msg, T data) {
        return new ResultDTO(-1, msg, data);
    }

    public static <T> ResultDTO<T> failure(int code, String msg, T data) {
        return new ResultDTO(code, msg, data);
    }


}

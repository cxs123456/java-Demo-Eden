package org.cxs.demo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class R<T> {

    private int code;
    private String message;
    private T data;

    public static <T> R<T> response(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> response(int code, String message, T t) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(t);
        return r;
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

}
package com.example.common;

// 导入枚举类 ResultCodeEnum，它可能包含了一些常用的状态码和状态信息
import com.example.common.enums.ResultCodeEnum;

// 定义一个通用的返回结果类 Result
public class Result {
    // 状态码
    private String code;
    // 状态信息
    private String msg;
    // 返回的数据
    private Object data;

    // 私有的构造方法，仅用于创建一个包含数据的 Result 对象
    private Result(Object data) {
        this.data = data;
    }

    // 默认的构造方法
    public Result() {
    }

    // 创建一个表示成功的 Result 对象
    public static Result success() {
        Result tResult = new Result();
        // 设置状态码为成功状态码
        tResult.setCode(ResultCodeEnum.SUCCESS.code);
        // 设置状态信息为成功状态信息
        tResult.setMsg(ResultCodeEnum.SUCCESS.msg);
        return tResult;
    }

    // 创建一个表示成功的 Result 对象，并包含返回的数据
    public static Result success(Object data) {
        Result tResult = new Result(data);
        // 设置状态码为成功状态码
        tResult.setCode(ResultCodeEnum.SUCCESS.code);
        // 设置状态信息为成功状态信息
        tResult.setMsg(ResultCodeEnum.SUCCESS.msg);
        return tResult;
    }

    // 创建一个表示错误的 Result 对象，默认使用系统错误状态码和状态信息
    public static Result error() {
        Result tResult = new Result();
        // 设置状态码为系统错误状态码
        tResult.setCode(ResultCodeEnum.SYSTEM_ERROR.code);
        // 设置状态信息为系统错误状态信息
        tResult.setMsg(ResultCodeEnum.SYSTEM_ERROR.msg);
        return tResult;
    }

    // 创建一个表示错误的 Result 对象，使用自定义的错误状态码和状态信息
    public static Result error(String code, String msg) {
        Result tResult = new Result();
        // 设置自定义的错误状态码
        tResult.setCode(code);
        // 设置自定义的错误状态信息
        tResult.setMsg(msg);
        return tResult;
    }

    // 创建一个表示错误的 Result 对象，使用枚举类中定义的错误状态码和状态信息
    public static Result error(ResultCodeEnum resultCodeEnum) {
        Result tResult = new Result();
        // 设置枚举类中定义的错误状态码
        tResult.setCode(resultCodeEnum.code);
        // 设置枚举类中定义的错误状态信息
        tResult.setMsg(resultCodeEnum.msg);
        return tResult;
    }

    // 获取状态码的 getter 方法
    public String getCode() {
        return code;
    }

    // 设置状态码的 setter 方法
    public void setCode(String code) {
        this.code = code;
    }

    // 获取状态信息的 getter 方法
    public String getMsg() {
        return msg;
    }

    // 设置状态信息的 setter 方法
    public void setMsg(String msg) {
        this.msg = msg;
    }

    // 获取返回数据的 getter 方法
    public Object getData() {
        return data;
    }

    // 设置返回数据的 setter 方法
    public void setData(Object data) {
        this.data = data;
    }
}
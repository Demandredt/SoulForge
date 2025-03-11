package com.maphaze.soulforge.core.utils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maphaze.soulforge.core.serializer.ResponseUtilSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;

@Schema(description = "通用响应工具类")
@Data
@NoArgsConstructor
@JsonSerialize(using = ResponseUtilSerializer.class)
public class ResponseUtil<T> {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "携带的数据")
    private T data;

    @Schema(description = "自定义字段名")
    private String fieldName = "data";



    public ResponseUtil(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseUtil(int code, String message, T data, String fieldName) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.fieldName = fieldName;
    }

    /**
     * 成功响应（自定义字段名）
     * @param fieldName
     * @param data
     * @return
     * @param <T>
     */
    public static <T> ResponseUtil<T> success(String fieldName,T data){
        return new ResponseUtil<>(200,"ok",data,fieldName);
    }
    //    成功响应
    public static <T> ResponseUtil<T> success(){
        return new ResponseUtil<>(200,"ok",null);
    }
    public static <T> ResponseUtil<T> success(String message){
        return new ResponseUtil<>(200,message,null);
    }
    public static <T> ResponseUtil<T> success(T data){
        return new ResponseUtil<>(200,"ok",data);
    }

//    失败响应
    public static <T> ResponseUtil<T> fail(int code,String message){
        return new ResponseUtil<>(code,message,null);
    }
    public static <T> ResponseUtil<T> fail(int code,String message,T data){
        return new ResponseUtil<>(code,message,data);
    }

}

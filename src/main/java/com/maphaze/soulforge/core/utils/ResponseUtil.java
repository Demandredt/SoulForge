package com.maphaze.soulforge.core.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;

@Schema(description = "通用响应工具类")
@Data
@NoArgsConstructor
public class ResponseUtil<T> {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "携带的数据")
    private T data;

    public ResponseUtil(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //    成功响应
    public static <T> ResponseUtil<T> success(){
        return new ResponseUtil<>(200,"ok",null);
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

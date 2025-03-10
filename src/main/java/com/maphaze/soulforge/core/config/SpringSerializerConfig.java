package com.maphaze.soulforge.core.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.maphaze.soulforge.core.serializer.ResponseUtilSerializer;
import com.maphaze.soulforge.core.utils.ResponseUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringSerializerConfig {
//      使用JsonSerialize代替，暂时弃用
//    @Bean
//    public Module customJacksonModule(){
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(ResponseUtil.class,new ResponseUtilSerializer());
//        return module;
//
//    }
}

package com.maphaze.soulforge.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maphaze.soulforge.core.utils.ResponseUtil;

import java.io.IOException;


public class ResponseUtilSerializer extends JsonSerializer<ResponseUtil<?>> {

    @Override
    public void serialize(ResponseUtil<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("code",value.getCode());
        gen.writeStringField("message",value.getMessage());
        gen.writeObjectField(value.getFieldName(), value.getData());
        gen.writeEndObject();
    }
}

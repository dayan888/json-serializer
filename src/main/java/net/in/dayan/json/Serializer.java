package net.in.dayan.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Serializer extends JsonSerializer<Wrapper> {
    private static Serializer serializer;
    private ObjectMapper mapper;

    private Serializer() {}

    public static synchronized Serializer getInstance() {
        if (serializer == null) {
            serializer = new Serializer();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Wrapper.class, serializer);
            serializer.mapper = new ObjectMapper();
            serializer.mapper.registerModule(simpleModule);
        }
        return serializer;
    }

    public String toJsonString(Wrapper wrapper) throws JsonProcessingException {
        return mapper.writeValueAsString(wrapper);
    }

    public String toJsonString(Object obj, List<String> includeList, List<String> excludeList, boolean isFinallyAllowed) throws JsonProcessingException {
        Wrapper wrapper = new Wrapper<>(obj);
        if (includeList != null) {
            wrapper.addInclude(includeList);
        }
        if (excludeList != null) {
            wrapper.addExclude(excludeList);
        }
        wrapper.setFinallyAllowed(isFinallyAllowed);

        return mapper.writeValueAsString(wrapper);
    }

    @Override
    public void serialize(Wrapper wrapper, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Writer writer = new Writer(wrapper, gen, serializers);
        try {
            writer.serialize();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

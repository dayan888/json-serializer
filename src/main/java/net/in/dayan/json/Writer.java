package net.in.dayan.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.*;

public class Writer {
    private Wrapper wrapper;
    private JsonGenerator gen;
    private SerializerProvider serializers;
    private Stack nameStack = new Stack();
    private static List<String> IGNORE_LIST = Arrays.asList(new String[]{"serialVersionUID"});

    protected Writer(Wrapper wrapper, JsonGenerator gen, SerializerProvider serializers) {
        this.wrapper = wrapper;
        this.gen = gen;
        this.serializers = serializers;
    }

    /**
     * serialize object to json string.
     *
     * @throws IllegalAccessException
     * @throws IOException
     * @throws InvocationTargetException
     */
    protected void serialize() throws IllegalAccessException, IOException, InvocationTargetException {
        Object obj = wrapper.value;
        write(null, obj, isIgnoreNull(obj));
    }

    /**
     * judge if use default serializer
     *
     * @param obj
     * @return
     */
    private boolean isDefaultType(Object obj) {
        return obj instanceof String
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Short
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof Boolean
                || obj instanceof Character
                || obj instanceof Byte
                || obj instanceof BigDecimal
                || obj instanceof Temporal
                || obj instanceof URL
                || obj instanceof URI
                || obj instanceof JsonNode;
    }

    /**
     * judge if a property should be included.
     *
     * @return
     */
    private boolean shouldIncluded() {
        String path = String.join(".", nameStack);
        for (String regex : (List<String>)wrapper.includeList) {
            if (path.matches(regex.replace(".", "\\.").replace("*", ".*"))) {
                return true;
            }
        }
        for (String regex : (List<String>)wrapper.excludeList) {
            if (path.matches(regex.replace(".", "\\.").replace("*", ".*"))) {
                return false;
            }
        }

        // allow if path not match both include and exclude list
        return wrapper.isFinallyAllowed;
    }

    /**
     * Write name and value according to the value type.
     *
     * @param name
     * @param val
     * @param ignoreNull
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void write(String name, Object val, boolean ignoreNull) throws IOException, InvocationTargetException, IllegalAccessException {

        if (val == null && ignoreNull || IGNORE_LIST.contains(name)) {
            return;
        }

        if (name != null) {
            nameStack.push(name);
            if (!shouldIncluded()) {
                nameStack.pop();
                return;
            }
            gen.writeFieldName(name);
        }

        if (val == null) {
            gen.writeNull();
        } else if (isDefaultType(val)) {
            serializers.defaultSerializeValue(val, gen);
        } else if (val instanceof Date) {
            serializers.defaultSerializeDateValue((Date) val, gen);
        } else if (val.getClass().isEnum()) {
            gen.writeString(((Enum)val).name());
        } else if (val instanceof byte[]) {
            gen.writeBinary((byte[])val);
        } else if (val.getClass().isArray()) {
            gen.writeStartArray();
            for (Object obj : Arrays.asList((Object[])val)) {
                write(null, obj, isIgnoreNull(obj));
            }
            gen.writeEndArray();
        } else if (val instanceof List || val instanceof Set) {
            gen.writeStartArray();
            for (Object obj : (Iterable<Object>)val) {
                write(null, obj, isIgnoreNull(obj));
            }
            gen.writeEndArray();
        } else if (val instanceof Map) {
            gen.writeStartObject();
            for (Map.Entry<Object, Object> e : ((Map<Object, Object>)val).entrySet()) {
                write(e.getKey().toString(), e.getValue(), isIgnoreNull(e.getValue()));
            }
            gen.writeEndObject();
        }
        else {
            writeObject(val);
        }

        if (name != null) {
            nameStack.pop();
        }
    }

    private void writeObject(Object obj) throws IOException, InvocationTargetException, IllegalAccessException {
        gen.writeStartObject();

        boolean ignoreNull = isIgnoreNull(obj);
        List<Method> methodList = getPublicGetMethods(obj);

        for (Field field : getDeclaredFields(obj)) {
            String name = field.getName();
            Method method = getPublicGetMethod(methodList, name);
            if (method != null) {
                Object ret = method.invoke(obj, (Object[])null);
                write(name, ret, ignoreNull);
                methodList.remove(method);
            }
            else if (!isJsonIgnore(field)) {
                field.setAccessible(true);
                write(name, field.get(obj), ignoreNull);
            }
        }

        for (Method method : methodList) {
            String name = method.getName();
            String fieldName = name.substring(3, 4).toLowerCase() + (name.length() > 3? name.substring(4): "");
            Object ret = method.invoke(obj, (Object[])null);
            write(fieldName, ret, ignoreNull);
        }

        gen.writeEndObject();
    }

    private Field[] getDeclaredFields(Object obj) {
        ArrayList list = new ArrayList<Field>();
        Class cls = obj.getClass();
        while (cls != null && cls != Object.class) {
            list.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return (Field[]) list.toArray(new Field[]{});
    }

    /**
     * judge if field should be ignored.
     *
     * A field which has any of the following attributes should be ignored.
     * - JsonIgnore annotation
     * - transient modifier
     *
     * @param field
     * @return
     */
    private boolean isJsonIgnore(Field field) {
        return field.getAnnotation(JsonIgnore.class) != null
                || Modifier.isTransient(field.getModifiers());
    }

    /**
     * judge if the object class has JsonInclude.Include.NON_NULL annotation.
     *
     * @param obj
     * @return
     */
    private boolean isIgnoreNull(Object obj) {
        JsonInclude an = obj.getClass().getAnnotation(JsonInclude.class);
        return an != null && an.value().equals(JsonInclude.Include.NON_NULL);
    }

    private Method getPublicGetMethod(List<Method> methodList, String name) {
        for (Method method : methodList) {
            String getName = "get" + name.substring(0, 1).toUpperCase() + (name.length() > 1? name.substring(1): "");
            if (method.getName().equals(getName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * return public instance method whose name is get***
     *
     * @param obj
     * @return
     */
    private List<Method> getPublicGetMethods(Object obj) {
        List<Method> list = new ArrayList<Method>();
        Class cls = obj.getClass();
        while (cls != null && cls != Object.class) {
            for (Method method : cls.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && method.getName().startsWith("get")) {
                    list.add(method);
                }
            }
            cls = cls.getSuperclass();
        }
        return list;
    }

}

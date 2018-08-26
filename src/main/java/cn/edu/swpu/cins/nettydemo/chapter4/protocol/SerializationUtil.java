package cn.edu.swpu.cins.nettydemo.chapter4.protocol;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    public SerializationUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getScheme(clazz);
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            T message = objenesis.newInstance(clazz);
            Schema<T> schema = getScheme(clazz);
            ProtobufIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getScheme(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaMap.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            if (schema != null) {
                schemaMap.put(clazz, schema);
            }
        }
        return schema;
    }
}

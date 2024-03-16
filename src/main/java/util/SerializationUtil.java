package util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo 序列化工具类
 */
public class SerializationUtil {

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public static byte[] serialize(Object obj) {
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Output output = new Output(baos)) {
            kryo.writeObject(output, obj);
            output.flush();
            return baos.toByteArray();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readObject(input, clazz);
        }
    }
}

package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationUtilTest {

    @Test
    public void testSerializeAndDeserialize() {
        TestObject original = new TestObject(1, "test");
        byte[] serialized = SerializationUtil.serialize(original);
        TestObject deserialized = SerializationUtil.deserialize(serialized, TestObject.class);

        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getName(), deserialized.getName());
    }

    static class TestObject {
        private int id;
        private String name;

        // Kryo 需要一个无参构造函数
        public TestObject() {
        }

        public TestObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

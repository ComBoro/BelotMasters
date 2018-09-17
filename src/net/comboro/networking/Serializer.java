package net.comboro.networking;

import java.io.*;

public class Serializer {

    public static byte[] serialize(SerializableMessage<?> message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            if (message instanceof TaggedMessage) {
                TaggedMessage<?> tagged = (TaggedMessage<?>) message;
                objectOutputStream.writeObject(tagged.getData());
                objectOutputStream.writeObject(tagged.getTags());
            } else {
                objectOutputStream.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] arr = byteArrayOutputStream.toByteArray();

        try {
            objectOutputStream.close();
        } catch (IOException | NullPointerException io) {

        }
        byteArrayOutputStream.reset();
        return arr;
    }

    public static SerializableMessage<?> deserialize(byte[] data) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            SerializableMessage<?> message = (SerializableMessage<?>) objectInputStream.readObject();
            if (message instanceof TaggedMessage) {
                String[] tags = (String[]) objectInputStream.readObject();
                return new TaggedMessage<>(message, tags);
            }
            return message;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}

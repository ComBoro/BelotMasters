package net.comboro.networking;

import java.io.Serializable;

public class SerializableMessage<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1623756670044688095L;
    protected T data;

    public SerializableMessage() {
        this.data = null;
    }

    public SerializableMessage(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

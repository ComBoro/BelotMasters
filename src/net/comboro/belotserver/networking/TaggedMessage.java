package net.comboro.belotserver.networking;

import java.io.Serializable;

public class TaggedMessage<D extends Serializable> extends SerializableMessage<D> {
    private static final long serialVersionUID = -3207683131463079673L;

    private final String[] tags;

    public TaggedMessage(SerializableMessage<D> message, String... tags) {
        this(message.getData());
    }

    public TaggedMessage(D message, String... tags) {
        super(message);
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }
}

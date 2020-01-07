package sx.shirogane;

import org.bson.types.ObjectId;

public final class Player {
    private ObjectId id;
    private String name;
    private String ref;

    public Player() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}


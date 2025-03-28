package Domain;

import java.io.Serializable;

/**
 * Abstract base class for all domain entities
 */
public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    public Entity() {
        this(0);
    }

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity entity = (Entity) obj;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Entity{" + "id=" + id + '}';
    }
}

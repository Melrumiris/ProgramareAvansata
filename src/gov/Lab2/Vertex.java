package gov.Lab2;

/**
 * A generic graph vertex that wraps a data element.
 * <p>
 * Identity is based on object reference equality of the wrapped data,
 * which keeps vertices distinct even when two data objects are logically equal.
 * </p>
 *
 * @param <T> the type of the wrapped data element
 */
public class Vertex<T> {
    private T data;

    /**
     * Constructs a new Vertex wrapping the given data.
     *
     * @param data the data element to wrap; must not be {@code null}
     */
    public Vertex(T data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the wrapped data.
     *
     * @return string representation of the data
     */
    @Override
    public String toString() {
        return data.toString();
    }

    /**
     * Two vertices are equal if and only if their data fields are the same object reference.
     *
     * @param o the object to compare with
     * @return {@code true} if both vertices wrap the same data instance
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return this.data == ((Vertex<?>) o).data;
    }

    /**
     * Returns a hash code based on the identity of the wrapped data object.
     *
     * @return identity-based hash code
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(data);
    }

    /**
     * Returns the data element wrapped by this vertex.
     *
     * @return the wrapped data
     */
    public T getData() {   return data;    }

    /**
     * Replaces the data element wrapped by this vertex.
     *
     * @param data the new data element
     * @return this vertex instance for method chaining
     */
    public Vertex<T> setData(T data) {
        this.data = data;
        return this;
    }
}

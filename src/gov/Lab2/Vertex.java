package gov.Lab2;


public class Vertex<T> {
    private T data;
    public Vertex(T data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return data.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.data == ((Vertex<?>) o).data;
    }

     public T getData() {   return data;    }
     public Vertex<T> setData(T data) {
         this.data = data;
         return this;
     }
}

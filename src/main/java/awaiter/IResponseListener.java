package awaiter;

public interface IResponseListener<T> {
    void register(T state);
    T getState(String identifier);
}

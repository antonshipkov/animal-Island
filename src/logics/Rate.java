package logics;

public class Rate <T extends Comparable<T>> {

    private T current;
    private final T min;
    private final T max;

    public Rate (T current ,T min ,T max) {
        this.current = current;
        this.min = min;
        this.max = max;
    }

    public T getCurrent ( ) {
        return current;
    }

    public void setCurrent (T current) {
        if (min.compareTo ( current ) > 0) {
            current = min;
        } else if (max.compareTo ( current ) < 0) {
            current = max;
        }
        this.current = current;
    }

    public T getMin ( ) {
        return min;
    }

    public T getMax ( ) {
        return max;
    }

    public boolean atMax ( ) {
        return getCurrent ( ).compareTo ( getMax ( ) ) == 0;
    }

    public boolean atMin ( ) {
        return getCurrent ( ).compareTo ( getMin ( ) ) == 0;
    }
}

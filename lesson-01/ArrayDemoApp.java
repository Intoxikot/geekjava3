
class Array<T> {
    T[] data;

    Array(T[] data) {
        this.data = data;
    }
}

public class ArrayDemoApp {
    public static void main(String[] args) {
        Array<Integer> box = new Array<>(new Integer[]{1,2,3,4,5});
    }
}
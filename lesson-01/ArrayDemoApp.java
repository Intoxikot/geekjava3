
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Array<T> {
    T[] data;

    Array(T[] data) {
        this.data = data;
    }

    // Метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);
    void replace(int first, int second) {
        T temp = data[first];
        data[first] = data[second];
        data[second] = temp;
    }

    // Метод, который преобразует массив в ArrayList;
    List<T> getArrayList() {
        return new ArrayList<T>(Arrays.asList(data));
    }

    void show() {
        System.out.print("array: ");
        for (int i = 0; i < data.length; i++)
            System.out.print(data[i] + " ");
        System.out.println();
    }

}

public class ArrayDemoApp {
    public static void main(String[] args) {
        // Проверим замену двух элементов массива по индексу
        Array<Integer> box = new Array<>(new Integer[]{1,2,3,4,5});
        box.show();
        box.replace(2,4);
        box.show();

        // Проверим преобразование массива к списку
        List<Integer> list = box.getArrayList();
        list.forEach(System.out::println);
    }
}
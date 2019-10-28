import java.util.Arrays;

// Решение задачи вручную без использования автоматических тестов
public class ArraysAlgs {

    public static final int SEARCH_VALUE = 4;

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1}))); // expected: [1]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1, 6}))); // expected: [1, 6]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1, 6, 7}))); // expected: [1, 6, 7]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{4, 4, 4, 4}))); // expected: empty []
        // System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3}))); // exception
    }

    // Вспомогательный метод, инкапсулирующий требования задачи
    public static int[] getArrayByRule(int[] input) {
        // по условию задачи требуется поиск числа 4, однако задача должна решаться универсально:
        // число вынесено в константы и использован промежуточный метод
        return getArrayAfterLastInclusion(input, SEARCH_VALUE);
    }

    // Преобразует массив по условию задачи к требуемому виду - возващает часть массива по правилу:
    // Ищет последнее вхождение числа и возвращает часть массива после этого числа
    private static int[] getArrayAfterLastInclusion(int[] input, int value) {
        int begin = getIndexOfLastValueInclusion(input, value) + 1; // прибавляется единица т.к. само значение не входит в реузльтирующий массив
        int last = input.length;
        return Arrays.copyOfRange(input, begin, last);
    }

    // Вспомогательный метод: возвращает индекс последнего включения числа в массив
    // т.е. проходит по массиву до конца, ищет совпадения и сохраняет индекс
    private static int getIndexOfLastValueInclusion(int[] input, int value) {
        int lastIndex = -1; // -1 сообщает о том, что совпадения не найдены
        for (int i = 0; i < input.length; i++)
            if (input[i] == value) lastIndex = i; // старое значение перезаписывается т.о. получаем индекс последнего включения
        if (lastIndex == -1) throw new RuntimeException(); // в нашем случае, ожидается, что значение должно присутствовать, иначе имеем исключение
        return lastIndex;
    }
}
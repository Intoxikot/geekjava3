import java.util.Arrays;

// Решение задачи вручную без использования автоматических тестов
public class ArraysAlgs {

    public static final int SEARCH_VALUE = 4;
    public static final int[] RESTRICTION_VALUES = new int[]{1,4};

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1}))); // expected: [1]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1, 6}))); // expected: [1, 6]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3, 4, 5, 4, 1, 6, 7}))); // expected: [1, 6, 7]
        System.out.println(Arrays.toString(getArrayByRule(new int[]{4, 4, 4, 4}))); // expected: empty []
        // System.out.println(Arrays.toString(getArrayByRule(new int[]{1, 2, 3}))); // exception

        System.out.println(haveInclusionByRule(new int[]{1,4,4,4})); // true
        System.out.println(haveInclusionByRule(new int[]{1,4,1,4})); // true
        System.out.println(haveInclusionByRule(new int[]{1,4,1,4,3})); // false
        System.out.println(haveInclusionByRule(new int[]{1,1,1,1,1})); // false
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

    // Проверяет соответствие массива условию задачи
    public static boolean haveInclusionByRule(int[] input) {
        return isStrictInclusion(input, RESTRICTION_VALUES); // ограничение можно поменять на любой другой массив/числа
    }

    // Аналогичным образом - вспомогательный универсальный метод проверяет, что в input содержаться все значения из restriction и никаких, кроме них
    private static boolean isStrictInclusion(int[] input, int[] restriction) {
        // фишка метода в том, что сперва первый массив проходит по второму, а потом наоборот - и обоих случаях они должны содержать значения друг друга
        // нет смысла дублировать двойной цикл дважды - достаточно переопределить его в другом методе и вызвать с противоположными параметрами
        boolean res1 = haveInclusion(input, restriction); // все значения input должны содержаться в restriction
        if (res1 == false) return false;
        boolean res2 = haveInclusion(restriction, input); // верно и обратное - все значения restriction должны присутствовать в input
        if (res2 == false) return false;
        return true; // все условия пройдены - успех
    }

    private static boolean haveInclusion(int[] first, int[] second) {
        for (int f = 0; f < first.length; f++) {
            boolean newValue = true; // флаг нового значения
            for (int s = 0; s < second.length; s++) {
                if (first[f] == second[s]) {
                    newValue = false;
                    break; // данное значение корректно, переходим к следующему
                }
            }
            if (newValue == true) return false;
        }
        return true;
    }
}
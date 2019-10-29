
package testingtools;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JavaTester {

    public static void start(Class inputClass) throws RuntimeException {
        Object obj = getInstance(inputClass); // без экземпляра класса не сможем вызвать его методы (поэтому конструктор должен существовать)

        // Делим методы по их типу исполнения (до, после, сами тесты), такой подход имеет один существенный минус - приходится несколько раз считывать все методы класса и их аннотации
        List<Method> beforeSuite = getMethodsByAnnotation(obj, BeforeSuite.class.getSimpleName());
        List<Method> tests = getMethodsByAnnotation(obj, Test.class.getSimpleName());
        List<Method> afterSuite = getMethodsByAnnotation(obj, AfterSuite.class.getSimpleName());

        // Проверка корректности аннотаций в тестировочном классе
        // Если класс имеет более одной реализации @BeforeSuite или @AfterSuite в методах, значит имеем необработанный случай
        if (beforeSuite.size() > 1 || afterSuite.size() > 1)
            new RuntimeException("testing class have ambiguous suite-methods");

        // Запуск инициализатора в классе-тесте (подготовка к работе)
        if (beforeSuite.size() != 0)
            runMethod(obj, beforeSuite.get(0));

        // Запуск и прогон тестов в классе (предварительно тесты сортируются в порядке приоритета)
        runTests(obj, sortingByPriority(tests));

        // Запуск финализатора в классе-тесте (завершение работы)
        if (afterSuite.size() != 0)
            runMethod(obj, afterSuite.get(0));
    }

    private static List<Method> getMethodsByAnnotation(Object obj, String annotationName) {
        List<Method> result = new ArrayList<>();
        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            Annotation[] annotations = m.getAnnotations();
            for (Annotation a : annotations)
                if (a.annotationType().getSimpleName().equals(annotationName))
                    result.add(m);
        }
        return result;
    }

    private static void runMethod(Object o, Method m) {
        try {
            m.invoke(o);
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void runTests(Object o, List<Method> methods) {
        try {
            //System.out.println("launch tests for " + o.getClass().getSimpleName());
            int i = 0;
            SimpleTimer timer = new SimpleTimer();
            for (Method m : methods) {
                i++;
                timer.start();
                Boolean success = (Boolean) m.invoke(o); // тесты должны иметь возвращаемое значение флаг успех/провал, иначе тест по-умолчанию будет считаться успешным
                timer.stop();
                CompletedTest currentTest = new CompletedTest(i, m.getName(), timer.runtime(), isNull(success));
                System.out.print("class:" + o.getClass().getSimpleName() + ". " + currentTest); // вывод имени класса и результата теста
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("error: " + e.getMessage());
        } /* catch (NullPointerException e) { // когда в методе теста нет возвращаемого значения - этот случай уже обработан функцией isNull
            System.out.println("error: return type of test-method is void (must be boolean)");
        } */
    }

    private static Object getInstance(Class inputClass) {
        try {
            Constructor[] constructors = inputClass.getConstructors();
            if (constructors.length == 0) throw new RuntimeException("test-class must have empty constructor"); // в тестировочном класс должен быть конструктор (иначе мы не сможем создать экземпляр классса)
            return constructors[0].newInstance(null);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            System.out.println("error: " + e.getMessage());
        }
        return null;
    }

    // Сортирует методы-тесты в порядке приоритета (0 - нименьший, 10 - наибольший)
    private static List<Method> sortingByPriority(List<Method> methods) {
        methods.sort(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) { // ну здесь все понятно

                int p1 = o1.getAnnotation(Test.class).priority();
                int p2 = o2.getAnnotation(Test.class).priority();

                if (p1 == p2) return 0;
                if (p1 < p2) return 1; else return -1;
            }
        });
        return methods;
    }

    private static boolean isNull(Boolean flag) {
        if (flag == null) return true; else return flag;
    }
}
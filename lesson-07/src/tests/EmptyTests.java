
package tests;

import testingtools.*;

// Пробный тестовый класс
public class EmptyTests {

    // Пустой конструктор позволяет инициализировать экземпляр класса в тестере (обязателен)
    public EmptyTests() {}

    @BeforeSuite // Имитируем подготовку к тестам
    public void initialize() {
        System.out.println("initialize data before tests");
    }

    @AfterSuite // Имитируем завершение работы
    public void finalize() {
        System.out.println("finalize data after tests");
    }

    // Набор некоторых тестов, которые выполняют какую-нибудь полезную нагрузку (ну или не выполняют - здесь это не важно)
    @Test(priority = 6)
    public boolean mediumTest() {
        System.out.println("medium test was launched");
        for (int i = 0; i < 1000000; i++);
        return true;
    }

    @Test(priority = 4)
    public boolean softTest() {
        System.out.println("soft test was launched");
        return true;
    }

    @Test(priority = 8)
    public void hardTest() { // не возвращает флага завершения, считаем, что он был завершен успешно, если был завершен вообще (если никаких ошибок не вылетело)
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) { }
        System.out.println("hard test was launched");
    }
}
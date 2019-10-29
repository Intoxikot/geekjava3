package main;

import testingtools.JavaTester;
import tests.EmptyTests;
import tests.MeowTests;

// Точка входа в приложение - отсюда будут запускаться все тесты
public class App {
    public static void main(String[] args) {
        JavaTester.start(EmptyTests.class);
        JavaTester.start(MeowTests.class);
    }
}

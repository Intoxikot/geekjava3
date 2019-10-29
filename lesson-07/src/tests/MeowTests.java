
package tests;

import testingtools.*;
import java.awt.*;
import main.Cat;

public class MeowTests {

    public MeowTests() { }

    private Cat testerCat; // кот-испытатель

    @BeforeSuite
    public void initialize() {
        testerCat = new Cat("Puhok", Color.WHITE, 1);
    }

    @AfterSuite
    public void finalize() {
        testerCat = null; // утилизация кошачих отходов
    }

    @Test(priority = 2)
    public void speedTest() {
        testerCat.run();
        testerCat.run();
        testerCat.run();
    }

    @Test(priority = 6)
    public void meowTest() {
        while (testerCat.sayMeow()) { } // будет мяукать до посинения, пока не устанет
    }

    @Test(priority = 8)
    public void fightTest() {
        Cat stranger = new Cat( "Barsik", Color.BLACK, 3);

        // Обмен любезностями
        stranger.sayMeow();
        testerCat.sayMeow();

        // Возникла конфликтная ситуация
        testerCat.Attack(stranger);
    }

    @Test(priority = 4)
    public void lifeCycleTest() {
        testerCat.sleep();
        testerCat.run();
        testerCat.eat();
        testerCat.sayMeow();
    }

    @Test(priority = 2)
    public void huntTest() {
        testerCat.run();
        testerCat.eat();
    }
}
package main;

import java.awt.*;

// Некоторый класс, который необходимо будет протестировать - будем теститровать котов
// Чтобы было интереснее: я описал простую "шуточную" внутреннюю логику кота и его возможные действия: жрать, спать и напасть на другого кота, например
public class Cat {
    private String name;
    private Color color;
    private int age;

    private int power = 100;
    private static final boolean showMessages = false; // на случай если мы не хотим выводить сообщений и читать новости о событиях кошачьей жизни

    public Cat(String name, Color color, int age) {
        this.name = name;
        this.color = color;
        this.age = age;
    }

    // Основное кошачье ремесло
    public boolean sleep() {
        boolean success = addPower(15);
        if (showMessages) System.out.println("Cat " + name + "is sleeping (+15e) - energy is " + power);
        return success;
    }

    // Охота, поиск и уничтожение еды
    public boolean eat() {
        boolean success = addPower(25);
        if (showMessages) System.out.println("Cat " + name + " is eating (+25e) - energy is " + power);
        return success;
    }

    // Прежде чем, продолжить спать - энергию нужно куда-то израсходовать
    public boolean run() {
        boolean success = usePower(10);
        if (showMessages) System.out.println("Cat " + name + " is running (-10e) - energy is " + power);
        return success;
    }

    // Базовый кошачий функционал
    public boolean sayMeow() {
        boolean success = usePower(1);
        if (showMessages) System.out.println("Cat " + name + " saying meow (-1e) - energy is " + power);
        return success;
    }

    public boolean Attack(Cat enemy) {
        if (showMessages) System.out.println("Cat " + this.name + " is attacking other cat " + enemy.name);

        // Бой весьма энергоемкая операция - отнимаем очки
        this.usePower(15);
        enemy.usePower(15);

        boolean win = (this.power >= enemy.power); // если запас сил равен, выигрывает нападавший
        if (win)
            results(this, enemy);
        else
            results(enemy, this);

        return win;
    }

    // Подвести итоги боя
    private static void results(Cat winner, Cat loser) {
        if (showMessages) System.out.println("Cat " + winner.name + " is winning " + loser.name);
        winner.sayMeow(); // победитель издает победоносный клич
        loser.run(); // проигравший убегает в страхе
    }

    // Добавить запас энергии, возвращает успех
    private boolean addPower(int energy) {
        if (isPowerFull())
            return false;
        power += energy;
        return true;
    }

    // Использовать энергию, возвращает успех
    private boolean usePower(int energy) {
        if (isPowerLow())
            return false;
        power -= energy;
        return true;
    }

    private boolean isPowerFull() {
        return (power >= 100);
    }

    private boolean isPowerLow() {
        return (power <= 0);
    }
}
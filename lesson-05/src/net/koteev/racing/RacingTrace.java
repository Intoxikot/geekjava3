package net.koteev.racing;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class RacingTrace {

    public static final int CARS_COUNT = 4;

    // На каждое событие гонки я добавил свой счетчик (внешне это очень похоже на очередь - полагаю, это упрощается объектами Concurrent Collections)
    public static CountDownLatch cdlPrepared = new CountDownLatch(CARS_COUNT);
    public static CountDownLatch cdlReady = new CountDownLatch(CARS_COUNT);
    public static CountDownLatch cdlFinish = new CountDownLatch(CARS_COUNT);
    public static CyclicBarrier cbBegin = new CyclicBarrier(CARS_COUNT); // обозначает точку синхронизации участников: начало гонки

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        try {
            cdlPrepared.await(); // ожидание подготовки
            cdlReady.await(); // ожидание готовности
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            cdlFinish.await(); // ждем, пока не финишируют все участники
        } catch(Exception e) {
            System.out.println(e.getMessage()); // весь стек-трейс выводить не будем, только сообщение
        } finally { // Гонка должна быть завершена в любом случае
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        }
    }
}
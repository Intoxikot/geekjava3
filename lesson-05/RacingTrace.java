
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

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

class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;
    private boolean winner; // флаг победителя (можно обойтись и без него, но он немного улучшает читаемость)

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            RacingTrace.cdlPrepared.countDown(); // участник гонки начал приготовления. уменьшить счетчик оставшихся машин
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            RacingTrace.cdlReady.countDown(); // участник готов. уменьшить счетчик машин, не готовых к старту
            RacingTrace.cbBegin.await(); // точка синхронизации. ждет, пока все остальные участники завершат приготовления к началу гонки
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        this.checkWin(); // проверить финишировал ли текущий участник первым
        if (this.isWinner()) System.out.println(name + " WIN"); // можно обойтись и без метода: if (winner)
        RacingTrace.cdlFinish.countDown();
    }

    private void checkWin() {
        // Перевод на более доступный/понятный язык: если число нефинишировавшиых участников равно количеству участников - значит у нас есть победитель
        if (RacingTrace.cdlFinish.getCount() == RacingTrace.CARS_COUNT) winner = true;
        // P.S. Данный метод можно реализовать в RacingTrace т.к. он целиком ссылается туда, но потребуется добавить еще несколько методов/переменных
    }

    public boolean isWinner() {
        return this.winner;
    }
}

abstract class Stage {
    protected int length;
    protected String description;
    public String getDescription() {
        return description;
    }
    public abstract void go(Car c);
}

class Road extends Stage {
    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }
    @Override
    public void go(Car c) {
        try {
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000);
            System.out.println(c.getName() + " закончил этап: " + description);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Tunnel extends Stage {

    // семафор-ограничитель перед точкой входа в туннель, в туннеле может находиться не более половины участников гонки одновременно
    public static Semaphore smpTunnelJoin = new Semaphore(RacingTrace.CARS_COUNT/2, true); // здесь я не совсем понимаю, следует ли использовать true или нет

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }
    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                Thread.sleep(100); // пусть вход в туннель тоже занимает некоторое время
                smpTunnelJoin.acquire(); // получить разрешение у семафора на вход в туннель
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                smpTunnelJoin.release(); // завершить работу с семафором - высвобождает занятые ресурсы, иначе выполнение программы остановится
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Race {
    private ArrayList<Stage> stages;
    public ArrayList<Stage> getStages() { return stages; }
    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
    }
}
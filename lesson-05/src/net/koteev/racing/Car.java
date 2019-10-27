package net.koteev.racing;

public class Car implements Runnable {
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
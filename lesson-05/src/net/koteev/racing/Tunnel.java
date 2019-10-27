package net.koteev.racing;

import java.util.concurrent.Semaphore;

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
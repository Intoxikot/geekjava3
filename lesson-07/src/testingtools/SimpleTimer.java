
package testingtools;

// Замер времени выделил в отдельный класс таймера:
// во-первых, он крайне прост и не надо подключать никакие инструменты
// во-вторых, код с замером времени выглядит значительно лучше
public class SimpleTimer {
    private long begin = 0;
    private long finish = 0;

    public SimpleTimer() { }
    public void start() {
        begin = System.currentTimeMillis();
    }
    public void stop() {
        finish = System.currentTimeMillis();
    }
    public long runtime() {
        return finish - begin;
    }
}

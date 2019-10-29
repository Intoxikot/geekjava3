
package testingtools;

// Простая реализация выполненного теста: имя теста, номер теста, время и успех выполнения
// Смысл класса прост: позволяет выводить на экран информацию в удобном виде
public class CompletedTest {
    private int id;
    private String name;
    private long time;
    private boolean success;

    private static final String SUCCESS = "successful";
    private static final String FAIL = "failed";

    public CompletedTest(int id, String name, long time, boolean success) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.success = success;
    }

    private static String getStatus(boolean success) {
        if (success) return SUCCESS; else return FAIL;
    }

    @Override
    public String toString() {
        return "test №" + id + " (" + name + ")"  + " is " + getStatus(success) + " in " + time + " ms\n";
    }
}

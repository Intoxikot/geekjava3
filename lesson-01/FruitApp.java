
import java.util.ArrayList;
import java.util.List;

class Fruit {
    int weight;
    Fruit(int weight) { this.weight = weight; }
    int getWeight() { return weight; }
}

class Orange extends Fruit {
    Orange(int weight) { super(weight); }
}

class Apple extends Fruit {
    Apple(int weight) { super(weight); }
}

class FruitBox<T extends Fruit> {
    List<T> fruits;

    FruitBox() {
        fruits = new ArrayList<>();
    }

    // Посчитать вес коробки
    int getWeight() {
        int sum = 0;
        for (Fruit f : fruits)
            sum += f.getWeight();
        return sum;
    }

    // Сравнить коробки по весу
    boolean compare(FruitBox<?> other) {
        return this.getWeight() == other.getWeight();
    }

    // Добавить фрукт в коробку
    void add(T item) {
        fruits.add(item);
        String fruitName = item.getClass().getName();
        System.out.println(fruitName + " (" + item.getWeight() + "g) was added to box #" + this.hashCode()); // будем уведомлять пользователя о всех наших действиях
    }

    // Пересыпать фрукты в другую коробку
    void throwTo(FruitBox<T> box) {
        for (Fruit f: fruits)
            box.add((T) f);
        fruits.clear();
        System.out.println("all fruits from box #" + hashCode() + " was moved to box #" + box.hashCode());
    }

    // Показать содержимое коробки с фруктами
    void show() {
        if (fruits.isEmpty())
            System.out.println("< empty fruit box #" + hashCode() + " >");
        else {
            String type = fruits.get(0).getClass().getName();
            System.out.print("< box #"  + hashCode() + " with " + type + "s contains fruits : ");
            for (Fruit f: fruits)
                System.out.print(f.weight + "g ");
            System.out.println(">");
        }
    }

}

public class FruitApp {
    public static void main(String[] args) {
        // Создать две коробки и сравнить их между собой
        FruitBox<Apple> appleBox = new FruitBox<>();
        appleBox.add(new Apple(140));
        appleBox.add(new Apple(160));
        appleBox.add(new Apple(175));
        appleBox.show();
        System.out.println("total: " + appleBox.getWeight());
        System.out.println();

        FruitBox<Orange> orangeBox = new FruitBox<>();
        orangeBox.add(new Orange(180));
        orangeBox.add(new Orange(190));
        orangeBox.add(new Orange(215));
        orangeBox.show();
        System.out.println("total: " + orangeBox.getWeight());
        System.out.println();

        System.out.println("boxes are equal by weight: " + orangeBox.compare(appleBox));

        // Добавим еще что-нибудь
        appleBox.add(new Apple(200));
        System.out.println();

        // Попробуем пересыпать фрукты из одной коробки в другую
        System.out.println("\t" + "before:");
        System.out.print("source box: "); appleBox.show();

        FruitBox<Apple> appleBox2 = new FruitBox<>();
        System.out.print("target box: "); appleBox2.show();
        System.out.println();

        appleBox.throwTo(appleBox2);
        System.out.println();

        System.out.println("\t" + "result:");
        System.out.print("source box: "); appleBox.show();
        System.out.print("target box: "); appleBox2.show();
    }
}
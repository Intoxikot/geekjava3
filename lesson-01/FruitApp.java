
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
}

public class FruitApp {
    public static void main(String[] args) {
        FruitBox<Apple> appleBox = new FruitBox<>();
	}
}

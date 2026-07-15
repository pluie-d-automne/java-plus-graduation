import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Dog implements Comparable<Dog>{

    private final String nickname;

    public Dog(String nickname){
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return nickname;
    }

    /* Вам предстоит реализовать метод compareTo и, возможно, equals —
    подумайте, какая между ними связь. */

    @Override
    public int compareTo(Dog anotherDog) {
        return this.nickname.compareTo(anotherDog.nickname);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Dog dog = (Dog) obj;
        return dog.nickname.equals(this.nickname);

    }
}

public class Practicum {

    public static void main(String[] args) {
        List<Dog> unsortedDogs = Stream.of(
                        "Дружок", "Пушок", "Тузик", "Ромео",
                        "Белка", "Стрелка", "Бобик", "Афоня",
                        "Волчок")
                .map(Dog::new)
                .collect(Collectors.toList());

        // найдите Белку
        Dog dog = new Dog("Белка");
        String result = search(unsortedDogs, dog)
                .map(d -> "А вот и собака по кличке " + d + " нашлась")
                .orElseGet(() -> "Нет собаки по кличке " + dog + " :(");

        System.out.println(result);
    }

    private static <T extends Comparable<T>> Optional<T> search(List<T> unsortedList, T searchObject) {
        List<T> sortedList = mergeSortDescending(unsortedList); // искать легче по упорядоченному списку — вам поможет алгоритм сортировки
        int idx = searchBinaryAscending(sortedList, searchObject);
        // найдите Белку, учтите, что idx может быть -1, если ничего не нашлось.
        return Optional.of(sortedList.get(idx));
    }

    // методы сортировки и поиска

    private static  <T extends Comparable<T>> List<T> mergeSortDescending(List<T> unsortedList) {
        if (unsortedList.size() <= 1) { // базовый случай рекурсии
            return unsortedList;
        }

        // заводим массив для результата сортировки
        List<T> ret = new ArrayList<>();

        // запускаем сортировку рекурсивно на левой половине
        List<T> left = mergeSortDescending(unsortedList.subList(0, unsortedList.size() / 2));

        // запускаем сортировку рекурсивно на правой половине
        List<T> right = mergeSortDescending(unsortedList.subList( unsortedList.size() / 2, unsortedList.size()));

        int leftIdx = 0;
        int rightIdx = 0;
        int retIdx= 0;

        // сливаем результаты
        while (leftIdx < left.size() && rightIdx < right.size()){
            /* Выбираем, из какого массива забрать минимальный элемент,
            тем самым сортируем значения в итоговом массиве. */
            if(left.get(leftIdx).compareTo(right.get(rightIdx)) >= 0){
                ret.set(retIdx, left.get(leftIdx));
                leftIdx++;
            } else {
                ret.set(retIdx, right.get(rightIdx));
                rightIdx++;
            }
            retIdx++;
        }

        /* Если один массив закончился раньше, чем второй, то
        переносим оставшиеся элементы второго массива в результирующий,
        иначе просто не заходим в цикл. */
        while (leftIdx < left.size()){
            ret.set(retIdx, left.get(leftIdx));
            leftIdx++;
            retIdx++;
        }
        // то же для правой части
        while (rightIdx < right.size()){
            ret.set(retIdx, right.get(rightIdx));
            rightIdx++;
            retIdx++;
        }

        return ret;
    }

    private static <T extends Comparable<T>>  int searchBinaryAscending(List<T> array, T elem) {
        // изначально мы запускаем двоичный поиск на всей длине массива
        return searchBinaryRecursiveAscending(array, elem, 0, array.size() - 1);
    }

    private static <T extends Comparable<T>>  int searchBinaryRecursiveAscending(List<T> array, T elem, int low, int high) {
        if(low > high) { // промежуток пуст
            return -1;
        }
        // промежуток не пуст
        int mid = low  + ((high - low) / 2);
        if (array.get(mid).equals(elem)) { // центральный элемент — искомый
            return mid;
        } else if(elem.compareTo(array.get(mid)) > 0) { // на этот раз искомый элемент больше центрального
            // все элементы больше центрального и располагаются в левой половине
            return searchBinaryRecursiveAscending(array, elem, mid + 1, high);
        } else { // иначе следует искать в правой половине
            return searchBinaryRecursiveAscending(array, elem, low, mid);
        }
    }
}

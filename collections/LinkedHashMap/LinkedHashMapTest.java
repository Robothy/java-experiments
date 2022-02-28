import java.util.LinkedHashMap;
import java.util.function.Function;

public class LinkedHashMapTest {
  public static void main(String[] args) {
    LinkedHashMap<Function<String, String>, String> map = new LinkedHashMap<>();
    map.put(LinkedHashMapTest::fun, "Hello");
    map.put(LinkedHashMapTest::fun, "World");
    System.out.println(map.size());
    System.out.println(map);
  }

  static String fun(String str) {
    return str;
  }

}

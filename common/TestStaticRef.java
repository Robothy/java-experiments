public class TestStaticRef {

  public static void main(String[] args) {
    TestStaticRef local = TestStaticRefA.field;
    System.out.println(local);
    TestStaticRefA.field = new TestStaticRef();
    local = TestStaticRefA.field;
    System.out.println(local);
    TestStaticRefA.field = new TestStaticRef();
    System.out.println(local);
  }

}



class TestStaticRefA {
  static TestStaticRef field;
}

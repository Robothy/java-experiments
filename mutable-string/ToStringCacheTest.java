import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ToStringCacheTest {

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<String> constructor = String.class.getDeclaredConstructor(char[].class, boolean.class);
        constructor.setAccessible(true);

        char[] value = new char[]{'R', 'o', 'b', 'o', 't', 'h', 'y'};

        String str = constructor.newInstance(value, true);

        System.out.println(str);
        value[0] = 'A';
        System.out.println(str);
    }

}

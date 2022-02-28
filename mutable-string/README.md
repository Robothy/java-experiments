# 可变字符串

环境： `JDK 1.8`

Java 中定义构造出来的 String 对象是不可变的常量。本实验通过发射机制，构造一个可变的字符串对象。

```
Constructor<String> constructor = String.class.getDeclaredConstructor(char[].class, boolean.class);
constructor.setAccessible(true);

char[] value = new char[]{'R', 'o', 'b', 'o', 't', 'h', 'y'};

String str = constructor.newInstance(value, true);

System.out.println(str);
value[0] = 'A';
System.out.println(str);
```

```
$ javac ToStringCacheTest.java
$ java ToStringCacheTest
Robothy
Aobothy
```
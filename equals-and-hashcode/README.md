# equals() 与 hashCode()


        System.out.println(set.size()); // 输出 2，期望值应该为 1。 程序出现 BUG。
    }
}
```

类似地，如果重写 hashCode 方法时使用的属性不是在 equals() 中用到的属性的子集，则 equals() 判断相等的两个对象也会出现 hashCode() 不等的情况。

```java
import java.lang.String;
import java.util.Objects;

class User{
    String id;
    String name;
    
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || obj.getClass() != getClass()) return false;
        User o = (User)obj;
        return Objects.equals(id, o.id);
    }
    
    public int hashCode(){
        return Objects.hash(id, name);
    }
    
    public User(String id, String name){
        this.id = id;
        this.name = name;
    }
    
    public static void main(String[] args){
        User a = new User("1", "Robothy");
	User b = new User("1", "Luo");
	System.out.println(a.equals(b)); // 输出 true
	System.out.println(a.hashCode() == b.hashCode()); /// 输出 false，不符合 hashCode 第 2 点约定
    }
}
```


**原则二** equals() / hashCode() 中应该使用能够标识对象的属性

所谓标识属性，即能够用来判断对象相等的属性，这些属性应该不经常发生变化。例如实体类中，有些字段诸如 updateTime，createTime 这些经常变化，且判断两个对象是否相等不需要用到它们，则不应该在重写 equals() 和 hashCode() 时应用它们。如果应用了这些可变字段，则可能导致同一个元素在哈希表中存放多次。

如下示例代码，使用 id 和 name 是两个标识属性，visitedTime 是一个经常变化，不用于识别一个 User。代码输出 1 的根本原因是两次 add 的时候，计算出来的哈希值不同，导致对象引用放到了不同的哈希桶中，使得同一个元素在 Set 中存放了两次。
```java
import java.util.HashSet;
import java.util.Date;
import java.lang.String;
import java.util.Objects;

class User{
    String id;
    String name;
    Date visitedTime;
    
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || obj.getClass() != getClass()) return false;
        User o = (User)obj;
        return Objects.equals(id, o.id) && Objects.equals(name, o.name) && Objects.equals(visitedTime, o.visitedTime);
    }
    
    public int hashCode(){
        return Objects.hash(id, name, visitedTime);
    }
    
    public User(String id, String name){
        this.id = id;
        this.name = name;
    }
    
    public static void main(String[] args){
        HashSet<User> set = new HashSet<>();
        User user = new User("1", "Robothy");
        set.add(user);
        user.visitedTime = new Date();
        set.add(user);
        System.out.println(set.size()); // 输出 2，期望 1，程序出现 BUG
    }
}
```

## 小结

equals() 和 hashCode() 在 Object 类中是两个本地方法，其实现跟虚拟机有关。自定义类未覆盖这两个方法的情况下，只有当两个对象引用指向同一个对象时，使用 equals() 比较这两个引用才返回 true；hashCode() 方法是将对象的地址转化为 32 位的整数，一般情况下不同的对象 hashCode() 返回的值是不同的。

Java 中还说明了 equals() 和 hashCode() 的一些性质和约定，开发人员在覆盖这两个方法时一定要符合这些约定，否则在使用 JDK 中的一些数据结构时会出现 BUG，典型的数据结构是哈希表，例如：HashMap, HashSet。最后总结了两条简单原则，并提供了一个正确覆盖这两个方法的代码模板。
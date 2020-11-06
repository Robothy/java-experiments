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
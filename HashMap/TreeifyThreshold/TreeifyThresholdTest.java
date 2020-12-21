import java.util.*;
import java.lang.reflect.*;

class TreeifyThresholdTest{
	
	public static void main(String[] args) throws Exception{
		HashMap<KeyNode, Integer> map = new HashMap<>(128);
		map.put(new KeyNode(), 1); // 1
		map.put(new KeyNode(), 1); // 2
		map.put(new KeyNode(), 1); // 3
		map.put(new KeyNode(), 1); // 4
		map.put(new KeyNode(), 1); // 5
		map.put(new KeyNode(), 1); // 6
		map.put(new KeyNode(), 1); // 7
		
		Field field = HashMap.class.getDeclaredField("table");
		field.setAccessible(true);
		
		Map.Entry<KeyNode, Integer>[] table = (Map.Entry<KeyNode, Integer>[])field.get(map);
		System.out.println(table[1].getClass()); // 输出 class java.util.HashMap$Node
		
		map.put(new KeyNode(), 1);  // 8
		System.out.println(table[1].getClass()); // 输出 class java.util.HashMap$Node
		
		map.put(new KeyNode(), 1);  // 9
		System.out.println(table[1].getClass()); // 输出 class java.util.HashMap$Node
	}	
}

class KeyNode{
	
	@Override
	public int hashCode(){
		return 1;
	}
	
}
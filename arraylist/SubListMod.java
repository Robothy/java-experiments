import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

class SubListMod{
    public static void main(String[] args){
		ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1,2,3));
		List<Integer> subList = list.subList(1,3);
		System.out.println(subList); // 输出 [2,3]
		list.add(4);
		subList.add(5); // 抛出 ConcurrentModificationException
	}
}
import java.util.ArrayList;
import java.util.Arrays;

class ArrayListRemoveTest{
    public static void main(String[] args){
	    ArrayList<Integer> list = new ArrayList<>(Arrays.asList(6,7,8));
		list.remove(Integer.valueOf(1));
		System.out.println(list); // 6,7,8
		boolean result = list.remove(1);
		System.out.println(list); // 6,8
		System.out.println(result);
	}
}
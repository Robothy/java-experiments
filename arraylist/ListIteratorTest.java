import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Arrays;

class ListIteratorTest{
    public static void main(String[] args){
        ArrayList<Character> list = new ArrayList<>(Arrays.asList('A', 'B', 'C', 'D'));
		ListIterator<Character> it = list.listIterator(); System.out.println(list); // ABCD
		it.add('E'); System.out.println(list); // EABCD
		it.next();
		it.remove(); System.out.println(list); // EBCD
		it.add('E'); System.out.println(list); // EEBCD
		it.next();
		it.add('F'); System.out.println(list); // EEBFCD
		it.next();
		it.previous();
		it.remove(); System.out.println(list); // EEBFD
		it.previous();
		it.set('A'); System.out.println(list); // EEBAD
		it.remove(); System.out.println(list); // EEBD
	}
}
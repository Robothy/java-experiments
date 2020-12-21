import java.util.LinkedList;


class LinkedListContainsTest{
	
	public static void main(String[] args){
		LinkedList<String> list = new LinkedList<>();
		
		String hello = "Hello";
		list.add(null);
		list.add(hello);
		System.out.println(list.contains(hello)); // true
		System.out.println(list.contains(null));  // true
	}
	
}
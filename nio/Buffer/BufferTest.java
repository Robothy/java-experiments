import java.nio.*;
import java.util.*;

class BufferTest{
	
	public static void main(String[] args){
		
		CharBuffer buf = CharBuffer.allocate(10);
        buf.put('A').put('B').put('C').put('D').put('E').put('F').put('G');
        buf.flip(); // limit = position, position = 0;
        System.out.println(buf.get()); // 输出：A
        System.out.println(buf.get()); // 输出：B
        System.out.println(buf.get()); // 输出：C
		buf.mark();
		System.out.println(buf.get()); // 输出：D
		buf.reset();
        buf.compact();
        System.out.println(Arrays.toString(buf.array())); // 输出：[D, E, F, G, E, F, G, , , ]
	}
	
}
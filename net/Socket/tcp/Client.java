import java.net.*;
import java.io.*;
import java.util.*;

class Client{
    
    public static void main(String[] args){
        
        try(Socket socket = new Socket("localhost", 9090)){
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\r\n");
            String msg = null;
            while( !(msg = scanner.next()).equals("Bye") ){
                System.out.printf("Send Msg --> %s \n", msg);
                out.write(msg);
                out.flush(); // 立即发送，否则需要积累到一定大小才一次性发送
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }
    
}
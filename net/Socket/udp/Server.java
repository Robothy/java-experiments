import java.net.*;
import java.io.*;

class Server {
    
    public static void main(String[] args){
        
        // 创建一个 DatagramPacket 实例，用来接收客户端发送过来的 UDP 数据报，这个实例可以重复利用。
        byte[] buf = new byte[8192]; // 缓存区
        int len = buf.length;         // 要利用的缓存区的大小
        DatagramPacket pac = new DatagramPacket(buf, len);
        
        // 创建服务端的套接字，需要指定绑定的端口号
        try(DatagramSocket serv = new DatagramSocket(9191)){
            
            while(true){
                serv.receive(pac); // 接收数据报。如果没有数据报发送过去，会阻塞
                System.out.println("Message --> " + new String(pac.getData(), 0, pac.getLength()));
            }
            
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
}
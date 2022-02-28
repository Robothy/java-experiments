import java.io.*;
import java.net.*;
import java.util.*;

class Client {
    public static void main(String[] args){
        
        // 创建一个客户端的 UDP 套接字，不需要指定任何信息
        try(DatagramSocket client = new DatagramSocket()){
            
            // 创建一个数据报实例，数据和长度在发送之前都会重新设置，所以这里直接置为 0 即可。 
            // 由于是发送端，所以需要设置服务端的地址和端口
            DatagramPacket pac = new DatagramPacket(new byte[0], 0, InetAddress.getByName("localhost"), 9191);
            
            // 扫描控制台输入
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\r\n");
            String msg = null;
            while( !(msg = scanner.next()).equals("Bye") ){
                // 设置要发送的数据
                pac.setData(msg.getBytes()); 
                // 发送数据报
                client.send(pac);
                System.out.println("Sent Message --> " + msg);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }
}
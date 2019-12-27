package chat.server;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //保存了所有的服务器端的sockert
//    public static List<Socket> sockets = new ArrayList<>();

    public static CrazyitMap<String,Socket>map=new CrazyitMap<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(30000);
        while (true) {
            Socket socket = serverSocket.accept();//会阻塞
            System.out.println("有客户端连接上来了");
//            sockets.add(socket);

            new Thread(new ServerThread(socket)).start();
        }
    }
}

package chat.client;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 30000);

        //启动客户端线程，读从服务器传输过来的数据  new Thread(Runnable)
        new Thread(new ClientThread(socket)).start();

        //得到输出流,为客户端发数据做个准备   socket 套接字
        PrintStream printStream = new PrintStream(socket.getOutputStream());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        //不停的从键盘输入
        while ((line = bufferedReader.readLine()) != null) {
            //数据发到服务器
            printStream.println(line);
        }


    }
}

package chat.server;


import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Set;

//为每一个客户端开一个新线程服务
public class ServerThread implements Runnable {
    private Socket socket;
    PrintStream printStream;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            printStream = new PrintStream(socket.getOutputStream());
            while ((line = readFromClient(bufferedReader)) != null) {
                System.out.println("从客户端读取到:");
                //登录信息
                if (line.startsWith(CrazyitProtocol.USER_ROUND) && line.endsWith(CrazyitProtocol.USER_ROUND)) {
                    String userInfo = this.getRealMsg(line);
                    String[] users = userInfo.split(CrazyitProtocol.SPLIT_SIGN);
                    String name = users[0];
                    String password = users[1];
                    //从文件中读数据校验用户是否合法   合法 向客户端输出 ㊥㊣1㊥㊣     不合法 向客户端输出 ㊥㊣-1㊥㊣
                    boolean f = this.checkName(name, password);
                    System.out.println("是否存在此用户:"+f);
                    String r = null;
                    if (f) {
                        r = CrazyitProtocol.LOGIN_STATUS + CrazyitProtocol.LOGIN_SUCCESS + CrazyitProtocol.LOGIN_STATUS;
                        Server.map.put(name,socket);
                        //2.登录成功后,把服务器端所有人的信息发回客户端
                        Set<String> ns = Server.map.keySet();  //  ☺♣zs,li,ww☺♣
                        String online = CrazyitProtocol.ONLINE_USERS + String.join(",", ns) + CrazyitProtocol.ONLINE_USERS;
//                        printStream.println(online);  //只是当前登录上来的人发送返回信息，其他已经登录的用户就无法获取最新登录的人,需要群发
                        //打开此用户的聊天窗口
                        printStream.println(r);
                        //将在线用户的字符串群发
                        returlUsers(online);
                    } else {
                        r = CrazyitProtocol.LOGIN_STATUS + CrazyitProtocol.LOGIN_ERROR + CrazyitProtocol.LOGIN_STATUS;
                        printStream.println(r);
                    }
                    //把登录成功与否的信息发回客户端


                }
                //聊天信息
                else if(line.startsWith(CrazyitProtocol.MSG_ROUND)&&line.endsWith(CrazyitProtocol.MSG_ROUND)){
                    System.out.println("群聊信息"+line);
                    String realMsg=this.getRealMsg(line);

                    //谁发的
                    String u= Server.map.getKeyByValue(socket);
                    //准备传回
                    String ret=CrazyitProtocol.MSG_ROUND+u+"说:"+realMsg+CrazyitProtocol.MSG_ROUND;
                    //群发
                    Collection<Socket>c=Server.map.values();
                    for(Socket s:c){
                        printStream = new PrintStream(s.getOutputStream());
                        printStream.println(ret);
                    }
                }else if(line.startsWith(CrazyitProtocol.REGIST_ROUND)&&line.endsWith(CrazyitProtocol.REGIST_ROUND)){
                    String regInfo=this.getRealMsg(line);
                    boolean b=false;
                    for (int i=0;i<Server.map.size();i++){
                        if(Server.map.containsKey(regInfo)){
                            b=true;
                            break;
                        }
                    }
                    if(b){
                        String s=CrazyitProtocol.REGIST_ROUND+1+CrazyitProtocol.REGIST_ROUND;
                        printStream.println(s);
                    }else{
                        String s=CrazyitProtocol.REGIST_ROUND+0+CrazyitProtocol.REGIST_ROUND;
                        printStream.println(s);
                    }

                }
            }//while结束
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 定义读取客户端数据的方法
    private String readFromClient(BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        }
        // 如果捕捉到异常，表明该Socket对应的客户端已经关闭
        catch (IOException e) {
            // 删除该Socket。
            Server.map.removeByValue(socket);      // ①
        }
        return null;
    }

    // 将读到的内容去掉前后的协议字符，恢复成真实数据
    private String getRealMsg(String line) {
        return line.substring(CrazyitProtocol.PROTOCOL_LEN
                , line.length() - CrazyitProtocol.PROTOCOL_LEN);
    }

    //从文件中读数据校验用户是否合法
    private boolean checkName(String name, String password) {
        boolean f = false;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("users.txt"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] userInfo = line.split(",");
                String n = userInfo[0];
                String p = userInfo[1];
                if (name.equals(n) && password.equals(p)) {
                    f = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }


    //群发数据给所有人
    public void returlUsers(String data) {
        Collection<Socket> sockets = Server.map.values();
        for (Socket s : sockets) {
            try {
                //所有人的输出流
                PrintStream ps = new PrintStream(s.getOutputStream());
                ps.println(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
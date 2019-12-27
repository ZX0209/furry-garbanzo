package chat.client;



import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Socket socket;
    private LoginFrame loginFrame;
    public ChatFrame chatFrame;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
    }

    public ClientThread(Socket socket, LoginFrame loginFrame) {
        this.socket = socket;
        this.loginFrame = loginFrame;
    }

    //不停的读从网络上来的数据
    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("从服务器读取到:"+line);
                if (line.startsWith(CrazyitProtocol.LOGIN_STATUS) && line.endsWith(CrazyitProtocol.LOGIN_STATUS)) {
                    String realMsg = this.getRealMsg(line);
                    if (realMsg.equals(CrazyitProtocol.LOGIN_SUCCESS)) {
                        //合法用户
                        //关闭登录窗口
                        loginFrame.dispose();
                        //打开聊天界面,传递socket
                        chatFrame=new ChatFrame(socket);
                        chatFrame.acceptPane.setText("群聊窗口:");
                    }else {
                        JOptionPane.showMessageDialog(null,"账号或密码错误","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    }
                }else if(line.startsWith(CrazyitProtocol.MSG_ROUND)&&line.endsWith(CrazyitProtocol.MSG_ROUND)){
                    String realMsg=this.getRealMsg(line);
                    String text=chatFrame.acceptPane.getText();
                    if(!"".equals(text)){
                        chatFrame.acceptPane.setText(text+"\n"+realMsg);
                    }else {
                        chatFrame.acceptPane.setText(realMsg);
                    }
                }//在线用户
                else if (line.startsWith(CrazyitProtocol.ONLINE_USERS) && line.endsWith(CrazyitProtocol.ONLINE_USERS)) {
                    System.out.println("在线用户:" + line);
                    String realMsg = this.getRealMsg(line);
                    //把数据输出到聊天界面的在线列表
                    chatFrame.lstUser.setListData(realMsg.split(","));
                }else if(line.startsWith(chat.server.CrazyitProtocol.REGIST_ROUND)&&line.endsWith(chat.server.CrazyitProtocol.REGIST_ROUND)){
                    String s=this.getRealMsg(line);
                    if(s.equals("1")){
                        JOptionPane.showMessageDialog(null,"此账户名可用","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null,"此账户名已存在","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }//while 结束
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 将读到的内容去掉前后的协议字符，恢复成真实数据
    private String getRealMsg(String line) {
        return line.substring(CrazyitProtocol.PROTOCOL_LEN
                , line.length() - CrazyitProtocol.PROTOCOL_LEN);
    }
}

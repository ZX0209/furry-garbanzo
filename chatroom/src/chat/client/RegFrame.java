package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class RegFrame extends JFrame {
    //登录窗体宽度
    private static final Integer FRAME_WIDTH = 400;
    // 登录窗体高度
    private static final Integer FRAME_HEIGHT = 300;

    public RegFrame(LoginFrame loginFrame) throws HeadlessException {
        loginFrame.dispose();
        this.setTitle("注册窗体");
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //关闭x按钮退出程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //窗口居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        this.setLocation((width - FRAME_WIDTH) / 2, (height - FRAME_HEIGHT) / 2);


        //设置背景
        //加载窗体的背景图片
        ImageIcon imageIcon = new ImageIcon("images/beijing2.jpg");
        //创建一个标签并将图片添加进去
        JLabel lblBackground = new JLabel(imageIcon);
        //设置lblBackground布局为空布局,采用绝对定位
        lblBackground.setLayout(null);
        //设置图片的位置和大小
        lblBackground.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        //添加到当前窗体中
        this.add(lblBackground);

        //创建一个账号标签
        JLabel lblUid = new JLabel("账 号: ");
        //设置位置、大小
        lblUid.setBounds(80, 40, 120, 30);
        lblUid.setFont(new Font("宋体", 0, 16));
        lblUid.setForeground(Color.WHITE);
        //把标签放到登录窗口
        lblBackground.add(lblUid);

        //账号文本框
        JTextField textUid = new JTextField();
        //设置文本框的位置、大小
        textUid.setBounds(150, 40, 160, 30);
        lblBackground.add(textUid);

        //创建第一个密码的标签
        JLabel lblPsw1 = new JLabel("密 码: ");
        //设置标签的位置、大小
        lblPsw1.setBounds(80, 80, 120, 30);
        lblPsw1.setFont(new Font("宋体", 0, 16));
        lblPsw1.setForeground(Color.WHITE);
        lblBackground.add(lblPsw1);
        //创建第一个密码框，用于输入用户密码
        JPasswordField textPsw1 = new JPasswordField();
        //设置密码框的位置、大小
        textPsw1.setBounds(150, 80, 160, 30);
        lblBackground.add(textPsw1);



        //创建第二个密码的标签
        JLabel lblPsw2 = new JLabel("确认密码: ");
        //设置标签的位置、大小
        lblPsw2.setBounds(60, 120, 120, 30);
        lblPsw2.setFont(new Font("宋体", 0, 16));
        lblPsw2.setForeground(Color.WHITE);
        lblBackground.add(lblPsw2);
        //创建第二个密码框，用于输入用户密码
        JPasswordField textPsw2 = new JPasswordField();
        //设置密码框的位置、大小
        textPsw2.setBounds(150, 120, 160, 30);
        lblBackground.add(textPsw2);



        JButton checkName=new JButton("检查");
        checkName.setBounds(60,170,80,25);
        lblBackground.add(checkName);


        checkName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.获取账号，密码
                String name = textUid.getText();
                char[] temp1 = textPsw1.getPassword();
                String password1 = new String(temp1, 0, temp1.length);
                char[] temp2 = textPsw1.getPassword();
                String password2 = new String(temp2, 0, temp2.length);

                //2.向服务器发socket请求,建立连接
                Socket socket = null;
                try {
                    socket = new Socket("127.0.0.1", 30000);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //3.为了区分信息的类型，带上协议字符
                String regInfo = CrazyitProtocol.REGIST_ROUND + name + CrazyitProtocol.REGIST_ROUND;
                //4.把上面的数据发给服务器去验证是否是合法用户
                PrintStream printStream = null;
                try {
                    printStream = new PrintStream(socket.getOutputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                printStream.println(regInfo);
                //启动客户端线程，读从服务器传输过来的数据  new Thread(Runnable)
                new Thread(new ClientThread(socket, loginFrame)).start();

            }
        });






        //创建一个注册按钮
        JButton regist = new JButton("注 册");
        //设置按钮的位置、大小
        regist.setBounds(215, 170, 80, 25);
        //添加到背景图片上
        lblBackground.add(regist);

        //显示窗口
        this.setVisible(true);
    }

}

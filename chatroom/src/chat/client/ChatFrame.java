package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ChatFrame extends JFrame {
    JTextPane acceptPane;
    JList lstUser;
    /**
     * 服务器窗体宽度
     */
    public static final Integer FRAME_WIDTH = 750;
    /**
     * 服务器窗体高度
     */
    public static final Integer FRAME_HEIGHT = 600;

    public ChatFrame(Socket socket) throws HeadlessException {
        this.setTitle("聊天室主界面");
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //关闭x按钮退出程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        //窗口居中
        this.setLocation((width - FRAME_WIDTH) / 2, (height - FRAME_HEIGHT) / 2);

        //设置背景
        //加载窗体的背景图片
        ImageIcon imageIcon = new ImageIcon("images/beijing.jpg");
        //创建一个标签并将图片添加进去
        JLabel frameBg = new JLabel(imageIcon);
        //设置lblBackground布局为空布局,采用绝对定位
        frameBg.setLayout(null);
        //设置图片的位置和大小
        frameBg.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        //添加到当前窗体中
        this.add(frameBg);

        // 接收框
        acceptPane = new JTextPane();
        acceptPane.setOpaque(false);//设置透明
        acceptPane.setFont(new Font("宋体", 0, 16));

        // 设置接收框滚动条
        JScrollPane scoPaneOne = new JScrollPane(acceptPane);
        scoPaneOne.setBounds(15, 20, 500, 332);
        //设置背景透明
        scoPaneOne.setOpaque(false);
        scoPaneOne.getViewport().setOpaque(false);
        frameBg.add(scoPaneOne);

        //当前在线用户列表
        lstUser = new JList();
        lstUser.setFont(new Font("宋体", 0, 14));
        lstUser.setVisibleRowCount(17);
        lstUser.setFixedCellWidth(180);
        lstUser.setFixedCellHeight(18);

        JScrollPane spUser = new JScrollPane(lstUser);
        spUser.setFont(new Font("宋体", 0, 14));
        spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        spUser.setBounds(530, 17, 200, 507);
        frameBg.add(spUser);

        // 输入框
        JTextPane sendPane = new JTextPane();
        sendPane.setOpaque(false);
        sendPane.setFont(new Font("宋体", 0, 16));

        JScrollPane scoPane = new JScrollPane(sendPane);// 设置滚动条
        scoPane.setBounds(15, 400, 500, 122);
        scoPane.setOpaque(false);
        scoPane.getViewport().setOpaque(false);
        frameBg.add(scoPane);

        // 添加表情选择
        JLabel lblface = new JLabel(new ImageIcon("images/face.png"));
        lblface.setBounds(14, 363, 25, 25);
        frameBg.add(lblface);

        // 添加抖动效果
        JLabel lbldoudong = new JLabel(new ImageIcon("images/doudong.png"));
        lbldoudong.setBounds(43, 363, 25, 25);
        frameBg.add(lbldoudong);

        // 设置字体选择
        JLabel lblfontChoose = new JLabel(new ImageIcon("images/ziti.png"));
        lblfontChoose.setBounds(44, 363, 80, 25);
        frameBg.add(lblfontChoose);

        //字体下拉选项
        JComboBox fontFamilyCmb = new JComboBox();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String[] str = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String string : str) {
            fontFamilyCmb.addItem(string);
        }
        fontFamilyCmb.setSelectedItem("楷体");
        fontFamilyCmb.setBounds(104, 363, 150, 25);
        frameBg.add(fontFamilyCmb);

        /*
         * 发送按钮
         */
        JButton send = new JButton("发 送");
        send.setBounds(15, 533, 125, 25);
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了发送按钮");
                String text=sendPane.getText();
                System.out.println(text);
                String msg=CrazyitProtocol.MSG_ROUND+text+CrazyitProtocol.MSG_ROUND;
                PrintStream ps = null;
                try {
                    ps = new PrintStream(socket.getOutputStream());
                    ps.println(msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //清空
                sendPane.setText(null);


            }
        });

        frameBg.add(send);

        //显示窗口
        this.setVisible(true);
    }




    private String readFromClient(BufferedReader bufferedReader) {
        try {
            String last = "";
            while (true) {
                String temp = bufferedReader.readLine();
                last = last + temp;
                boolean f = this.isEnd(temp);
                if (f) {
                    break;
                }
                last = last + "\r\n";
            }
            return last;
        }
        // 如果捕捉到异常，表明该Socket对应的客户端已经关闭
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //判断是否结束
    public boolean isEnd(String line) {
        String protocol = line.substring(line.length() - 2);
        String[] ps = {CrazyitProtocol.LOGIN_STATUS, CrazyitProtocol.MSG_ROUND, CrazyitProtocol.LOGIN_STATUS, CrazyitProtocol.PRIVATE_ROUND, CrazyitProtocol.USER_ROUND};
        boolean f = false;
        for (String p : ps) {
            if (p.equals(protocol)) {
                f = true;
                break;
            }
        }
        return f;
    }


}

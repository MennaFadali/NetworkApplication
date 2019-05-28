import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GUI extends JFrame {
    static final Dimension fd = new Dimension(500, 100);
    static final Dimension bd = new Dimension(50, 30);
    static final Dimension td = new Dimension(500, 50);
    static final Font font = new Font("", Font.PLAIN, 20);
    static String name;
    static int port, server;
    static JTextArea received;
    static DataOutputStream dos;

    GUI() {
        super();
        this.setSize(fd);
        this.setLocation(500, 250);
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dos.writeUTF("Quit");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                super.windowClosing(e);
                System.exit(0);
            }
        });
        setEnternamepannel();
        this.setVisible(true);
    }


    public static void main(String[] args) {
        new GUI();
    }

    static boolean check(String name) throws IOException {
        Socket one = new Socket("localhost", 1234);
        Socket two = new Socket("localhost", 6789);
        DataInputStream din1 = new DataInputStream(one.getInputStream());
        DataOutputStream dout1 = new DataOutputStream(one.getOutputStream());
        DataInputStream din2 = new DataInputStream(two.getInputStream());
        DataOutputStream dout2 = new DataOutputStream(two.getOutputStream());
        dout1.writeUTF("#");
        String f1 = din1.readUTF();
        dout2.writeUTF("#");
        String f2 = din2.readUTF();
        if (f1.contains(name) || f2.contains(name)) return false;
        return true;
    }

    static String[] getMembers() throws IOException {
        Socket one = new Socket("localhost", 1234);
        Socket two = new Socket("localhost", 6789);
        DataInputStream din1 = new DataInputStream(one.getInputStream());
        DataOutputStream dout1 = new DataOutputStream(one.getOutputStream());
        DataInputStream din2 = new DataInputStream(two.getInputStream());
        DataOutputStream dout2 = new DataOutputStream(two.getOutputStream());
        dout1.writeUTF("#");
        String f1 = din1.readUTF();
        dout2.writeUTF("#");
        String f2 = din2.readUTF();
        String res = "Choose who to the send the message to #"+f1 + f2;
        return res.split("#");

    }

    void setMainPannel() throws IOException {
        Engine();
        this.setSize(new Dimension(1000, 1000));
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        received = new JTextArea("                                                             "+name  + "'s Messages");
        received.setEditable(false);
        received.setLineWrap(true);
        received.setWrapStyleWord(true);
        received.setFont(font);
        received.setForeground(Color.GREEN);
        received.setSize(new Dimension(1000, 500));
        main.add(received, BorderLayout.CENTER);
        JButton sendMessage = new JButton("Send Messgae");
        sendMessage.setFont(font);
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Window();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        main.add(sendMessage, BorderLayout.SOUTH);
        this.setContentPane(main);


    }

    void Engine() throws IOException {
        Socket socket = new Socket("localhost", port);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(name);
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = dis.readUTF();
                        StringBuilder sb = new StringBuilder(received.getText());
                        sb.append("\n" + message);
                        received.setText(sb.toString());
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
        readMessage.start();

    }

    void setEnternamepannel() {
        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.white);
        panel1.setLayout(new BorderLayout());
        JLabel label1 = new JLabel("Please Enter Your Name");
        label1.setFont(font);
        JTextField txtarea = new JTextField();
        txtarea.setFont(font);
        txtarea.setSize(td);
        JLabel error = new JLabel("This name is incorrect");
        error.setVisible(false);
        error.setForeground(Color.red);
        JButton nxt = new JButton("Next");
        nxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtarea.getText().length() == 0) {
                    error.setText("Your name cannot be empty");
                    error.setVisible(true);
                    return;
                }
                try {
                    boolean can = check(txtarea.getText());
                    if (!can) {
                        error.setText("Name is already taken please choose another one");
                        error.setVisible(true);
                        return;
                    }
                    name = txtarea.getText();
                    panel1.setVisible(false);
                    selectServerPanel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        panel1.add(label1, BorderLayout.NORTH);
        panel1.add(txtarea, BorderLayout.CENTER);
        panel1.add(nxt, BorderLayout.EAST);
        panel1.add(error, BorderLayout.SOUTH);
        this.setContentPane(panel1);
        panel1.setVisible(true);
    }

    void selectServerPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new BorderLayout());
        JLabel label2 = new JLabel("Choose the server you want to join");
        String[] options = {"Server 1", "Server 2"};
        JComboBox servers = new JComboBox(options);
        servers.setSelectedIndex(0);
        JButton button = new JButton("Join");
        button.setPreferredSize(bd);
        panel.add(label2, BorderLayout.NORTH);
        panel.add(servers, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        panel.setVisible(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int s = servers.getSelectedIndex();
                if (s == 0) {
                    port = 1234;
                    server = 1;
                } else {
                    port = 6789;
                    server = 1;
                }
                try {
                    panel.setVisible(false);
                    setMainPannel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.setContentPane(panel);
    }

    static class Window extends JFrame implements ActionListener {
        String[] clients;
        JComboBox options;
        JTextArea message;

        Window() throws IOException {
            this.setSize(new Dimension(500, 200));
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            message = new JTextArea();
            message.setFont(font);
            clients = getMembers();
            options = new JComboBox(clients);
            options.setEditable(false);
            options.setFont(font);
            options.setForeground(Color.GREEN);
            options.setSelectedIndex(0);
            JButton button = new JButton("SEND");
            button.addActionListener(this);
            panel.add(options, BorderLayout.NORTH);
            panel.add(message, BorderLayout.CENTER);
            panel.add(button, BorderLayout.SOUTH);
            panel.setVisible(true);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            panel.setBackground(Color.black);
            this.setContentPane(panel);
            this.setVisible(true);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (options.getSelectedIndex()==0) return;
                dos.writeUTF(clients[options.getSelectedIndex()] + "#" + message.getText());
                this.dispose();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


}

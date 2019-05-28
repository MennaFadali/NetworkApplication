import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

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
        if (f1.contains(name) || f2.contains(name)) return true;
        return false;
    }

    static boolean checkformat(String name) {
        if (name.length() >= 7 && name.substring(0, 5).equals("Join(") && name.charAt(name.length() - 1) == ')')
            return true;
        return false;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        String name = "";
        while (true) {
            name = inFromUser.readLine();
            if (!checkformat(name)) {
                System.out.println("Wrong request format");
                continue;
            }
            name = extractName(name);
            if (!check(name)) break;
            System.out.println("Already Used username choose another one");
        }
        System.out.println("Which server do you want to join");
        int n = Integer.parseInt(inFromUser.readLine());
        while (n > 2) {
            System.out.println("Wrong server please choose another one");
            n = Integer.parseInt(inFromUser.readLine());
        }
        int port = (n == 1 ? 1234 : 6789);
        Socket socket = new Socket("localhost", port);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(name);
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = inFromUser.readLine();
                        String[] a = message.split("#");
                        if (a.length == 2 || message.equals("Quit") || message.equals("GetMemberList()"))
                            dos.writeUTF(message);
                        else
                            System.out.println("NOT VALID __ SPECIFY RECEIVER");
                    } catch (Exception e) {
                        return;
                    }
                }

            }
        });
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = dis.readUTF();
                        System.out.println(message);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
        sendMessage.start();
        readMessage.start();

    }

    static String extractName(String in) {
        return in.substring(5, in.length() - 1);
    }
}
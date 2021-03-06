import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server2 {
    public static HashMap<String, ClientHandler> clients;
    static ServerSocket listener;

    public static void main(String[] args) throws IOException {
        listener = new ServerSocket(6789);
        clients = new HashMap<>();
        System.out.println("Server 2 is running at port # 6789 ");
        while (true) {
            Socket client = null;
            try {
                client = listener.accept();
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                String name = dis.readUTF();
                if (name.charAt(0) == '#') {
                    if (name.length() == 1) {
                        String names = "";
                        for (String cur : clients.keySet())
                            names += cur + "#";
                        dos.writeUTF(names);
                    } else {
                        String[] code = name.substring(1).split("#");
                        OtherHandler mtch = new OtherHandler(2, client, dis, dos, code);
                        Thread tmp = new Thread(mtch);
                        tmp.start();
                    }
                } else {
                    ClientHandler mtch = new ClientHandler(client, dis, dos, 2);
                    mtch.setName(name);
                    System.out.println("Client " + name + " is connected");
                    Thread tmp = new Thread(mtch);
                    clients.put(name, mtch);
                    clients.put(name, mtch);
                    tmp.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    void Route(String Message, String Destination) {

    }
}